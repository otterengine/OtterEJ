package net.xdefine.db.drivers.mysql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import net.xdefine.db.criterion.Criterion;
import net.xdefine.db.impl.XSessionImpl;
import net.xdefine.db.utils.XQuery;
import net.xdefine.db.utils.XQueryImpl;
import net.xdefine.db.vo.MetaColumn;
import net.xdefine.db.vo.MetaTable;
import net.xdefine.db.vo.QueryObject;

public class XQueryForMySQL extends XQueryImpl {

	public XQueryForMySQL(XSessionImpl session) {
		super(session);
	}

	public XQueryForMySQL(XSessionImpl session, XQuery.Mode mode, String query) {
		super(session, mode, query);
	}

	public XQueryForMySQL(XSessionImpl session, XQuery.Mode mode, String query, Class<?> clazz) {
		super(session, mode, query, clazz);
	}

	@Override
	protected QueryObject generateQuery(List<MetaTable> metadatas) {
		
		QueryObject object = new QueryObject();
		
		MetaTable def = metadatas.get(0);
		Map<String, JSONObject> fieldObjects = new HashMap<String, JSONObject>();
		
		StringBuilder fields = new StringBuilder();
		for (MetaTable t : metadatas) {
			for (MetaColumn field : t.getProperties()) {
				if (!fields.toString().isEmpty()) fields.append(", ");
				
				if (field.getName().startsWith("(")) {
					fields.append(field.getName() + " " + field.getAlias());
				}
				else {
					fields.append(t.getUnique().trim() + ".`" + field.getName() + "` " + field.getAlias());
				}
				
				String alias = field.getAlias().substring(t.getUnique().trim().length() + 1);
				if (t != def) {
					alias = t.getRefs() + "." + alias;
				}
				field.getObject().put("unique", t.getUnique());
				fieldObjects.put(alias, field.getObject());
			}
		}
		
		Pattern p = Pattern.compile("\\$\\{([a-zA-Z0-9\\-\\_\\.]*)\\}");

		StringBuilder sbTables = new StringBuilder();
		sbTables.append(def.getName() + " " + def.getUnique().trim());
		
		if (metadatas.size() > 1) {
			for (int n = 1; n < metadatas.size(); n++) {
				MetaTable tb = metadatas.get(n);
				sbTables.append(" LEFT JOIN " + tb.getName() + " " + tb.getUnique().trim());
				sbTables.append(" ON " + tb.getJoin() + " ");
			}
		}

		
		StringBuilder options = new StringBuilder();
		for (Criterion criterion : criterions) {
			options.append((!options.toString().isEmpty()) ? " AND " : " WHERE ");
			
			QueryObject sub = criterion.toSQLString();
			String query = sub.getQuery();
			Matcher m = p.matcher(query);
			while(m.find()) {
				
				if (!fieldObjects.containsKey(m.group(1).trim())){
					throw new IllegalStateException("not found Field : " + m.group(1).trim());
				}
				
				JSONObject field = fieldObjects.get(m.group(1).trim());
				System.out.println(field);
				query = query.replaceAll("\\$\\{" + m.group(1).trim() + "\\}", field.getString("unique") + "." + field.getString("db-var"));
			}
		
			object.getData().addAll(sub.getData());
			options.append(query);
		}
		
		object.setQuery("SELECT " + fields.toString() + " FROM " + sbTables.toString() + options);
		
		return object;
	}

	@Override
	protected String appendRowControlQuery(String queryString) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM (");
		sb.append(queryString);
		sb.append(") TB ");
		if (this.start >= 0 && this.length >= 0) {
			sb.append("LIMIT " + start + "," + length);
		}
		else if (this.length >= 0) {
			sb.append("LIMIT " + length);
		}
		else if (this.start >= 0) {
			sb.append("LIMIT " + start + ", -1");
		}
		
		return sb.toString();
	}


}

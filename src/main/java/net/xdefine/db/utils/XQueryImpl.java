package net.xdefine.db.utils;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.xdefine.db.criterion.Criterion;
import net.xdefine.db.impl.XSessionFactoryImpl;
import net.xdefine.db.impl.XSessionImpl;
import net.xdefine.db.vo.DynamicRow;
import net.xdefine.db.vo.MetaColumn;
import net.xdefine.db.vo.MetaTable;
import net.xdefine.db.vo.PagedList;
import net.xdefine.db.vo.QueryObject;

@SuppressWarnings("rawtypes")
public abstract class XQueryImpl implements XQuery {
	
	private XSessionFactoryImpl sessionFactory;
	private XSessionImpl session;
	private XQuery.Mode mode;
	private Class<?> clazz = null;
	
	protected List<Criterion> criterions = new ArrayList<Criterion>();
	
	protected String args;
	
	protected long start = -1;
	protected long length = -1;
	
	public XQueryImpl(XSessionImpl session) {
		this.session = session;
	}

	public XQueryImpl(XSessionImpl session, XQuery.Mode mode, String args) {
		this.sessionFactory = session.getSessionFactory();
		this.session = session;
		this.mode = mode;
		this.args = args;
	}

	public XQueryImpl(XSessionImpl session, XQuery.Mode mode, String args, Class<?> clazz) {
		this.sessionFactory = session.getSessionFactory();
		this.session = session;
		this.mode = mode;
		this.args = args;
		this.clazz = clazz;
	}
	
	@Override
	public XQuery setMaxResult(int max) {
		this.length = max;
		return this;
	}

	@Override
	public XQuery setFirstResult(int start) {
		this.start = start;
		return this;
	}
	
	protected QueryObject getQuery() {
		QueryObject queryString = null;
		if (this.mode == XQuery.Mode.NATIVE_QUERY) {
			queryString = new QueryObject(this.args);
		}
		else if (this.mode == XQuery.Mode.BUILDER) {
			
			List<MetaTable> metas = new ArrayList<MetaTable>();
			JSONObject table = this.sessionFactory.entities.getJSONObject(this.args);
			
			
			System.out.println(table);
			
			int i = 1;
			MetaTable def = new MetaTable();
			def.setUnique("_0x" + String.format("%02X%n", i).trim());
			if (table.containsKey("catalog")) {
				def.setName(table.getString("catalog") + "." + table.getString("table"));
			}
			else {
				def.setName(table.getString("table"));
			}
			
			metas.add(def);
			
			for (Object object : table.getJSONArray("columns")) {
				JSONObject data = (JSONObject) object;
				
				System.out.println(data);
				
				MetaColumn column = new MetaColumn();
				column.setAlias(def.getUnique().trim() + "_" + data.getString("name"));
				column.setName(data.getString("db-var"));
				column.setObject(data);
				def.getProperties().add(column);
			}
			
			queryString = this.generateQuery(metas);
		}
		
		return queryString;
	}

	protected abstract QueryObject generateQuery(List<MetaTable> metadatas);
	protected abstract String appendRowControlQuery(String queryString);

	@Override
	public PagedList list() {
		return this.list(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PagedList list(Class<?> resultClass) {
		
		if (resultClass == null) resultClass = this.clazz;

		List<MetaTable> metaTables = null;
		QueryObject queryString = null;
		if (this.mode == XQuery.Mode.NATIVE_QUERY) {
			queryString = new QueryObject(this.args);
		}
		else if (this.mode == XQuery.Mode.BUILDER) {
			metaTables = new ArrayList<MetaTable>();
			JSONObject table = this.sessionFactory.entities.getJSONObject(this.args);

			int i = 1;
			MetaTable def = new MetaTable();
			def.setUnique("_0x" + String.format("%02X%n", i).trim());
			if (table.containsKey("catalog")) {
				def.setName(table.getString("catalog") + "." + table.getString("table"));
			}
			else {
				def.setName(table.getString("table"));
			}
			
			metaTables.add(def);
			
			for (Object object : table.getJSONArray("columns")) {
				JSONObject data = (JSONObject) object;
				
				if (data.containsKey("join-table")) {
					i++;
					JSONObject jt = this.sessionFactory.entities.getJSONObject(data.getString("join-table"));
					MetaTable jtm = new MetaTable();
					jtm.setUnique("_0x" + String.format("%02X%n", i).trim());
					jtm.setName((jt.containsKey("catalog") ? (table.getString("catalog") + ".") : "") + jt.getString("table"));
					jtm.setJoin(data.getString("join-var"));

					JSONArray fs = jt.getJSONObject("filters").getJSONArray(data.getString("join-filter"));
					System.out.println(fs);
					for (Object sobject : jt.getJSONArray("columns")) {
						JSONObject sdata = (JSONObject) sobject;
						if (!fs.contains(sdata.getString("name"))) continue;
						System.out.println(sdata);

						MetaColumn column = new MetaColumn();
						column.setAlias(def.getUnique().trim() + "_" + data.getString("name"));
						column.setName(data.getString("db-var"));
						column.setObject(data);
						jtm.getProperties().add(column);
						

//						def.getProperties().add(column);
						
					}
					
				}
				else {
					MetaColumn column = new MetaColumn();
					column.setAlias(def.getUnique().trim() + "_" + data.getString("name"));
					column.setName(data.getString("db-var"));
					column.setObject(data);
					def.getProperties().add(column);
				}
				
			}
			
			queryString = this.generateQuery(metaTables);
		}
		
		if (this.start >= 0 || this.length >= 0) {
			queryString.setQuery(this.appendRowControlQuery(queryString.getQuery()));
		}
		
		System.out.println("XQUERY :: " + queryString.getQuery());
		
		try {
			PagedList result = new PagedList();
			ResultSet rs = this.session.createResultset(queryString.getQuery(), queryString.getData());
			while(rs.next()) {
				DynamicRow dr = new DynamicRow(rs, metaTables);
				if (this.clazz == null && resultClass == null) {
					result.add(dr);
				}
				else {
					result.add(dr.assignObject(resultClass));
				}
			}
			rs.close();
			return result;
		}
		catch(Exception ex) {
			throw new IllegalStateException(ex);
		}

	}

	@Override
	public Object uniqueResult() {
		return this.uniqueResult(null);
	}

	@Override
	public Object uniqueResult(Class<?> clazz) {
		PagedList objects = this.list(clazz);
		if (objects.size() > 1) {
			throw new IllegalStateException( "This is not unique result.");
		}
		else if (objects.size() <= 0) {
			return null;
		}
		else {
			return objects.get(0);
		}
	}

	@Override
	public int executeUpdate() {

		String queryString = this.getQuery().getQuery();
		
		try {
			int result = this.session.execute(queryString);
			return result;
		}
		catch(Exception ex) {
			throw new IllegalStateException(ex);
		}

	}

	@Override
	public XQuery add(Criterion restriction) {
		criterions.add(restriction);
		return this;
	}
	

}

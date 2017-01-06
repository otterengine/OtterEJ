package net.xdefine.db.drivers.mysql;

import java.util.List;

import net.xdefine.db.impl.XSessionImpl;
import net.xdefine.db.utils.XQuery;
import net.xdefine.db.utils.XQueryImpl;
import net.xdefine.db.vo.MetaTable;

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
	protected String generateQuery(List<MetaTable> metadatas) {
		
		/*
		StringBuilder sb = new StringBuilder();
		for (String field : fields) {
			if (!sb.toString().isEmpty()) sb.append(", ");
			sb.append("_this.`" + field + "`");
		}
		
		return "SELECT " + sb.toString() + " FROM " + tableName + " _this";*/
		
		return null;
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

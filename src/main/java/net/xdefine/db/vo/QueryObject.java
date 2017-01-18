package net.xdefine.db.vo;

import java.util.ArrayList;
import java.util.List;

public class QueryObject {
	
	private String query;
	private List<Object> data = new ArrayList<Object>();

	public QueryObject() {
		// TODO Auto-generated constructor stub
	}

	public QueryObject(String string) {
		this.query = string;
	}
	
	public QueryObject(String string, Object... values) {
		this.query = string;
		if (values != null) {
			for (Object value : values) {
				this.data.add(value);
			}
		}
	}
	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public List<Object> getData() {
		return data;
	}

	public void setData(List<Object> data) {
		this.data = data;
	}

}

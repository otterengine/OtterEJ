package net.xdefine.db.vo;

import net.sf.json.JSONObject;

public class MetaColumn {

	private String name;
	private String alias;
	private JSONObject object;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public JSONObject getObject() {
		return object;
	}

	public void setObject(JSONObject object) {
		this.object = object;
	}

}

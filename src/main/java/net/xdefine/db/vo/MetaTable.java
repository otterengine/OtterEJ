package net.xdefine.db.vo;

import java.util.ArrayList;
import java.util.List;

public class MetaTable {

	private String name;
	private String unique;
	private String join;
	private List<MetaColumn> properties = new ArrayList<MetaColumn>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnique() {
		return unique;
	}

	public void setUnique(String unique) {
		this.unique = unique;
	}

	public String getJoin() {
		return join;
	}

	public void setJoin(String join) {
		this.join = join;
	}

	public List<MetaColumn> getProperties() {
		return properties;
	}

	public void setProperties(List<MetaColumn> properties) {
		this.properties = properties;
	}

}

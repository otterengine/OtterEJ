package net.xdefine.db.criterion;

import net.xdefine.db.vo.QueryObject;

@SuppressWarnings("serial")
public class NullExpression implements Criterion {

	private final String propertyName;

	protected NullExpression(String propertyName) {
		this.propertyName = propertyName;
	}

	public String toString() {
		return propertyName + " is null";
	}
	
	@Override
	public QueryObject toSQLString() {
		return new QueryObject("${" + propertyName + "} IS NULL");
	}
}

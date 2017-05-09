package net.xdefine.db.criterion;

import net.xdefine.db.vo.QueryObject;

@SuppressWarnings("serial")
public class NotNullExpression implements Criterion {

	private final String propertyName;

	protected NotNullExpression(String propertyName) {
		this.propertyName = propertyName;
	}

	public String toString() {
		return propertyName + " is not null";
	}

	@Override
	public QueryObject toSQLString() {
		return new QueryObject("${" + propertyName + "} IS NOT NULL");
	}
}

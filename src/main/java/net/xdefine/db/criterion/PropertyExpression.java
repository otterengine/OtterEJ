package net.xdefine.db.criterion;

import net.xdefine.db.vo.QueryObject;

@SuppressWarnings("serial")
public class PropertyExpression implements Criterion {

	private final String propertyName;
	private final String otherPropertyName;
	private final String op;

	protected PropertyExpression(String propertyName, String otherPropertyName, String op) {
		this.propertyName = propertyName;
		this.otherPropertyName = otherPropertyName;
		this.op = op;
	}


	public String toString() {
		return propertyName + getOp() + otherPropertyName;
	}

	public String getOp() {
		return op;
	}
	
	@Override
	public QueryObject toSQLString() {
		return new QueryObject("${" + propertyName + "} " + getOp() + " ${" + otherPropertyName + "} ");
	}
}
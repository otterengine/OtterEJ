package net.xdefine.db.criterion;

import net.xdefine.db.vo.QueryObject;

@SuppressWarnings("serial")
public class SimpleExpression implements Criterion {

	private final String propertyName;
	private final Object value;
	private final String op;

	protected SimpleExpression(String propertyName, Object value, String op) {
		this.propertyName = propertyName;
		this.value = value;
		this.op = op;
	}

	public String toString() {
		return propertyName + getOp() + value;
	}

	protected final String getOp() {
		return op;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public QueryObject toSQLString() {
		return new QueryObject("${" + propertyName + "} " + getOp() + " ? ", value);
	}
}

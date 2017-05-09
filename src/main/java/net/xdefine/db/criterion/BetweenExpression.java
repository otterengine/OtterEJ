package net.xdefine.db.criterion;

import net.xdefine.db.vo.QueryObject;

@SuppressWarnings("serial")
public class BetweenExpression implements Criterion {

	private final String propertyName;
	private final Object lo;
	private final Object hi;

	protected BetweenExpression(String propertyName, Object lo, Object hi) {
		this.propertyName = propertyName;
		this.lo = lo;
		this.hi = hi;
	}

	public String toString() {
		return propertyName + " between " + lo + " and " + hi;
	}

	@Override
	public QueryObject toSQLString() {
		return new QueryObject("${" + propertyName + "} BETWEEN ? AND ?", lo, hi);
	}

}
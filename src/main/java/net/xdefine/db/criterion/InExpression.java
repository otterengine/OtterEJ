package net.xdefine.db.criterion;

import org.apache.commons.lang.StringUtils;

import net.xdefine.db.vo.QueryObject;

@SuppressWarnings("serial")
public class InExpression implements Criterion {

	private final String propertyName;
	private final Object[] values;

	protected InExpression(String propertyName, Object[] values) {
		this.propertyName = propertyName;
		this.values = values;
	}

	public String toString() {
		return propertyName + " in (" + StringUtils.join(values) + ')';
	}

	@Override
	public QueryObject toSQLString() {
		return new QueryObject("${" + propertyName + "} in (?)", StringUtils.join(values));
	}
}
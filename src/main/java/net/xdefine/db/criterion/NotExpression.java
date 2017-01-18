package net.xdefine.db.criterion;

import net.xdefine.db.vo.QueryObject;

@SuppressWarnings("serial")
public class NotExpression implements Criterion {

	private Criterion criterion;

	protected NotExpression(Criterion criterion) {
		this.criterion = criterion;
	}

	public String toString() {
		return "not " + criterion.toString();
	}

	@Override
	public QueryObject toSQLString() {
		QueryObject param = criterion.toSQLString();
		param.setQuery("NOT( " + param.getQuery() + " )");
		return param;
	}
}

package net.xdefine.db.criterion;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.xdefine.db.vo.QueryObject;

@SuppressWarnings("serial")
public class Junction implements Criterion {
	private final Nature nature;
	private final List<Criterion> conditions = new ArrayList<Criterion>();

	protected Junction(Nature nature) {
		this.nature = nature;
	}
	
	public Junction add(Criterion criterion) {
		conditions.add( criterion );
		return this;
	}

	public Nature getNature() {
		return nature;
	}

	public Iterable<Criterion> conditions() {
		return conditions;
	}

	@Override
	public String toString() {
		return '(' + StringUtils.join( conditions.iterator(), ' ' + nature.getOperator() + ' ' ) + ')';
	}

	@Override
	public QueryObject toSQLString() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static enum Nature {
		AND, OR;
		public String getOperator() {
			return name().toLowerCase();
		}
	}
}

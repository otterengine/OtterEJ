package net.xdefine.db.criterion;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Restrictions {

	Restrictions() {
	}
	
	public static SimpleExpression eq(String propertyName, Object value) {
		return new SimpleExpression(propertyName, value, "=");
	}

	public static SimpleExpression ne(String propertyName, Object value) {
		return new SimpleExpression(propertyName, value, "<>");
	}

	public static SimpleExpression like(String propertyName, Object value) {
		return new SimpleExpression(propertyName, value, " like ");
	}

	public static SimpleExpression gt(String propertyName, Object value) {
		return new SimpleExpression(propertyName, value, ">");
	}

	public static SimpleExpression lt(String propertyName, Object value) {
		return new SimpleExpression(propertyName, value, "<");
	}

	public static SimpleExpression le(String propertyName, Object value) {
		return new SimpleExpression(propertyName, value, "<=");
	}

	public static SimpleExpression ge(String propertyName, Object value) {
		return new SimpleExpression(propertyName, value, ">=");
	}

	public static Criterion between(String propertyName, Object lo, Object hi) {
		return new BetweenExpression(propertyName, lo, hi);
	}

	public static Criterion in(String propertyName, Object[] values) {
		return new InExpression(propertyName, values);
	}

	public static Criterion in(String propertyName, Collection<Object> values) {
		return new InExpression(propertyName, values.toArray());
	}

	public static Criterion isNull(String propertyName) {
		return new NullExpression(propertyName);
	}

	public static PropertyExpression eqProperty(String propertyName, String otherPropertyName) {
		return new PropertyExpression(propertyName, otherPropertyName, "=");
	}

	public static PropertyExpression neProperty(String propertyName, String otherPropertyName) {
		return new PropertyExpression(propertyName, otherPropertyName, "<>");
	}

	public static PropertyExpression ltProperty(String propertyName, String otherPropertyName) {
		return new PropertyExpression(propertyName, otherPropertyName, "<");
	}

	public static PropertyExpression leProperty(String propertyName, String otherPropertyName) {
		return new PropertyExpression(propertyName, otherPropertyName, "<=");
	}

	public static PropertyExpression gtProperty(String propertyName, String otherPropertyName) {
		return new PropertyExpression(propertyName, otherPropertyName, ">");
	}

	public static PropertyExpression geProperty(String propertyName, String otherPropertyName) {
		return new PropertyExpression(propertyName, otherPropertyName, ">=");
	}

	public static Criterion isNotNull(String propertyName) {
		return new NotNullExpression(propertyName);
	}

	public static Junction and(Criterion... predicates) {
		Junction conjunction = new Junction(Junction.Nature.AND);
		if (predicates != null) {
			for (Criterion predicate : predicates) {
				conjunction.add(predicate);
			}
		}
		return conjunction;
	}
	
	public static Junction or(Criterion... predicates) {
		Junction disjunction = new Junction(Junction.Nature.OR);
		if (predicates != null) {
			for (Criterion predicate : predicates) {
				disjunction.add(predicate);
			}
		}
		return disjunction;
	}

	public static Criterion not(Criterion expression) {
		return new NotExpression(expression);
	}

	public static Criterion allEq(Map<String, Object> propertyNameValues) {
		Junction conj = new Junction(Junction.Nature.AND);
		Iterator<Entry<String, Object>> iter = propertyNameValues.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Object> me = iter.next();
			conj.add(eq((String) me.getKey(), me.getValue()));
		}
		return conj;
	}

}
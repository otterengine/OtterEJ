package net.xdefine.db.utils;

import net.xdefine.db.criterion.Criterion;
import net.xdefine.db.vo.PagedList;

@SuppressWarnings("rawtypes")
public interface XQuery {
	
	XQuery setMaxResult(int max);

	XQuery setFirstResult(int start);
	
	XQuery add(Criterion restriction);

	PagedList list();

	PagedList list(Class<?> clazz);

	Object uniqueResult();
	
	Object uniqueResult(Class<?> clazz);
	
	int executeUpdate();
	
	enum Mode { NATIVE_QUERY, BUILDER };
}

package net.xdefine.db.utils;

import java.util.Collection;

import net.xdefine.db.vo.PagedList;

@SuppressWarnings("rawtypes")
public interface XQuery {

	XQuery setParameter(String name, Object value);

	XQuery setParameter(String name, Collection value);

	XQuery setMaxResult(int max);

	XQuery setFirstResult(int start);

	PagedList list();

	PagedList list(Class<?> clazz);

	Object uniqueResult();
	
	Object uniqueResult(Class<?> clazz);
	
	int executeUpdate();
	
	enum Mode { NATIVE_QUERY, BUILDER };
}

package net.xdefine.db;

import java.util.Map;

import net.xdefine.db.utils.XQuery;

public interface XSession {

	void setReadOnly(boolean b);

	void beginTrasaction();

	void commit();
	
	void close();

	boolean isClosed();

	boolean isReadOnly();

	boolean isAutoCommit();

	Object get(String table, long id);

	Object get(Class<?> clazz, long id);

	Object persist(String table, Map<String, Object> object);

	Object persist(Class<?> clazz);

	Object merge(String table, Map<String, Object> object);

	Object merge(Class<?> clazz);

	XQuery createQueryByFile(String name);

	XQuery createQueryByNative(String name);

	XQuery createQueryByBuilder(String name);

	XQuery createQueryByBuilder(Class<?> clazz);

}

package net.xdefine.db.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONObject;
import net.xdefine.db.annotations.Column;
import net.xdefine.db.impl.XSessionFactoryImpl;
import net.xdefine.db.impl.XSessionImpl;
import net.xdefine.db.vo.DynamicRow;
import net.xdefine.db.vo.PagedList;
import net.xdefine.db.vo.MetaTable;

@SuppressWarnings("rawtypes")
public abstract class XQueryImpl implements XQuery {
	
	private XSessionFactoryImpl sessionFactory;
	private XSessionImpl session;
	private XQuery.Mode mode;
	private Class<?> clazz = null;
	
	protected String args;
	
	protected long start = -1;
	protected long length = -1;
	
	public XQueryImpl(XSessionImpl session) {
		this.session = session;
	}

	public XQueryImpl(XSessionImpl session, XQuery.Mode mode, String args) {
		this.sessionFactory = session.getSessionFactory();
		this.session = session;
		this.mode = mode;
		this.args = args;
	}

	public XQueryImpl(XSessionImpl session, XQuery.Mode mode, String args, Class<?> clazz) {
		this.sessionFactory = session.getSessionFactory();
		this.session = session;
		this.mode = mode;
		this.args = args;
		this.clazz = clazz;
	}

	@Override
	public XQuery setParameter(String name, Collection value) {
		return this.setParameter(name, "(" + StringUtils.join(value, ",") + ")");
	}

	@Override
	public XQuery setParameter(String name, Object value) {
		
		return this;
	}
	
	@Override
	public XQuery setMaxResult(int max) {
		this.length = max;
		return this;
	}

	@Override
	public XQuery setFirstResult(int start) {
		this.start = start;
		return this;
	}
	
	protected String getQuery() {
		String queryString = null;
		if (this.mode == XQuery.Mode.NATIVE_QUERY) {
			queryString = this.args;
		}
		else if (this.mode == XQuery.Mode.BUILDER) {
			
			List<MetaTable> metas = new ArrayList<MetaTable>();
			JSONObject table = this.sessionFactory.entities.getJSONObject(this.args);
			
			int i = 1;
			MetaTable meta = new MetaTable();
			meta.setUnique("_0x" + String.format("%02X%n", i));
			meta.setName(this.args);
			metas.add(meta);
			
			System.out.println(table);
			
			for (Object object : table.getJSONArray("property")) {
				System.out.println(((JSONObject) object));
//				meta.getProperties().add(((JSONObject) object).getJSONObject("column").getString("@name"));
			}
			
			queryString = this.generateQuery(metas);
		}
		
		return queryString;
	}

	protected abstract String generateQuery(List<MetaTable> metadatas);
	protected abstract String appendRowControlQuery(String queryString);

	@Override
	public PagedList list() {
		return this.list(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public PagedList list(Class<?> resultClass) {
		
		if (resultClass == null) resultClass = this.clazz;
		
		String queryString = this.getQuery();
		if (this.start >= 0 || this.length >= 0) {
			queryString = this.appendRowControlQuery(queryString);
		}
		
		System.out.println("XQUERY :: " + queryString);
		
		try {
			PagedList result = new PagedList();
			ResultSet rs = this.session.createResultset(queryString);
			while(rs.next()) {
				if (this.clazz == null && resultClass == null) {
					result.add(new DynamicRow(rs));
				}
				else {
					Object row = resultClass.newInstance();
					for (Field field : resultClass.getFields()) {
						if (field.getAnnotation(Column.class) == null) continue;
						//field.getAnnotation(DBField.class)
					}
					for (Method method : resultClass.getMethods()) {
						if (method.getAnnotation(Column.class) == null) continue;
						Column dbf = method.getAnnotation(Column.class);
						
						String name = method.getName();
						if (name.startsWith("get") || name.startsWith("set"))	
							name = "set" + name.substring(3);
						else if (name.startsWith("is"))
							name = "set" + name.substring(2);
						
						String paramType = method.getReturnType().toString();
						if (!paramType.contains(".") && paramType.startsWith(paramType.substring(0, 1).toLowerCase())) {
							// int, long, double, float 등.. 
							
							if (paramType.equals("long")) {
								long value = ((Long) rs.getObject(dbf.name())).longValue();
								resultClass.getMethod(name, method.getReturnType()).invoke(row, value);
							}
							else if (paramType.equals("int")) {
								int value = ((Integer) rs.getObject(dbf.name())).intValue();
								resultClass.getMethod(name, method.getReturnType()).invoke(row, value);
							}
							else if (paramType.equals("float")) {
								float value = ((Double) rs.getObject(dbf.name())).floatValue();
								resultClass.getMethod(name, method.getReturnType()).invoke(row, value);
							}
							else if (paramType.equals("double")) {
								double value = ((Double) rs.getObject(dbf.name())).doubleValue();
								resultClass.getMethod(name, method.getReturnType()).invoke(row, value);
							}
							else if (paramType.equals("boolean")) {
								boolean value = ((Boolean) rs.getObject(dbf.name())).booleanValue();
								resultClass.getMethod(name, method.getReturnType()).invoke(row, value);
							}
							else if (paramType.equals("byte")) {
								byte value = ((Byte) rs.getObject(dbf.name())).byteValue();
								resultClass.getMethod(name, method.getReturnType()).invoke(row, value);
							}
							else if (paramType.equals("short")) {
								short value = ((Short) rs.getObject(dbf.name())).shortValue();
								resultClass.getMethod(name, method.getReturnType()).invoke(row, value);
							}
							else {
								System.out.println(paramType);
								System.out.println(name);
							}
						}
						else {
							Object value = method.getReturnType().cast(rs.getObject(dbf.name()));
							resultClass.getMethod(name, method.getReturnType()).invoke(row, value);
						}
						
					}
					result.add(row);
				}
			}
			rs.close();
			return result;
		}
		catch(Exception ex) {
			throw new IllegalStateException(ex);
		}

	}

	@Override
	public Object uniqueResult() {
		return this.uniqueResult(null);
	}

	@Override
	public Object uniqueResult(Class<?> clazz) {
		PagedList objects = this.list(clazz);
		if (objects.size() > 1) {
			throw new IllegalStateException( "This is not unique result.");
		}
		else if (objects.size() <= 0) {
			return null;
		}
		else {
			return objects.get(0);
		}
	}

	@Override
	public int executeUpdate() {

		String queryString = this.getQuery();
		if (this.start > 0 && this.length > 0) {
			queryString = this.appendRowControlQuery(queryString);
		}
		
		try {
			int result = this.session.execute(queryString);
			return result;
		}
		catch(Exception ex) {
			throw new IllegalStateException(ex);
		}

	}
	
	

}

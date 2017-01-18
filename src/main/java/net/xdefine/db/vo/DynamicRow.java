package net.xdefine.db.vo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONObject;
import net.xdefine.db.annotations.Column;

@SuppressWarnings("serial")
public class DynamicRow extends HashMap<String, Object> {
	
	private ResultSet rs;
	
	public DynamicRow() {
		
	}
	
	public DynamicRow(ResultSet rs) {
		this.rs = rs;
		try {
			
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				String label = rs.getMetaData().getColumnLabel(i);
				this.put(label, rs.getObject(i));
			}
			
		}
		catch(Exception ex) {
			ex.printStackTrace();
			
		}
	}

	public DynamicRow(ResultSet rs, List<MetaTable> metaTables) {
		this.rs = rs;
		MetaTable def = metaTables.get(0);
		
		try {
			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				String label = rs.getMetaData().getColumnLabel(i);
				if (label.startsWith(def.getUnique())) {
					label = label.substring(def.getUnique().length() + 1);
					this.put(label, rs.getObject(i));
				}
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			
		}
		
		
	}

	public Object assignObject(Class<?> resultClass) throws Exception {
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
		
		return row;
		
	}
	
	public String getString(String key) {
		return (this.get(key) == null ? null : this.get(key).toString());
	}

	public JSONObject getJSONObject(String key) {
		return (this.get(key) == null ? null : JSONObject.fromObject(this.get(key).toString()));
	}

	public long getLong(String key) {
		return (this.get(key) == null ? -1L : (Long) this.get(key));
	}

}

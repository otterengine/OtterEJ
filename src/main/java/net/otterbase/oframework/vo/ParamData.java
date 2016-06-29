package net.otterbase.oframework.vo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.springframework.beans.SimpleTypeConverter;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ParamData {
	
	private static Map data = null;
	public static void sync(ServletRequest request) {
		data = request.getParameterMap();
	}
	
	public static ParamData newInstance() {
		return new ParamData(data);
	}
	
	

	private Map params = null;
	private ParamData(Map params) {
		this.params = new HashMap(params);
	}
	
	public Object get(String key) {
		return String.join(",", (String[]) params.get(key));
	}
	
	public void cleanForEntity(Class<?> clazz) {
		
		List<String> mfields = new ArrayList<String>();
		List<Object> rfields = new ArrayList<Object>();
		
		for (Field field : clazz.getDeclaredFields()) {
			mfields.add(field.getName());
		}

		for (Object key : params.keySet()) {
			if (!mfields.contains(key.toString())) rfields.add(key);
		}
		
		for (Object key : rfields) params.remove(key);
	}
	
	public void assign(Object object) {

		SimpleTypeConverter converter = new SimpleTypeConverter();
		
		Class<?> clazz = object.getClass();
		for (Method method : clazz.getMethods()) {
			if (method.getParameterCount() != 1 || !method.getName().startsWith("set")) continue;
			
			Class[] types = method.getParameterTypes();
			Object fieldname = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);
			if (params.containsKey(fieldname)) {
				try {
					Object value = converter.convertIfNecessary(params.get(fieldname), types[0]);
					Method method2 = clazz.getMethod(method.getName(), types[0]);
					method2.invoke(object, value);				
				}
				catch(Exception ex) {
				}
			}
		}
	}
	

}

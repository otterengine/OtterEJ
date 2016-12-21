package net.xdefine.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletContextHolder {

	private static Map<String, ServletContextHolder> _instance = new HashMap<String, ServletContextHolder>();
	
	public static void newInstance(int hashCode, HttpServletRequest request, HttpServletResponse response) {
		_instance.put(String.valueOf(hashCode), new ServletContextHolder(request, response));
	}
	
	public static void removeInstance(int hashCode) {
		_instance.remove(String.valueOf(hashCode));
	}
	
	public static ServletContextHolder getInstance() {
		return _instance.get(String.valueOf(Thread.currentThread().hashCode()));
	}

	private HttpServletRequest request;
	private HttpServletResponse response;
	public ServletContextHolder(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}
	
	
	
}

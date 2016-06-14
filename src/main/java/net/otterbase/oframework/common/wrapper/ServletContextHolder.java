package net.otterbase.oframework.common.wrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletContextHolder {

	private static ServletContextHolder _instance;
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	public static ServletContextHolder instance() {
		if (_instance == null) _instance = new ServletContextHolder();
		return _instance;
	}
	
	private ServletContextHolder() {
		
	}
	
	public static HttpServletRequest getRequest() {
		return _instance.request;
	}

	public static HttpServletResponse getResponse() {
		return _instance.response;
	}
	
	public void sync(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}
}

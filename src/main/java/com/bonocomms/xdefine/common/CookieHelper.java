package com.bonocomms.xdefine.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bonocomms.xdefine.common.wrapper.ServletContextHolder;

public class CookieHelper {

	private static CookieHelper _instance;
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	public static CookieHelper instance() {
		if (_instance == null) _instance = new CookieHelper();
		_instance.request = ServletContextHolder.getRequest();
		_instance.response = ServletContextHolder.getResponse();
		return _instance;
	}
	
	public static CookieHelper sync(HttpServletRequest request, HttpServletResponse response) {
		if (_instance == null) _instance = new CookieHelper();
		_instance.request = request;
		_instance.response = response;
		return _instance;
	}
	
	
	private CookieHelper(){
	}

	public String getCookie(String key) {
		
		HttpServletRequest request = ServletContextHolder.getRequest();
		HttpServletResponse response = ServletContextHolder.getResponse();
		
		if (request == null) request = this.request;
		if (response == null) response = this.response;

		try
		{			
			Cookie cookie = null;
			Cookie[] cookies = request.getCookies();
			if(cookies != null)
				for(Cookie item : cookies) {
					if (item == null || item.getName() == null || !item.getName().equals(key)) continue;
					cookie = item;			
				}
	
			if(cookie == null) return null;
			return URLDecoder.decode(cookie.getValue(), "UTF-8");
		}
		catch(Exception ex) {
			return null;
		}
	}
	public void setCookie(String key, String value) throws UnsupportedEncodingException {
		
		HttpServletRequest request = ServletContextHolder.getRequest();
		HttpServletResponse response = ServletContextHolder.getResponse();

		if (request == null) request = this.request;
		if (response == null) response = this.response;
		
		Cookie cookie = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for(Cookie item : cookies) {
				if (!item.getName().equals(key)) continue;
				cookie = item;			
			}
		}

		if(cookie == null)  {
			cookie = new Cookie(key, URLEncoder.encode(value, "UTF-8"));
			cookie.setPath("/");
		}
		else {
			cookie.setValue(URLEncoder.encode(value, "UTF-8"));
			cookie.setPath("/");
		}

		response.addCookie(cookie);
	}
}

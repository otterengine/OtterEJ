package net.otterbase.oframework.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.otterbase.oframework.common.wrapper.ServletContextHolder;

public class CookieHelper {

	private static CookieHelper _instance;
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	public static CookieHelper instance() {
		if (_instance == null) _instance = new CookieHelper();
		return _instance;
	}
	
	private CookieHelper(){
	}

	public String getCookie(String key) {
		
		this.request = ServletContextHolder.getRequest();
		this.response = ServletContextHolder.getResponse();

		try
		{
			HttpServletRequest request = this.request;
			
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
		
		this.request = ServletContextHolder.getRequest();
		this.response = ServletContextHolder.getResponse();
		
		Cookie cookie = null;
		Cookie[] cookies = this.request.getCookies();
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

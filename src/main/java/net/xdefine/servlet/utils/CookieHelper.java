package net.xdefine.servlet.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.xdefine.common.Hasher;

public class CookieHelper {

	private HttpServletRequest request;
	private HttpServletResponse response;
	
	
	public CookieHelper(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}
	
	public String getCookie(String key) {
		
		StringBuilder sb = new StringBuilder();
	
		try {			
		
			Cookie cookie = null;
			Cookie[] cookies = request.getCookies();
			if(cookies != null)
				for(Cookie item : cookies) {
					sb.append(item.getName() + " : " + item.getValue() + "\n");
					if (item == null || item.getName() == null || !item.getName().trim().equals(key.trim())) continue;
					cookie = item;
					break;
				}
	
			if(cookie == null) throw new Exception("not found cookie for " + key);
			
			String result = URLDecoder.decode(cookie.getValue(), "UTF-8");
			if (result.startsWith("{AES}")) {
				result = Hasher.decodeAES128(result.substring(5), "MOETCHAN");
			}
			return result;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	public void setCookie(String key, String value) throws UnsupportedEncodingException {
		
		Cookie cookie = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for(Cookie item : cookies) {
				if (!item.getName().equals(key)) continue;
				cookie = item;			
			}
		}
		
		String newValue = null;
		try {
			newValue = "{AES}" + Hasher.encodeAES128(value, "MOETCHAN");
		}
		catch(Exception ex) {
			newValue = value;
			ex.printStackTrace();
		}

		if(cookie == null)  {
			cookie = new Cookie(key, URLEncoder.encode(newValue, "UTF-8"));
			cookie.setPath("/");
		}
		else {
			cookie.setValue(URLEncoder.encode(newValue, "UTF-8"));
			cookie.setPath("/");
		}

		response.addCookie(cookie);
	}
}

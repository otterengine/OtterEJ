package net.xdefine.servlet.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.xdefine.security.utils.Hasher;

public class CookieHelper {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private static Map<String, Cookie> _cookies = null;
	
	
	public CookieHelper(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		if (_cookies == null) _cookies = new HashMap<String, Cookie>();
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
			
			if (cookie == null && _cookies != null) {
				if (_cookies.containsKey(key)) {
					cookie = _cookies.get(key);
				}
				else {
					throw new Exception("not found cookie for " + key);
				}
			}
	
			String result = URLDecoder.decode(cookie.getValue(), "UTF-8");
			if (result.startsWith("{AES}")) {
				result = Hasher.decodeAES128(result.substring(5), "MOETCHAN");
			}
			return result;
		}
		catch(Exception ex) {
			return null;
		}
	}

	public void setCookie(String key, String value) throws UnsupportedEncodingException {
		this.setCookie(key, value, -1);
	}
	
	public void setCookie(String key, String value, int expiry) throws UnsupportedEncodingException {
		this.setCookie(key, value, expiry, false);
	}

	
	public void setCookie(String key, String value, int expiry, boolean secure) throws UnsupportedEncodingException {
		
		Cookie cookie = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for(Cookie item : cookies) {
				if (!item.getName().equals(key)) continue;
				cookie = item;			
			}
		}
		
		String newValue = value;
		if (value != null && !value.isEmpty()) newValue = URLEncoder.encode(value, "UTF-8");

		if(cookie == null)  {
			cookie = new Cookie(key, newValue);
		}

		cookie.setSecure(secure);
		cookie.setValue(newValue);
		cookie.setPath("/");
		cookie.setMaxAge(expiry);

		response.addCookie(cookie);
		_cookies.put(cookie.getName(), cookie);
	}
}

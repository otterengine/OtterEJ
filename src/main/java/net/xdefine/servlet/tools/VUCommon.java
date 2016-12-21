package net.xdefine.servlet.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.xdefine.servlet.ServletContextHolder;


public class VUCommon {
	
	@Autowired private MessageSource messageSource;
	
	public static long on(Object obj) {
		if (obj != null) {
			String a = obj.toString();
			a = a.replaceAll("[^0-9]", "");
			return Long.parseLong(a);
		}
		else {
			return 0L;
		}
	}

	public boolean isJSONArray(Object object) {
		return (object instanceof JSONArray);
	}
	
	public String arrToStr(List<String> str, Object idxs, int s) {
		try {
			int idx = Integer.parseInt(idxs.toString());
			if (str.size() < idx - s) return null;
			return str.get(idx - s);
		}
		catch(Exception ex) {
			return "";
		}
	}
	
	public String f(String b, Object[] args) {
		if (b == null) return b;
		return String.format(b, args);
	}

	public String getMsg(Object[] ids) {

		ServletContextHolder sch = ServletContextHolder.getInstance();
		HttpServletRequest request = sch.getRequest();

		String al = request.getHeader("Accept-Language");
		String acceptLang = (al == null || al.isEmpty()) ? "" : al.substring(0, 2);

		try {
			
			String langcode;
			Map<String, String> allowLanguage = new HashMap<String, String>();
			allowLanguage.put("zh-CN", "zh-CN");
			allowLanguage.put("zh-TW", "zh-TW");
			allowLanguage.put("ko-KR", "ko-KR");
			allowLanguage.put("cn", "zh-CN");
			allowLanguage.put("tw", "zh-TW");
			allowLanguage.put("ko", "ko-KR");
			
			try {
				langcode = acceptLang.substring(0, 2);
				if (allowLanguage.containsKey(langcode)) langcode = allowLanguage.get(langcode);
				if (allowLanguage.containsKey(acceptLang)) langcode = allowLanguage.get(acceptLang);
				
				if (!allowLanguage.containsKey(langcode)) throw new Exception();
			} 
			catch (Exception ex) {
				langcode = "zh-CN";
			}

			String id = org.apache.commons.lang.StringUtils.join(ids, ".");
			return messageSource.getMessage(id, new String[] {}, "", Locale.forLanguageTag(langcode));
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return "";
		}
		
	}
	
	public String num(long num, int digit) {
		return String.format("%0" + digit + "d", num);
	}
	
	public boolean isEmpty(String str) {
		return str == null || str.isEmpty();
	}
	
	public String phoneReorder(String str) {
		String str1 = str.replaceAll("[^0-9]", "");
		if (str1.length() > 10) {
			return str1.substring(0, 3) + "-" + str1.substring(3, 7) + "-" + str1.substring(7);
		}
		else if (str1.length() > 9) {
			return str1.substring(0, 3) + "-" + str1.substring(3, 6) + "-" + str1.substring(6);
		}
		else {
			return str1;
		}
	}
	
	public JSONObject toJSONObject(String str) {
		if(str != null) if(str.trim().equals("")) return null;
		try {
			return JSONObject.fromObject(str);
		}
		catch(Exception ex) {
			return null;
		}
	}
	
	public JSONArray toJSONArray(String str) {
		if(str != null) if(str.trim().equals("")) return null;
		try {
			return JSONArray.fromObject(str);
		}
		catch(Exception ex) {
			return null;
		}
	}

	public String replaceCrLf(String str) {
		if (str == null) return "";
		return str.replaceAll("\\n", "<br />");
	}

	public String cutHTMLString(String str, Integer len) {
		return cutString(str.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>", "").replaceAll("&nbsp;", "").trim(), len);
	}
	
	public static String getParams(List<String> arr, String prefix) {
		
		HttpServletRequest request = ServletContextHolder.getInstance().getRequest();
		if (request.getQueryString() == null) return "";
		
		StringBuilder params = new StringBuilder();
		String[] queries = request.getQueryString().split("&");
		for (String query : queries) {
			if (query.length() > 0 && arr != null && arr.size() > 0 && arr.contains(query.substring(0, query.indexOf("=")))) continue;
			params.append("&" + query);
		}
		
		if (params.toString() == null || params.toString().trim().isEmpty()) 
			return "";
		else {
			String result = params.toString().substring(1);
			return prefix + result;
		}
	}
	
	public String cutString(String str, Integer len) {
		
		if (str == null || str.isEmpty()) return str;
		
		String r_val = str.replace("\u00a0", " ");
		int nLength = len;
		int oL = 0, rF = 0, rL = 0;
		
		try {
			
			byte[] bytes = r_val.getBytes("UTF-8"); // 바이트로 보관
			// x부터 y길이만큼 잘라낸다. 한글안깨지게.
			int j = rF;
			
			while (j < bytes.length) {
				if ((bytes[j] & 0x80) != 0) {
					if (oL + 2 > nLength) break;
					
					oL += 2;
					rL += 3;
					j += 3;
				}
				else {
					if (oL + 1 > nLength) break;
					++oL;
					++rL;
					++j;
				}
			}
			
			r_val = new String(bytes, rF, rL, "UTF-8"); // charset 옵션
			return r_val + (str.equals(r_val) ? "" : "..");
			
		}
		catch (Exception e) { }
		return r_val;
	}
	
	public String urlDecode(String str) {
		if (str == null) return "";
		try {
			return URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}
}

package com.bonocomms.xdefine.views.helper;

import java.util.List;

import com.bonocomms.xdefine.common.wrapper.ServletContextHolder;

public class VUHtmlTag {
	
	public String selected(Object data1, Object data2) {
		return this.htmlattr(data1, data2, "selected=\"selected\"", false);
	}

	public String checked(Object data1, Object data2) {
		return this.htmlattr(data1, data2, "checked=\"checked\"", false);
	}

	public String selected(Object data1, Object data2, boolean def) {
		return this.htmlattr(data1, data2, "selected=\"selected\"", def);
	}

	public String checked(Object data1, Object data2, boolean def) {
		return this.htmlattr(data1, data2, "checked=\"checked\"", def);
	}
	

	public String htmlattr(Object data1, Object data2, String attr, boolean def) {
		if (data1 == null || data2 == null) return "";
		
		String str1 = data1.toString();
		String str2 = data2.toString();
		
		if (str1.isEmpty() || str2.isEmpty()) return "";
		
		boolean isnull = false;
		boolean contains = (str2.subSequence(0, 1).equals("%"));
		if (contains) str2 = str2.substring(1);
		
		if (str1.subSequence(0, 1).equals("$")) {
			str1 = this.param(str1.substring(1));
			if (str1 == null) {
				str1 = data1.toString();
				isnull = true;
			}
		}
		
		if ( ( contains ? str1.contains(str2) : str1.equals(str2) ) || (isnull && def) ) {
			return attr;
		}
		else {
			return "";
		}
	}
	
	public String val(List<String> vals, String v) {
		return vals.get(Integer.parseInt(v));
	}

	public String param(String key) {
		if (ServletContextHolder.getRequest().getParameterValues(key) == null) return null;
		return org.apache.commons.lang.StringUtils.join(ServletContextHolder.getRequest().getParameterValues(key), ",");
	}

}

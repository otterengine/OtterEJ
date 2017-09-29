package net.xdefine.servlet.wrapper;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class XRequestWrapper extends HttpServletRequestWrapper {

	private String header;

	public XRequestWrapper(HttpServletRequest request, String header) {
		super(request);
		this.header = header;
	}

	@Override
	public String getProtocol() {
		return super.getProtocol();
	}

	@Override
	public String getScheme() {
		String realProto = super.getHeader("X-Forwarded-Proto");
		return realProto != null ? realProto : super.getScheme();
	}

	@Override
	public String getRemoteAddr() {
		String realIP = super.getHeader(this.header);
		return realIP != null ? realIP : super.getRemoteAddr();
	}

	@Override
	public String getRemoteHost() {
		try {
			return InetAddress.getByName(getRemoteAddr()).getHostName();
		} catch (UnknownHostException e) {
			return getRemoteAddr();
		}
	}

	@Override
	public String getParameter(String name) {
		String v = super.getParameter(name);
		return (v != null ? v.replaceAll("<(no)?script[^>]*>.*?</(no)?script>", "") : null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map getParameterMap() {
		Map m = super.getParameterMap();
		if (m == null) return null;

		Map map = new HashMap(m);
		for (Object o : map.keySet()) {
			map.put(o, map.get(o).toString().replaceAll("<(no)?script[^>]*>.*?</(no)?script>", ""));
		}
		return map;
	}

	@Override
	public String[] getParameterValues(String name) {
		
		String[] v = super.getParameterValues(name);
		if (v == null) return null;
		
		List<String> v1 = Arrays.asList(v);
		for (int i = 0; i < v1.size(); i++) {
			v1.set(i, v1.get(i).replaceAll("<(no)?script[^>]*>.*?</(no)?script>", ""));
		}
		return v1.toArray(new String[v1.size()]);
	}
	
	
	
}

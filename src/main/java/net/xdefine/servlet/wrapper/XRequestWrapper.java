package net.xdefine.servlet.wrapper;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
		// TODO Auto-generated method stub
		return super.getProtocol();
	}



	@Override
	public String getScheme() {
		// TODO Auto-generated method stub
		return super.getScheme();
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
}

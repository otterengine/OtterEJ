package net.otterbase.oframework.common.wrapper;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class RealIPFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		

		try {
			if (request instanceof HttpServletRequest) {
				chain.doFilter(new RealIPRequestWrapper((HttpServletRequest) request), response);
			} 
			else {
				chain.doFilter(request, response);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
//			chain.doFilter(request, response);
		}
	}

	public void destroy() {
		
	}

	public void init(FilterConfig arg0) throws ServletException {

	}
}
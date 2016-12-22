package net.xdefine;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.CharacterEncodingFilter;

import net.xdefine.servlet.wrapper.XRequestWrapper;

public class XBaseFilter extends CharacterEncodingFilter implements Filter {

	private String headerName;

	public String getHeaderName() {
		return headerName;
	}

	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		HttpServletRequest wrapper = request;
		if (request instanceof HttpServletRequest) wrapper = new XRequestWrapper(request, headerName);
		super.doFilterInternal(wrapper, response, filterChain);
	}

}

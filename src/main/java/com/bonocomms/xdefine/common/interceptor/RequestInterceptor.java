package com.bonocomms.xdefine.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.bonocomms.xdefine.common.UAgentInfo;
import com.bonocomms.xdefine.common.wrapper.ServletContextHolder;
import com.bonocomms.xdefine.vo.ParamData;

public class RequestInterceptor extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		ServletContextHolder.instance().sync(request, response);
		ParamData.sync(request);
		
		
//		request.setAttribute("http_url", pm.get("webapp.site_web_url"));
//		request.setAttribute("https_url", pm.get("webapp.site_ssl_url"));
		
		UAgentInfo uaInfo = new UAgentInfo(request.getHeader("user-agent"), null);
		
		request.setAttribute("_xs", uaInfo.isMobilePhone);
		request.setAttribute("_sm", uaInfo.isMobilePhone || uaInfo.isTierTablet);
		request.setAttribute("_cpx", request.getContextPath());
				
		request.setCharacterEncoding("UTF-8");
		return super.preHandle(request, response, handler);
		
	}
}

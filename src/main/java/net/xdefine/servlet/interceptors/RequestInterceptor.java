package net.xdefine.servlet.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import net.xdefine.servlet.ServletContextHolder;
import net.xdefine.servlet.utils.UAgentInfo;

public class RequestInterceptor extends HandlerInterceptorAdapter {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		ServletContextHolder.newInstance(Thread.currentThread().hashCode(), request, response);
		
		UAgentInfo uaInfo = new UAgentInfo(request.getHeader("user-agent"), null);
		
		request.setAttribute("_xs", uaInfo.isMobilePhone);
		request.setAttribute("_sm", uaInfo.isMobilePhone || uaInfo.isTierTablet);
		request.setAttribute("_cpx", request.getContextPath());
				
		request.setCharacterEncoding("UTF-8");
		return super.preHandle(request, response, handler);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		
		super.afterCompletion(request, response, handler, ex);
		ServletContextHolder.removeInstance(Thread.currentThread().hashCode());
	}
	
	
}

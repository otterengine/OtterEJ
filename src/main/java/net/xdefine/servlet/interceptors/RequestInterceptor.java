package net.xdefine.servlet.interceptors;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import net.xdefine.XFContext;
import net.xdefine.security.XFSecurity;
import net.xdefine.security.core.Authentication;
import net.xdefine.security.utils.VUSecurity;
import net.xdefine.servlet.ServletContextHolder;
import net.xdefine.servlet.utils.CookieHelper;
import net.xdefine.servlet.utils.UAgentInfo;

public class RequestInterceptor extends HandlerInterceptorAdapter {
	
	private XFSecurity security;
	public void setSecurity(XFSecurity security) {
		this.security = security;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		ServletContextHolder.newInstance(Thread.currentThread().hashCode(), request, response);

		if (security != null && ServletContextHolder.getInstance() != null) {
			String url = request.getRequestURI();
			url = url.substring(request.getContextPath().length());

			String pfx = XFContext.getProperty("webapp.security.prefix");
			if (pfx == null) pfx = "";			

			CookieHelper cookie = ServletContextHolder.getInstance().getCookieHelper();
			String autoText = cookie.getCookie(pfx + "_xdsec_remember");
			
			if (!VUSecurity.isSigned() && autoText != null && !autoText.isEmpty()) {
				Authentication authentication = security.authenticate(autoText);
				if (authentication == null) {
					cookie.setCookie(pfx + "_xdsec_remember", "", -1);
				}
				else {
					String sess = cookie.getCookie(pfx + "_xdsec_details");
					cookie.setCookie(pfx + "_xdsec_details", sess, 60 * 30);
				}
			}

			Map<String, String[]> maps = security.getSecurityPath();
			for (String path : maps.keySet()) {
				Matcher m = Pattern.compile(path).matcher(url);
				if (m.matches() && !VUSecurity.anyGranted(maps.get(path))) {
					
					String qs = request.getQueryString();
					if (qs != null && !qs.isEmpty()) 
						qs = "?" + qs;
					else 
						qs = "";
					
					request.getSession().setAttribute("_securl", url + qs);
					response.setStatus(302);
					response.setHeader("Location", request.getContextPath() + XFContext.getProperty("webapp.security.login_page"));
					ServletContextHolder.removeInstance(Thread.currentThread().hashCode());
					return false;
				}
			}
			
		}
		
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

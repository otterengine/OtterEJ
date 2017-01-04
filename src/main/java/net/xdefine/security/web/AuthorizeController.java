package net.xdefine.security.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import net.sf.json.JSONObject;
import net.xdefine.XFContext;
import net.xdefine.security.XFSecurity;
import net.xdefine.security.core.Authentication;
import net.xdefine.security.exceptions.AuthenticationException;
import net.xdefine.security.utils.Hasher;
import net.xdefine.servlet.ServletContextHolder;
import net.xdefine.servlet.utils.CookieHelper;

@Controller
@RequestMapping("/authorize")
public class AuthorizeController {

	@Autowired private XFSecurity security;

	@RequestMapping("/create")
	public void create(HttpServletRequest request, HttpServletResponse response) {
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String redirect_url = request.getParameter("redirect_url");
		boolean isRemember = request.getParameter("remember") != null && request.getParameter("remember").equals("true");

		String url = request.getContextPath() + XFContext.getProperty("webapp.security.login_page");

		try {
			Authentication authentication = security.authenticate(new Authentication(username, password));
			
			if (authentication == null) {
				response.setStatus(302);
				response.setHeader("Location", url + (url.contains("?") ? "&" : "?") + "error=username");
			}
			else if (authentication.getCredentials().equals(security.encode(password))) {
				response.setStatus(302);
				response.setHeader("Location", url + (url.contains("?") ? "&" : "?") + "error=password");
			}
			else {
				String sessionid = request.getSession().getId();
				CookieHelper cookie = new CookieHelper(request, response);
				
				
				String pfx = XFContext.getProperty("webapp.security.prefix");
				if (pfx == null) pfx = "";
				
				String sess = authentication.getCookieString(sessionid);
				cookie.setCookie(pfx + "_xdsec_details", sess, 60 * 30);
				
				if (isRemember) {
					cookie.setCookie(pfx + "_xdsec_remember", authentication.getRememberData(), 60 * 60 * 24 * 365);
				}
				
				security.onAuthenticationSuccess(request, response, JSONObject.fromObject(Hasher.decodeAES128(sess, sessionid)));
				
				String _surl = (String) request.getSession().getAttribute("_securl");
				if (_surl != null && _surl.endsWith("authorize/destroy")) _surl = null;
				if (redirect_url != null && !redirect_url.isEmpty()) {
					response.setStatus(302);
					response.setHeader("Location", request.getContextPath() + redirect_url);
				}
				else if (_surl != null && !_surl.isEmpty()) {
					response.setStatus(302);
					response.setHeader("Location", request.getContextPath() + _surl);
				}
				else { 
					response.setStatus(302);
					response.setHeader("Location", request.getContextPath() + "/");
				}
			}
			
		}
		catch(AuthenticationException ex) {
			security.onAuthenticationFailure(request, response, ex);
			response.setStatus(302);
			response.setHeader("Location", url + (url.contains("?") ? "&" : "?") + "error=" + ex.getQuery());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			response.setStatus(302);
			response.setHeader("Location", url + (url.contains("?") ? "&" : "?") + "error=" + ex.getMessage());
		}
		
	}
	
	@RequestMapping("/destroy")
	public void destroy(HttpServletRequest request, HttpServletResponse response) {

		try {
			security.onAuthenticationDestroy(request, response, ServletContextHolder.getInstance().getSecurityJSON());

			String pfx = XFContext.getProperty("webapp.security.prefix");
			if (pfx == null) pfx = "";
			
			CookieHelper cookie = new CookieHelper(request, response);
			cookie.setCookie(pfx + "_xdsec_details", "", -1);
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		response.setStatus(302);
		response.setHeader("Location", request.getContextPath() + "/");

	}
	
}

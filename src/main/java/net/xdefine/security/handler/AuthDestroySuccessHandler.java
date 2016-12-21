package net.xdefine.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import net.xdefine.security.XFSecurity;

public class AuthDestroySuccessHandler implements LogoutSuccessHandler, LogoutHandler {

	private XFSecurity security;

	public XFSecurity getSecurity() {
		return security;
	}

	public void setSecurity(XFSecurity security) {
		this.security = security;
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		if (authentication != null && authentication.getDetails() != null) {
            try {
            	if (this.security != null) this.security.onAuthenticationDestroy(request, response, authentication);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		
		response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		response.setHeader("Location", request.getContextPath() + "/");
	}

}

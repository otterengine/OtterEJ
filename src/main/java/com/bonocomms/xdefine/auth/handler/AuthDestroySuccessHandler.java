package com.bonocomms.xdefine.auth.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.bonocomms.xdefine.base.XFSecurity;

public class AuthDestroySuccessHandler implements LogoutSuccessHandler {

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
            	request.getSession().invalidate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
 
	}

}

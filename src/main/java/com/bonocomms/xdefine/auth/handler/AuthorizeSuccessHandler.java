package com.bonocomms.xdefine.auth.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.bonocomms.xdefine.base.OFSecurity;

public class AuthorizeSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler
		implements AuthenticationSuccessHandler {

	private OFSecurity security;

	public OFSecurity getSecurity() {
		return security;
	}

	public void setSecurity(OFSecurity security) {
		this.security = security;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		if (security != null) {
			security.onAuthenticationSuccess(request, response, authentication);
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}

}

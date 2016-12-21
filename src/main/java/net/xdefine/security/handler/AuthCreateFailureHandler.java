package net.xdefine.security.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import net.xdefine.security.XFSecurity;

public class AuthCreateFailureHandler extends SimpleUrlAuthenticationFailureHandler
		implements AuthenticationFailureHandler {

	private XFSecurity security;

	public XFSecurity getSecurity() {
		return security;
	}

	public void setSecurity(XFSecurity security) {
		this.security = security;
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {

		if (security != null) {
			security.onAuthenticationFailure(request, response, exception);
		}
		super.onAuthenticationFailure(request, response, exception);
	}

}

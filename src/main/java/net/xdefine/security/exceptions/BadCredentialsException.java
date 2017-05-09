package net.xdefine.security.exceptions;

@SuppressWarnings("serial")
public class BadCredentialsException extends AuthenticationException {

	public BadCredentialsException(String string) {
		super(string);
		this.query = "password";
	}

}

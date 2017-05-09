package net.xdefine.security.exceptions;

@SuppressWarnings("serial")
public class UserNotActivateException extends AuthenticationException {

	public UserNotActivateException(String string) {
		super(string);
	}

}

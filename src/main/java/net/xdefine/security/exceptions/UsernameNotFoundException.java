package net.xdefine.security.exceptions;

@SuppressWarnings("serial")
public class UsernameNotFoundException extends AuthenticationException {

	public UsernameNotFoundException(String string) {
		super(string);
		this.query = "username";
	}

}

package net.xdefine.security.exceptions;

@SuppressWarnings("serial")
public class AuthenticationException extends IllegalStateException {
	
	protected String query;

	public AuthenticationException() {
		super();
	}

	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException(Throwable cause) {
		super(cause);
	}

	public String getQuery() {
		return this.query;
	}
}

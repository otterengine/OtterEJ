package net.xdefine.security.authentication;

import java.util.Collection;

import net.xdefine.security.core.Authentication;
import net.xdefine.security.core.GrantedAuthority;
import net.xdefine.security.core.userdetails.SignedDetails;

public class UsernamePasswordAuthenticationToken extends Authentication {

	public UsernamePasswordAuthenticationToken(SignedDetails principal, String password, Collection<? extends GrantedAuthority> authorities) {
		super(principal.getUsername(), password);
		this.principal = principal;
		this.authorities = authorities;
	}

}

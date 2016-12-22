package net.xdefine.security;

import java.util.Collection;

import net.xdefine.security.userdetails.Authentication;
import net.xdefine.security.userdetails.GrantedAuthority;
import net.xdefine.security.userdetails.SignedDetails;

public class UsernamePasswordAuthenticationToken extends Authentication {

	public UsernamePasswordAuthenticationToken(SignedDetails principal, String password, Collection<? extends GrantedAuthority> authorities) {
		super(principal.getUsername(), password);
		this.principal = principal;
		this.authorities = authorities;
	}

}

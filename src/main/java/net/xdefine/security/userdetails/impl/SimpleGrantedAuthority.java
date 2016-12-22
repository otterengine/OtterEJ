package net.xdefine.security.userdetails.impl;

import net.xdefine.security.userdetails.GrantedAuthority;

public class SimpleGrantedAuthority implements GrantedAuthority {

	protected String role;
	
	public SimpleGrantedAuthority(String role) {
		super();
		this.role = role;
	}

	@Override
	public String getAuthority() {
		return this.role;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return role;
	}
	
}

package com.bonocomms.xdefine.auth.vo;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import net.sf.json.JSONObject;


@SuppressWarnings("serial")
public class SignedDetails extends User implements UserDetails {
	
	private JSONObject userData;

	public SignedDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
		this.userData = new JSONObject();
	}

	public SignedDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.userData = new JSONObject();
	}
	
	public SignedDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, JSONObject object) {
		super(username, password, authorities);
		this.userData = object;
	}

	public SignedDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, JSONObject object) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.userData = object;
	}
	

	public Object get(String key) {
		return userData.get(key);
	}
	
	public long getLong(String key) {
		return userData.getLong(key);
	}

	public String getString(String key) {
		return userData.getString(key);
	}
	
	
}
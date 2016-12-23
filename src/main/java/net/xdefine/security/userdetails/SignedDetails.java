package net.xdefine.security.userdetails;

import java.io.Serializable;
import java.util.Collection;

import net.sf.json.JSONObject;
import net.xdefine.security.core.GrantedAuthority;

@SuppressWarnings("serial")
public class SignedDetails implements Serializable {

	private String username;
	private String password;
	private Collection<? extends GrantedAuthority> authorities;
	private JSONObject userData;

	public SignedDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
			JSONObject object) {
		this.username = username;
		this.password = password;
		this.authorities = authorities;
		this.userData = object;
	}

	public SignedDetails(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities,
			JSONObject object) {
		this.username = username;
		this.password = password;
		this.authorities = authorities;
		this.userData = object;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
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

	public JSONObject getUserData() {
		return this.userData;
	}


}
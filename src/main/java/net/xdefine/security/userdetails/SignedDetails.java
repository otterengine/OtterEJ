package net.xdefine.security.userdetails;

import java.io.Serializable;
import java.util.Collection;

import net.sf.json.JSONObject;

@SuppressWarnings("serial")
public class SignedDetails implements Serializable {

	private JSONObject userData;

	public SignedDetails(String username, String password, Collection<? extends GrantedAuthority> authorities,
			JSONObject object) {
		this.userData = object;
	}

	public SignedDetails(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities,
			JSONObject object) {
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

	public JSONObject getUserData() {
		return this.userData;
	}

}
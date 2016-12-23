package net.xdefine.security.authority;

import net.sf.json.JSONObject;
import net.xdefine.security.GrantedAuthority;

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

	@Override
	public JSONObject getJSON() {
		JSONObject result = new JSONObject();
		result.put("role", this.role);
		return result;
	}
	
}

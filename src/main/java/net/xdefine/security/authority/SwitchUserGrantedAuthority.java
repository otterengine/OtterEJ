package net.xdefine.security.authority;

import net.sf.json.JSONObject;
import net.xdefine.security.core.Authentication;
import net.xdefine.security.core.GrantedAuthority;

public class SwitchUserGrantedAuthority implements GrantedAuthority {

	private String role;
	private Authentication source;

	public SwitchUserGrantedAuthority(String role, Authentication source) {
		super();
		this.role = role;
		this.source = source;
	}

	@Override
	public String getAuthority() {
		return role;
	}

	public Authentication getSource() {
		return source;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
	
	@Override
	public JSONObject getJSON() {
		JSONObject result = new JSONObject();
		result.put("role", this.role);
		return result;
	}

}

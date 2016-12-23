package net.xdefine.security;

import net.sf.json.JSONObject;

public interface GrantedAuthority {
	String getAuthority();
	JSONObject getJSON();
}

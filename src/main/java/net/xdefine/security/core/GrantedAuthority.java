package net.xdefine.security.core;

import net.sf.json.JSONObject;

public interface GrantedAuthority {
	String getAuthority();
	JSONObject getJSON();
}

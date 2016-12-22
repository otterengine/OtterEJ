package net.xdefine.security.userdetails;

import net.sf.json.JSONObject;

public interface GrantedAuthority {
	String getAuthority();
	JSONObject getJSON();
}

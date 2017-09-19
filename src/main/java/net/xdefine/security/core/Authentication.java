package net.xdefine.security.core;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.xdefine.XFContext;
import net.xdefine.security.core.userdetails.SignedDetails;
import net.xdefine.security.utils.Hasher;

public class Authentication {

	protected String name;
	protected String credentials;
	
	protected SignedDetails principal;
	protected Collection<? extends GrantedAuthority> authorities;

	public Authentication(String name, String credentials) {
		super();
		this.name = name;
		this.credentials = credentials;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCredentials() {
		return credentials;
	}

	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	public String getCookieString(HttpServletRequest request, String encryptKey) {
		
		JSONArray authoritiesData = new JSONArray();
		for (GrantedAuthority authority : authorities) {
			authoritiesData.add(authority.getJSON());
		}

		JSONObject object = new JSONObject();
		object.put("principal", name);
		object.put("authorities", authoritiesData);
		object.put("userdata", principal.getUserData());
		object.put("bip", "");
		object.put("uip", request.getRemoteAddr());
		
		try {
			return Hasher.encodeAES128(object.toString(), encryptKey);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getRememberData() {
		try {
			JSONObject object = new JSONObject();
			object.put("name", name);
			object.put("credentials", credentials);
			return name + "{AES}" + Hasher.encodeAES128(object.toString(), name + XFContext.getProperty("security.key"));
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}

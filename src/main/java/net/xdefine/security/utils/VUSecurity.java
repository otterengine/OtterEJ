package net.xdefine.security.utils;

import java.util.HashSet;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.xdefine.servlet.ServletContextHolder;

public class VUSecurity {

	public static String getPrincipal() {
		JSONObject object = ServletContextHolder.getInstance().getSecurityJSON();
		return (object != null) ? object.getString("principal") : null;
	}
	
	public static boolean isSigned() {
		JSONObject object = ServletContextHolder.getInstance().getSecurityJSON();
		return (object != null && object.has("principal")); 
	}

	public static long getLong(String key) {
		try {
			JSONObject object = ServletContextHolder.getInstance().getSecurityJSON();
			return object.getJSONObject("userdata").getLong(key);
		}
		catch(Exception ex) {
			return -1;
		}
	}
	
	public static String getString(String key) {
		try {
			JSONObject object = ServletContextHolder.getInstance().getSecurityJSON();
			return object.getJSONObject("userdata").getString(key);
		}
		catch(Exception ex) {
			return null;
		}
	}
	
	
	public static boolean allGranted(String[] checkForAuths) {
		Set<String> userAuths = getUserAuthorities();
		for (String auth : checkForAuths) {
			if (userAuths.contains(auth))
				continue;
			return false;
		}
		return true;
	}

	public static boolean anyGranted(String[] checkForAuths) {
		Set<String> userAuths = getUserAuthorities();
		for (String auth : checkForAuths) {
			if (userAuths.contains(auth))
				return true;
		}
		return false;
	}

	public static boolean noneGranted(String[] checkForAuths) {
		Set<String> userAuths = getUserAuthorities();
		for (String auth : checkForAuths) {
			if (userAuths.contains(auth))
				return false;
		}
		return true;
	}

	private static Set<String> getUserAuthorities() {

		Set<String> roles = new HashSet<String>();
		try {
			JSONObject object = ServletContextHolder.getInstance().getSecurityJSON();
			JSONArray authorities = object.getJSONArray("authorities");
			for (int i = 0; i < authorities.size(); i++) {
				roles.add(authorities.getJSONObject(i).getString("role"));
			}
		}
		catch(Exception ex) {
		}
		
		return roles;
	}

}
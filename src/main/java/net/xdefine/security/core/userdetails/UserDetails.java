package net.xdefine.security.core.userdetails;

public interface UserDetails {
	
	String getUsername();
	String getPassword();
	
	boolean isAccountNonExpired();
	boolean isAccountNonLocked();
	boolean isCredentialsNonExpired();
	boolean isEnabled();

}

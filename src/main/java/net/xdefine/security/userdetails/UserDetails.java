package net.xdefine.security.userdetails;

public interface UserDetails {
	
	String getUsername();
	String getPassword();
	
	boolean isAccountNonExpired();
	boolean isAccountNonLocked();
	boolean isCredentialsNonExpired();
	boolean isEnabled();

}

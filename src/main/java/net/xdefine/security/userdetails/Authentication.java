package net.xdefine.security.userdetails;

import java.util.Collection;

public interface Authentication {

	Collection<? extends GrantedAuthority> getAuthorities();
}

package net.otterbase.oframework.base;

import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public abstract class OFSecurity implements PasswordEncoder, UserDetailsService {

	protected SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public abstract Map<String, String> getSecurityPath();

}

package net.xdefine.security;

import java.util.Map;

import org.hibernate.SessionFactory;

import net.xdefine.security.exceptions.AuthenticationException;
import net.xdefine.security.userdetails.Authentication;
import net.xdefine.security.userdetails.SignedDetails;

public abstract class XFSecurity {

	protected SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public abstract Map<String, String[]> getSecurityPath();
	public abstract SignedDetails attemptLogin(String username, String password);

	public void onAuthenticationSuccess(Authentication authentication) {
		
	}
	
	public void onAuthenticationFailure(AuthenticationException exception) {
		
	}
	
	public void onAuthenticationDestroy(Authentication authentication) { 
		
	}

	/*
	
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        
        SignedDetails principal = (SignedDetails) this.loadUserByUsername(username);
       	if (principal != null) {
       		// ID 가져온 뒤 처리 방식.
       		if (!this.matches(password, principal.getPassword())) {
       			System.out.println("not matched password.");
       			throw new BadCredentialsException("Not matched Password.");
       		}
       	}
       	else {
       		// 외부 연동시 Password를 못가져오는 경우가 종종 있음.
       		// 해당 시 직접 가져오도록
       		principal = this.attemptLogin(username, password);
       	}
       	
       	if (principal == null) throw new BadCredentialsException("Not matched Password."); 
       	
		Authentication auth = new UsernamePasswordAuthenticationToken(principal, password, principal.getAuthorities());
		return auth;
    }
    */
    
}

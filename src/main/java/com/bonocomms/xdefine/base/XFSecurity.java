package com.bonocomms.xdefine.base;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SessionFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bonocomms.xdefine.auth.vo.SignedDetails;

public abstract class XFSecurity implements PasswordEncoder, UserDetailsService, AuthenticationProvider {

	protected SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public abstract Map<String, String[]> getSecurityPath();
	public abstract SignedDetails attemptLogin(String username, String password);
	
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) { }
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) { }
	public void onAuthenticationDestroy(HttpServletRequest request, HttpServletResponse response, Authentication authentication) { }
	

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return this.attemptLogin(username, null);
	}

    @Override
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
    
    
}

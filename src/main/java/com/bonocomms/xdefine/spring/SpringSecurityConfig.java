package com.bonocomms.xdefine.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;

import com.bonocomms.xdefine.XFContext;
import com.bonocomms.xdefine.auth.handler.AuthCreateFailureHandler;
import com.bonocomms.xdefine.auth.handler.AuthCreateSuccessHandler;
import com.bonocomms.xdefine.auth.handler.AuthDestroySuccessHandler;
import com.bonocomms.xdefine.base.XFSecurity;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter implements ApplicationContextAware {
	
	@Autowired private SessionFactory sessionFactory;
	
	private ApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		super.setApplicationContext(applicationContext);
		this.context = applicationContext;
	}
	

    @Bean
    protected XFSecurity getOSecurity() {
    	
    	Reflections reflections = new Reflections(XFContext.getProperty("webapp.package"));
		Set<Class<? extends XFSecurity>> subTypes = reflections.getSubTypesOf(XFSecurity.class);

		XFSecurity result = null;
		for (Class<?> subType : subTypes) {
			try {
				result = (XFSecurity) subType.newInstance();
				result.setSessionFactory(sessionFactory);
				break;
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
		
    	return result;
    }
    
    @Autowired
    @Bean
    protected TokenBasedRememberMeServices rememberMeServices(XFSecurity oSecurity) {
    	if (oSecurity == null) return null;
    	TokenBasedRememberMeServices rememberMeServices = new TokenBasedRememberMeServices("xdefine_remembers", oSecurity);
    	rememberMeServices.setAlwaysRemember(false);
    	rememberMeServices.setParameter("remember_me");
    	rememberMeServices.setTokenValiditySeconds(900);
    	rememberMeServices.setCookieName("tt");
    	return rememberMeServices;
    }
    
    @Autowired
    @Bean
    protected ProviderManager authenticationManager(XFSecurity oSecurity) {
    	List<AuthenticationProvider> providers = new ArrayList<AuthenticationProvider>();
    	providers.add(oSecurity);
    	return new ProviderManager(providers);
    }
    
    @Bean
    protected SessionRegistry sessionRegistry() {
    	return new SessionRegistryImpl();
    }
    
    @Autowired
    @Bean
    protected ConcurrentSessionControlAuthenticationStrategy concurrentSessionControlStrategy(SessionRegistry sessionRegistry) {
    	ConcurrentSessionControlAuthenticationStrategy strategy = new ConcurrentSessionControlAuthenticationStrategy(sessionRegistry);
    	return strategy;
    }

    @Bean
    protected AuthCreateSuccessHandler authorizeSuccessHandler() {
    	AuthCreateSuccessHandler authorizeSuccessHandler = new AuthCreateSuccessHandler();
		authorizeSuccessHandler.setTargetUrlParameter("redirect_uri");
		authorizeSuccessHandler.setSecurity(context.getBean(XFSecurity.class));
		
		return authorizeSuccessHandler;
    }
    
    @Bean
    protected AuthCreateFailureHandler authorizeFailureHandler() {
		AuthCreateFailureHandler authorizeFailureHandler = new AuthCreateFailureHandler();
		authorizeFailureHandler.setDefaultFailureUrl(XFContext.getProperty("webapp.security.login_page") + "?error");
		authorizeFailureHandler.setSecurity(context.getBean(XFSecurity.class));
		return authorizeFailureHandler;
    }

    @Bean
    protected AuthDestroySuccessHandler authorizeDestroyHandler() {
    	AuthDestroySuccessHandler authorizeDestroyHandler = new AuthDestroySuccessHandler();
    	authorizeDestroyHandler.setSecurity(context.getBean(XFSecurity.class));
		return authorizeDestroyHandler;
    }

    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(context.getBean(XFSecurity.class));
    }
    
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		if (context.getBean(XFSecurity.class) == null) return;

		http.csrf().disable();
		http.exceptionHandling().accessDeniedPage(XFContext.getProperty("webapp.security.login_page"));
		
		http
		   .headers()
		      .frameOptions()
		         .sameOrigin();
		
		String sessions = XFContext.getProperty("webapp.security.max_session");
		
		SessionManagementConfigurer<HttpSecurity> smc = http.sessionManagement();
		
		smc.sessionAuthenticationStrategy(context.getBean(ConcurrentSessionControlAuthenticationStrategy.class));
		smc.invalidSessionUrl("/");
		
		
		if (sessions != null && !sessions.isEmpty()) {
			smc.maximumSessions(Integer.parseInt(sessions)).expiredUrl("/");
		}
				
		http.formLogin()
			.loginPage(XFContext.getProperty("webapp.security.login_page"))
			.loginProcessingUrl("/authorize/create")
			.usernameParameter("username")
			.passwordParameter("password")
			.successHandler(context.getBean(AuthCreateSuccessHandler.class))
			.failureHandler(context.getBean(AuthCreateFailureHandler.class));
		
        Map<String, String[]> securityPath = context.getBean(XFSecurity.class).getSecurityPath();
        if (securityPath != null) {
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry security = http.authorizeRequests();
			for (String key : securityPath.keySet()) {
				security.regexMatchers(key).access("hasAnyRole('" + StringUtils.join(securityPath.get(key), "','") + "')");
			}
        }
        
        TokenBasedRememberMeServices rservice = context.getBean(TokenBasedRememberMeServices.class);
        if (rservice != null) {
    		http.rememberMe()
				.key("xdefine_remembers")
				.rememberMeServices(rservice);
        }
		
		http.logout()
			.logoutUrl("/authorize/destroy")
			.logoutSuccessHandler(context.getBean(AuthDestroySuccessHandler.class))
			.addLogoutHandler(context.getBean(AuthDestroySuccessHandler.class))
			.logoutSuccessUrl("/");
		
	}
}

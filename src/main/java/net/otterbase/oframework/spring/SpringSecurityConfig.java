package net.otterbase.oframework.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import net.otterbase.oframework.OFContext;
import net.otterbase.oframework.auth.handler.AuthorizeFailureHandler;
import net.otterbase.oframework.auth.handler.AuthorizeSuccessHandler;
import net.otterbase.oframework.base.OFSecurity;

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
    protected OFSecurity getOSecurity() {
    	
    	Reflections reflections = new Reflections(OFContext.getProperty("webapp.package"));
		Set<Class<? extends OFSecurity>> subTypes = reflections.getSubTypesOf(OFSecurity.class);

		OFSecurity result = null;
		for (Class<?> subType : subTypes) {
			try {
				result = (OFSecurity) subType.newInstance();
				result.setSessionFactory(sessionFactory);
				break;
			}
			catch(Exception ex) {
			}
		}
    	
    	return result;
    }
    
    @Autowired
    @Bean
    protected TokenBasedRememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
    	TokenBasedRememberMeServices rememberMeServices = new TokenBasedRememberMeServices("otter_remembers", userDetailsService);
    	rememberMeServices.setAlwaysRemember(false);
    	rememberMeServices.setParameter("remember_me");
    	rememberMeServices.setTokenValiditySeconds(900);
    	rememberMeServices.setCookieName("tt");
    	return rememberMeServices;
    }

    @Autowired
    @Bean
    protected AuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService) {
    	DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
    	daoAuthenticationProvider.setUserDetailsService(userDetailsService);
    	return daoAuthenticationProvider;
    }
    
    @Autowired
    @Bean
    protected ProviderManager authenticationManager(AuthenticationProvider provider) {
    	List<AuthenticationProvider> providers = new ArrayList<AuthenticationProvider>();
    	providers.add(provider);
    	return new ProviderManager(providers);
    }

    @Bean
    protected AuthorizeSuccessHandler authorizeSuccessHandler() {
    	AuthorizeSuccessHandler authorizeSuccessHandler = new AuthorizeSuccessHandler();
		authorizeSuccessHandler.setTargetUrlParameter("redirect_uri");
		return authorizeSuccessHandler;
    }
    
    @Bean
    protected AuthorizeFailureHandler authorizeFailureHandler() {
		AuthorizeFailureHandler authorizeFailureHandler = new AuthorizeFailureHandler();
		authorizeFailureHandler.setDefaultFailureUrl("/login?error");
		return authorizeFailureHandler;
    }

    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(context.getBean(OFSecurity.class))
        	.passwordEncoder(context.getBean(OFSecurity.class));
    }
    
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		if (context.getBean(OFSecurity.class) == null) return;

		http.csrf().disable();
		
		http.exceptionHandling().accessDeniedPage("/login");
		
		String sessions = OFContext.getProperty("webapp.security.max_session");
		
		if (sessions != null && !sessions.isEmpty()) {
			http.sessionManagement()
				.invalidSessionUrl("/")
				.maximumSessions(Integer.parseInt(sessions))
				.expiredUrl("/");
		}
				
		http.formLogin()
			.loginPage("/login")
			.loginProcessingUrl("/session/create")
			.usernameParameter("username")
			.passwordParameter("password")
			.successHandler(context.getBean(AuthorizeSuccessHandler.class))
			.failureHandler(context.getBean(AuthorizeFailureHandler.class));
		
        Map<String, String> securityPath = context.getBean(OFSecurity.class).getSecurityPath();
		ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry security = http.authorizeRequests();
		for (String key : securityPath.keySet()) {
			security.antMatchers(key).access(securityPath.get(key));
		}

		http.rememberMe()
			.key("otter_remembers")
			.rememberMeServices(context.getBean(TokenBasedRememberMeServices.class));
		
		http.logout()
			.logoutUrl("/session/destroy")
			.invalidateHttpSession(true)
			.logoutSuccessUrl("/");
		
	}
}

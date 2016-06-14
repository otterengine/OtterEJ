package net.otterbase.oframework.spring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
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

import net.otterbase.oframework.RSContext;
import net.otterbase.oframework.auth.enc.MySQLEncoder;
import net.otterbase.oframework.auth.handler.AuthorizeFailureHandler;
import net.otterbase.oframework.auth.handler.AuthorizeSuccessHandler;
import net.otterbase.oframework.auth.service.MemberDetailsServiceImpl;

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
	protected UserDetailsService userDetailsService() {
		MemberDetailsServiceImpl service = new MemberDetailsServiceImpl();
    	service.setSessionFactory(sessionFactory);
    	return service;
	}

    @Bean
    protected MySQLEncoder mysqlEncoder() {
    	return new MySQLEncoder();
    }
    
    @Autowired
    @Bean
    protected TokenBasedRememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
    	TokenBasedRememberMeServices rememberMeServices = new TokenBasedRememberMeServices("rs_remembers", userDetailsService);
    	rememberMeServices.setAlwaysRemember(false);
    	rememberMeServices.setParameter("rs_rememberme");
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
		authorizeSuccessHandler.setTargetUrlParameter("rs_redirect");
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
        auth.userDetailsService(context.getBean(UserDetailsService.class))
        	.passwordEncoder(context.getBean(MySQLEncoder.class));
    }
    
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable();
		
		http.exceptionHandling().accessDeniedPage("/login");
		
		String sessions = RSContext.getProperty("rsengine.security.max_session");
		
		if (sessions != null && !sessions.isEmpty()) {
			http.sessionManagement()
				.invalidSessionUrl("/")
				.maximumSessions(Integer.parseInt(sessions))
				.expiredUrl("/");
		}
				
		http.formLogin()
			.loginPage("/login")
			.loginProcessingUrl("/session/create")
			.usernameParameter("rs_username")
			.passwordParameter("rs_password")
			.successHandler(context.getBean(AuthorizeSuccessHandler.class))
			.failureHandler(context.getBean(AuthorizeFailureHandler.class));
		
		if (RSContext.getApplication() != null) {
	        Map<String, String> securityPath = RSContext.getApplication().getSecurityPath();
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry security = http.authorizeRequests();
			for (String key : securityPath.keySet()) {
				security.antMatchers(key).access(securityPath.get(key));
			}
		}

		http.rememberMe()
			.key("rs_remembers")
			.rememberMeServices(context.getBean(TokenBasedRememberMeServices.class));
		
		http.logout()
			.logoutUrl("/session/destroy")
			.invalidateHttpSession(true)
			.logoutSuccessUrl("/");
		
	}
}

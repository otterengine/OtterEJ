package net.xdefine.spring;

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

import net.xdefine.XFContext;
import net.xdefine.auth.handler.AuthCreateFailureHandler;
import net.xdefine.auth.handler.AuthCreateSuccessHandler;
import net.xdefine.auth.handler.AuthDestroySuccessHandler;
import net.xdefine.base.XFSecurity;

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
		
		System.out.println(result);
    	return result;
    }
    
    @Autowired
    @Bean
    protected ProviderManager authenticationManager(XFSecurity oSecurity) {
    	List<AuthenticationProvider> providers = new ArrayList<AuthenticationProvider>();
    	providers.add(oSecurity);
    	return new ProviderManager(providers);
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

		http.csrf().disable();
		http.headers().frameOptions().sameOrigin();
		
		System.out.println(context.getBean(XFSecurity.class));
		if (context.getBean(XFSecurity.class) == null) return;

		http.exceptionHandling().accessDeniedPage(XFContext.getProperty("webapp.security.login_page"));
		
				
		http.formLogin()
			.loginPage(XFContext.getProperty("webapp.security.login_page"))
			.loginProcessingUrl("/authorize/create")
			.usernameParameter("username")
			.passwordParameter("password")
			.successHandler(context.getBean(AuthCreateSuccessHandler.class))
			.failureHandler(context.getBean(AuthCreateFailureHandler.class));
		
		http.sessionManagement().sessionFixation().none();
		
        Map<String, String[]> securityPath = context.getBean(XFSecurity.class).getSecurityPath();
        if (securityPath != null) {
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry security = http.authorizeRequests();
			for (String key : securityPath.keySet()) {
				security.regexMatchers(key).access("hasAnyRole('" + StringUtils.join(securityPath.get(key), "','") + "')");
			}
        }
		
		http.logout()
			.logoutUrl("/authorize/destroy")
			.logoutSuccessHandler(context.getBean(AuthDestroySuccessHandler.class))
			.addLogoutHandler(context.getBean(AuthDestroySuccessHandler.class))
			.logoutSuccessUrl("/");
		
	}
}

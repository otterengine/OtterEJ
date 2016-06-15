## OtterEJ – Otter Engine in Java

*OtterEngine* is a light-weight Website frameworks, released under a Modified BSD licence.

현재 자바버전은 다음 모듈을 사용합니다.
- SpringFramework 4.x
- Hibernate 5.x
- Velocity 2.x


## Quickstart

*/src/main/resource/rsengine.properties*
	
	webapp.site_name={ Site Name }
	webapp.site_web_url={ Site Http URL }
	webapp.site_ssl_url={ Site Https URL }
	
	webapp.package={ Web Application Main Package }
	
	webapp.mail.smtp.host={ SMTP Host }
	webapp.mail.smtp.port={ SMTP Port }
	webapp.mail.smtp.auth={ SMTP Authorize? true / false }
	webapp.mail.smtp.starttls.enable={Start TLS Enable}
	webapp.mail.username={ SMTP ID }
	webapp.mail.password={ SMTP Password }
	
	webapp.db.package={ Hibernate Entity Package }
	webapp.db.jdbc_url={ JDBC URL }
	webapp.db.username={ JDBC ID }
	webapp.db.password={ JDBC Password }
	
	hibernate.show_sql=false
	
	hibernate.connection.driver_class=com.mysql.jdbc.Driver
	hibernate.connection.useUnicode=true
	hibernate.connection.characterEncoding=utf-8
	
	hibernate.dialect=org.hibernate.dialect.MySQLDialect
	
	hibernate.current_session_context_class=thread
	hibernate.cache.use_second_level_cache=true
	hibernate.cache.region.factory_class=org.hibernate.cache.ehcache.EhCacheRegionFactory
	hibernate.cache.use_query_cache=true
	hibernate.query.substitutions=true 1,false 0
	

*/WEB-INF/web.xml*

	<filter>
		<filter-name>encodingFilter</filter-name>
		<filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
		<init-param>
			<param-name>encoding</param-name>
			<param-value>UTF-8</param-value>
		</init-param>
	</filter>

	<filter-mapping>
		<filter-name>encodingFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>

	<filter>
		<filter-name>realIPFilter</filter-name>
		<filter-class>net.otterbase.oframework.common.wrapper.RealIPFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>realIPFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>

	<filter>
		<filter-name>springSecurityFilterChain</filter-name>
		<filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>springSecurityFilterChain</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>

	<context-param>
		<param-name>contextClass</param-name>
		<param-value>org.springframework.web.context.support.AnnotationConfigWebApplicationContext</param-value>
	</context-param>

	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>net.otterbase.oframework.spring.RootConfig</param-value>
	</context-param>
	
	<listener>
		<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
	</listener>
	
	<servlet>
		<servlet-name>appServlet</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
		<init-param>
			<param-name>contextClass</param-name>
			<param-value>org.springframework.web.context.support.AnnotationConfigWebApplicationContext</param-value>
		</init-param>
		<init-param>
			<param-name>contextConfigLocation</param-name>
			<param-value>net.otterbase.oframework.spring.MvcConfig</param-value>
		</init-param>
		<load-on-startup>1</load-on-startup>
	</servlet>

	<servlet-mapping>
		<servlet-name>appServlet</servlet-name>
		<url-pattern>/*</url-pattern>
	</servlet-mapping>

	<distributable />
	

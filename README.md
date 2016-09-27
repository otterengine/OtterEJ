# XDefine Framework for Java


### Benefit

* It can give the "SpringFramework" settings easy and convenient. ( like Spring Boot )
* Through compatibility with Framework4J, Framework4P easy conversion of PHP and JAVA.
* Easily and quickly by a process using a site building tools are available.


### Usage Framework & Platform

* SpringFramework 4.x
* SpringSecurity 4.x
* Hibernate 5.x
* Velocity 2.x


### Install

This method was created based on the Java Maven project by default.

* pom.xml

```
<dependency>
	<groupId>com.bonocomms.xdefine</groupId>
	<artifactId>Framework4J</artifactId>
	<version>1.0.4</version>
</dependency>
```

* src/main/resources/xdefine.properties

```
webapp.site_name=
webapp.site_web_url=http://localhost:8080
webapp.site_ssl_url=

webapp.package=kr.co.planetpeople.zigooincard

webapp.db.package=kr.co.planetpeople.zigooincard.entity
webapp.db.driver=mysql
webapp.db.hostname=121.254.170.244:3306
webapp.db.database=zigooincard
webapp.db.username=zigooincard
webapp.db.password={DB PASSWORD}

webapp.file.path=/Users/moonsuhan/Documents/file
webapp.file.imagemagick=

webapp.security.login_page=/login
webapp.security.max_session=

webapp.mail.smtp.host=smtp.worksmobile.com
webapp.mail.smtp.port=587
webapp.mail.smtp.auth=true
webapp.mail.smtp.starttls.enable=true
webapp.mail.display.name=Ry-han
webapp.mail.display.mail=no-reply@ry-han.com
webapp.mail.username=no-reply@ry-han.com
webapp.mail.password={SMTP PASSWORD}
```
 
* src/main/webapp/WEB-INF/web.xml
```
<web-app ...>
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
	    <filter-class>com.bonocomms.xdefine.common.wrapper.RealIPFilter</filter-class>
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
	    <param-value>com.bonocomms.xdefine.spring.RootConfig</param-value>
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
	        <param-value>com.bonocomms.xdefine.spring.MvcConfig</param-value>
	    </init-param>
	    <load-on-startup>1</load-on-startup>
	</servlet>
	<servlet-mapping>
	    <servlet-name>appServlet</servlet-name>
	    <url-pattern>/*</url-pattern>
	</servlet-mapping>
	<distributable />
</webapp>

```

For more information, please refer to the Wiki page.



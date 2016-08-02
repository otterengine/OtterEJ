# XDefine Framework for Java


### Benefit

* 스프링 설정을 쉽고 편리하게 해줄 수 있다. ( like Spring Boot )
* Framework4J 와 Framework4P의 호환성을 통하여 PHP와 JAVA의 변환이 쉽다.
* 툴을 사용하여 Scafoldding 처리를 함으로서 쉽고 빠르게 사이트 구축이 가능하다.


### Usage Framework & Platform

* SpringFramework 4.x
* SpringSecurity 4.x
* Hibernate 5.x
* Velocity 2.x


### Install

본 방법은 기본적으로 Maven을 사용하는 자바 프로젝트에 기반하여 작성되었습니다.

* Open pom.xml and add dependency 

```
<dependency>
	<groupId>com.bonocomms.xdefine</groupId>
	<artifactId>Framework4J</artifactId>
	<version>1.0.4</version>
</dependency>
```

* Create setting files.
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


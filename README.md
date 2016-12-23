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
	<version>1.8.1</version>
</dependency>
```

* src/main/resources/xdefine.properties

```
webapp.package={::Website Package::}

webapp.mail.smtp.host=
webapp.mail.smtp.port=
webapp.mail.smtp.auth=
webapp.mail.smtp.starttls.enable=
webapp.mail.display.name=
webapp.mail.display.mail=
webapp.mail.username=
webapp.mail.password=

webapp.db.jdbc_url=
webapp.db.username=
webapp.db.password=
webapp.db.package={:: Hibernate Entity Package ::}

webapp.file.path=
webapp.file.imagemagick={install imagemagick path}

webapp.security.login_page=/login
webapp.security.max_session=

```
 
* src/main/webapp/WEB-INF/web.xml
```
<web-app ...>
	<filter>
		<filter-name>XBaseFilter</filter-name>
		<filter-class>net.xdefine.XBaseFilter</filter-class>
		<init-param>
			<param-name>encoding</param-name>
			<param-value>UTF-8</param-value>
		</init-param>
		<init-param>
			<param-name>headerName</param-name>
			<param-value>X-Real-IP</param-value> <!-- Real IP Receive Header.. -->
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>XBaseFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
	<context-param>
		<param-name>contextClass</param-name>
		<param-value>org.springframework.web.context.support.AnnotationConfigWebApplicationContext
		</param-value>
	</context-param>
	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>net.xdefine.XFConfiguration</param-value>
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
			<param-value>net.xdefine.servlet.javaconfig.SpringMVC</param-value>
		</init-param>
		<load-on-startup>1</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>appServlet</servlet-name>
		<url-pattern>/*</url-pattern>
	</servlet-mapping>
	<distributable />
</web-app>

```

For more information, please refer to the Wiki page.



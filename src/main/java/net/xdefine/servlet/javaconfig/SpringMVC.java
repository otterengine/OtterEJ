package net.xdefine.servlet.javaconfig;

import java.io.File;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.velocity.app.VelocityEngine;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import net.xdefine.XFContext;
import net.xdefine.security.XFSecurity;
import net.xdefine.security.web.AuthorizeController;
import net.xdefine.servlet.interceptors.RequestInterceptor;
import net.xdefine.servlet.interceptors.XFInterceptor;
import net.xdefine.tools.SMTPMailSender;
import net.xdefine.tools.services.AttachFileContext;

@Configuration
@EnableWebMvc
@PropertySource("classpath:xdefine.properties")
@ComponentScan(basePackages = "${webapp.package}")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Import(value = { VelocityConfig.class })
@EnableScheduling
public class SpringMVC extends WebMvcConfigurerAdapter implements ApplicationContextAware {

	private ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

	@Bean
	public AttachFileContext fileContext() {
		return new AttachFileContext();
	}

	@Bean
	public CommonsMultipartResolver multipartResolver() {
		return new CommonsMultipartResolver();
	}

	@Bean
	public AuthorizeController authorizeController() {
		try {
			Object o = context.getBean(XFSecurity.class);
			if (o == null)
				throw new Exception();
			return new AuthorizeController();
		} catch (Exception ex) {
			return null;
		}
	}

	@Bean
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(3);
		taskExecutor.setMaxPoolSize(30);
		taskExecutor.setQueueCapacity(128);
		return taskExecutor;
	}

	@Bean
	public JavaMailSender javaMailSender() {

		if (XFContext.getProperty("webapp.mail.port") != null && !XFContext.getProperty("webapp.mail.port").isEmpty()) {

			Properties properties = new Properties();
			for (Object key : XFContext.keySet()) {
				if (!key.toString().startsWith("webapp.mail.smtp."))
					continue;
				String rkey = key.toString().substring(key.toString().indexOf(".") + 1);
				properties.put(rkey, XFContext.getProperty(key.toString()).trim());
			}

			JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
			mailSender.setHost(XFContext.getProperty("webapp.mail.host"));
			mailSender.setPort(Integer.parseInt(XFContext.getProperty("webapp.mail.port")));
			mailSender.setProtocol(XFContext.getProperty("webapp.mail.protocol"));
			mailSender.setUsername(XFContext.getProperty("webapp.mail.username"));
			mailSender.setPassword(XFContext.getProperty("webapp.mail.password"));
			mailSender.setJavaMailProperties(properties);

			return mailSender;
		} else {
			return null;
		}
	}

	@Bean
	public SMTPMailSender mailSender() {
		SMTPMailSender sender = new SMTPMailSender();
		sender.setJavaMailSender((JavaMailSender) context.getBean("javaMailSender"));
		sender.setVelocityEngine((VelocityEngine) context.getBean("velocityEngine"));
		return sender;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		RequestInterceptor interceptor = new RequestInterceptor();
		try {
			interceptor.setSecurity(context.getBean(XFSecurity.class));
		} catch (Exception ex) {
			interceptor.setSecurity(null);
		}
		registry.addInterceptor(interceptor);

		Reflections reflections = new Reflections(XFContext.getProperty("webapp.package"));
		for (Class<? extends XFInterceptor> subType : reflections.getSubTypesOf(XFInterceptor.class)) {
			registry.addInterceptor(context.getBean(subType));
		}

	}

	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		try {

			ServletContext servletContext = context.getBean(ServletContext.class);
			File dir = new File(servletContext.getRealPath("/"));

			for (File file : dir.listFiles()) {
				String name = file.getName();
				if (name.toLowerCase().endsWith("-inf") || name.isEmpty())
					continue;

				if (file.isDirectory()) {
					registry.addResourceHandler("/" + name + "/**").addResourceLocations("/" + name + "/");
				} else {
					registry.addResourceHandler("/" + name).addResourceLocations("/" + name);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}

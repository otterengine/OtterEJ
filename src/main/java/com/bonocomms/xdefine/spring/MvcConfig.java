package com.bonocomms.xdefine.spring;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.velocity.app.VelocityEngine;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.bonocomms.xdefine.XFContext;
import com.bonocomms.xdefine.base.XFInterceptor;
import com.bonocomms.xdefine.common.interceptor.RequestInterceptor;
import com.bonocomms.xdefine.file.AttachFileContext;
import com.bonocomms.xdefine.mail.SMTPMailSender;

@Configuration
@EnableWebMvc
@PropertySource("classpath:xdefine.properties")
@ComponentScan(basePackages = "${webapp.package}")
@Import(value = { SpringVelocityConfig.class })
public class MvcConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {

	private Logger logger = LoggerFactory.getLogger("");
	private ApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;

		logger.info("  ");
		logger.info(" #############################################################");
		logger.info("  ");
		logger.info(" Welcome to X-Define");
		logger.info(" (c) Copyright Bono Communications Inc, Korea");
		logger.info(" ");
		logger.info(" Using the Tool can work more convenient.");
		logger.info(" Detailed information on the framework, please refer to the site.");
		logger.info(" Thank you.");
		logger.info(" ");
		logger.info(" http://www.xdefine.net");
		logger.info(" ");
		logger.info(" ");
		logger.info(" #############################################################");
		logger.info(" ");
		
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
	public ReloadableResourceBundleMessageSource messageSource() {
		String sep = File.separator;
		String prefix = sep + "WEB-INF" + sep + "messages";
		
		try {
			ServletContext servletContext = context.getBean(ServletContext.class);
			File dir = new File(servletContext.getRealPath("/WEB-INF/messages/"));
			
			List<String> names = new ArrayList<String>();
			if (dir.exists() && dir.listFiles() != null) {
				for (File file : dir.listFiles()) {
					if (file == null || !file.isFile()) continue;
					String name = file.getName();
					name = name.substring(0, name.lastIndexOf("."));
					if (name.contains("_")) name = name.substring(0, name.indexOf("_"));
					if (!names.contains(prefix + sep + name)) names.add(prefix + sep + name);
				}
			}
			
			ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
			messageSource.setDefaultEncoding("UTF-8");
			if (names.size() > 0) {
				String[] nameArray = names.toArray(new String[names.size()]);
				messageSource.setBasenames(nameArray);
				messageSource.setCacheSeconds(0);
			}
			
			return messageSource;
		}
		catch(Exception ex) {
			return null;
		}
	}
	
	@Bean
	public JavaMailSender javaMailSender() {

		Properties properties = new Properties();
		for(Object key : XFContext.keySet()) {
			if (!key.toString().startsWith("webapp.mail.smtp.")) continue;
			String rkey = key.toString().substring(key.toString().indexOf(".") + 1);
			properties.put(rkey, XFContext.getProperty(key.toString()).trim());
		}
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setUsername(XFContext.getProperty("webapp.mail.username"));
		mailSender.setPassword(XFContext.getProperty("webapp.mail.password"));
		mailSender.setJavaMailProperties(properties);
		
		return mailSender;
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

		registry.addInterceptor(new RequestInterceptor());

		Reflections reflections = new Reflections(XFContext.getProperty("webapp.package"));
		for (Class<? extends XFInterceptor> subType : reflections.getSubTypesOf(XFInterceptor.class)) {
			try {
				XFInterceptor interceptor = subType.newInstance();
				if (interceptor != null) registry.addInterceptor(interceptor);
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}

	}
	
	@Override
	public void addResourceHandlers(final ResourceHandlerRegistry registry) {
		try {
			
			ServletContext servletContext = context.getBean(ServletContext.class);
			File dir = new File(servletContext.getRealPath("/"));
			
			FileFilter directoryFilter = new FileFilter() {
				public boolean accept(File file) {
					return file.isDirectory();
				}
			};
			
			for (File file : dir.listFiles(directoryFilter)) {
				String name = file.getName();
				if (name.toLowerCase().endsWith("-inf") || name.isEmpty()) continue;
				registry.addResourceHandler("/" + name + "/**").addResourceLocations("/" + name + "/");
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	

	

}

package net.otterbase.oframework.spring;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
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

import net.otterbase.oframework.OFContext;
import net.otterbase.oframework.base.OFInterceptor;
import net.otterbase.oframework.common.interceptor.RequestInterceptor;
import net.otterbase.oframework.file.AttachFileContext;
import net.otterbase.oframework.mail.SMTPMailSender;

@Configuration
@EnableWebMvc
@PropertySource("classpath:otter.properties")
@ComponentScan(basePackages = "${webapp.package}")
@Import(value = { SpringVelocityConfig.class })
public class MvcConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {

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
	public ReloadableResourceBundleMessageSource messageSource() {
		String sep = File.separator;
		String prefix = sep + "WEB-INF" + sep + "messages";

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
	
	@Bean
	public JavaMailSender javaMailSender() {

		Properties properties = new Properties();
		for(Object key : OFContext.keySet()) {
			if (!key.toString().startsWith("webapp.mail.smtp.")) continue;
			properties.put(key, OFContext.getProperty(key.toString()).trim());
		}
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setUsername(OFContext.getProperty("webapp.mail.username"));
		mailSender.setPassword(OFContext.getProperty("webapp.mail.password"));
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

		Reflections reflections = new Reflections(OFContext.getProperty("webapp.package"));
		for (Class<? extends OFInterceptor> subType : reflections.getSubTypesOf(OFInterceptor.class)) {
			try {
				OFInterceptor interceptor = subType.newInstance();
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

package net.otterbase.oframework.spring;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.reflections.Reflections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import net.otterbase.oframework.RSContext;
import net.otterbase.oframework.base.RSComponent;
import net.otterbase.oframework.base.RSInterceptor;
import net.otterbase.oframework.common.interceptor.RequestInterceptor;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "*", 
	useDefaultFilters = false, 
	includeFilters = { 
			@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class),
			@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Service.class),
			@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = RSComponent.class)
	})
@Import(value = { SpringVelocityConfig.class })
public class MvcConfig extends WebMvcConfigurerAdapter {

	@Bean
	public CommonsMultipartResolver multipartResolver() {
		return new CommonsMultipartResolver();
	}
	
	@Bean
	public ReloadableResourceBundleMessageSource messageSource() {
		String sep = File.separator;
		String prefix = sep + "WEB-INF" + sep + "messages";

		File dir = new File(RSContext.getPath() + prefix);
		
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
	public JavaMailSender mailSender() {

		Properties properties = new Properties();
		for(Object key : RSContext.keySet()) {
			if (!key.toString().startsWith("mail.smtp.")) continue;
			properties.put(key, RSContext.getProperty(key.toString()).trim());
		}
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setUsername(RSContext.getProperty("mail.username"));
		mailSender.setPassword(RSContext.getProperty("mail.password"));
		mailSender.setJavaMailProperties(properties);
		
		return mailSender;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(new RequestInterceptor());

		Reflections reflections = new Reflections(RSContext.getProperty("rsengine.package"));
		for (Class<? extends RSInterceptor> subType : reflections.getSubTypesOf(RSInterceptor.class)) {
			try {
				RSInterceptor interceptor = subType.newInstance();
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

			File dir = new File(RSContext.getPath());
			
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

package net.xdefine.servlet.javaconfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.velocity.exception.VelocityException;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;

import net.xdefine.XFContext;
import net.xdefine.security.utils.VUSecurity;
import net.xdefine.servlet.VMLViewResolver;
import net.xdefine.servlet.VMToolboxView;
import net.xdefine.servlet.annotations.ViewHelper;
import net.xdefine.servlet.tools.VUCommon;
import net.xdefine.servlet.tools.VUHtmlTag;

@Configuration
public class VelocityConfig implements ApplicationContextAware {
	
	private ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

	@Bean
	public VUSecurity veloSecUser() {
		return new VUSecurity();
	}
	
	@Bean
	public VUHtmlTag veloHTMLUtils() {
		return new VUHtmlTag();
	}
	
	@Bean
	public VUCommon veloStringUtils() {
		return new VUCommon();
	}
	
	@Bean
	public VelocityConfigurer velocityConfig() {
		String path = XFContext.getProperty("webapp.view.path");
		if (path == null || path.isEmpty()) path = "/WEB-INF/views/";

		VelocityConfigurer configurer = new VelocityConfigurer();
		configurer.setResourceLoaderPath(path);
		Properties props = new Properties();
		props.put("resource.loader", "file");
		props.put("input.encoding", "utf-8");
		props.put("output.encoding", "utf-8");
		configurer.setVelocityProperties(props);

		return configurer;
	}

	@Bean
	public VelocityEngineFactoryBean velocityEngine() throws VelocityException, IOException {
		String path = XFContext.getProperty("webapp.view.path");
		if (path == null || path.isEmpty()) path = "/WEB-INF/views/";
		
		VelocityEngineFactoryBean factory = new VelocityEngineFactoryBean();
		factory.setResourceLoaderPath(path);
		Properties props = new Properties();
		props.put("resource.loader", "file");
		props.put("input.encoding", "utf-8");
		props.put("output.encoding", "utf-8");
		factory.setVelocityProperties(props);
		
		return factory;
	}

	@Bean
	public ViewResolver viewResolver() {
		
		String path = XFContext.getProperty("webapp.view.path");
		if (path == null || path.isEmpty()) path = "/WEB-INF/views/";
		
		String engine = XFContext.getProperty("webapp.view.engine");
		if (engine == null) engine = "vml";
		if (engine.equals("jsp")) {

			InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
			viewResolver.setViewClass(JstlView.class);
			viewResolver.setPrefix(path);
			viewResolver.setSuffix("." + engine);
			return viewResolver;
		}
		else {
			

			VMLViewResolver resolver = new VMLViewResolver();
			resolver.setCache(true);
			resolver.setSuffix(".vm");
			resolver.setContentType("text/html; charset=UTF-8");
			resolver.setExposeSpringMacroHelpers(true);
			resolver.setViewClass(VMToolboxView.class);
			resolver.setToolboxConfigLocation("/WEB-INF/views/tools.xml");

			Map<String, Object> attributes = new HashMap<String, Object>();

			Reflections reflections = new Reflections(XFContext.getProperty("rsengine.package"));
			Set<Class<?>> subTypes = reflections.getTypesAnnotatedWith(ViewHelper.class);
			for (Class<?> subType : subTypes) {
				ViewHelper anno = (ViewHelper) subType.getAnnotation(ViewHelper.class);
				attributes.put(anno.name(), context.getBean(subType));
			}
			
			attributes.put("sec", context.getBean(VUSecurity.class));
			attributes.put("html", context.getBean(VUHtmlTag.class));
			attributes.put("str", context.getBean(VUCommon.class));

			resolver.setAttributesMap(attributes);

			Map<String, String> mappings = new HashMap<String, String>();
			mappings.put("admin/*", "shared/layout.admin.vm");
			mappings.put("mobile/*", "shared/layout.mobile.vm");
			mappings.put("*", "shared/layout.default.vm");
			resolver.setMappings(mappings);

			return resolver;
			
		}

	}
	

}

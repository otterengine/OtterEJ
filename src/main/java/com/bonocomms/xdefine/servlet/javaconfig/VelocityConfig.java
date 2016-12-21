package com.bonocomms.xdefine.servlet.javaconfig;

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
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;

import com.bonocomms.xdefine.XFContext;
import com.bonocomms.xdefine.annotation.ViewHelper;
import com.bonocomms.xdefine.servlet.VMLViewResolver;
import com.bonocomms.xdefine.servlet.VMToolboxView;
import com.bonocomms.xdefine.views.helper.VUCommon;
import com.bonocomms.xdefine.views.helper.VUHtmlTag;
import com.bonocomms.xdefine.views.helper.VUSecurity;

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
		VelocityConfigurer configurer = new VelocityConfigurer();
		configurer.setResourceLoaderPath("/WEB-INF/views/");
		Properties props = new Properties();
		props.put("resource.loader", "file");
		props.put("input.encoding", "utf-8");
		props.put("output.encoding", "utf-8");
		configurer.setVelocityProperties(props);

		return configurer;
	}

	@Bean
	public VelocityEngineFactoryBean velocityEngine() throws VelocityException, IOException {
		VelocityEngineFactoryBean factory = new VelocityEngineFactoryBean();
		factory.setResourceLoaderPath("/WEB-INF/views/");
		Properties props = new Properties();
		props.put("resource.loader", "file");
		props.put("input.encoding", "utf-8");
		props.put("output.encoding", "utf-8");
		factory.setVelocityProperties(props);
		
		return factory;
	}

	@Bean
	public ViewResolver viewResolver() {

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
		mappings.put("*", "shared/layout.default.vm");
		resolver.setMappings(mappings);

		return resolver;
	}
	

}

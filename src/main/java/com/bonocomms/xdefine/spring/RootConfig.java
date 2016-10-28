package com.bonocomms.xdefine.spring;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@Import({ SpringDatabaseConfig.class, SpringSecurityConfig.class })
public class RootConfig implements ApplicationContextAware {

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
				System.out.println(names);
				String[] nameArray = names.toArray(new String[names.size()]);
				messageSource.setBasenames(nameArray);
				messageSource.setCacheSeconds(0);
			}
			
			return messageSource;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}

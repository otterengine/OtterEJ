package net.xdefine;

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
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableAsync
@EnableWebMvc
public abstract class XFConfigSimple implements ApplicationContextAware {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	protected ApplicationContext context;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
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
			
			System.out.println(names);
			
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
			ex.printStackTrace();
			return null;
		}
	}
	
	
}

package net.xdefine;

import java.io.File;
import java.net.URLDecoder;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import net.sf.json.JSONObject;

public class XFContext {

	private static Logger logger = LoggerFactory.getLogger(XFContext.class);
	private static final String VERSION = "2.0.0-2017.01";
	private static XFContext _instance;
	
	public static XFContext getInstance() {
		if (_instance == null) _instance = new XFContext();
		return _instance;
	}

	
	@Autowired private ReloadableResourceBundleMessageSource messageSource;
	private JSONObject property;
	private XFContext() {
		try {
			
			System.out.println("RELOAD...");
			
			logger.info("  ");
			logger.info(" #############################################################");
			logger.info("  ");
			logger.info(" Welcome to X-Define (" + VERSION + ")");
			logger.info(" (c) Copyright THR Studio Inc, Korea");
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
			
			Properties p = new Properties();

			String propPath = getClass().getResource("/xdefine.properties").getPath();
			propPath = propPath.substring(0, propPath.lastIndexOf("/"));
			propPath = URLDecoder.decode(propPath, "UTF-8");
			if (System.getProperty("os.name").toLowerCase().contains("windows")) propPath = propPath.substring(1);
			
			File dir = new File(propPath);
			for (File file : dir.listFiles()) {
				if (file.isDirectory() || !file.getName().endsWith(".properties")) continue;
				p.load(getClass().getResourceAsStream("/" + file.getName()));
			}

			property = new JSONObject();
			for(Object key : p.keySet()) {
				property.put(key.toString(), p.getProperty(key.toString()));
			}
			
			p.clear();
			
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public static String getLanguage(String key) {
		try {
			if (_instance == null) _instance = new XFContext();
			return _instance.messageSource.getMessage(key, new Object[]{}, Locale.getDefault());
		}
		catch(Exception ex) {
			return null;
		}
	}
	
	public static String getProperty(String key) {
		try {
			if (_instance == null) _instance = new XFContext();
			return _instance.property.getString(key);
		}
		catch(Exception ex) {
			return null;
		}
	}


	@SuppressWarnings("unchecked")
	public static Set<String> keySet() {
		if (_instance == null) _instance = new XFContext();
		return _instance.property.keySet();
	}

}

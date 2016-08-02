package com.bonocomms.xdefine.spring;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.bonocomms.xdefine.XFContext;

@Configuration
@EnableTransactionManagement
@EnableAsync
public class SpringDatabaseConfig {

	@Autowired
	@Bean(name = "transactionManager")
	public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory) {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager(sessionFactory);
		return transactionManager;
	}
	
	@Bean(name = "dataSource")
	public DataSource getDataSource() {
		
		String driver = XFContext.getProperty("webapp.db.driver").trim();
		String jdbcUrl = "";
		String driverClass = "";
		
		if (driver.equals("mysql")) {
			driverClass = "com.mysql.jdbc.Driver";
			jdbcUrl = "jdbc:mysql://" + XFContext.getProperty("webapp.db.hostname").trim() + "/" + XFContext.getProperty("webapp.db.database").trim() + 
					"?failOverReadOnly=true&autoReconnect=true&autoReconnectForPools=true&characterEncoding=UTF-8";
		}
		else {
			driverClass = XFContext.getProperty("hibernate.connection.driver_class").trim();
			jdbcUrl = XFContext.getProperty("webapp.db.jdbc_url").trim();
		}

		BasicDataSource dataSource;
		try {
			dataSource = new BasicDataSource();
			dataSource.setDriverClassName(driverClass);
			dataSource.setUrl(jdbcUrl);
			dataSource.setUsername(XFContext.getProperty("webapp.db.username").trim());
			dataSource.setPassword(XFContext.getProperty("webapp.db.password").trim());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			dataSource = null;
		}

		return dataSource;
	}

	@Autowired
	@Bean(name = "sessionFactory")
	public SessionFactory getSessionFactory(DataSource dataSource) {
		
		LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);

		try {
			sessionBuilder.scanPackages(XFContext.getProperty("webapp.db.package").trim());

			String driver = XFContext.getProperty("webapp.db.driver").trim();

			Properties properties = new Properties();
			if (driver.equals("mysql")) {
				properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
				properties.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
			}
			
			properties.put("hibernate.connection.useUnicode", true);
			properties.put("hibernate.connection.characterEncoding", "utf-8");
			
			properties.put("hibernate.show_sql", false);
			properties.put("hibernate.current_session_context_class", "thread");
			properties.put("hibernate.query.substitutions", "true 1,false 0");
			
			properties.put("hibernate.cache.use_second_level_cache", true);
			properties.put("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory");
			properties.put("hibernate.cache.use_query_cache", true);
			
			for(Object key : XFContext.keySet()) {
				if (!key.toString().startsWith("hibernate.")) continue;
				properties.put(key, XFContext.getProperty(key.toString()).trim());
			}
			sessionBuilder.addProperties(properties);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			dataSource = null;
		}
		
		return sessionBuilder.buildSessionFactory();
	}

}

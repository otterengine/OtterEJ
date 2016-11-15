package com.bonocomms.xdefine.spring;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.bonocomms.xdefine.XFContext;
import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@EnableTransactionManagement
@EnableAsync
public class SpringDatabaseConfig {
	
	@Bean(name = "dataSource")
	public DataSource getDataSource() {
		
		String driver = XFContext.getProperty("webapp.db.driver");
		String jdbcUrl = "";
		String driverClass = "";
		
		if (driver != null && driver.trim().equals("mysql")) {
			driverClass = "com.mysql.jdbc.Driver";
			jdbcUrl = "jdbc:mysql://" + XFContext.getProperty("webapp.db.hostname").trim() + "/" + XFContext.getProperty("webapp.db.database").trim() + 
					"?failOverReadOnly=true&autoReconnect=true&autoReconnectForPools=true&characterEncoding=UTF-8";
		}
		else {
			driverClass = XFContext.getProperty("hibernate.connection.driver_class").trim();
			jdbcUrl = XFContext.getProperty("webapp.db.jdbc_url").trim();
		}

		ComboPooledDataSource cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass( driverClass ); //loads the jdbc driver            
			cpds.setJdbcUrl( jdbcUrl );
			cpds.setUser(XFContext.getProperty("webapp.db.username").trim());
			cpds.setPassword(XFContext.getProperty("webapp.db.password").trim());                                
				
			cpds.setMinPoolSize(5);                                     
			cpds.setAcquireIncrement(5);
			cpds.setMaxPoolSize(20);
			
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return cpds;
	}

	@Autowired
	@Bean(name = "sessionFactory")
	public SessionFactory getSessionFactory(DataSource dataSource) {
		
		
		System.out.println("sessionFactory init.");
		
		LocalSessionFactoryBean sessionBuilder = new LocalSessionFactoryBean();

		try {
			Properties properties = new Properties();
			if (XFContext.getProperty("webapp.db.driver").trim().equals("mysql")) {
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
			
			sessionBuilder.setPackagesToScan(XFContext.getProperty("webapp.db.package").trim());
			sessionBuilder.setHibernateProperties(properties);
			sessionBuilder.setDataSource(dataSource);
			sessionBuilder.afterPropertiesSet();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return sessionBuilder.getObject();
	}

	@Bean(name = "dataSourceRO")
	public DataSource getDataSourceRO() {
		
		String jdbcUrl = "";
		String driverClass = "";
		
		driverClass = XFContext.getProperty("hibernate.connection.driver_class").trim();
		jdbcUrl = XFContext.getProperty("webapp.db.RO.jdbc_url").trim();

		ComboPooledDataSource cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass( driverClass ); //loads the jdbc driver            
			cpds.setJdbcUrl( jdbcUrl );
			cpds.setUser(XFContext.getProperty("webapp.db.username").trim());
			cpds.setPassword(XFContext.getProperty("webapp.db.password").trim());                                
				
			cpds.setMinPoolSize(5);                                     
			cpds.setAcquireIncrement(5);
			cpds.setMaxPoolSize(20);
			
			cpds.getConnection().setReadOnly(true);
			cpds.getConnection().setAutoCommit(false);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return cpds;
	}

	@Autowired
	@Bean(name = "sessionFactoryRO")
	public SessionFactory getSessionFactoryRO(DataSource dataSourceRO) {
		
		LocalSessionFactoryBean sessionBuilder = new LocalSessionFactoryBean();

		try {
			Properties properties = new Properties();
			
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

			properties.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");

			sessionBuilder.setPackagesToScan(XFContext.getProperty("webapp.db.package").trim());
			sessionBuilder.setHibernateProperties(properties);
			sessionBuilder.setDataSource(dataSourceRO);
			sessionBuilder.afterPropertiesSet();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return sessionBuilder.getObject();
	}
	
	@Autowired
	@Bean(name = "transactionManager")
	public HibernateTransactionManager getTransactionManager(SessionFactory sessionFactory) {
		System.out.println(sessionFactory);
		HibernateTransactionManager transactionManager = new HibernateTransactionManager(sessionFactory);
		return transactionManager;
	}

}

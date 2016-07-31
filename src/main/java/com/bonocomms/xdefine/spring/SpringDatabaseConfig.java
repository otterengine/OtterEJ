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

		BasicDataSource dataSource;
		try {
			dataSource = new BasicDataSource();
			dataSource.setDriverClassName(XFContext.getProperty("hibernate.connection.driver_class").trim());
			dataSource.setUrl(XFContext.getProperty("webapp.db.jdbc_url").trim());
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

			Properties properties = new Properties();
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

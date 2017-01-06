package net.xdefine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.jolbox.bonecp.BoneCPDataSource;

import net.xdefine.db.XSessionFactory;
import net.xdefine.db.impl.XSessionFactoryImpl;
import net.xdefine.db.interceptors.ServiceInterceptor;

@Configuration
@EnableAsync
@EnableWebMvc
public class XFConfiguration extends XFConfigSimple {

	private DataSource getDataSource(String suffix) {
		BoneCPDataSource ds = new BoneCPDataSource();
	 	ds.setDriverClass(XFContext.getProperty(suffix + ".driver"));
		ds.setJdbcUrl(XFContext.getProperty(suffix + ".jdbc_url"));
		ds.setUsername(XFContext.getProperty(suffix + ".username"));
		ds.setPassword(XFContext.getProperty(suffix + ".password"));
		ds.setMaxConnectionsPerPartition(5);
		ds.setMinConnectionsPerPartition(1);
		ds.setPartitionCount(1);
		ds.setAcquireIncrement(3);
		ds.setStatementsCacheSize(100);
		ds.setDeregisterDriverOnClose(true);
		ds.setResetConnectionOnClose(true);
		return ds;
	}

	private DataSource dataSource;
	
	@Bean(name = "sessionFactory")
	public XSessionFactory sessionFactory() {
		
		List<String> dbStrings = new ArrayList<String>();
		for (String key : XFContext.keySet()) {
			if (key.startsWith("webapp.db.")) {
				key = key.substring(0, key.lastIndexOf("."));
				if (!dbStrings.contains(key)) dbStrings.add(key);
			}
		}
		
		Map<String, DataSource> datasources = new HashMap<String, DataSource>();
		for (String key : dbStrings) {
			datasources.put(key.substring(key.lastIndexOf(".") + 1), this.getDataSource(key));
		}
		
		if (!datasources.containsKey("default")) throw new IllegalStateException(XFContext.getLanguage("xdefine.language.db.not_found_default"));
		
		dataSource = datasources.get("default");
		return new XSessionFactoryImpl(datasources);
	}

	@Bean(name = "transactionManager")
	public DataSourceTransactionManager transactionManager() {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
		return transactionManager;
	}
	
	@Bean(name = "serviceInterceptor") 
	public ServiceInterceptor serviceInterceptor() {
		return new ServiceInterceptor();
	}
	
}

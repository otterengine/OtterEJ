package com.bonocomms.xdefine.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
@Import({ SpringDatabaseConfig.class, SpringSecurityConfig.class })
public class RootConfig {
	
}

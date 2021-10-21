package com.mycompany.webapp.configuration;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.TransactionTemplate;

@PropertySource("classpath:/application.properties")
@EnableTransactionManagement
@Configuration
public class TransactionManagerConfig {
	
	@Resource
	private DataSource dataSource;
	
	// 트렌젝션을 관리하는 manager 객체 생성
	@Bean
	public PlatformTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource);
	}
	
	// 프로그래밍적 트랜잭션을 위한 template
	@Bean
	public TransactionTemplate transactionTemplate() {
		return new TransactionTemplate(transactionManager());
	}
	
}
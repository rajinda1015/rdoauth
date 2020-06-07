package com.rad.rdoauth.configs;

import java.util.HashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Oauth data source configurations
 * 
 * @author rajinda
 * @since 04th May 2020
 * @version 1.0
 */
@Configuration
@EnableJpaRepositories(
		basePackages = {"com.rad.rdoauth.entity"},
		entityManagerFactoryRef = "oauthEntityManager",
		transactionManagerRef = "oauthTransactionManager"
)
public class RDOauthDataSource {
	
	@Value("${oauth.spring.datasource.url}")
	private String authUrl;
	
	@Value("${oauth.spring.datasource.username}")
	private String authUsername;
	
	@Value("${oauth.spring.datasource.password}")
	private String authPasswd;
	
	@Value("${oauth.spring.datasource.driverClassName}")
	private String authDriverClassName;
	
	@Value("${oauth.spring.datasource.dialect}")
	private String authDialect;
	
	@Value("${oauth.spring.datasource.show_sql}")
	private String authShowSql;
	
	@Primary
	@Bean(name = "oauthDataSource")
	public DataSource oauthDataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(authDriverClassName);
		dataSource.setUrl(authUrl);
		dataSource.setUsername(authUsername);
		dataSource.setPassword(authPasswd);
		return dataSource;
	}
	
	@Primary
	@Bean
	public LocalContainerEntityManagerFactoryBean oauthEntityManager() {
		LocalContainerEntityManagerFactoryBean entityManager = new LocalContainerEntityManagerFactoryBean();
		entityManager.setDataSource(oauthDataSource());
		entityManager.setPackagesToScan("com.rad.rdoauth.entity.model");
		entityManager.setPersistenceUnitName("oauthEntityManager");
		
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		entityManager.setJpaVendorAdapter(adapter);
		
		HashMap<String, Object> hbProperties = new HashMap<String, Object>();
		hbProperties.put("hibernate.dialect", authDialect);
		hbProperties.put("hibernate.show_sql", authShowSql);
		entityManager.setJpaPropertyMap(hbProperties);
		return entityManager;
	}
	
	@Primary
	@Bean(name = "oauthTransactionManager")
	public PlatformTransactionManager oauthTransactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(oauthEntityManager().getObject());
		return transactionManager;
	}
	
	@Bean(name = "oauthJdbcTemplate")
	public JdbcTemplate oauthJdbcTemplate(@Qualifier("oauthDataSource") DataSource oauthDataSource) {
		return new JdbcTemplate(oauthDataSource);
	}
}

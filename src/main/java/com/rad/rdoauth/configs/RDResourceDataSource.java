package com.rad.rdoauth.configs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Resource database
 * 
 * @author rajinda
 * @since 04th May 2020
 * @version 1.0
 */
@Configuration
public class RDResourceDataSource {

	@Value("${resource.datasource.url}")
	private String resourceUrl;
	
	@Value("${resource.datasource.username}")
	private String resourceUsername;
		
	@Value("${resource.datasource.password}")
	private String resourcePasswd;
	
	@Value("${resource.datasource.deriverClassName}")
	private String resourceDriverClassName;
	
	@Bean(name = "resourceDataSource")
	public DataSource resourceDataSource() {
		DriverManagerDataSource resourceDataSource = new DriverManagerDataSource();
		resourceDataSource.setUrl(resourceUrl);
		resourceDataSource.setUsername(resourceUsername);
		resourceDataSource.setPassword(resourcePasswd);
		resourceDataSource.setDriverClassName(resourceDriverClassName);
		return resourceDataSource;
	}
	
	@Bean(name = "resourceJdbcTemplate")
	public JdbcTemplate resourceJdbcTemplate(@Qualifier("resourceDataSource") DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}
}

package com.rad.rdoauth.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

/**
 * Authorization server
 * 
 * @author rajinda
 * @since 06th May 2020
 * @version 1.0
 */
@Configuration
@EnableAuthorizationServer
public class RDOauthServer extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private TokenStore tokenStore;

	@Autowired
	private JwtAccessTokenConverter accessTokenConverter;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired @Qualifier("oauthJdbcTemplate")
	private JdbcTemplate oauthJdbcTemplate;
	
	@Override
	public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
		configurer.jdbc(oauthJdbcTemplate.getDataSource()).passwordEncoder(new BCryptPasswordEncoder());
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endPoints) throws Exception {
		endPoints
				.tokenStore(tokenStore)
				.reuseRefreshTokens(false)
				.accessTokenConverter(accessTokenConverter)
				.authenticationManager(authenticationManager)
				.userDetailsService(userDetailsService);
	}
	
	public void configurer(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
		oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
	}
}

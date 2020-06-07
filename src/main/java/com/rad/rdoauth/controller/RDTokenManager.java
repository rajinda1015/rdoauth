package com.rad.rdoauth.controller;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RefreshScope
@RequestMapping(value = "/tokenManager")
public class RDTokenManager {
	
	private static final Logger LOGGER = LogManager.getLogger(RDTokenManager.class);
	
	@Autowired
	private TokenStore tokenStore;
	
	@RequestMapping(value = "/revokeAccessToken", method = RequestMethod.GET)
	public ResponseEntity<?> removeUserToken(@RequestParam Map<String, String> paramMap) throws Exception {

		if (null == paramMap.get("username") || paramMap.get("username").isEmpty() || "null" == paramMap.get("username")) {
			LOGGER.info("Auth: User is not provided. Logout attempt is failed");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is not provided");
		}
		
		String client = new String(Base64.decodeBase64(paramMap.get("client").getBytes()));
		Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientIdAndUserName(client, paramMap.get("username"));

		if (null == tokens || tokens.isEmpty()) {
			LOGGER.info("Auth: No user logged in sessions found. User ID : " + paramMap.get("username"));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid User Credentials");

		} else {
			tokens.forEach((token) -> {
				OAuth2RefreshToken rfToken = token.getRefreshToken();
				tokenStore.removeAccessToken(token);
				tokenStore.removeRefreshToken(rfToken);
			});
		}
		
		LOGGER.info("Auth: User successfully logged out : " + paramMap.get("username"));
		return ResponseEntity.status(HttpStatus.OK).body("User logged out");
	}

	@RequestMapping(value = "/removeRFToken", method = RequestMethod.GET)
	public ResponseEntity<?> removeRefreshToken(
			@RequestParam Map<String, String> paramMap,
			@RequestHeader(name = "rfToken", required = true) String rfToken) throws Exception {

		if (null == paramMap.get("username") || paramMap.get("username").isEmpty() || "null" == paramMap.get("username")) {
			LOGGER.info("Auth: Token is not provided. Logout attempt is failed");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token is not provided");
		}

		OAuth2RefreshToken token = tokenStore.readRefreshToken(rfToken);

		if (null == token || token.getValue().isEmpty()) {
			LOGGER.info("Auth: Token does not exist. User ID : " + paramMap.get("username"));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token does not exist");

		} else {
			tokenStore.removeAccessTokenUsingRefreshToken(token);
			tokenStore.removeRefreshToken(token);
		}
		
		LOGGER.info("Auth: Token was removed successfully. User ID : " + paramMap.get("username"));
		return ResponseEntity.status(HttpStatus.OK).body("Token was removed successfully");
	}
	
}

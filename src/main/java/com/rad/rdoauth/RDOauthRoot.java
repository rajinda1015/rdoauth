package com.rad.rdoauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@RefreshScope
public class RDOauthRoot {

	public static void main(String[] args) {
		SpringApplication.run(RDOauthRoot.class, args);
	}

}

package com.edwin.rrhh_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {UserDetailsServiceAutoConfiguration.class})
public class RrhhApiRestFulApplication {

	public static void main(String[] args) {
		SpringApplication.run(RrhhApiRestFulApplication.class, args);
	}

}

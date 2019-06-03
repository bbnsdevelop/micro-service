package br.com.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.tokencore.security.token.creator.TokenCreator;

@Configuration
public class ApplicationConfig {
	
	@Bean
	public TokenCreator tokenCreator() {
		return new TokenCreator();
	}


}

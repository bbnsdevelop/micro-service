package br.com.course.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.tokencore.security.token.converter.TokenConverter;


@Configuration
public class ApplicationConfig {
	
	@Bean
	public TokenConverter tokenConverter() {
		return new TokenConverter();
	}


}

package br.com.gateway.sercurity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import br.com.core.property.JWTConfiguration;
import br.com.tokencore.security.config.SecurityTokenConfig;
import br.com.tokencore.security.token.converter.TokenConverter;

@EnableWebSecurity
public class SecurityConfig extends SecurityTokenConfig{

	@Autowired
	private TokenConverter tokenConverter;
	
	public SecurityConfig(JWTConfiguration jwtConfiguration, TokenConverter tokenConverter) {
		super(jwtConfiguration);
		this.tokenConverter = tokenConverter;
	}

}

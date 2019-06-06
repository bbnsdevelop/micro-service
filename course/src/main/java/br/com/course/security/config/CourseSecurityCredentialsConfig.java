package br.com.course.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.core.property.JWTConfiguration;
import br.com.tokencore.security.config.SecurityTokenConfig;
import br.com.tokencore.security.filter.JwtTokenAuthorizationFilter;
import br.com.tokencore.security.token.converter.TokenConverter;

@EnableWebSecurity
public class CourseSecurityCredentialsConfig extends SecurityTokenConfig {

	
	@Autowired
	private TokenConverter tokenConverter;
	
	public CourseSecurityCredentialsConfig(JWTConfiguration jwtConfiguration, TokenConverter tokenConverter) {
		super(jwtConfiguration);
		this.tokenConverter = tokenConverter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http
		.addFilterAfter(new JwtTokenAuthorizationFilter(jwtConfiguration, tokenConverter), UsernamePasswordAuthenticationFilter.class);
		super.configure(http);

	}
		
}

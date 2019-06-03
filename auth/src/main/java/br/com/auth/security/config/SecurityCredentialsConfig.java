package br.com.auth.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.auth.security.filter.JwtUsernameAndaPasswordAuthenticationFilter;
import br.com.core.property.JWTConfiguration;
import br.com.tokencore.security.config.SecurityTokenConfig;
import br.com.tokencore.security.filter.JwtTokenAuthorizationFilter;
import br.com.tokencore.security.token.converter.TokenConverter;
import br.com.tokencore.security.token.creator.TokenCreator;

@EnableWebSecurity
public class SecurityCredentialsConfig extends SecurityTokenConfig {

	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private TokenCreator tokenCreator;
	
	@Autowired
	private TokenConverter tokenConverter;
	
	public SecurityCredentialsConfig(JWTConfiguration jwtConfiguration, UserDetailsService userDetailsService, TokenCreator tokenCreator, TokenConverter tokenConverter) {
		super(jwtConfiguration);
		this.userDetailsService = userDetailsService;
		this.tokenCreator = tokenCreator;
		this.tokenConverter = tokenConverter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.addFilter(new JwtUsernameAndaPasswordAuthenticationFilter(authenticationManager(), jwtConfiguration,
				tokenCreator))
		.addFilterAfter(new JwtTokenAuthorizationFilter(jwtConfiguration, tokenConverter), UsernamePasswordAuthenticationFilter.class);
		super.configure(http);

	}
	
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}

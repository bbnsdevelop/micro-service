package br.com.token.security.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;

import br.com.core.property.JWTConfiguration;

public class SecurityTokenConfig extends WebSecurityConfigurerAdapter {
	
	
	protected JWTConfiguration jWTConfiguration;
	
	@Autowired
	public SecurityTokenConfig(JWTConfiguration jWTConfiguration) {
		this.jWTConfiguration = jWTConfiguration;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
	
		http
			.csrf().disable()
			.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
			.and()
				.sessionManagement().sessionCreationPolicy(STATELESS)
			.and()
				.exceptionHandling().authenticationEntryPoint((req, resp, e) -> resp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
			.and()
				.authorizeRequests()
				.antMatchers(HttpMethod.GET, 
						 "/",
						 "/*.html",
						 "/favicon.ico",
						 "/**/*.html",
						 "/**/*.css",
						 "/**/*.js").permitAll()
				.antMatchers(jWTConfiguration.getLoginUrl()+"/**").permitAll()
				.antMatchers("/course/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated();
					
	}

}

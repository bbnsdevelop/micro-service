package br.com.auth.security.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;

import br.com.auth.security.filter.JwtUsernameAndaPasswordAuthenticationFilter;
import br.com.core.property.JWTConfiguration;

@EnableWebSecurity
public class SecurityCredentialsConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private JWTConfiguration jWTConfiguration;
	
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
				.addFilter(new JwtUsernameAndaPasswordAuthenticationFilter(authenticationManager(), jWTConfiguration))
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
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {	
		auth.userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder());
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}

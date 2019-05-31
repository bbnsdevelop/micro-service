package br.com.auth.security.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


public class JwtUsernameAndaPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private static final Logger log = LoggerFactory.getLogger(JwtUsernameAndaPasswordAuthenticationFilter.class);

}

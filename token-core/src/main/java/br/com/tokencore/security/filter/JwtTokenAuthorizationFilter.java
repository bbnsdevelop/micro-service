package br.com.tokencore.security.filter;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nimbusds.jwt.SignedJWT;

import br.com.core.property.JWTConfiguration;
import br.com.tokencore.security.token.converter.TokenConverter;
import br.com.tokencore.security.util.SecurityContextUtil;


public class JwtTokenAuthorizationFilter extends OncePerRequestFilter {
	
	private static final Logger log = LoggerFactory.getLogger(JwtTokenAuthorizationFilter.class);
	
	@Autowired
	protected JWTConfiguration jWTConfiguration;
	
	@Autowired
	protected TokenConverter tokenConverter;

	public JwtTokenAuthorizationFilter(JWTConfiguration jWTConfiguration, TokenConverter tokenConverter) {
		this.jWTConfiguration = jWTConfiguration;
		this.tokenConverter = tokenConverter;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String header = request.getHeader(this.jWTConfiguration.getHeader().getName());
		if (header == null || !header.startsWith(jWTConfiguration.getHeader().getPrefix())) {
			log.info("verify if header is null on request");
			filterChain.doFilter(request, response);
			return;
		}
		String token = header.replace(this.jWTConfiguration.getHeader().getPrefix(), "").trim();
		log.info("get token on header");
		
		SecurityContextUtil.setSecurityContext(StringUtils.equalsAnyIgnoreCase("signed", this.jWTConfiguration.getType())? validate(token) : drecyptValidating(token) );
		filterChain.doFilter(request, response);
	}
	
	private SignedJWT drecyptValidating(String encryptToken) {
		log.info("decrypt and validating token");
		String signedToken = null;
		try {
			signedToken = this.tokenConverter.decryptToken(encryptToken);
			this.tokenConverter.validateTokenSignature(signedToken);
			return SignedJWT.parse(signedToken);
		} catch (ParseException e) {
			log.error("Error on decrypt token and validating ------> {}", e.getCause());
			e.printStackTrace();
		}
		return null;
	}
	
	private SignedJWT validate(String signedToken) {
		log.info("validating token is Signature");
		tokenConverter.validateTokenSignature(signedToken);
		try {
			return SignedJWT.parse(signedToken);
		} catch (ParseException e) {
			log.error("Error on valide ------> {}", e.getCause());
			e.printStackTrace();
		}
		return null;
		
	}
	
	
}

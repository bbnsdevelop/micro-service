package br.com.gateway.sercurity.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import br.com.core.property.JWTConfiguration;
import br.com.tokencore.security.filter.JwtTokenAuthorizationFilter;
import br.com.tokencore.security.token.converter.TokenConverter;
import br.com.tokencore.security.util.SecurityContextUtil;

public class GatewayJwtTokenAuthorizationFilter extends JwtTokenAuthorizationFilter{
	
	
	
	public GatewayJwtTokenAuthorizationFilter(JWTConfiguration jWTConfiguration, TokenConverter tokenConverter) {
		super(jWTConfiguration, tokenConverter);
	}

	@Override
	@SuppressWarnings("duplicates")
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String header = request.getHeader(jWTConfiguration.getHeader().getName());
		if(header != null | !header.startsWith(jWTConfiguration.getHeader().getName())) {
			filterChain.doFilter(request, response);
			return;
		}
		String token = header.replace(jWTConfiguration.getHeader().getPrefix(), "").trim();
		
		SecurityContextUtil.SetSecurityContext(StringUtils.equalsAnyIgnoreCase("signed", jWTConfiguration.getType())? validate(token) : drecyptValidating(token) );
		filterChain.doFilter(request, response);
	}

}

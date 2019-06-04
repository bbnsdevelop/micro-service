package br.com.gateway.sercurity.filter;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;

import com.netflix.zuul.context.RequestContext;
import com.nimbusds.jwt.SignedJWT;

import br.com.core.property.JWTConfiguration;
import br.com.tokencore.security.filter.JwtTokenAuthorizationFilter;
import br.com.tokencore.security.token.converter.TokenConverter;
import static br.com.tokencore.security.util.SecurityContextUtil.setSecurityContext;

public class GatewayJwtTokenAuthorizationFilter extends JwtTokenAuthorizationFilter{
	
	
	
	public GatewayJwtTokenAuthorizationFilter(JWTConfiguration jWTConfiguration, TokenConverter tokenConverter) {
		super(jWTConfiguration, tokenConverter);
	}

	@Override
	@SuppressWarnings("Duplicates")
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
			throws ServletException, IOException {
		
		String header = request.getHeader(jWTConfiguration.getHeader().getName());
		 if (header == null || !header.startsWith(jWTConfiguration.getHeader().getPrefix())) {
			filterChain.doFilter(request, response);
			return;
		}
		String token = header.replace(jWTConfiguration.getHeader().getPrefix(), "").trim();
		String signedToken = this.tokenConverter.decryptToken(token);
		this.tokenConverter.validateTokenSignature(signedToken);
		
		try {
			setSecurityContext(SignedJWT.parse(signedToken));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if(this.jWTConfiguration.getType().equalsIgnoreCase("signed")) {
			RequestContext.getCurrentContext().addZuulRequestHeader("Authorization", this.jWTConfiguration.getHeader().getPrefix() + signedToken);
		}
		
		filterChain.doFilter(request, response);
	}

}

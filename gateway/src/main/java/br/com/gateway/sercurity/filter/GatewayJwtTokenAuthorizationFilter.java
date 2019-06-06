package br.com.gateway.sercurity.filter;


import java.io.IOException;
import java.text.ParseException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import com.netflix.zuul.context.RequestContext;
import com.nimbusds.jwt.SignedJWT;

import br.com.core.property.JWTConfiguration;
import br.com.tokencore.security.filter.JwtTokenAuthorizationFilter;
import br.com.tokencore.security.token.converter.TokenConverter;
import br.com.tokencore.security.util.SecurityContextUtil;

public class GatewayJwtTokenAuthorizationFilter extends JwtTokenAuthorizationFilter{
	
	private static final Logger log = LoggerFactory.getLogger(GatewayJwtTokenAuthorizationFilter.class);
	
	public GatewayJwtTokenAuthorizationFilter(JWTConfiguration jWTConfiguration, TokenConverter tokenConverter) {
		super(jWTConfiguration, tokenConverter);
	}

	@Override
	@SuppressWarnings("Duplicates")
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
			throws ServletException, IOException {
		log.info("override the method doFilterInternal");
		String header = request.getHeader(jWTConfiguration.getHeader().getName());
		 if (header == null || !header.startsWith(jWTConfiguration.getHeader().getPrefix())) {
			 log.info("verify header is null");
			 filterChain.doFilter(request, response);
			return;
		}
		String token = header.replace(jWTConfiguration.getHeader().getPrefix(), "").trim();
		String signedToken = this.tokenConverter.decryptToken(token);
		this.tokenConverter.validateTokenSignature(signedToken);
		log.info("validated token");
		try {
			log.info("seting signedToken on security Context");
			SecurityContextUtil.setSecurityContext(SignedJWT.parse(signedToken));
		} catch (ParseException e) {
			log.error("Erro when set signedToken in context -------> {}", e.getMessage());
			e.printStackTrace();
		}
		
		if(this.jWTConfiguration.getType().equalsIgnoreCase("signed")) {
			log.info("Add Authorization in -----> RequestContext.getCurrentContext().addZuulRequestHeader()");
			RequestContext.getCurrentContext().addZuulRequestHeader("Authorization", this.jWTConfiguration.getHeader().getPrefix() + signedToken);
		}
		
		filterChain.doFilter(request, response);
	}

}

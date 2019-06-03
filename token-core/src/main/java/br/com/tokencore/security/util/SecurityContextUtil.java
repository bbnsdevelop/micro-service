package br.com.tokencore.security.util;

import static java.util.stream.Collectors.toList;

import java.text.ParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import br.com.core.model.ApplicationUser;

public class SecurityContextUtil {
	private static final Logger log = LoggerFactory.getLogger(SecurityContextUtil.class);
	private SecurityContextUtil() {
		
	}
	
	public static void SetSecurityContext(SignedJWT signedJWT) {
		
		try {
			JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
			String username = jwtClaimsSet.getSubject();
			if(username == null) {
				throw new JOSEException("");
			}
			List<String> authorities = jwtClaimsSet.getStringListClaim("authorities");
			ApplicationUser user = new ApplicationUser();
			user.setId(jwtClaimsSet.getLongClaim("userId"));
			user.setUsername(username);
			user.setRole(String.join(",",authorities));
			UsernamePasswordAuthenticationToken auth  = new UsernamePasswordAuthenticationToken(user,null, createAuthorities(authorities));
			auth.setDetails(signedJWT.serialize());
			SecurityContextHolder.getContext().setAuthentication(auth);
		} catch (ParseException | JOSEException e) {
			log.error("Error setting security context - > {}", e.getCause());
			e.printStackTrace();
			SecurityContextHolder.clearContext();
		}
		
		
	}
	/*
	 * auth.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(toList())
	 * */
	
	private static List<SimpleGrantedAuthority> createAuthorities(List<String> authorities) {
		return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(toList());
		
	}

}

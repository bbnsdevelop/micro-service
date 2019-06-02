package br.com.auth.security.filter;

import static java.util.Collections.emptyList;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

import br.com.core.model.ApplicationUser;
import br.com.core.property.JWTConfiguration;
import br.com.tokencore.security.token.creator.TokenCreator;

public class JwtUsernameAndaPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private static final Logger log = LoggerFactory.getLogger(JwtUsernameAndaPasswordAuthenticationFilter.class);
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JWTConfiguration jwtConfiguration;
	
	@Autowired
	private TokenCreator tokenCreator;
	
	public JwtUsernameAndaPasswordAuthenticationFilter(AuthenticationManager authenticationManager,	JWTConfiguration jWTConfiguration, TokenCreator tokenCreator) {
		this.authenticationManager = authenticationManager;
		this.jwtConfiguration = jWTConfiguration;
		this.tokenCreator = tokenCreator;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
		log.info("Attempting authentication . ..");
		ApplicationUser applicationUser = null;
		try {
			applicationUser = new ObjectMapper().readValue(request.getInputStream(), ApplicationUser.class);
		} catch (IOException e) {
			log.error("Error ObjectMapper read value");
			e.printStackTrace();
		}
		if(applicationUser == null) {
			throw new UsernameNotFoundException("Unable to retrieve the username or password");
		}
		log.info("Creating the authentication object for the user '{}' and calling UserDetailsServiceImpl loadUserByUsername", applicationUser.getUsername());
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(applicationUser.getUsername(), applicationUser.getPassword(), emptyList());
		authenticationToken.setDetails(applicationUser);
		return this.authenticationManager.authenticate(authenticationToken);
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		log.info("Authentication was sucessful for the user '{}', generating JWE token", auth.getName());
		SignedJWT signedJWT = null;
		try {
			signedJWT = tokenCreator.createSignedJWT(auth);
		} catch (NoSuchAlgorithmException | JOSEException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String encryptToken = null;
		try {
			encryptToken = tokenCreator.encryptToken(signedJWT);
			log.info("Token generated sucessfully, adding it to the response header");
		} catch (JOSEException e) {
			e.printStackTrace();
			log.error("Error on encrypted Token in the response header");
		}
		if(encryptToken == null) {
			throw new RuntimeCryptoException("Erro on encrypted token");
		}
		response.addHeader("Access-Control-Expose-Headers", "XSRF-TOKEN," + this.jwtConfiguration.getHeader().getName());
		response.addHeader(this.jwtConfiguration.getHeader().getName(),this.jwtConfiguration.getHeader().getPrefix() + encryptToken);
	}
	
	
}

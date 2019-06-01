package br.com.auth.security.filter;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bouncycastle.crypto.RuntimeCryptoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import br.com.core.model.ApplicationUser;
import br.com.core.property.JWTConfiguration;

public class JwtUsernameAndaPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private static final Logger log = LoggerFactory.getLogger(JwtUsernameAndaPasswordAuthenticationFilter.class);
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JWTConfiguration jwtConfiguration;
	
	public JwtUsernameAndaPasswordAuthenticationFilter(AuthenticationManager authenticationManager,	JWTConfiguration jWTConfiguration) {
		this.authenticationManager = authenticationManager;
		this.jwtConfiguration = jWTConfiguration;
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
		SignedJWT signedJWT = createdSignedJWT(auth);
		String encryptToken = null;
		try {
			encryptToken = encryptToken(signedJWT);
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
	
	// criptografando token
	private SignedJWT createdSignedJWT(Authentication auth) {
		
		log.info("Starting to create the signed JWT");
		
		ApplicationUser applicationUser = (ApplicationUser) auth.getPrincipal();
		JWTClaimsSet jwtClaimsSet = createJWTClaimsSet(auth, applicationUser);
		
		KeyPair rsakeys = generateKeyPair();
		log.info("Building JWK from the RSA keys");
		
		JWK jwk = new RSAKey.Builder((RSAPublicKey)rsakeys.getPublic()).keyID(UUID.randomUUID().toString()).build();
		SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256)
				.jwk(jwk)
				.type(JOSEObjectType.JWT).build(), jwtClaimsSet);
		
		log.info("Signing the token with the private RSA key");
		RSASSASigner rsassaSigner = new RSASSASigner(rsakeys.getPrivate());
		try {
			signedJWT.sign(rsassaSigner);
		} catch (JOSEException e) {
			log.error("Error on Assigner token key");
		}		
		
		log.info("Serialized token '{}' ", signedJWT.serialize());
		return signedJWT;
		
	}
	private JWTClaimsSet createJWTClaimsSet(Authentication auth, ApplicationUser applicationUser ) {
		log.info("Starting to create the JWTClaimsSet Object for '{}' ", applicationUser);
		return new JWTClaimsSet.Builder()
					.subject(applicationUser.getUsername())
					 .claim("authorities", auth.getAuthorities()
						 .stream()
						 .map(GrantedAuthority::getAuthority)
						 .collect(toList()))
				 		 .issuer("https://github.com/bbnsdevelop")
				 		 .issueTime(new Date())
				 		 .expirationTime(new Date(System.currentTimeMillis() +(this.jwtConfiguration.getExpiration() * 1000)))
				 		 .build();
	}
	private KeyPair generateKeyPair() {
		log.info("Generating RSA 2048 bits keys");
		KeyPairGenerator generator = null;
		try {
			generator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		generator.initialize(2048);
		return generator.generateKeyPair();
	}
	
	private String encryptToken(SignedJWT signedJWT) throws JOSEException {
		log.info("Starting the encryptToken method");
		DirectEncrypter directEncrypter = new DirectEncrypter(this.jwtConfiguration.getPrivateKey().getBytes());
		JWEObject jweObject =  new JWEObject(new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256).contentType("JWT")
				.build(), new Payload(signedJWT));
		
		log.info("Encrypting token with system's private key");
		jweObject.encrypt(directEncrypter);
		log.info("Token encrypted");
		
		return jweObject.serialize();
	}
}

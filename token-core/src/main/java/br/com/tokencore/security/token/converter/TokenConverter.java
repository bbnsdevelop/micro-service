package br.com.tokencore.security.token.converter;

import java.nio.file.AccessDeniedException;
import java.text.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;

import br.com.core.property.JWTConfiguration;

@Service
public class TokenConverter {
	private static final Logger log = LoggerFactory.getLogger(TokenConverter.class);
	
	@Autowired
	private JWTConfiguration jwtConfiguration;
	
	public String decryptToken(String encryptToken) {
		log.info("Decrypting token");
		JWEObject jweObject = null;
		DirectDecrypter directDecrypter = null;
		try {
			jweObject = JWEObject.parse(encryptToken);
			directDecrypter = new DirectDecrypter(this.jwtConfiguration.getPrivateKey().getBytes());
			jweObject.decrypt(directDecrypter);
		} catch (ParseException | JOSEException e) {
			log.error("Error on decrypt token");
			e.printStackTrace();
		}
		log.info("Token decrypted, returning signed token ....");
	
		return jweObject.getPayload().toSignedJWT().serialize();		
	}
	
	public void validateTokenSignature(String signedToken) {
		log.info("Starting method to validate token signature ....");
		
		SignedJWT signedJWT = null;
		try {
			signedJWT = SignedJWT.parse(signedToken);
		} catch (ParseException e) {
			log.error("Error on parse signed token -> {}", e.getCause());
			e.printStackTrace();
		}
		log.info("Token Parsed! Retrieving public key from signe token ....");
		RSAKey publicKey = null;
		try {
			publicKey = RSAKey.parse(signedJWT.getHeader().getJWK().toJSONObject());
		} catch (ParseException e) {
			log.error("Error on parse public key --> {}", e.getCause());
			e.printStackTrace();
		}
		try {
			if(!signedJWT.verify(new RSASSAVerifier(publicKey))) {
				throw new AccessDeniedException("Invalid token signature!");
			}
		} catch (AccessDeniedException | JOSEException e) {
			e.printStackTrace();
		}
		
		log.info("The token has a valid signature");
		
	}

}

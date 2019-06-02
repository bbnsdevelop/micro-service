package br.com.token.security.token.creator;

import static java.util.stream.Collectors.toList;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

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

@Service
public class TokenCreator {

	private static final Logger log = LoggerFactory.getLogger(TokenCreator.class);

	private JWTConfiguration jwtConfiguration;
	

	@Autowired
	public TokenCreator(JWTConfiguration jwtConfiguration) {
		this.jwtConfiguration = jwtConfiguration;
	}

	// criptografando token
	public SignedJWT createdSignedJWT(Authentication auth) {

		log.info("Starting to create the signed JWT");

		ApplicationUser applicationUser = (ApplicationUser) auth.getPrincipal();
		JWTClaimsSet jwtClaimsSet = createJWTClaimsSet(auth, applicationUser);

		KeyPair rsakeys = generateKeyPair();
		log.info("Building JWK from the RSA keys");

		JWK jwk = new RSAKey.Builder((RSAPublicKey) rsakeys.getPublic()).keyID(UUID.randomUUID().toString()).build();
		SignedJWT signedJWT = new SignedJWT(
				new JWSHeader.Builder(JWSAlgorithm.RS256).jwk(jwk).type(JOSEObjectType.JWT).build(), jwtClaimsSet);

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

	private JWTClaimsSet createJWTClaimsSet(Authentication auth, ApplicationUser applicationUser) {
		log.info("Starting to create the JWTClaimsSet Object for '{}' ", applicationUser);
		return new JWTClaimsSet.Builder().subject(applicationUser.getUsername())
				.claim("authorities",
						auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(toList()))
				.issuer("https://github.com/bbnsdevelop").issueTime(new Date())
				.expirationTime(new Date(System.currentTimeMillis() + (this.jwtConfiguration.getExpiration() * 1000)))
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

	public String encryptToken(SignedJWT signedJWT) throws JOSEException {
		log.info("Starting the encryptToken method");
		DirectEncrypter directEncrypter = new DirectEncrypter(this.jwtConfiguration.getPrivateKey().getBytes());
		JWEObject jweObject = new JWEObject(
				new JWEHeader.Builder(JWEAlgorithm.DIR, EncryptionMethod.A128CBC_HS256).contentType("JWT").build(),
				new Payload(signedJWT));

		log.info("Encrypting token with system's private key");
		jweObject.encrypt(directEncrypter);
		log.info("Token encrypted");

		return jweObject.serialize();
	}

}
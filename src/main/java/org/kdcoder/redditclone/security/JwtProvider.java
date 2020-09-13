package org.kdcoder.redditclone.security;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.kdcoder.redditclone.exception.SpringRedditException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtProvider {
	
	private KeyStore keyStore;
	@Value("${jwt.expiration.time}")
	private Long jwtExpirationInMillis;
	
	@PostConstruct
	public void init() {
		try {
			keyStore = KeyStore.getInstance("JKS");
			InputStream resourceStream = getClass().getResourceAsStream("/reditclone.jsk");
			keyStore.load(resourceStream, "kdcoder".toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			throw new SpringRedditException("Exception while loading key form jks!!");
		}
	}
	
	public String generatToken(Authentication authentication) {
		User user = (User) authentication.getPrincipal();
		return Jwts.builder()
				.setSubject(user.getUsername())
				.setIssuedAt(Date.from(Instant.now()))
				.signWith(getPrivateKey())
				.setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
				.compact();
	}
	
	public String generatTokenUsingUsername(String username) {
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(Date.from(Instant.now()))
				.signWith(getPrivateKey())
				.setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
				.compact();
	}

	private PrivateKey getPrivateKey() {
		try {
			return (PrivateKey) keyStore.getKey("reditclone", "kdcoder".toCharArray());
		} catch (KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException e) {
			throw new SpringRedditException("Exception while getting private key !!!");
		}
	}
	
	public boolean validateJwt(String token) {
		Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(token);
		return true;
	}

	private PublicKey getPublicKey() {
		try {
			return keyStore.getCertificate("reditclone").getPublicKey();
		} catch (KeyStoreException e) {
			throw new SpringRedditException("Exception while getting public key !!!");
		}
		
	}
	
	public String getUsernameFromJwt(String token) {
		Claims claim = Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(token).getBody();
		return claim.getSubject();
	}
	
	public Long getJwtExpirationInMillis() {
		return jwtExpirationInMillis;
	}
	
}

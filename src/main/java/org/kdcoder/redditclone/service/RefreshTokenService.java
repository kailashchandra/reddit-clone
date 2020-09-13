package org.kdcoder.redditclone.service;

import java.time.Instant;
import java.util.UUID;

import org.kdcoder.redditclone.exception.SpringRedditException;
import org.kdcoder.redditclone.model.RefreshToken;
import org.kdcoder.redditclone.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
@Transactional
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;
	
	public RefreshToken generateRefreshToken() {
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setToken(UUID.randomUUID().toString());
		refreshToken.setCreatedDate(Instant.now());
		
		return refreshTokenRepository.save(refreshToken); 
	}
	
	void validateRefreshToken(String token) {
		refreshTokenRepository.findByToken(token).orElseThrow(() ->
		 new SpringRedditException("Token not found for :"+token));
	}
	
	public void deleteRefreshToken(String token) {
		refreshTokenRepository.deleteByToken(token);
	}
}

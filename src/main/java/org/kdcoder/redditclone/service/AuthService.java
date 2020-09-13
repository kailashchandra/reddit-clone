package org.kdcoder.redditclone.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.kdcoder.redditclone.dto.AuthenticationResponse;
import org.kdcoder.redditclone.dto.LoginRequest;
import org.kdcoder.redditclone.dto.RefreshTokenRequest;
import org.kdcoder.redditclone.dto.RegisterRequest;
import org.kdcoder.redditclone.exception.SpringRedditException;
import org.kdcoder.redditclone.model.NotificationEmail;
import org.kdcoder.redditclone.model.User;
import org.kdcoder.redditclone.model.VerificationToken;
import org.kdcoder.redditclone.repository.UserRepository;
import org.kdcoder.redditclone.repository.VerificationTokenRepository;
import org.kdcoder.redditclone.security.JwtProvider;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final VerificationTokenRepository verificationTokenRepository;
	private final MailService MailService;
	private final AuthenticationManager authenticationManager;
	private final JwtProvider jwtProvider;
	private final RefreshTokenService refreshTokenService;
	
	@Transactional
	public void signup(RegisterRequest registerRequest) {
		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setEmail(registerRequest.getEmail());
		user.setCreated(Instant.now());
		user.setEnabled(false);
		
		userRepository.save(user);
		
		String token = generateVerificationToken(user);
		
		MailService.sendMail(new NotificationEmail("Activation of account",
				user.getEmail(), "Thank you for signing up to Spring Reddit, " +
		                "please click on the below url to activate your account : " +
//		                "http://localhost:8080/api/auth/accountVerification/" + token));
						"https://kdcoder-reddit-clone.herokuapp.com/api/auth/accountVerification/" + token));
	}

	private String generateVerificationToken(User user) {
		String token = UUID.randomUUID().toString();
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(token);
		verificationToken.setUser(user);
		
		verificationTokenRepository.save(verificationToken);
		return token;
		
	}

	public void verifyAccount(String token) {
		Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
		fetchUserAndEnable(verificationToken.orElseThrow(() -> 
			new SpringRedditException("Invalid Token")));
	}
	
	@Transactional
	private void fetchUserAndEnable(VerificationToken verificationToken) {
		String username = verificationToken.getUser().getUsername();
		User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User not found with name : "+username));
		user.setEnabled(true);
		userRepository.save(user);
	}

	public AuthenticationResponse login(LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtProvider.generatToken(authentication);
//		return new AuthenticationResponse(token, loginRequest.getUsername());
		return AuthenticationResponse.builder()
									.authenticationToken(token)
									.refreshToken(refreshTokenService.generateRefreshToken().getToken())
									.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
									.username(loginRequest.getUsername())
									.build();
	}

	@Transactional(readOnly = true)
	public User getCurrentUser() {
		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return userRepository.findByUsername(user.getUsername()).orElseThrow(() -> 
						new SpringRedditException("Current user not found!!"));
	}

	public AuthenticationResponse refreshToken(@Valid RefreshTokenRequest refreshTokenReq) {
		refreshTokenService.validateRefreshToken(refreshTokenReq.getRefreshToken());
		String token = jwtProvider.generatTokenUsingUsername(refreshTokenReq.getUsername());
		return AuthenticationResponse.builder()
				.authenticationToken(token)
				.refreshToken(refreshTokenReq.getRefreshToken())
				.expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
				.username(refreshTokenReq.getUsername())
				.build();
	}

	public boolean isLoggedIn() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return !(authentication instanceof AnonymousAuthenticationToken) &&
		authentication.isAuthenticated();
	}
}

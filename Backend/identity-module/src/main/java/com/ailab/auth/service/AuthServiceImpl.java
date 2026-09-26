package com.ailab.auth.service;

import com.ailab.auth.api.AuthDtos;
import com.ailab.auth.security.AccessTokenIssuer;
import com.ailab.auth.security.AuthenticationEligibilityPolicy;
import com.ailab.auth.token.RefreshTokenOperations;
import com.ailab.auth.token.RefreshTokenService;
import com.ailab.auth.verification.EmailVerificationService;
import com.ailab.common.mail.EmailDeliveryService;
import com.ailab.user.domain.User;
import com.ailab.user.service.UserAccountService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserAccountService users;
    private final AuthenticationManager authenticationManager;
    private final AccessTokenIssuer jwtService;
    private final RefreshTokenOperations refreshTokens;
    private final EmailVerificationService verificationService;
    private final EmailDeliveryService emailDeliveryService;
    private final AuthenticationEligibilityPolicy eligibilityPolicy;

    public AuthServiceImpl(UserAccountService users,
                           AuthenticationManager authenticationManager,
                           AccessTokenIssuer jwtService,
                           RefreshTokenOperations refreshTokens,
                           EmailVerificationService verificationService,
                           EmailDeliveryService emailDeliveryService,
                           AuthenticationEligibilityPolicy eligibilityPolicy) {
        this.users = users;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokens = refreshTokens;
        this.verificationService = verificationService;
        this.emailDeliveryService = emailDeliveryService;
        this.eligibilityPolicy = eligibilityPolicy;
    }

    @Override
    public AuthDtos.RegisterResponse register(AuthDtos.RegisterRequest request) {
        User user = users.register(request.username(), request.email(), request.password());
        var challenge = verificationService.createRegisterChallenge(user.getId(), Duration.ofMinutes(30));
        emailDeliveryService.sendVerificationEmail(user.getEmail(), user.getUsername(), challenge.rawToken(), Duration.ofMinutes(30));
        return new AuthDtos.RegisterResponse(user.getId(), user.getUsername(), user.getEmail(), true);
    }

    @Override
    public AuthDtos.AuthenticationResult login(AuthDtos.LoginRequest request) {
        User user = users.findByEmail(request.email());
        if (!user.hasPassword()) {
            throw new BadCredentialsException("Account does not have a password configured");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), request.password()));
        eligibilityPolicy.assertEligibleForPasswordLogin(user);
        return tokensFor(user, refreshTokens.issue(user.getId()).rawToken());
    }

    @Override
    public AuthDtos.AuthenticationResult refresh(String token) {
        RefreshTokenService.IssuedToken rotated = refreshTokens.rotate(token);
        User user = users.findById(rotated.userId());
        eligibilityPolicy.assertEligibleForRefresh(user);
        return tokensFor(user, rotated.rawToken());
    }

    @Override
    public void logout(String token) {
        refreshTokens.revoke(token);
    }

    @Override
    public void logoutAll(String userId) {
        if (userId != null && !userId.isBlank()) {
            refreshTokens.revokeAll(userId);
            users.invalidateSessions(userId);
        }
    }

    @Override
    public AuthDtos.VerifyEmailResponse verifyEmail(AuthDtos.VerifyEmailRequest request) {
        var result = verificationService.verifyToken(request.token());
        return new AuthDtos.VerifyEmailResponse(result.success(), result.message());
    }

    @Override
    public void resendVerification(AuthDtos.ResendVerificationRequest request) {
        verificationService.resendVerification(request.email());
    }

    private AuthDtos.AuthenticationResult tokensFor(User user, String refreshToken) {
        return new AuthDtos.AuthenticationResult(
                new AuthDtos.TokenResponse(jwtService.issue(user), jwtService.expiresInSeconds(), "Bearer"),
                refreshToken);
    }
}
package com.ailab.auth.service;

import com.ailab.auth.api.AuthDtos;
import com.ailab.auth.security.AccessTokenIssuer;
import com.ailab.auth.token.RefreshTokenOperations;
import com.ailab.auth.token.RefreshTokenService;
import com.ailab.user.domain.User;
import com.ailab.user.service.UserAccountService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserAccountService users;
    private final AuthenticationManager authenticationManager;
    private final AccessTokenIssuer jwtService;
    private final RefreshTokenOperations refreshTokens;

    public AuthServiceImpl(UserAccountService users, AuthenticationManager authenticationManager,
                           AccessTokenIssuer jwtService, RefreshTokenOperations refreshTokens) {
        this.users = users;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokens = refreshTokens;
    }

    @Override
    public AuthDtos.RegisterResponse register(AuthDtos.RegisterRequest request) {
        User user = users.register(request.username(), request.email(), request.password());
        return new AuthDtos.RegisterResponse(user.getId(), user.getUsername(), user.getEmail());
    }

    @Override
    public AuthDtos.AuthenticationResult login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        User user = users.findByEmail(request.email());
        return tokensFor(user, refreshTokens.issue(user.getId()).rawToken());
    }

    @Override
    public AuthDtos.AuthenticationResult refresh(String token) {
        RefreshTokenService.IssuedToken rotated = refreshTokens.rotate(token);
        User user = users.findById(rotated.userId());
        return tokensFor(user, rotated.rawToken());
    }

    @Override
    public void logout(String token) {
        refreshTokens.revoke(token).ifPresent(users::invalidateSessions);
    }

    private AuthDtos.AuthenticationResult tokensFor(User user, String refreshToken) {
        return new AuthDtos.AuthenticationResult(
                new AuthDtos.TokenResponse(jwtService.issue(user), refreshToken, jwtService.expiresInSeconds(), "Bearer"),
                refreshToken);
    }
}

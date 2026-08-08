package com.ailab.auth.controller;

import com.ailab.auth.api.AuthDtos;
import com.ailab.auth.service.AuthService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService service;
    @Value("${app.security.refresh-cookie-name:refresh_token}")
    private String refreshCookieName;
    @Value("${app.security.refresh-cookie-secure:false}")
    private boolean refreshCookieSecure;
    @Value("${app.security.refresh-token-ttl}")
    private Duration refreshTokenTtl;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthDtos.RegisterResponse register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        return service.register(request);
    }

    @PostMapping("/login")
    public AuthDtos.TokenResponse login(@Valid @RequestBody AuthDtos.LoginRequest request, HttpServletResponse response) {
        return writeTokenResponse(service.login(request), response);
    }

    @PostMapping("/refresh")
    public AuthDtos.TokenResponse refresh(
            HttpServletRequest httpRequest,
            @RequestBody(required = false) AuthDtos.LogoutRequest request,
            HttpServletResponse response) {
        String cookieToken = refreshCookie(httpRequest);
        String token = cookieToken != null ? cookieToken : request == null ? null : request.refreshToken();
        if (token == null || token.isBlank()) throw new IllegalArgumentException("Refresh token is required");
        return writeTokenResponse(service.refresh(token), response);
    }

    @PostMapping("/logout")
    public AuthDtos.SuccessResponse logout(
            HttpServletRequest httpRequest,
            @RequestBody(required = false) AuthDtos.LogoutRequest request,
            HttpServletResponse response) {
        String cookieToken = refreshCookie(httpRequest);
        String token = cookieToken != null ? cookieToken : request == null ? null : request.refreshToken();
        if (token != null && !token.isBlank()) service.logout(token);
        clearRefreshCookie(response);
        return new AuthDtos.SuccessResponse(true);
    }

    private AuthDtos.TokenResponse writeTokenResponse(AuthDtos.AuthenticationResult result, HttpServletResponse response) {
        response.addHeader("Set-Cookie", refreshCookie(result.refreshToken()).toString());
        return result.response();
    }

    private ResponseCookie refreshCookie(String token) {
        return ResponseCookie.from(refreshCookieName, token).httpOnly(true).secure(refreshCookieSecure)
                .sameSite("Strict").path("/api/v1/auth").maxAge(refreshTokenTtl).build();
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        response.addHeader("Set-Cookie", ResponseCookie.from(refreshCookieName, "").httpOnly(true)
                .secure(refreshCookieSecure).sameSite("Strict").path("/api/v1/auth").maxAge(Duration.ZERO).build().toString());
    }

    private String refreshCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (var cookie : request.getCookies()) {
            if (refreshCookieName.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }
}

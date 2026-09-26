package com.ailab.auth.controller;

import com.ailab.auth.api.AuthDtos;
import com.ailab.auth.service.AuthService;
import com.ailab.auth.token.InvalidRefreshTokenException;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService service;
    @Value("${app.security.refresh-cookie-name:refresh_token}")
    private String refreshCookieName;
    @Value("${app.security.refresh-cookie-secure:false}")
    private boolean refreshCookieSecure;
    @Value("${app.security.refresh-cookie-same-site:Strict}")
    private String refreshCookieSameSite;
    @Value("${app.security.refresh-token-ttl}")
    private Duration refreshTokenTtl;

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private List<String> allowedOrigins;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthDtos.RegisterResponse register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        return service.register(request);
    }

    @PostMapping("/email/verify")
    public AuthDtos.VerifyEmailResponse verifyEmail(@Valid @RequestBody AuthDtos.VerifyEmailRequest request) {
        return service.verifyEmail(request);
    }

    @PostMapping("/email/verification/resend")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void resendVerification(@Valid @RequestBody AuthDtos.ResendVerificationRequest request) {
        service.resendVerification(request);
    }

    @GetMapping("/oauth/google/link")
    public java.util.Map<String, String> getGoogleLinkUrl(
            @org.springframework.security.core.annotation.AuthenticationPrincipal String userId,
            HttpServletResponse response) {
        if (userId == null || userId.isBlank()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED: You must be logged in to link an OAuth provider");
        }
        com.ailab.auth.oauth.CookieUtils.addCookie(response,
                com.ailab.auth.oauth.HttpCookieOAuth2AuthorizationRequestRepository.LINK_USER_ID_COOKIE_NAME,
                userId, 300, refreshCookieSecure, "Lax");
        com.ailab.auth.oauth.CookieUtils.addCookie(response,
                com.ailab.auth.oauth.HttpCookieOAuth2AuthorizationRequestRepository.ACTION_COOKIE_NAME,
                "link", 300, refreshCookieSecure, "Lax");
        return java.util.Map.of("url", "/oauth2/authorization/google?action=link");
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
        if (cookieToken != null) {
            validateCookieOrigin(httpRequest);
        }
        String token = cookieToken != null ? cookieToken : request == null ? null : request.refreshToken();
        if (token == null || token.isBlank()) throw new InvalidRefreshTokenException("Refresh token is required");
        return writeTokenResponse(service.refresh(token), response);
    }

    @PostMapping("/logout")
    public AuthDtos.SuccessResponse logout(
            HttpServletRequest httpRequest,
            @RequestBody(required = false) AuthDtos.LogoutRequest request,
            HttpServletResponse response) {
        String cookieToken = refreshCookie(httpRequest);
        if (cookieToken != null) {
            validateCookieOrigin(httpRequest);
        }
        String token = cookieToken != null ? cookieToken : request == null ? null : request.refreshToken();
        if (token != null && !token.isBlank()) service.logout(token);
        clearRefreshCookie(response);
        return new AuthDtos.SuccessResponse(true);
    }

    @PostMapping("/logout-all")
    public AuthDtos.SuccessResponse logoutAll(
            HttpServletRequest httpRequest,
            @RequestBody(required = false) AuthDtos.LogoutRequest request,
            org.springframework.security.core.Authentication authentication,
            HttpServletResponse response) {
        String cookieToken = refreshCookie(httpRequest);
        if (cookieToken != null) {
            validateCookieOrigin(httpRequest);
        }
        String userId = null;
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            userId = authentication.getName();
        }
        if (userId != null) {
            service.logoutAll(userId);
        } else {
            String token = cookieToken != null ? cookieToken : request == null ? null : request.refreshToken();
            if (token != null && !token.isBlank()) {
                service.logout(token);
            }
        }
        clearRefreshCookie(response);
        return new AuthDtos.SuccessResponse(true);
    }

    private void validateCookieOrigin(HttpServletRequest request) {
        if (allowedOrigins == null || allowedOrigins.isEmpty()) return;
        String origin = request.getHeader("Origin");
        if (origin == null || origin.isBlank()) {
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isBlank()) {
                try {
                    java.net.URI uri = java.net.URI.create(referer);
                    origin = uri.getScheme() + "://" + uri.getAuthority();
                } catch (Exception ignored) {}
            }
        }
        if (origin != null && !origin.isBlank()) {
            String cleanOrigin = origin.trim().replaceAll("/+$", "");
            boolean match = allowedOrigins.stream().anyMatch(o -> o.trim().replaceAll("/+$", "").equalsIgnoreCase(cleanOrigin));
            if (!match) {
                throw new org.springframework.web.server.ResponseStatusException(
                        HttpStatus.FORBIDDEN, "CSRF_DETECTED: Cross-origin request not permitted for cookie session");
            }
        }
    }

    private AuthDtos.TokenResponse writeTokenResponse(AuthDtos.AuthenticationResult result, HttpServletResponse response) {
        response.addHeader("Set-Cookie", refreshCookie(result.refreshToken()).toString());
        return result.response();
    }

    private ResponseCookie refreshCookie(String token) {
        return ResponseCookie.from(refreshCookieName, token).httpOnly(true).secure(refreshCookieSecure)
                .sameSite(refreshCookieSameSite).path("/api/v1/auth").maxAge(refreshTokenTtl).build();
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        response.addHeader("Set-Cookie", ResponseCookie.from(refreshCookieName, "").httpOnly(true)
                .secure(refreshCookieSecure).sameSite(refreshCookieSameSite).path("/api/v1/auth").maxAge(Duration.ZERO).build().toString());
    }

    private String refreshCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (var cookie : request.getCookies()) {
            if (refreshCookieName.equals(cookie.getName())) return cookie.getValue();
        }
        return null;
    }
}

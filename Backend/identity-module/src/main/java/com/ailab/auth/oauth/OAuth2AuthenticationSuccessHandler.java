package com.ailab.auth.oauth;

import com.ailab.auth.token.RefreshTokenOperations;
import com.ailab.auth.token.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final GoogleOAuthService googleOAuthService;
    private final RefreshTokenOperations refreshTokenService;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    @Value("${app.mail.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.security.refresh-cookie-name:refresh_token}")
    private String refreshCookieName;

    @Value("${app.security.refresh-cookie-secure:false}")
    private boolean refreshCookieSecure;

    @Value("${app.security.refresh-cookie-same-site:Strict}")
    private String refreshCookieSameSite;

    public OAuth2AuthenticationSuccessHandler(GoogleOAuthService googleOAuthService,
                                              RefreshTokenOperations refreshTokenService,
                                              HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository) {
        this.googleOAuthService = googleOAuthService;
        this.refreshTokenService = refreshTokenService;
        this.authorizationRequestRepository = authorizationRequestRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String subject = oauthUser.getAttribute("sub");
        if (subject == null && oauthUser instanceof OidcUser oidcUser) {
            subject = oidcUser.getSubject();
        }
        if (subject == null) {
            subject = oauthUser.getName();
        }

        String email = oauthUser.getAttribute("email");
        Object emailVerifiedObj = oauthUser.getAttribute("email_verified");
        boolean emailVerified = false;
        if (emailVerifiedObj instanceof Boolean b) {
            emailVerified = b;
        } else if (emailVerifiedObj instanceof String s) {
            emailVerified = Boolean.parseBoolean(s);
        }

        String name = oauthUser.getAttribute("name");
        String picture = oauthUser.getAttribute("picture");

        OAuthUserData userData = new OAuthUserData("google", subject, email, emailVerified, name, picture);

        String linkUserId = CookieUtils.getCookieValue(request, HttpCookieOAuth2AuthorizationRequestRepository.LINK_USER_ID_COOKIE_NAME).orElse(null);

        try {
            GoogleOAuthService.OAuthProcessResult result = googleOAuthService.processOAuthLogin(userData, linkUserId);

            // Issue refresh token
            RefreshTokenService.IssuedToken issuedToken = refreshTokenService.issue(result.user().getId());

            // Set refresh_token cookie (30 days) with path /api/v1/auth
            org.springframework.http.ResponseCookie refreshCookie = org.springframework.http.ResponseCookie.from(refreshCookieName, issuedToken.rawToken())
                    .path("/api/v1/auth")
                    .httpOnly(true)
                    .secure(refreshCookieSecure)
                    .sameSite(refreshCookieSameSite)
                    .maxAge(java.time.Duration.ofDays(30))
                    .build();
            response.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, refreshCookie.toString());

            // Clean up oauth request cookies
            authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

            String targetUrl;
            if (result.redirectTarget() != null) {
                targetUrl = frontendUrl + result.redirectTarget();
            } else {
                targetUrl = frontendUrl + "/auth/callback";
            }

            response.sendRedirect(targetUrl);
        } catch (Exception ex) {
            authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
            String errorCode = ex.getMessage();
            String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/auth/callback")
                    .queryParam("error", URLEncoder.encode(errorCode != null ? errorCode : "oauth_error", StandardCharsets.UTF_8))
                    .build().toUriString();
            response.sendRedirect(redirectUrl);
        }
    }
}
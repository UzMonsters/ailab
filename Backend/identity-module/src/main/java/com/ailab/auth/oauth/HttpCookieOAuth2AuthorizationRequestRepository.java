package com.ailab.auth.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Component
public class HttpCookieOAuth2AuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "oauth2_redirect_uri";
    public static final String ACTION_COOKIE_NAME = "oauth2_action";
    public static final String LINK_USER_ID_COOKIE_NAME = "oauth2_link_user_id";
    private static final int COOKIE_EXPIRE_SECONDS = 180;

    @Value("${app.security.refresh-cookie-secure:false}")
    private boolean secure;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CookieUtils.getCookieValue(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(v -> CookieUtils.deserialize(v, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response);
            return;
        }

        CookieUtils.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                CookieUtils.serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS, secure, "Lax");

        String redirectUriAfterLogin = request.getParameter("redirect_uri");
        if (redirectUriAfterLogin != null && !redirectUriAfterLogin.isBlank()) {
            CookieUtils.addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin, COOKIE_EXPIRE_SECONDS, secure, "Lax");
        }

        String action = request.getParameter("action");
        if (action != null && !action.isBlank()) {
            CookieUtils.addCookie(response, ACTION_COOKIE_NAME, action, COOKIE_EXPIRE_SECONDS, secure, "Lax");
        }

        String linkUserId = request.getParameter("link_user_id");
        if (linkUserId != null && !linkUserId.isBlank()) {
            CookieUtils.addCookie(response, LINK_USER_ID_COOKIE_NAME, linkUserId, COOKIE_EXPIRE_SECONDS, secure, "Lax");
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
        CookieUtils.deleteCookie(request, response, ACTION_COOKIE_NAME);
        CookieUtils.deleteCookie(request, response, LINK_USER_ID_COOKIE_NAME);
    }
}
package com.ailab.auth.oauth;

import com.ailab.user.domain.User;

public interface GoogleOAuthService {

    record OAuthProcessResult(User user, boolean isNewUser, boolean isLinked, String redirectTarget) {}

    OAuthProcessResult processOAuthLogin(OAuthUserData userData, String linkUserId);
}
package com.ailab.auth.security;

import com.ailab.user.domain.User;

public interface AccessTokenIssuer {
    String issue(User user);
    long expiresInSeconds();
}

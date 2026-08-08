package com.ailab.auth.token;

import java.util.Optional;

public interface RefreshTokenOperations {
    RefreshTokenService.IssuedToken issue(String userId);

    RefreshTokenService.IssuedToken rotate(String rawToken);

    Optional<String> revoke(String rawToken);
}

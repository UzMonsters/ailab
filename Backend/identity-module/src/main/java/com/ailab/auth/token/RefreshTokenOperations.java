package com.ailab.auth.token;

import com.ailab.user.api.UserDtos;

import java.util.Optional;

public interface RefreshTokenOperations {
    RefreshTokenService.IssuedToken issue(String userId);

    RefreshTokenService.IssuedToken rotate(String rawToken);

    Optional<String> revoke(String rawToken);

    void revokeAll(String userId);

    UserDtos.SessionListResponse getActiveSessions(String userId, int page, int size);

    void revokeSession(String userId, String sessionId);
}

package com.ailab.user.service;

public interface ReAuthTokenOperations {
    void checkRateLimit(String userId);

    void recordFailedAttempt(String userId);

    void resetFailedAttempts(String userId);

    ReAuthTokenService.IssuedReAuthToken issueToken(String userId);

    boolean validateAndConsumeToken(String userId, String token);

    boolean isValidToken(String userId, String token);
}

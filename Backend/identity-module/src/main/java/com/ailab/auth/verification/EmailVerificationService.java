package com.ailab.auth.verification;

import java.time.Duration;

public interface EmailVerificationService {

    IssuedVerificationChallenge createRegisterChallenge(String userId, Duration ttl);

    IssuedVerificationChallenge createEmailChangeChallenge(String userId, String newEmail, Duration ttl);

    VerificationResult verifyToken(String rawToken);

    void resendVerification(String email);

    record IssuedVerificationChallenge(String rawToken, String tokenHash, java.time.Instant expiresAt) { }
    record VerificationResult(boolean success, String purpose, String message) { }
}
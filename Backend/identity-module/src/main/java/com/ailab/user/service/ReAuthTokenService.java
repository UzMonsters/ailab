package com.ailab.user.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ReAuthTokenService implements ReAuthTokenOperations {
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(5);
    private static final Duration TOKEN_TTL = Duration.ofMinutes(10);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final Map<String, TokenEntry> activeTokens = new ConcurrentHashMap<>();
    private final Map<String, RateLimitEntry> rateLimitMap = new ConcurrentHashMap<>();

    public void checkRateLimit(String userId) {
        RateLimitEntry entry = rateLimitMap.get(userId);
        if (entry != null && entry.isLocked()) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "REAUTH_RATE_LIMITED: Too many failed re-authentication attempts. Please try again later.");
        }
    }

    public void recordFailedAttempt(String userId) {
        rateLimitMap.compute(userId, (k, v) -> {
            Instant now = Instant.now();
            if (v == null || v.isExpired(now)) {
                return new RateLimitEntry(1, now.plus(LOCKOUT_DURATION));
            }
            int newCount = v.count + 1;
            Instant lockUntil = newCount >= MAX_FAILED_ATTEMPTS ? now.plus(LOCKOUT_DURATION) : v.lockUntil;
            return new RateLimitEntry(newCount, lockUntil);
        });
    }

    public void resetFailedAttempts(String userId) {
        rateLimitMap.remove(userId);
    }

    public IssuedReAuthToken issueToken(String userId) {
        resetFailedAttempts(userId);
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        String token = "reauth_" + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        Instant expiresAt = Instant.now().plus(TOKEN_TTL);
        activeTokens.put(token, new TokenEntry(userId, expiresAt));
        return new IssuedReAuthToken(token, expiresAt);
    }

    public boolean validateAndConsumeToken(String userId, String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        TokenEntry entry = activeTokens.get(token);
        if (entry == null) {
            return false;
        }
        if (entry.isExpired(Instant.now())) {
            activeTokens.remove(token);
            return false;
        }
        if (!entry.userId.equals(userId)) {
            return false;
        }
        activeTokens.remove(token);
        return true;
    }

    public boolean isValidToken(String userId, String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        TokenEntry entry = activeTokens.get(token);
        if (entry == null || entry.isExpired(Instant.now())) {
            return false;
        }
        return entry.userId.equals(userId);
    }

    public record IssuedReAuthToken(String token, Instant expiresAt) {
    }

    private record TokenEntry(String userId, Instant expiresAt) {
        boolean isExpired(Instant now) {
            return now.isAfter(expiresAt);
        }
    }

    private record RateLimitEntry(int count, Instant lockUntil) {
        boolean isLocked() {
            return count >= MAX_FAILED_ATTEMPTS && Instant.now().isBefore(lockUntil);
        }

        boolean isExpired(Instant now) {
            return now.isAfter(lockUntil);
        }
    }
}

package com.ailab.auth.security;

import com.ailab.user.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Component
public class AuthenticationEligibilityPolicy {

    public void assertEligibleForStatus(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED: User not found");
        }
        String status = user.getStatus();
        if ("BLOCKED".equalsIgnoreCase(status) || "DEACTIVATED".equalsIgnoreCase(status) || "DELETION_SCHEDULED".equalsIgnoreCase(status)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ACCOUNT_BLOCKED: Account is blocked or deactivated");
        }
        if (user.getBlockedUntil() != null && user.getBlockedUntil().isAfter(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "ACCOUNT_BLOCKED: Account is temporarily suspended");
        }
    }

    public void assertEligibleForPasswordLogin(User user) {
        assertEligibleForStatus(user);
        if (!user.isEmailVerified()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "EMAIL_NOT_VERIFIED: Email address is not verified");
        }
    }

    public void assertEligibleForRefresh(User user) {
        assertEligibleForStatus(user);
    }

    public void assertEligibleForJwt(User user) {
        assertEligibleForStatus(user);
    }

    public void assertEligibleForWs(User user) {
        assertEligibleForStatus(user);
    }

    public void assertEligibleForOAuth(User user) {
        assertEligibleForStatus(user);
    }
}
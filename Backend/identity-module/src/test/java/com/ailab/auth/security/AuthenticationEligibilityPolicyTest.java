package com.ailab.auth.security;

import com.ailab.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthenticationEligibilityPolicyTest {

    private AuthenticationEligibilityPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new AuthenticationEligibilityPolicy();
    }

    @Test
    void allowsActiveAndVerifiedUserForPasswordLogin() {
        User user = new User("alice", "alice@example.com", "hash");
        user.markEmailVerified();

        assertThatCode(() -> policy.assertEligibleForPasswordLogin(user))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsUnverifiedUserForPasswordLogin() {
        User user = new User("alice", "alice@example.com", "hash");
        // emailVerifiedAt is null by default

        assertThatThrownBy(() -> policy.assertEligibleForPasswordLogin(user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void rejectsBlockedUserForPasswordLogin() {
        User user = new User("alice", "alice@example.com", "hash");
        user.markEmailVerified();
        user.block("Violation of TOS", null);

        assertThatThrownBy(() -> policy.assertEligibleForPasswordLogin(user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void rejectsTemporarilySuspendedUser() {
        User user = new User("alice", "alice@example.com", "hash");
        user.markEmailVerified();
        user.block("Cooling off", Instant.now().plusSeconds(3600));

        assertThatThrownBy(() -> policy.assertEligibleForStatus(user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void rejectsDeactivatedUser() {
        User user = new User("alice", "alice@example.com", "hash");
        user.markEmailVerified();
        user.deactivate("User requested deactivation");

        assertThatThrownBy(() -> policy.assertEligibleForRefresh(user))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void rejectsNullUser() {
        assertThatThrownBy(() -> policy.assertEligibleForStatus(null))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
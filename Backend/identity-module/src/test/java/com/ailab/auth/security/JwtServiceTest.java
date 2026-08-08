package com.ailab.auth.security;

import com.ailab.user.domain.User;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {
    private static final String SECRET = "test-secret-that-is-at-least-256-bits-long-123456";

    @Test
    void issuesAndParsesTokenWithUserClaims() {
        JwtService service = new JwtService(SECRET, Duration.ofMinutes(15));
        User user = new User("alice", "alice@example.com", "hash");

        String token = service.issue(user);

        assertThat(service.parse(token).getSubject()).isEqualTo(user.getId());
        assertThat(service.parse(token).get("role", String.class)).isEqualTo("ROLE_USER");
        assertThat(service.parse(token).get("tokenVersion", Long.class)).isEqualTo(0L);
        assertThat(service.expiresInSeconds()).isEqualTo(900);
    }

    @Test
    void rejectsTooShortSecret() {
        assertThatThrownBy(() -> new JwtService("too-short", Duration.ofMinutes(15)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 256 bits");
    }

    @Test
    void rejectsTamperedToken() {
        JwtService service = new JwtService(SECRET, Duration.ofMinutes(15));
        String token = service.issue(new User("alice", "alice@example.com", "hash"));

        assertThatThrownBy(() -> service.parse(token + "tampered"))
                .isInstanceOf(Exception.class);
    }
}

package com.ailab.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReAuthTokenServiceTest {
    private ReAuthTokenService service;

    @BeforeEach
    void setUp() {
        service = new ReAuthTokenService();
    }

    @Test
    void issuesAndValidatesToken() {
        ReAuthTokenService.IssuedReAuthToken issued = service.issueToken("usr_1");
        assertThat(issued.token()).startsWith("reauth_");
        assertThat(service.isValidToken("usr_1", issued.token())).isTrue();
        assertThat(service.isValidToken("usr_2", issued.token())).isFalse();

        boolean consumed = service.validateAndConsumeToken("usr_1", issued.token());
        assertThat(consumed).isTrue();

        boolean reConsumed = service.validateAndConsumeToken("usr_1", issued.token());
        assertThat(reConsumed).isFalse();
    }

    @Test
    void enforcesRateLimitAfterMaxFailedAttempts() {
        for (int i = 0; i < 4; i++) {
            service.recordFailedAttempt("usr_1");
            service.checkRateLimit("usr_1");
        }

        service.recordFailedAttempt("usr_1");

        assertThatThrownBy(() -> service.checkRateLimit("usr_1"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    }

    @Test
    void resetsFailedAttemptsOnSuccess() {
        for (int i = 0; i < 4; i++) {
            service.recordFailedAttempt("usr_1");
        }
        service.issueToken("usr_1");
        service.checkRateLimit("usr_1");
    }
}

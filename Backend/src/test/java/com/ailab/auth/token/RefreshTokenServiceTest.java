package com.ailab.auth.token;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {
    @Mock RefreshTokenRepository repository;
    private RefreshTokenService service;

    @BeforeEach
    void setUp() {
        service = new RefreshTokenService(repository);
        ReflectionTestUtils.setField(service, "ttl", Duration.ofDays(30));
    }

    @Test
    void issuesOpaqueTokenAndStoresOnlyItsHash() {
        when(repository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshTokenService.IssuedToken issued = service.issue("usr_123");

        assertThat(issued.rawToken()).doesNotContain("usr_123");
        verify(repository).save(argThat(token -> token.getUserId().equals("usr_123")
                && token.getTokenHash().length() == 64
                && !token.getTokenHash().equals(issued.rawToken())));
    }

    @Test
    void rotatesValidTokenAndRevokesPreviousToken() {
        RefreshToken current = new RefreshToken("id", "usr_123", "family", "hash", Instant.now().plus(Duration.ofDays(1)));
        when(repository.findByTokenHash(anyString())).thenReturn(Optional.of(current));
        when(repository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshTokenService.IssuedToken rotated = service.rotate("old-token");

        assertThat(rotated.userId()).isEqualTo("usr_123");
        assertThat(current.isRevoked()).isTrue();
        verify(repository, times(2)).save(any(RefreshToken.class));
    }

    @Test
    void detectsReuseAndRevokesTokenFamily() {
        RefreshToken reused = new RefreshToken("id", "usr_123", "family", "hash", Instant.now().plus(Duration.ofDays(1)));
        reused.revoke("replacement");
        when(repository.findByTokenHash(anyString())).thenReturn(Optional.of(reused));

        assertThatThrownBy(() -> service.rotate("reused-token"))
                .isInstanceOf(RefreshTokenReuseException.class);
        verify(repository).revokeAllByFamilyId("family");
    }

    @Test
    void rejectsExpiredToken() {
        RefreshToken expired = new RefreshToken("id", "usr_123", "family", "hash", Instant.now().minusSeconds(1));
        when(repository.findByTokenHash(anyString())).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> service.rotate("expired-token"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid or expired");
    }
}

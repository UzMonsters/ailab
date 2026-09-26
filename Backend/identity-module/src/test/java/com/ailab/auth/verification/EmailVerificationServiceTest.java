package com.ailab.auth.verification;

import com.ailab.auth.security.AuthenticationEligibilityPolicy;
import com.ailab.auth.token.RefreshTokenOperations;
import com.ailab.common.mail.EmailDeliveryService;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock
    private EmailVerificationTokenRepository tokenRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RefreshTokenOperations refreshTokenService;
    @Mock
    private EmailDeliveryService emailDeliveryService;
    @Mock
    private AuthenticationEligibilityPolicy eligibilityPolicy;

    private EmailVerificationServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EmailVerificationServiceImpl(
                tokenRepository,
                userRepository,
                refreshTokenService,
                emailDeliveryService,
                eligibilityPolicy
        );
    }

    @Test
    void createsRegisterChallengeAndInvalidatesPrevious() {
        String userId = "usr_123";

        var challenge = service.createRegisterChallenge(userId, Duration.ofMinutes(30));

        assertThat(challenge.rawToken()).isNotBlank();
        assertThat(challenge.tokenHash()).isNotBlank();
        assertThat(challenge.expiresAt()).isAfter(Instant.now());

        verify(tokenRepository).invalidateAllForUserAndPurpose(eq(userId), eq("REGISTER"), any(Instant.class));
        verify(tokenRepository).save(any(EmailVerificationToken.class));
    }

    @Test
    void verifiesValidRegistrationToken() {
        String rawToken = "sample-raw-token-12345";
        User user = new User("bob", "bob@example.com", "hash");
        EmailVerificationToken token = new EmailVerificationToken(user.getId(), "tokenHash", "REGISTER", null, Instant.now().plusSeconds(1800));

        when(tokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token));
        when(tokenRepository.consumeAtomically(eq(token.getId()), any(Instant.class))).thenReturn(1);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        var result = service.verifyToken(rawToken);

        assertThat(result.success()).isTrue();
        assertThat(result.purpose()).isEqualTo("REGISTER");
        assertThat(user.isEmailVerified()).isTrue();
        verify(userRepository).save(user);
    }

    @Test
    void rejectsExpiredVerificationToken() {
        String rawToken = "expired-token";
        EmailVerificationToken token = new EmailVerificationToken("usr_1", "hash", "REGISTER", null, Instant.now().minusSeconds(60));

        when(tokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> service.verifyToken(rawToken))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.GONE);
    }

    @Test
    void rejectsAlreadyConsumedToken() {
        String rawToken = "consumed-token";
        EmailVerificationToken token = new EmailVerificationToken("usr_1", "hash", "REGISTER", null, Instant.now().plusSeconds(1800));
        token.markConsumed(Instant.now().minusSeconds(100));

        when(tokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> service.verifyToken(rawToken))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isIn(HttpStatus.BAD_REQUEST, HttpStatus.CONFLICT);
    }

    @Test
    void verifiesEmailChangeTokenAndRevokesSessions() {
        String rawToken = "email-change-raw-token";
        User user = new User("alice", "old@example.com", "hash");
        EmailVerificationToken token = new EmailVerificationToken(user.getId(), "tokenHash", "EMAIL_CHANGE", "new@example.com", Instant.now().plusSeconds(1800));

        when(tokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(token));
        when(tokenRepository.consumeAtomically(eq(token.getId()), any(Instant.class))).thenReturn(1);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoreCaseAndIdNot("new@example.com", user.getId())).thenReturn(false);

        var result = service.verifyToken(rawToken);

        assertThat(result.success()).isTrue();
        assertThat(result.purpose()).isEqualTo("EMAIL_CHANGE");
        assertThat(user.getEmail()).isEqualTo("new@example.com");
        assertThat(user.isEmailVerified()).isTrue();
        verify(userRepository).save(user);
        verify(refreshTokenService).revokeAll(user.getId());
    }

    @Test
    void resendVerificationEnforcesCooldown() {
        User user = new User("alice", "alice@example.com", "hash");
        // Created just 10 seconds ago (within 60s cooldown)
        EmailVerificationToken recentToken = new EmailVerificationToken(user.getId(), "hash", "REGISTER", null, Instant.now().plusSeconds(1800));

        when(userRepository.findByEmailIgnoreCase("alice@example.com")).thenReturn(Optional.of(user));
        when(tokenRepository.findTopByUserIdAndPurposeOrderByCreatedAtDesc(eq(user.getId()), eq("REGISTER")))
                .thenReturn(Optional.of(recentToken));

        assertThatThrownBy(() -> service.resendVerification("alice@example.com"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    }

    @Test
    void resendVerificationAntiEnumerationDoesNotLeakMissingUser() {
        when(userRepository.findByEmailIgnoreCase("nonexistent@example.com")).thenReturn(Optional.empty());

        // Should return silently without exception
        service.resendVerification("nonexistent@example.com");

        verifyNoInteractions(emailDeliveryService);
    }
}
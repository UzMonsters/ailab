package com.ailab.auth.verification;

import com.ailab.auth.security.AuthenticationEligibilityPolicy;
import com.ailab.auth.token.RefreshTokenOperations;
import com.ailab.common.mail.EmailDeliveryService;
import com.ailab.user.domain.User;
import com.ailab.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);
    private static final Duration COOLDOWN = Duration.ofSeconds(60);
    private static final int MAX_RESENDS_PER_HOUR = 5;

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final RefreshTokenOperations refreshTokenService;
    private final EmailDeliveryService emailDeliveryService;
    private final AuthenticationEligibilityPolicy eligibilityPolicy;

    public EmailVerificationServiceImpl(
            EmailVerificationTokenRepository tokenRepository,
            UserRepository userRepository,
            RefreshTokenOperations refreshTokenService,
            EmailDeliveryService emailDeliveryService,
            AuthenticationEligibilityPolicy eligibilityPolicy) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.emailDeliveryService = emailDeliveryService;
        this.eligibilityPolicy = eligibilityPolicy;
    }

    @Override
    public IssuedVerificationChallenge createRegisterChallenge(String userId, Duration ttl) {
        Duration actualTtl = ttl != null ? ttl : DEFAULT_TTL;
        tokenRepository.invalidateAllForUserAndPurpose(userId, EmailVerificationToken.PURPOSE_REGISTER, Instant.now());

        String rawToken = generateRawToken();
        String tokenHash = hashToken(rawToken);
        Instant expiresAt = Instant.now().plus(actualTtl);

        EmailVerificationToken token = new EmailVerificationToken(userId, tokenHash, EmailVerificationToken.PURPOSE_REGISTER, null, expiresAt);
        tokenRepository.save(token);

        return new IssuedVerificationChallenge(rawToken, tokenHash, expiresAt);
    }

    @Override
    public IssuedVerificationChallenge createEmailChangeChallenge(String userId, String newEmail, Duration ttl) {
        Duration actualTtl = ttl != null ? ttl : DEFAULT_TTL;
        tokenRepository.invalidateAllForUserAndPurpose(userId, EmailVerificationToken.PURPOSE_EMAIL_CHANGE, Instant.now());

        String rawToken = generateRawToken();
        String tokenHash = hashToken(rawToken);
        Instant expiresAt = Instant.now().plus(actualTtl);

        EmailVerificationToken token = new EmailVerificationToken(userId, tokenHash, EmailVerificationToken.PURPOSE_EMAIL_CHANGE, newEmail, expiresAt);
        tokenRepository.save(token);

        return new IssuedVerificationChallenge(rawToken, tokenHash, expiresAt);
    }

    @Override
    public VerificationResult verifyToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VERIFICATION_TOKEN_INVALID: Verification token is required");
        }

        String hash = hashToken(rawToken.trim());
        EmailVerificationToken token = tokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "VERIFICATION_TOKEN_INVALID: Invalid verification token"));

        if (token.isConsumed()) {
            if (EmailVerificationToken.PURPOSE_REGISTER.equals(token.getPurpose())) {
                User user = userRepository.findById(token.getUserId()).orElse(null);
                if (user != null && user.isEmailVerified()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "VERIFICATION_ALREADY_COMPLETED: Email is already verified");
                }
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VERIFICATION_TOKEN_INVALID: Verification token has already been used");
        }

        if (token.isExpired(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "VERIFICATION_TOKEN_EXPIRED: Verification token has expired");
        }

        int updated = tokenRepository.consumeAtomically(token.getId(), Instant.now());
        if (updated == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VERIFICATION_TOKEN_INVALID: Verification token could not be consumed");
        }

        tokenRepository.invalidateAllForUserAndPurpose(token.getUserId(), token.getPurpose(), Instant.now());

        User user = userRepository.findById(token.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (EmailVerificationToken.PURPOSE_REGISTER.equals(token.getPurpose())) {
            user.markEmailVerified(Instant.now());
            userRepository.save(user);
            return new VerificationResult(true, token.getPurpose(), "Email verified successfully");
        } else if (EmailVerificationToken.PURPOSE_EMAIL_CHANGE.equals(token.getPurpose())) {
            String newEmail = token.getNewEmail();
            if (newEmail == null || newEmail.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VERIFICATION_TOKEN_INVALID: No target email in challenge");
            }
            String normalizedNewEmail = newEmail.trim().toLowerCase(Locale.ROOT);
            if (userRepository.existsByEmailIgnoreCaseAndIdNot(normalizedNewEmail, user.getId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "EMAIL_TAKEN: Email is already taken");
            }

            user.updateEmail(normalizedNewEmail);
            user.markEmailVerified(Instant.now());
            user.incrementTokenVersion();
            userRepository.save(user);

            refreshTokenService.revokeAll(user.getId());

            return new VerificationResult(true, token.getPurpose(), "Email updated successfully. Please log in again with your new email.");
        }

        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VERIFICATION_TOKEN_INVALID: Unknown challenge purpose");
    }

    @Override
    public void resendVerification(String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(normalizedEmail);
        if (userOpt.isEmpty()) {
            return;
        }

        User user = userOpt.get();
        if (user.isEmailVerified()) {
            return;
        }

        String status = user.getStatus();
        if ("BLOCKED".equalsIgnoreCase(status) || "DEACTIVATED".equalsIgnoreCase(status) || "DELETION_SCHEDULED".equalsIgnoreCase(status)) {
            return;
        }
        if (user.getBlockedUntil() != null && user.getBlockedUntil().isAfter(Instant.now())) {
            return;
        }

        Optional<EmailVerificationToken> latest = tokenRepository.findTopByUserIdAndPurposeOrderByCreatedAtDesc(
                user.getId(), EmailVerificationToken.PURPOSE_REGISTER);
        if (latest.isPresent() && latest.get().getCreatedAt() != null && latest.get().getCreatedAt().isAfter(Instant.now().minus(COOLDOWN))) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "VERIFICATION_RESEND_LIMITED: Please wait before requesting another verification email");
        }

        long recentAttempts = tokenRepository.countByUserIdAndPurposeAndCreatedAtAfter(
                user.getId(), EmailVerificationToken.PURPOSE_REGISTER, Instant.now().minus(Duration.ofHours(1)));
        if (recentAttempts >= MAX_RESENDS_PER_HOUR) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "VERIFICATION_RESEND_LIMITED: Too many verification attempts. Please try again later.");
        }

        IssuedVerificationChallenge challenge = createRegisterChallenge(user.getId(), DEFAULT_TTL);
        emailDeliveryService.sendVerificationEmail(user.getEmail(), user.getUsername(), challenge.rawToken(), DEFAULT_TTL);
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
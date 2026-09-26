package com.ailab.auth.verification;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "email_verification_tokens", indexes = {
        @Index(name = "idx_email_verification_tokens_user_purpose", columnList = "user_id, purpose"),
        @Index(name = "idx_email_verification_tokens_expires_at", columnList = "expires_at")
})
public class EmailVerificationToken {

    public static final String PURPOSE_REGISTER = "REGISTER";
    public static final String PURPOSE_EMAIL_CHANGE = "EMAIL_CHANGE";

    @Id
    @Column(length = 64, nullable = false, updatable = false)
    private String id;

    @Column(name = "user_id", length = 64, nullable = false)
    private String userId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(nullable = false, length = 32)
    private String purpose;

    @Column(name = "new_email", length = 320)
    private String newEmail;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "consumed_at")
    private Instant consumedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected EmailVerificationToken() {
    }

    public EmailVerificationToken(String userId, String tokenHash, String purpose, String newEmail, Instant expiresAt) {
        this.id = "evt_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.purpose = purpose;
        this.newEmail = newEmail != null ? newEmail.trim().toLowerCase(java.util.Locale.ROOT) : null;
        this.expiresAt = expiresAt;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getTokenHash() { return tokenHash; }
    public String getPurpose() { return purpose; }
    public String getNewEmail() { return newEmail; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getConsumedAt() { return consumedAt; }
    public Instant getCreatedAt() { return createdAt; }

    public boolean isConsumed() {
        return consumedAt != null;
    }

    public void markConsumed(Instant consumedAt) {
        this.consumedAt = consumedAt;
    }

    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }
}
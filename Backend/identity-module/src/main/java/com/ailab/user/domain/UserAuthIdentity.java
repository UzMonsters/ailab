package com.ailab.user.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_auth_identities", uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_auth_identities_provider_subject", columnNames = {"provider", "provider_subject"})
})
public class UserAuthIdentity {

    @Id
    @Column(length = 64, nullable = false, updatable = false)
    private String id;

    @Column(name = "user_id", length = 64, nullable = false)
    private String userId;

    @Column(nullable = false, length = 32)
    private String provider;

    @Column(name = "provider_subject", nullable = false, length = 255)
    private String providerSubject;

    @Column(name = "provider_email", length = 320)
    private String providerEmail;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected UserAuthIdentity() {
    }

    public UserAuthIdentity(String userId, String provider, String providerSubject, String providerEmail) {
        this.id = "ident_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        this.userId = userId;
        this.provider = provider;
        this.providerSubject = providerSubject;
        this.providerEmail = providerEmail != null ? providerEmail.trim().toLowerCase(java.util.Locale.ROOT) : null;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getProvider() { return provider; }
    public String getProviderSubject() { return providerSubject; }
    public String getProviderEmail() { return providerEmail; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setProviderEmail(String providerEmail) {
        this.providerEmail = providerEmail != null ? providerEmail.trim().toLowerCase(java.util.Locale.ROOT) : null;
    }
}
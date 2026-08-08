package com.ailab.user.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Collections;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(length = 64, nullable = false, updatable = false)
    private String id;
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    @Column(nullable = false, unique = true, length = 320)
    private String email;
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    @Column(nullable = false)
    private int level = 1;
    @Column(nullable = false)
    private long xp = 0;
    @Column(nullable = false, length = 10)
    private String language = "en";
    @Column(nullable = false, length = 20)
    private String theme = "light";
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "application_settings", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> applicationSettings = new HashMap<>();
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Long> statistics = new HashMap<>();
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Set<String> achievements = new HashSet<>();
    @Column(name = "token_version", nullable = false)
    private long tokenVersion = 0;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected User() { }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public String getAvatarUrl() { return avatarUrl; }
    public int getLevel() { return level; }
    public long getXp() { return xp; }
    public String getLanguage() { return language; }
    public String getTheme() { return theme; }
    public Map<String, Object> getApplicationSettings() { return Collections.unmodifiableMap(new HashMap<>(applicationSettings)); }
    public Map<String, Long> getStatistics() { return Collections.unmodifiableMap(new HashMap<>(statistics)); }
    public Set<String> getAchievements() { return Collections.unmodifiableSet(new HashSet<>(achievements)); }
    public long getTokenVersion() { return tokenVersion; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public User(String username, String email, String passwordHash) {
        this(username, email, passwordHash, Role.USER);
    }

    public User(String username, String email, String passwordHash, Role role) {
        this.id = "usr_" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public void updateProfile(String username, String avatarUrl) {
        if (username != null && !username.isBlank()) this.username = username;
        if (avatarUrl != null) this.avatarUrl = avatarUrl;
    }

    public void removeAvatar() {
        this.avatarUrl = null;
    }

    public void updatePreferences(String language, String theme, Map<String, Object> settings) {
        if (language != null && !language.isBlank()) this.language = language;
        if (theme != null && !theme.isBlank()) this.theme = theme;
        if (settings != null) this.applicationSettings = new HashMap<>(settings);
    }

    public void updateAdminProfile(String username, String email, Role role) {
        if (username != null && !username.isBlank()) this.username = username;
        if (email != null && !email.isBlank()) this.email = email;
        if (role != null && role != this.role) {
            this.role = role;
            incrementTokenVersion();
        }
    }

    public void incrementTokenVersion() {
        this.tokenVersion++;
    }
}

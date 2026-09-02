package com.ailab.admin.settings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "admin_settings_history")
public class AdminSettingsHistoryEntity {

    @Id
    @Column(length = 64, nullable = false)
    private String id;

    @Column(nullable = false)
    private Long version;

    @Column(name = "actor_id", length = 64, nullable = false)
    private String actorId;

    @Column(name = "actor_name", length = 100, nullable = false)
    private String actorName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "changed_keys", columnDefinition = "jsonb", nullable = false)
    private List<String> changedKeys;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "settings_snapshot", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> settingsSnapshot;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected AdminSettingsHistoryEntity() {
    }

    public AdminSettingsHistoryEntity(Long version, String actorId, String actorName,
                                      List<String> changedKeys, Map<String, Object> settingsSnapshot) {
        this.id = "sethist_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        this.version = version;
        this.actorId = actorId;
        this.actorName = actorName;
        this.changedKeys = changedKeys;
        this.settingsSnapshot = settingsSnapshot;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public Long getVersion() { return version; }
    public String getActorId() { return actorId; }
    public String getActorName() { return actorName; }
    public List<String> getChangedKeys() { return changedKeys; }
    public Map<String, Object> getSettingsSnapshot() { return settingsSnapshot; }
    public Instant getCreatedAt() { return createdAt; }
}

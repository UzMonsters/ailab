package com.ailab.admin.settings;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "admin_settings")
public class AdminSettingsEntity {

    @Id
    @Column(length = 64, nullable = false)
    private String id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "settings_data", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> settingsData;

    @Column(nullable = false)
    private Long version;

    @Column(nullable = false, length = 64)
    private String etag;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "updated_by_id", length = 64, nullable = false)
    private String updatedById;

    @Column(name = "updated_by_name", length = 100, nullable = false)
    private String updatedByName;

    protected AdminSettingsEntity() {
    }

    public AdminSettingsEntity(String id, Map<String, Object> settingsData, Long version,
                               String etag, Instant updatedAt, String updatedById, String updatedByName) {
        this.id = id;
        this.settingsData = settingsData;
        this.version = version;
        this.etag = etag;
        this.updatedAt = updatedAt;
        this.updatedById = updatedById;
        this.updatedByName = updatedByName;
    }

    public String getId() { return id; }
    public Map<String, Object> getSettingsData() { return settingsData; }
    public Long getVersion() { return version; }
    public String getEtag() { return etag; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getUpdatedById() { return updatedById; }
    public String getUpdatedByName() { return updatedByName; }

    public void update(Map<String, Object> settingsData, Long version, String etag, Instant updatedAt, String updatedById, String updatedByName) {
        this.settingsData = settingsData;
        this.version = version;
        this.etag = etag;
        this.updatedAt = updatedAt;
        this.updatedById = updatedById;
        this.updatedByName = updatedByName;
    }
}

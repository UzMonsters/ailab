package com.ailab.admin.audit;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "admin_audit_events")
public class AdminAuditEventEntity implements Persistable<String> {

    @Id
    @Column(length = 64, nullable = false, updatable = false)
    private String id;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt = Instant.now();

    @Column(name = "actor_id", length = 64, nullable = false, updatable = false)
    private String actorId;

    @Column(name = "actor_name", length = 100, nullable = false, updatable = false)
    private String actorName;

    @Column(name = "actor_role", length = 50, nullable = false, updatable = false)
    private String actorRole;

    @Column(nullable = false, length = 100, updatable = false)
    private String action;

    @Column(name = "entity_type", length = 100, nullable = false, updatable = false)
    private String entityType;

    @Column(name = "entity_id", length = 100, nullable = false, updatable = false)
    private String entityId;

    @Column(name = "entity_label", length = 200, updatable = false)
    private String entityLabel;

    @Column(length = 100, updatable = false)
    private String subject;

    @Column(nullable = false, length = 50, updatable = false)
    private String source;

    @Column(nullable = false, length = 50, updatable = false)
    private String result;

    @Column(nullable = false, length = 50, updatable = false)
    private String severity;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "before_state", columnDefinition = "jsonb", updatable = false)
    private Map<String, Object> beforeState;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "after_state", columnDefinition = "jsonb", updatable = false)
    private Map<String, Object> afterState;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "changed_keys", columnDefinition = "jsonb", updatable = false)
    private List<String> changedKeys;

    @Column(name = "request_id", length = 100, updatable = false)
    private String requestId;

    @Column(name = "ip_address", length = 100, updatable = false)
    private String ipAddress;

    @Column(name = "user_agent", length = 500, updatable = false)
    private String userAgent;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", updatable = false)
    private Map<String, Object> metadata;

    @jakarta.persistence.Transient
    private boolean isNew = false;

    protected AdminAuditEventEntity() {
        this.occurredAt = Instant.now();
    }

    public AdminAuditEventEntity(String actorId, String actorName, String actorRole,
                                 String action, String entityType, String entityId, String entityLabel,
                                 String subject, String source, String result, String severity,
                                 Object beforeState, Object afterState, List<String> changedKeys,
                                 String requestId, String ipAddress, String userAgent, Map<String, Object> metadata) {
        this.id = "aud_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        this.occurredAt = Instant.now();
        this.actorId = (actorId != null && !actorId.isBlank()) ? (actorId.length() > 64 ? actorId.substring(0, 64) : actorId) : "usr_admin";
        this.actorName = (actorName != null && !actorName.isBlank()) ? actorName : "System";
        this.actorRole = (actorRole != null && !actorRole.isBlank()) ? actorRole : "ADMIN";
        this.action = (action != null && !action.isBlank()) ? action : "unknown";
        this.entityType = (entityType != null && !entityType.isBlank()) ? entityType : "UNKNOWN";
        this.entityId = (entityId != null && !entityId.isBlank()) ? entityId : "unknown";
        this.entityLabel = entityLabel != null ? entityLabel : this.entityType + " " + this.entityId;
        this.subject = subject;
        this.source = source != null ? source : "ADMIN_WEB";
        this.result = result != null ? result : "SUCCESS";
        this.severity = severity != null ? severity : "MEDIUM";
        this.beforeState = toStateMap(beforeState);
        this.afterState = toStateMap(afterState);
        this.changedKeys = changedKeys;
        this.requestId = requestId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.metadata = metadata;
        this.isNew = true;
    }

    private static Map<String, Object> toStateMap(Object state) {
        if (state == null) {
            return null;
        }
        if (state instanceof Map<?, ?> m) {
            Map<String, Object> res = new java.util.LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                res.put(String.valueOf(entry.getKey()), entry.getValue());
            }
            return res;
        }
        return Map.of("value", state);
    }

    @Override
    public boolean isNew() {
        return isNew || occurredAt == null;
    }

    @PrePersist
    protected void onCreate() {
        if (this.occurredAt == null) {
            this.occurredAt = Instant.now();
        }
        if (this.actorId == null || this.actorId.isBlank()) {
            this.actorId = "usr_admin";
        }
    }

    @PostPersist
    @PostLoad
    protected void markNotNew() {
        this.isNew = false;
    }

    public String getId() { return id; }
    public Instant getOccurredAt() { return occurredAt; }
    public String getActorId() { return actorId; }
    public String getActorName() { return actorName; }
    public String getActorRole() { return actorRole; }
    public String getAction() { return action; }
    public String getEntityType() { return entityType; }
    public String getEntityId() { return entityId; }
    public String getEntityLabel() { return entityLabel; }
    public String getSubject() { return subject; }
    public String getSource() { return source; }
    public String getResult() { return result; }
    public String getSeverity() { return severity; }
    public Map<String, Object> getBeforeState() { return beforeState; }
    public Map<String, Object> getAfterState() { return afterState; }
    public List<String> getChangedKeys() { return changedKeys; }
    public String getRequestId() { return requestId; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public Map<String, Object> getMetadata() { return metadata; }
}

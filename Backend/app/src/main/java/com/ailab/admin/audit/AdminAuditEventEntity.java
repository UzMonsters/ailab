package com.ailab.admin.audit;

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
@Table(name = "admin_audit_events")
public class AdminAuditEventEntity {

    @Id
    @Column(length = 64, nullable = false, updatable = false)
    private String id;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

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
    private Object beforeState;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "after_state", columnDefinition = "jsonb", updatable = false)
    private Object afterState;

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

    protected AdminAuditEventEntity() {
    }

    public AdminAuditEventEntity(String actorId, String actorName, String actorRole,
                                 String action, String entityType, String entityId, String entityLabel,
                                 String subject, String source, String result, String severity,
                                 Object beforeState, Object afterState, List<String> changedKeys,
                                 String requestId, String ipAddress, String userAgent, Map<String, Object> metadata) {
        this.id = "aud_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        this.occurredAt = Instant.now();
        this.actorId = actorId != null ? actorId : "anonymous";
        this.actorName = actorName != null ? actorName : "System";
        this.actorRole = actorRole != null ? actorRole : "ADMIN";
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityLabel = entityLabel != null ? entityLabel : entityType + " " + entityId;
        this.subject = subject;
        this.source = source != null ? source : "ADMIN_WEB";
        this.result = result != null ? result : "SUCCESS";
        this.severity = severity != null ? severity : "MEDIUM";
        this.beforeState = beforeState;
        this.afterState = afterState;
        this.changedKeys = changedKeys;
        this.requestId = requestId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.metadata = metadata;
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
    public Object getBeforeState() { return beforeState; }
    public Object getAfterState() { return afterState; }
    public List<String> getChangedKeys() { return changedKeys; }
    public String getRequestId() { return requestId; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public Map<String, Object> getMetadata() { return metadata; }
}

package com.ailab.workspace.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "experiment_measurements")
public class MeasurementEntity {

    @Id
    private String id;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "workspace_id")
    private String workspaceId;

    @Column(name = "sensor_item_id")
    private String sensorItemId;

    @Column(name = "target_item_id")
    private String targetItemId;

    @Column(name = "kind", nullable = false)
    private String kind; // TEMPERATURE, PH, MASS, PRESSURE, VOLUME, VOLTAGE

    @Column(name = "value", nullable = false, precision = 20, scale = 8)
    private BigDecimal value;

    @Column(name = "unit", nullable = false)
    private String unit;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt = Instant.now();

    public MeasurementEntity() {}

    public MeasurementEntity(String id, String sessionId, String workspaceId, String sensorItemId, String targetItemId, String kind, BigDecimal value, String unit, Instant recordedAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.workspaceId = workspaceId;
        this.sensorItemId = sensorItemId;
        this.targetItemId = targetItemId;
        this.kind = kind;
        this.value = value;
        this.unit = unit;
        this.recordedAt = recordedAt != null ? recordedAt : Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getWorkspaceId() { return workspaceId; }
    public void setWorkspaceId(String workspaceId) { this.workspaceId = workspaceId; }

    public String getSensorItemId() { return sensorItemId; }
    public void setSensorItemId(String sensorItemId) { this.sensorItemId = sensorItemId; }

    public String getTargetItemId() { return targetItemId; }
    public void setTargetItemId(String targetItemId) { this.targetItemId = targetItemId; }

    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Instant getRecordedAt() { return recordedAt; }
    public void setRecordedAt(Instant recordedAt) { this.recordedAt = recordedAt; }
}

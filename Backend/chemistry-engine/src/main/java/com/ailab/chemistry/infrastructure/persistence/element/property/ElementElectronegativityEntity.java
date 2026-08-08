package com.ailab.chemistry.infrastructure.persistence.element.property;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "element_electronegativities", schema = "chemistry")
public class ElementElectronegativityEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private ElementPropertyProfileEntity profile;

    @Column(name = "value", nullable = false, precision = 10, scale = 4)
    private BigDecimal value;

    @Column(name = "scale", nullable = false)
    private String scale;

    @Column(name = "is_predicted", nullable = false)
    private boolean isPredicted;

    @Column(name = "evidence_status", nullable = false)
    private String evidenceStatus;

    @Column(name = "source_identifier", nullable = false)
    private String sourceIdentifier;

    @Column(name = "source_title", nullable = false)
    private String sourceTitle;

    public ElementElectronegativityEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ElementPropertyProfileEntity getProfile() { return profile; }
    public void setProfile(ElementPropertyProfileEntity profile) { this.profile = profile; }
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
    public String getScale() { return scale; }
    public void setScale(String scale) { this.scale = scale; }
    public boolean isPredicted() { return isPredicted; }
    public void setPredicted(boolean predicted) { isPredicted = predicted; }
    public String getEvidenceStatus() { return evidenceStatus; }
    public void setEvidenceStatus(String evidenceStatus) { this.evidenceStatus = evidenceStatus; }
    public String getSourceIdentifier() { return sourceIdentifier; }
    public void setSourceIdentifier(String sourceIdentifier) { this.sourceIdentifier = sourceIdentifier; }
    public String getSourceTitle() { return sourceTitle; }
    public void setSourceTitle(String sourceTitle) { this.sourceTitle = sourceTitle; }
}

package com.ailab.chemistry.infrastructure.persistence.element.property;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "element_radii", schema = "chemistry")
public class ElementRadiusEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private ElementPropertyProfileEntity profile;

    @Column(name = "kind", nullable = false)
    private String kind;

    @Column(name = "radius_pm", nullable = false, precision = 10, scale = 4)
    private BigDecimal radiusPm;

    @Column(name = "ionic_charge")
    private Integer ionicCharge;

    @Column(name = "coordination_number")
    private Integer coordinationNumber;

    @Column(name = "spin_state")
    private String spinState;

    @Column(name = "evidence_status", nullable = false)
    private String evidenceStatus;

    @Column(name = "source_identifier", nullable = false)
    private String sourceIdentifier;

    @Column(name = "source_title", nullable = false)
    private String sourceTitle;

    public ElementRadiusEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ElementPropertyProfileEntity getProfile() { return profile; }
    public void setProfile(ElementPropertyProfileEntity profile) { this.profile = profile; }
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    public BigDecimal getRadiusPm() { return radiusPm; }
    public void setRadiusPm(BigDecimal radiusPm) { this.radiusPm = radiusPm; }
    public Integer getIonicCharge() { return ionicCharge; }
    public void setIonicCharge(Integer ionicCharge) { this.ionicCharge = ionicCharge; }
    public Integer getCoordinationNumber() { return coordinationNumber; }
    public void setCoordinationNumber(Integer coordinationNumber) { this.coordinationNumber = coordinationNumber; }
    public String getSpinState() { return spinState; }
    public void setSpinState(String spinState) { this.spinState = spinState; }
    public String getEvidenceStatus() { return evidenceStatus; }
    public void setEvidenceStatus(String evidenceStatus) { this.evidenceStatus = evidenceStatus; }
    public String getSourceIdentifier() { return sourceIdentifier; }
    public void setSourceIdentifier(String sourceIdentifier) { this.sourceIdentifier = sourceIdentifier; }
    public String getSourceTitle() { return sourceTitle; }
    public void setSourceTitle(String sourceTitle) { this.sourceTitle = sourceTitle; }
}

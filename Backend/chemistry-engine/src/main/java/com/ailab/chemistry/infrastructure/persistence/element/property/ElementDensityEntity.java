package com.ailab.chemistry.infrastructure.persistence.element.property;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "element_density_data", schema = "chemistry")
public class ElementDensityEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private ElementPropertyProfileEntity profile;

    @Column(name = "density_kg_m3", nullable = false, precision = 15, scale = 6)
    private BigDecimal densityKgM3;

    @Column(name = "ref_temp_k", precision = 10, scale = 4)
    private BigDecimal refTempK;

    @Column(name = "ref_pressure_kpa", precision = 12, scale = 4)
    private BigDecimal refPressureKpa;

    @Column(name = "ref_state")
    private String refState;

    @Column(name = "evidence_status", nullable = false)
    private String evidenceStatus;

    @Column(name = "source_identifier", nullable = false)
    private String sourceIdentifier;

    @Column(name = "source_title", nullable = false)
    private String sourceTitle;

    public ElementDensityEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ElementPropertyProfileEntity getProfile() { return profile; }
    public void setProfile(ElementPropertyProfileEntity profile) { this.profile = profile; }
    public BigDecimal getDensityKgM3() { return densityKgM3; }
    public void setDensityKgM3(BigDecimal densityKgM3) { this.densityKgM3 = densityKgM3; }
    public BigDecimal getRefTempK() { return refTempK; }
    public void setRefTempK(BigDecimal refTempK) { this.refTempK = refTempK; }
    public BigDecimal getRefPressureKpa() { return refPressureKpa; }
    public void setRefPressureKpa(BigDecimal refPressureKpa) { this.refPressureKpa = refPressureKpa; }
    public String getRefState() { return refState; }
    public void setRefState(String refState) { this.refState = refState; }
    public String getEvidenceStatus() { return evidenceStatus; }
    public void setEvidenceStatus(String evidenceStatus) { this.evidenceStatus = evidenceStatus; }
    public String getSourceIdentifier() { return sourceIdentifier; }
    public void setSourceIdentifier(String sourceIdentifier) { this.sourceIdentifier = sourceIdentifier; }
    public String getSourceTitle() { return sourceTitle; }
    public void setSourceTitle(String sourceTitle) { this.sourceTitle = sourceTitle; }
}

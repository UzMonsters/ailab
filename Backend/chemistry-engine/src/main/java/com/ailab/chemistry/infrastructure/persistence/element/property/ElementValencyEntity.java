package com.ailab.chemistry.infrastructure.persistence.element.property;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "element_valencies", schema = "chemistry")
public class ElementValencyEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private ElementPropertyProfileEntity profile;

    @Column(name = "valency", nullable = false)
    private int valency;

    @Column(name = "is_common", nullable = false)
    private boolean isCommon;

    @Column(name = "evidence_status", nullable = false)
    private String evidenceStatus;

    @Column(name = "source_identifier", nullable = false)
    private String sourceIdentifier;

    @Column(name = "source_title", nullable = false)
    private String sourceTitle;

    public ElementValencyEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ElementPropertyProfileEntity getProfile() { return profile; }
    public void setProfile(ElementPropertyProfileEntity profile) { this.profile = profile; }
    public int getValency() { return valency; }
    public void setValency(int valency) { this.valency = valency; }
    public boolean isCommon() { return isCommon; }
    public void setCommon(boolean common) { isCommon = common; }
    public String getEvidenceStatus() { return evidenceStatus; }
    public void setEvidenceStatus(String evidenceStatus) { this.evidenceStatus = evidenceStatus; }
    public String getSourceIdentifier() { return sourceIdentifier; }
    public void setSourceIdentifier(String sourceIdentifier) { this.sourceIdentifier = sourceIdentifier; }
    public String getSourceTitle() { return sourceTitle; }
    public void setSourceTitle(String sourceTitle) { this.sourceTitle = sourceTitle; }
}

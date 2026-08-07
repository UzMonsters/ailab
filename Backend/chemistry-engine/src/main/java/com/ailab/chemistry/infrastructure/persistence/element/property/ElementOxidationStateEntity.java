package com.ailab.chemistry.infrastructure.persistence.element.property;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "element_oxidation_states", schema = "chemistry")
public class ElementOxidationStateEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private ElementPropertyProfileEntity profile;

    @Column(name = "state", nullable = false)
    private int state;

    @Column(name = "is_common", nullable = false)
    private boolean isCommon;

    @Column(name = "is_uncommon", nullable = false)
    private boolean isUncommon;

    @Column(name = "is_predicted", nullable = false)
    private boolean isPredicted;

    @Column(name = "evidence_status", nullable = false)
    private String evidenceStatus;

    @Column(name = "source_identifier", nullable = false)
    private String sourceIdentifier;

    @Column(name = "source_title", nullable = false)
    private String sourceTitle;

    public ElementOxidationStateEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ElementPropertyProfileEntity getProfile() { return profile; }
    public void setProfile(ElementPropertyProfileEntity profile) { this.profile = profile; }
    public int getState() { return state; }
    public void setState(int state) { this.state = state; }
    public boolean isCommon() { return isCommon; }
    public void setCommon(boolean common) { isCommon = common; }
    public boolean isUncommon() { return isUncommon; }
    public void setUncommon(boolean uncommon) { isUncommon = uncommon; }
    public boolean isPredicted() { return isPredicted; }
    public void setPredicted(boolean predicted) { isPredicted = predicted; }
    public String getEvidenceStatus() { return evidenceStatus; }
    public void setEvidenceStatus(String evidenceStatus) { this.evidenceStatus = evidenceStatus; }
    public String getSourceIdentifier() { return sourceIdentifier; }
    public void setSourceIdentifier(String sourceIdentifier) { this.sourceIdentifier = sourceIdentifier; }
    public String getSourceTitle() { return sourceTitle; }
    public void setSourceTitle(String sourceTitle) { this.sourceTitle = sourceTitle; }
}

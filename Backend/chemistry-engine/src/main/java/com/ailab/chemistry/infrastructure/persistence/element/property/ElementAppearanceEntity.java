package com.ailab.chemistry.infrastructure.persistence.element.property;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "element_appearance", schema = "chemistry")
public class ElementAppearanceEntity {
    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private ElementPropertyProfileEntity profile;

    @Column(name = "normalized_color_name")
    private String normalizedColorName;

    @Column(name = "appearance_description")
    private String appearanceDescription;

    @Column(name = "evidence_status", nullable = false)
    private String evidenceStatus;

    @Column(name = "source_identifier", nullable = false)
    private String sourceIdentifier;

    @Column(name = "source_title", nullable = false)
    private String sourceTitle;

    public ElementAppearanceEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ElementPropertyProfileEntity getProfile() { return profile; }
    public void setProfile(ElementPropertyProfileEntity profile) { this.profile = profile; }
    public String getNormalizedColorName() { return normalizedColorName; }
    public void setNormalizedColorName(String normalizedColorName) { this.normalizedColorName = normalizedColorName; }
    public String getAppearanceDescription() { return appearanceDescription; }
    public void setAppearanceDescription(String appearanceDescription) { this.appearanceDescription = appearanceDescription; }
    public String getEvidenceStatus() { return evidenceStatus; }
    public void setEvidenceStatus(String evidenceStatus) { this.evidenceStatus = evidenceStatus; }
    public String getSourceIdentifier() { return sourceIdentifier; }
    public void setSourceIdentifier(String sourceIdentifier) { this.sourceIdentifier = sourceIdentifier; }
    public String getSourceTitle() { return sourceTitle; }
    public void setSourceTitle(String sourceTitle) { this.sourceTitle = sourceTitle; }
}

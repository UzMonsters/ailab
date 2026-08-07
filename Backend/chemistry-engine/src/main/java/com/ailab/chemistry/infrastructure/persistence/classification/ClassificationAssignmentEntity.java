package com.ailab.chemistry.infrastructure.persistence.classification;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "compound_classification_assignments", schema = "chemistry")
public class ClassificationAssignmentEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false)
    private ClassificationProfileEntity profile;

    @Column(name = "dimension", nullable = false)
    private String dimension;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "basis", nullable = false)
    private String basis;

    @Column(name = "evidence_status", nullable = false)
    private String evidenceStatus;

    @Column(name = "rule_code")
    private String ruleCode;

    @Column(name = "source_identifier")
    private String sourceIdentifier;

    @Column(name = "source_title")
    private String sourceTitle;

    @Column(name = "explanatory_note")
    private String explanatoryNote;

    public ClassificationAssignmentEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ClassificationProfileEntity getProfile() { return profile; }
    public void setProfile(ClassificationProfileEntity profile) { this.profile = profile; }
    public String getDimension() { return dimension; }
    public void setDimension(String dimension) { this.dimension = dimension; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getBasis() { return basis; }
    public void setBasis(String basis) { this.basis = basis; }
    public String getEvidenceStatus() { return evidenceStatus; }
    public void setEvidenceStatus(String evidenceStatus) { this.evidenceStatus = evidenceStatus; }
    public String getRuleCode() { return ruleCode; }
    public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }
    public String getSourceIdentifier() { return sourceIdentifier; }
    public void setSourceIdentifier(String sourceIdentifier) { this.sourceIdentifier = sourceIdentifier; }
    public String getSourceTitle() { return sourceTitle; }
    public void setSourceTitle(String sourceTitle) { this.sourceTitle = sourceTitle; }
    public String getExplanatoryNote() { return explanatoryNote; }
    public void setExplanatoryNote(String explanatoryNote) { this.explanatoryNote = explanatoryNote; }
}

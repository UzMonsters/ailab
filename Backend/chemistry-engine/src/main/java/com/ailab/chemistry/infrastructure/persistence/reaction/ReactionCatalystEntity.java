package com.ailab.chemistry.infrastructure.persistence.reaction;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "reaction_catalysts", schema = "chemistry")
public class ReactionCatalystEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reaction_id", nullable = false)
    private ReactionEntity reaction;

    @Column(name = "reference_type", nullable = false)
    private String referenceType;

    @Column(name = "reference_code", nullable = false)
    private String referenceCode;

    @Column(name = "compound_id")
    private UUID compoundId;

    @Column(name = "element_atomic_number")
    private Integer elementAtomicNumber;

    @Column(name = "catalyst_role", nullable = false)
    private String catalystRole;

    @Column(name = "physical_form")
    private String physicalForm;

    @Column(name = "loading_description")
    private String loadingDescription;

    @Column(name = "evidence_status")
    private String evidenceStatus;

    @Column(name = "source_document_id")
    private String sourceDocumentId;

    public ReactionCatalystEntity() {}

    public ReactionCatalystEntity(UUID id, ReactionEntity reaction, String referenceType, String referenceCode,
                                  UUID compoundId, Integer elementAtomicNumber, String catalystRole,
                                  String physicalForm, String loadingDescription, String evidenceStatus, String sourceDocumentId) {
        this.id = id;
        this.reaction = reaction;
        this.referenceType = referenceType;
        this.referenceCode = referenceCode;
        this.compoundId = compoundId;
        this.elementAtomicNumber = elementAtomicNumber;
        this.catalystRole = catalystRole;
        this.physicalForm = physicalForm;
        this.loadingDescription = loadingDescription;
        this.evidenceStatus = evidenceStatus;
        this.sourceDocumentId = sourceDocumentId;
    }

    public UUID getId() {
        return id;
    }

    public ReactionEntity getReaction() {
        return reaction;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public UUID getCompoundId() {
        return compoundId;
    }

    public Integer getElementAtomicNumber() {
        return elementAtomicNumber;
    }

    public String getCatalystRole() {
        return catalystRole;
    }

    public String getPhysicalForm() {
        return physicalForm;
    }

    public String getLoadingDescription() {
        return loadingDescription;
    }

    public String getEvidenceStatus() {
        return evidenceStatus;
    }

    public String getSourceDocumentId() {
        return sourceDocumentId;
    }
}

package com.ailab.chemistry.infrastructure.persistence.reaction;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reactions", schema = "chemistry")
public class ReactionEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "reaction_code", nullable = false, unique = true)
    private String reactionCode;

    @Column(name = "primary_name", nullable = false)
    private String primaryName;

    @Column(name = "original_equation", nullable = false)
    private String originalEquation;

    @Column(name = "normalized_equation", nullable = false)
    private String normalizedEquation;

    @Column(name = "canonical_balanced_equation", nullable = false)
    private String canonicalBalancedEquation;

    @Column(name = "reaction_signature", nullable = false)
    private String reactionSignature;

    @Column(name = "directionality", nullable = false)
    private String directionality;

    @Column(name = "catalog_version_id", nullable = false)
    private String catalogVersionId;

    @Column(name = "source_document_id", nullable = false)
    private String sourceDocumentId;

    @Column(name = "provenance_notes")
    private String provenanceNotes;

    @OneToMany(mappedBy = "reaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReactionAliasEntity> aliases = new ArrayList<>();

    @OneToMany(mappedBy = "reaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReactionTermEntity> terms = new ArrayList<>();

    @OneToMany(mappedBy = "reaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReactionConditionSetEntity> conditionSets = new ArrayList<>();

    @OneToMany(mappedBy = "reaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReactionCatalystEntity> catalysts = new ArrayList<>();

    @OneToMany(mappedBy = "reaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReactionTypeAssignmentEntity> typeAssignments = new ArrayList<>();

    public ReactionEntity() {}

    public ReactionEntity(UUID id, String reactionCode, String primaryName, String originalEquation,
                          String normalizedEquation, String canonicalBalancedEquation, String reactionSignature,
                          String directionality, String catalogVersionId, String sourceDocumentId, String provenanceNotes) {
        this.id = id;
        this.reactionCode = reactionCode;
        this.primaryName = primaryName;
        this.originalEquation = originalEquation;
        this.normalizedEquation = normalizedEquation;
        this.canonicalBalancedEquation = canonicalBalancedEquation;
        this.reactionSignature = reactionSignature;
        this.directionality = directionality;
        this.catalogVersionId = catalogVersionId;
        this.sourceDocumentId = sourceDocumentId;
        this.provenanceNotes = provenanceNotes;
    }

    public UUID getId() {
        return id;
    }

    public String getReactionCode() {
        return reactionCode;
    }

    public String getPrimaryName() {
        return primaryName;
    }

    public String getOriginalEquation() {
        return originalEquation;
    }

    public String getNormalizedEquation() {
        return normalizedEquation;
    }

    public String getCanonicalBalancedEquation() {
        return canonicalBalancedEquation;
    }

    public String getReactionSignature() {
        return reactionSignature;
    }

    public String getDirectionality() {
        return directionality;
    }

    public String getCatalogVersionId() {
        return catalogVersionId;
    }

    public String getSourceDocumentId() {
        return sourceDocumentId;
    }

    public String getProvenanceNotes() {
        return provenanceNotes;
    }

    public List<ReactionAliasEntity> getAliases() {
        return aliases;
    }

    public List<ReactionTermEntity> getTerms() {
        return terms;
    }

    public List<ReactionConditionSetEntity> getConditionSets() {
        return conditionSets;
    }

    public List<ReactionCatalystEntity> getCatalysts() {
        return catalysts;
    }

    public List<ReactionTypeAssignmentEntity> getTypeAssignments() {
        return typeAssignments;
    }
}

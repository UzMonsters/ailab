package com.ailab.chemistry.infrastructure.persistence.reaction;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "reaction_type_assignments", schema = "chemistry")
public class ReactionTypeAssignmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reaction_id", nullable = false)
    private ReactionEntity reaction;

    @Column(name = "type_code", nullable = false)
    private String typeCode;

    @Column(name = "derivation_basis", nullable = false)
    private String derivationBasis;

    @Column(name = "explanation")
    private String explanation;

    public ReactionTypeAssignmentEntity() {}

    public ReactionTypeAssignmentEntity(ReactionEntity reaction, String typeCode, String derivationBasis, String explanation) {
        this.reaction = reaction;
        this.typeCode = typeCode;
        this.derivationBasis = derivationBasis;
        this.explanation = explanation;
    }

    public UUID getId() {
        return id;
    }

    public ReactionEntity getReaction() {
        return reaction;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public String getDerivationBasis() {
        return derivationBasis;
    }

    public String getExplanation() {
        return explanation;
    }
}

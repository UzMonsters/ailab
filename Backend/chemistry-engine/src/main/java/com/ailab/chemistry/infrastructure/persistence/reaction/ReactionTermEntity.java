package com.ailab.chemistry.infrastructure.persistence.reaction;

import jakarta.persistence.*;

import java.math.BigInteger;
import java.util.UUID;

@Entity
@Table(name = "reaction_terms", schema = "chemistry")
public class ReactionTermEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reaction_id", nullable = false)
    private ReactionEntity reaction;

    @Column(name = "compound_id", nullable = false)
    private UUID compoundId;

    @Column(name = "compound_code", nullable = false)
    private String compoundCode;

    @Column(name = "formula", nullable = false)
    private String formula;

    @Column(name = "side", nullable = false)
    private String side;

    @Column(name = "coefficient", nullable = false)
    private BigInteger coefficient;

    @Column(name = "species_state")
    private String speciesState;

    @Column(name = "term_order", nullable = false)
    private int termOrder;

    public ReactionTermEntity() {}

    public ReactionTermEntity(ReactionEntity reaction, UUID compoundId, String compoundCode, String formula,
                              String side, BigInteger coefficient, String speciesState, int termOrder) {
        this.reaction = reaction;
        this.compoundId = compoundId;
        this.compoundCode = compoundCode;
        this.formula = formula;
        this.side = side;
        this.coefficient = coefficient;
        this.speciesState = speciesState;
        this.termOrder = termOrder;
    }

    public UUID getId() {
        return id;
    }

    public ReactionEntity getReaction() {
        return reaction;
    }

    public UUID getCompoundId() {
        return compoundId;
    }

    public String getCompoundCode() {
        return compoundCode;
    }

    public String getFormula() {
        return formula;
    }

    public String getSide() {
        return side;
    }

    public BigInteger getCoefficient() {
        return coefficient;
    }

    public String getSpeciesState() {
        return speciesState;
    }

    public int getTermOrder() {
        return termOrder;
    }
}

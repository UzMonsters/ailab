package com.ailab.chemistry.domain.reaction;

import com.ailab.chemistry.domain.compound.CompoundId;

import java.math.BigInteger;
import java.util.Objects;
import java.util.UUID;

public final class ReactionTerm {
    private final UUID compoundId;
    private final String compoundCode;
    private final String formula;
    private final ReactionSide side;
    private final BigInteger coefficient;
    private final ReactionSpeciesState speciesState;
    private final int termOrder;

    public ReactionTerm(UUID compoundId, String compoundCode, String formula, ReactionSide side, BigInteger coefficient, ReactionSpeciesState speciesState, int termOrder) {
        Objects.requireNonNull(compoundCode, "Compound code must not be null");
        Objects.requireNonNull(side, "Reaction side must not be null");
        Objects.requireNonNull(coefficient, "Coefficient must not be null");

        if (compoundCode.isBlank()) {
            throw new ReactionException(ReactionErrorCode.INVALID_REACTION_TERM, "Compound code must not be blank");
        }
        if ("e-".equalsIgnoreCase(compoundCode.trim()) || "e-".equalsIgnoreCase(formula != null ? formula.trim() : "")) {
            throw new ReactionException(ReactionErrorCode.INVALID_REACTION_TERM, "Free electron e- is not accepted as a compound term in Phase 6A");
        }
        if (coefficient.compareTo(BigInteger.ZERO) <= 0) {
            throw new ReactionException(ReactionErrorCode.REACTION_COEFFICIENT_INVALID, "Coefficient must be positive (greater than zero)");
        }

        this.compoundId = compoundId != null ? compoundId : UUID.nameUUIDFromBytes(("compound-" + compoundCode.trim()).getBytes());
        this.compoundCode = compoundCode.trim();
        this.formula = formula != null ? formula.trim() : compoundCode.trim();
        this.side = side;
        this.coefficient = coefficient;
        this.speciesState = speciesState != null ? speciesState : ReactionSpeciesState.UNKNOWN;
        this.termOrder = termOrder;
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

    public ReactionSide getSide() {
        return side;
    }

    public BigInteger getCoefficient() {
        return coefficient;
    }

    public ReactionSpeciesState getSpeciesState() {
        return speciesState;
    }

    public int getTermOrder() {
        return termOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionTerm that = (ReactionTerm) o;
        return termOrder == that.termOrder &&
                Objects.equals(compoundId, that.compoundId) &&
                Objects.equals(compoundCode, that.compoundCode) &&
                Objects.equals(formula, that.formula) &&
                side == that.side &&
                Objects.equals(coefficient, that.coefficient) &&
                speciesState == that.speciesState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(compoundId, compoundCode, formula, side, coefficient, speciesState, termOrder);
    }
}

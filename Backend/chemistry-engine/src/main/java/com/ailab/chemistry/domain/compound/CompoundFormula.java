package com.ailab.chemistry.domain.compound;

import java.util.Objects;

public final class CompoundFormula {
    private final String originalFormula;
    private final String normalizedFormula;
    private final String compositionFormula;
    private final CompoundComposition composition;
    private final CompoundCharge netCharge;
    private final String hydrateInfo;

    public CompoundFormula(String originalFormula, String normalizedFormula, String compositionFormula, CompoundComposition composition, CompoundCharge netCharge, String hydrateInfo) {
        if (originalFormula == null || originalFormula.isBlank()) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_FORMULA, "Original formula cannot be blank");
        }
        if (normalizedFormula == null || normalizedFormula.isBlank()) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_FORMULA, "Normalized formula cannot be blank");
        }
        if (compositionFormula == null || compositionFormula.isBlank()) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_FORMULA, "Composition formula cannot be blank");
        }
        if (composition == null) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_FORMULA, "Composition cannot be null");
        }
        if (netCharge == null) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_FORMULA, "Net charge cannot be null");
        }
        this.originalFormula = originalFormula.trim();
        this.normalizedFormula = normalizedFormula.trim();
        this.compositionFormula = compositionFormula.trim();
        this.composition = composition;
        this.netCharge = netCharge;
        this.hydrateInfo = (hydrateInfo == null || hydrateInfo.isBlank()) ? null : hydrateInfo.trim();
    }

    public String getOriginalFormula() {
        return originalFormula;
    }

    public String getNormalizedFormula() {
        return normalizedFormula;
    }

    public String getCompositionFormula() {
        return compositionFormula;
    }

    public CompoundComposition getComposition() {
        return composition;
    }

    public CompoundCharge getNetCharge() {
        return netCharge;
    }

    public String getHydrateInfo() {
        return hydrateInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompoundFormula that = (CompoundFormula) o;
        return Objects.equals(originalFormula, that.originalFormula) &&
               Objects.equals(normalizedFormula, that.normalizedFormula) &&
               Objects.equals(compositionFormula, that.compositionFormula) &&
               Objects.equals(composition, that.composition) &&
               Objects.equals(netCharge, that.netCharge) &&
               Objects.equals(hydrateInfo, that.hydrateInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalFormula, normalizedFormula, compositionFormula, composition, netCharge, hydrateInfo);
    }

    @Override
    public String toString() {
        return normalizedFormula;
    }
}

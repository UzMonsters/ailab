package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.formula.ChemicalFormula;
import com.ailab.chemistry.domain.formula.DefaultFormulaParser;
import com.ailab.chemistry.domain.formula.ElementSymbol;
import com.ailab.chemistry.domain.formula.FormulaParser;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class ConjugatePair {

    private final String pairCode;
    private final String acidSpeciesCode;
    private final String baseSpeciesCode;

    public ConjugatePair(String pairCode, String acidSpeciesCode, String baseSpeciesCode) {
        if (pairCode == null || pairCode.isBlank() || acidSpeciesCode == null || acidSpeciesCode.isBlank() || baseSpeciesCode == null || baseSpeciesCode.isBlank()) {
            throw new AcidBaseException(AcidBaseErrorCode.CONJUGATE_PAIR_NOT_FOUND, "Pair code, acid code, and base code must not be null or blank");
        }
        this.pairCode = pairCode.trim();
        this.acidSpeciesCode = acidSpeciesCode.trim();
        this.baseSpeciesCode = baseSpeciesCode.trim();

        if (this.acidSpeciesCode.equalsIgnoreCase(this.baseSpeciesCode)) {
            throw new AcidBaseException(AcidBaseErrorCode.INVALID_PROTON_DIFFERENCE, "Acid and base species in conjugate pair must be distinct");
        }
    }

    public static void validateProtonDifference(ChemicalSpecies acid, ChemicalSpecies base) {
        Objects.requireNonNull(acid, "Acid species must not be null");
        Objects.requireNonNull(base, "Base species must not be null");

        // 1. Charge validation: acid.charge - base.charge == +1
        int chargeDiff = acid.getCharge().getValue() - base.getCharge().getValue();
        if (chargeDiff != 1) {
            throw new AcidBaseException(AcidBaseErrorCode.INVALID_PROTON_DIFFERENCE, "Conjugate pair acid (" + acid.getFormulaStr() + ") and base (" + base.getFormulaStr() + ") must have a charge difference of exactly +1 (found " + chargeDiff + ")");
        }

        // 2. Element counts validation via FormulaParser
        FormulaParser parser = new DefaultFormulaParser();
        ChemicalFormula acidFormula = parser.parse(acid.getFormulaStr());
        ChemicalFormula baseFormula = parser.parse(base.getFormulaStr());

        Map<ElementSymbol, BigInteger> acidCounts = acidFormula.getElementCounts();
        Map<ElementSymbol, BigInteger> baseCounts = baseFormula.getElementCounts();

        ElementSymbol hydrogen = new ElementSymbol("H");
        BigInteger acidH = acidCounts.getOrDefault(hydrogen, BigInteger.ZERO);
        BigInteger baseH = baseCounts.getOrDefault(hydrogen, BigInteger.ZERO);

        if (!acidH.equals(baseH.add(BigInteger.ONE))) {
            throw new AcidBaseException(AcidBaseErrorCode.INVALID_PROTON_DIFFERENCE, "Conjugate pair acid (" + acid.getFormulaStr() + ") must have exactly one additional H atom than base (" + base.getFormulaStr() + ")");
        }

        Set<ElementSymbol> allElements = new HashSet<>(acidCounts.keySet());
        allElements.addAll(baseCounts.keySet());
        allElements.remove(hydrogen);

        for (ElementSymbol symbol : allElements) {
            BigInteger acidCount = acidCounts.getOrDefault(symbol, BigInteger.ZERO);
            BigInteger baseCount = baseCounts.getOrDefault(symbol, BigInteger.ZERO);
            if (!acidCount.equals(baseCount)) {
                throw new AcidBaseException(AcidBaseErrorCode.INVALID_PROTON_DIFFERENCE, "Conjugate pair non-hydrogen element counts mismatch for element " + symbol.getSymbol() + ": acid=" + acidCount + ", base=" + baseCount);
            }
        }
    }

    public String getPairCode() {
        return pairCode;
    }

    public String getAcidSpeciesCode() {
        return acidSpeciesCode;
    }

    public String getBaseSpeciesCode() {
        return baseSpeciesCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConjugatePair that = (ConjugatePair) o;
        return pairCode.equalsIgnoreCase(that.pairCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pairCode.toUpperCase());
    }

    @Override
    public String toString() {
        return pairCode + ": Acid(" + acidSpeciesCode + ") / Base(" + baseSpeciesCode + ")";
    }
}

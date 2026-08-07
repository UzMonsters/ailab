package com.ailab.chemistry.api;

import java.math.BigDecimal;
import java.util.UUID;

public class CompoundSummary {
    private final UUID id;
    private final String compoundCode;
    private final String primaryName;
    private final String originalFormula;
    private final String normalizedFormula;
    private final String compositionFormula;
    private final int netCharge;
    private final BigDecimal molarMassValue;
    private final String molarMassUnit;

    public CompoundSummary(UUID id, String compoundCode, String primaryName, String originalFormula, String normalizedFormula, String compositionFormula, int netCharge, BigDecimal molarMassValue, String molarMassUnit) {
        this.id = id;
        this.compoundCode = compoundCode;
        this.primaryName = primaryName;
        this.originalFormula = originalFormula;
        this.normalizedFormula = normalizedFormula;
        this.compositionFormula = compositionFormula;
        this.netCharge = netCharge;
        this.molarMassValue = molarMassValue;
        this.molarMassUnit = molarMassUnit;
    }

    public UUID getId() { return id; }
    public String getCompoundCode() { return compoundCode; }
    public String getPrimaryName() { return primaryName; }
    public String getOriginalFormula() { return originalFormula; }
    public String getNormalizedFormula() { return normalizedFormula; }
    public String getCompositionFormula() { return compositionFormula; }
    public int getNetCharge() { return netCharge; }
    public BigDecimal getMolarMassValue() { return molarMassValue; }
    public String getMolarMassUnit() { return molarMassUnit; }
}

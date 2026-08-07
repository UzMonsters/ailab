package com.ailab.chemistry.api;

import java.util.Objects;

public final class ConjugatePairDetails {

    private final String pairCode;
    private final String acidSpeciesCode;
    private final String baseSpeciesCode;

    public ConjugatePairDetails(String pairCode, String acidSpeciesCode, String baseSpeciesCode) {
        this.pairCode = Objects.requireNonNull(pairCode);
        this.acidSpeciesCode = Objects.requireNonNull(acidSpeciesCode);
        this.baseSpeciesCode = Objects.requireNonNull(baseSpeciesCode);
    }

    public String getPairCode() { return pairCode; }
    public String getAcidSpeciesCode() { return acidSpeciesCode; }
    public String getBaseSpeciesCode() { return baseSpeciesCode; }
}

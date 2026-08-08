package com.ailab.chemistry.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class CompoundDetails {
    private final UUID id;
    private final String compoundCode;
    private final String primaryName;
    private final List<String> aliases;
    private final String originalFormula;
    private final String normalizedFormula;
    private final String compositionFormula;
    private final int netCharge;
    private final String hydrateInfo;
    private final BigDecimal molarMassValue;
    private final BigDecimal molarMassLowerBound;
    private final BigDecimal molarMassUpperBound;
    private final String molarMassKind;
    private final String molarMassUnit;
    private final List<ComponentDetail> components;
    private final List<String> externalIdentifiers;
    private final String catalogVersion;
    private final String sourceIdentifier;

    public CompoundDetails(UUID id, String compoundCode, String primaryName, List<String> aliases, String originalFormula, String normalizedFormula, String compositionFormula, int netCharge, String hydrateInfo, BigDecimal molarMassValue, BigDecimal molarMassLowerBound, BigDecimal molarMassUpperBound, String molarMassKind, String molarMassUnit, List<ComponentDetail> components, List<String> externalIdentifiers, String catalogVersion, String sourceIdentifier) {
        this.id = id;
        this.compoundCode = compoundCode;
        this.primaryName = primaryName;
        this.aliases = aliases;
        this.originalFormula = originalFormula;
        this.normalizedFormula = normalizedFormula;
        this.compositionFormula = compositionFormula;
        this.netCharge = netCharge;
        this.hydrateInfo = hydrateInfo;
        this.molarMassValue = molarMassValue;
        this.molarMassLowerBound = molarMassLowerBound;
        this.molarMassUpperBound = molarMassUpperBound;
        this.molarMassKind = molarMassKind;
        this.molarMassUnit = molarMassUnit;
        this.components = components;
        this.externalIdentifiers = externalIdentifiers;
        this.catalogVersion = catalogVersion;
        this.sourceIdentifier = sourceIdentifier;
    }

    public UUID getId() { return id; }
    public String getCompoundCode() { return compoundCode; }
    public String getPrimaryName() { return primaryName; }
    public List<String> getAliases() { return aliases; }
    public String getOriginalFormula() { return originalFormula; }
    public String getNormalizedFormula() { return normalizedFormula; }
    public String getCompositionFormula() { return compositionFormula; }
    public int getNetCharge() { return netCharge; }
    public String getHydrateInfo() { return hydrateInfo; }
    public BigDecimal getMolarMassValue() { return molarMassValue; }
    public BigDecimal getMolarMassLowerBound() { return molarMassLowerBound; }
    public BigDecimal getMolarMassUpperBound() { return molarMassUpperBound; }
    public String getMolarMassKind() { return molarMassKind; }
    public String getMolarMassUnit() { return molarMassUnit; }
    public List<ComponentDetail> getComponents() { return components; }
    public List<String> getExternalIdentifiers() { return externalIdentifiers; }
    public String getCatalogVersion() { return catalogVersion; }
    public String getSourceIdentifier() { return sourceIdentifier; }

    public static record ComponentDetail(int atomicNumber, String symbol, String atomCount) {}
}

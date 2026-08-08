package com.ailab.chemistry.api;

import java.math.BigDecimal;

public final class ElementDetails {
    private final int atomicNumber;
    private final String symbol;
    private final String name;
    private final String latinName;
    private final BigDecimal representativeMass;
    private final String massKind;
    private final BigDecimal lowerBound;
    private final BigDecimal upperBound;
    private final int period;
    private final Integer groupNumber;
    private final String block;
    private final String electronConfiguration;
    private final String electronConfigurationStatus;
    private final String standardState;
    private final String radioactivityStatus;
    private final String category;
    private final String series;
    private final String catalogVersion;
    private final String dataProvenance;

    public ElementDetails(int atomicNumber, String symbol, String name, String latinName,
                          BigDecimal representativeMass, String massKind, BigDecimal lowerBound, BigDecimal upperBound,
                          int period, Integer groupNumber, String block, String electronConfiguration,
                          String electronConfigurationStatus, String standardState, String radioactivityStatus,
                          String category, String series, String catalogVersion, String dataProvenance) {
        this.atomicNumber = atomicNumber;
        this.symbol = symbol;
        this.name = name;
        this.latinName = latinName;
        this.representativeMass = representativeMass;
        this.massKind = massKind;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.period = period;
        this.groupNumber = groupNumber;
        this.block = block;
        this.electronConfiguration = electronConfiguration;
        this.electronConfigurationStatus = electronConfigurationStatus;
        this.standardState = standardState;
        this.radioactivityStatus = radioactivityStatus;
        this.category = category;
        this.series = series;
        this.catalogVersion = catalogVersion;
        this.dataProvenance = dataProvenance;
    }

    public int getAtomicNumber() { return atomicNumber; }
    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public String getLatinName() { return latinName; }
    public BigDecimal getRepresentativeMass() { return representativeMass; }
    public String getMassKind() { return massKind; }
    public BigDecimal getLowerBound() { return lowerBound; }
    public BigDecimal getUpperBound() { return upperBound; }
    public int getPeriod() { return period; }
    public Integer getGroupNumber() { return groupNumber; }
    public String getBlock() { return block; }
    public String getElectronConfiguration() { return electronConfiguration; }
    public String getElectronConfigurationStatus() { return electronConfigurationStatus; }
    public String getStandardState() { return standardState; }
    public String getRadioactivityStatus() { return radioactivityStatus; }
    public String getCategory() { return category; }
    public String getSeries() { return series; }
    public String getCatalogVersion() { return catalogVersion; }
    public String getDataProvenance() { return dataProvenance; }
}

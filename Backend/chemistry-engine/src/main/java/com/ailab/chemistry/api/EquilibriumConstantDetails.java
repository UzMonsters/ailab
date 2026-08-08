package com.ailab.chemistry.api;

import java.math.BigDecimal;
import java.util.Objects;

public final class EquilibriumConstantDetails {

    private final String speciesCode;
    private final String type;
    private final int stepNumber;
    private final BigDecimal value;
    private final BigDecimal pValue;
    private final BigDecimal temperatureCelsius;
    private final String solventCode;

    public EquilibriumConstantDetails(String speciesCode, String type, int stepNumber, BigDecimal value, BigDecimal pValue, BigDecimal temperatureCelsius, String solventCode) {
        this.speciesCode = Objects.requireNonNull(speciesCode);
        this.type = Objects.requireNonNull(type);
        this.stepNumber = stepNumber;
        this.value = Objects.requireNonNull(value);
        this.pValue = Objects.requireNonNull(pValue);
        this.temperatureCelsius = Objects.requireNonNull(temperatureCelsius);
        this.solventCode = Objects.requireNonNull(solventCode);
    }

    public String getSpeciesCode() { return speciesCode; }
    public String getType() { return type; }
    public int getStepNumber() { return stepNumber; }
    public BigDecimal getValue() { return value; }
    public BigDecimal getPValue() { return pValue; }
    public BigDecimal getTemperatureCelsius() { return temperatureCelsius; }
    public String getSolventCode() { return solventCode; }
}

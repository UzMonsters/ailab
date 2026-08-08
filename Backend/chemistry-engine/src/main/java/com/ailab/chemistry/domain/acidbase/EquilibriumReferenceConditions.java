package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;

import java.util.Objects;

public final class EquilibriumReferenceConditions {

    public static final EquilibriumReferenceConditions STANDARD_WATER_25C = new EquilibriumReferenceConditions(
            Temperature.of("25.0", TemperatureUnit.CELSIUS),
            "COMP-H2O"
    );

    private final Temperature temperature;
    private final String solventCompoundCode;

    public EquilibriumReferenceConditions(Temperature temperature, String solventCompoundCode) {
        this.temperature = Objects.requireNonNull(temperature, "Temperature must not be null");
        if (solventCompoundCode == null || solventCompoundCode.isBlank()) {
            throw new AcidBaseException(AcidBaseErrorCode.MISSING_REFERENCE_CONDITIONS, "Solvent compound code must not be null or blank");
        }
        this.solventCompoundCode = solventCompoundCode.trim();
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public String getSolventCompoundCode() {
        return solventCompoundCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquilibriumReferenceConditions that = (EquilibriumReferenceConditions) o;
        return temperature.equals(that.temperature) && solventCompoundCode.equalsIgnoreCase(that.solventCompoundCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(temperature, solventCompoundCode.toUpperCase());
    }

    @Override
    public String toString() {
        return temperature + " in " + solventCompoundCode;
    }
}

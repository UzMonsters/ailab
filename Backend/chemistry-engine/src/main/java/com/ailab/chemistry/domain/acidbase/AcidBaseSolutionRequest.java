package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.math.BigDecimal;
import java.util.Objects;

public final class AcidBaseSolutionRequest {

    private final String speciesCode;
    private final MolarConcentration concentration;
    private final Temperature temperature;
    private final String solventCode;

    public AcidBaseSolutionRequest(String speciesCode, MolarConcentration concentration, Temperature temperature, String solventCode) {
        this.speciesCode = speciesCode;
        this.concentration = concentration;
        this.temperature = Objects.requireNonNull(temperature, "Temperature must not be null");
        this.solventCode = (solventCode == null || solventCode.isBlank()) ? "COMP-H2O" : solventCode.trim();

        if (!"COMP-H2O".equalsIgnoreCase(this.solventCode)) {
            throw new AcidBaseCalculationException(AcidBaseCalculationErrorCode.UNSUPPORTED_SOLVENT, "Only water solvent (COMP-H2O) is supported in Phase 7D: " + this.solventCode);
        }
    }

    public static AcidBaseSolutionRequest pureWater(Temperature temperature) {
        return new AcidBaseSolutionRequest("SPEC-H2O", null, temperature, "COMP-H2O");
    }

    public static AcidBaseSolutionRequest of(String speciesCode, MolarConcentration concentration, Temperature temperature) {
        if (concentration == null || concentration.in(MolarConcentrationUnit.MOL_PER_LITER).compareTo(BigDecimal.ZERO) <= 0) {
            throw new AcidBaseCalculationException(AcidBaseCalculationErrorCode.NON_POSITIVE_CONCENTRATION, "Concentration must be strictly positive (> 0)");
        }
        return new AcidBaseSolutionRequest(speciesCode, concentration, temperature, "COMP-H2O");
    }

    public String getSpeciesCode() {
        return speciesCode;
    }

    public MolarConcentration getConcentration() {
        return concentration;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public String getSolventCode() {
        return solventCode;
    }
}

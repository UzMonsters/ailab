package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public record ActivityCorrectionRequest(
        ActivityModel model,
        ActivityEquilibriumSystemType systemType,
        String speciesCode,
        MolarConcentration concentration,
        Temperature temperature,
        String solventCode,
        String spectatorIonCode,
        int spectatorIonCharge,
        BigDecimal spectatorStoichiometry,
        BigDecimal ka,
        BigDecimal kb,
        BigDecimal kw,
        PolyproticAcidFamily polyproticFamily,
        String acidFamilyCode,
        PolyproticInitialForm polyproticInitialForm,
        ActivityParameterSet parameterSet
) {
    public ActivityCorrectionRequest {
        Objects.requireNonNull(model, "model must not be null");
        Objects.requireNonNull(systemType, "systemType must not be null");
        Objects.requireNonNull(temperature, "temperature must not be null");
        if (solventCode == null || solventCode.isBlank()) {
            throw new ActivityException(ActivityErrorCode.UNSUPPORTED_SOLVENT, "Solvent code must not be blank");
        }
        solventCode = solventCode.trim();
        if (systemType != ActivityEquilibriumSystemType.PURE_WATER) {
            Objects.requireNonNull(concentration, "concentration must not be null");
            if (concentration.in(MolarConcentrationUnit.MOL_PER_LITER).compareTo(BigDecimal.ZERO) <= 0) {
                throw new ActivityException(ActivityErrorCode.NEGATIVE_CONCENTRATION, "Equilibrium concentration must be positive");
            }
        }
        spectatorStoichiometry = spectatorStoichiometry == null ? BigDecimal.ZERO : spectatorStoichiometry;
        if (spectatorStoichiometry.compareTo(BigDecimal.ZERO) < 0) {
            throw new ActivityException(ActivityErrorCode.INVALID_SPECIES_CHARGE, "Spectator stoichiometry must not be negative");
        }
        speciesCode = speciesCode == null || speciesCode.isBlank() ? null : speciesCode.trim();
        spectatorIonCode = spectatorIonCode == null || spectatorIonCode.isBlank() ? null : spectatorIonCode.trim();
        acidFamilyCode = acidFamilyCode == null || acidFamilyCode.isBlank() ? polyproticFamily == null ? null : polyproticFamily.familyCode() : acidFamilyCode.trim();
    }

    public static ActivityCorrectionRequest monoprotic(
            ActivityModel model,
            ActivityEquilibriumSystemType systemType,
            String speciesCode,
            MolarConcentration concentration,
            Temperature temperature,
            String solventCode,
            String spectatorIonCode,
            int spectatorIonCharge,
            BigDecimal ka,
            BigDecimal kb,
            BigDecimal kw,
            ActivityParameterSet parameterSet) {
        return new ActivityCorrectionRequest(model, systemType, speciesCode, concentration, temperature, solventCode,
                spectatorIonCode, spectatorIonCharge, spectatorIonCode == null ? BigDecimal.ZERO : BigDecimal.ONE,
                ka, kb, kw, null, null, null, parameterSet);
    }

    public static ActivityCorrectionRequest polyprotic(
            ActivityModel model,
            PolyproticAcidFamily family,
            PolyproticInitialForm initialForm,
            MolarConcentration concentration,
            Temperature temperature,
            String solventCode,
            String spectatorIonCode,
            int spectatorIonCharge,
            BigDecimal spectatorStoichiometry,
            BigDecimal kw,
            ActivityParameterSet parameterSet) {
        return new ActivityCorrectionRequest(model, ActivityEquilibriumSystemType.POLYPROTIC, null, concentration, temperature,
                solventCode, spectatorIonCode, spectatorIonCharge, spectatorStoichiometry, null, null, kw,
                family, family == null ? null : family.familyCode(), initialForm, parameterSet);
    }

    public static ActivityCorrectionRequest forSpecies(
            ActivityModel model,
            ActivityEquilibriumSystemType systemType,
            String speciesCode,
            MolarConcentration concentration,
            Temperature temperature,
            String solventCode) {
        return new ActivityCorrectionRequest(model, systemType, speciesCode, concentration, temperature, solventCode,
                null, 0, BigDecimal.ZERO, null, null, null, null, null, null, null);
    }

    public static ActivityCorrectionRequest forPolyprotic(
            ActivityModel model,
            String acidFamilyCode,
            PolyproticInitialForm initialForm,
            MolarConcentration concentration,
            Temperature temperature,
            String solventCode,
            String spectatorIonCode,
            BigDecimal spectatorStoichiometry) {
        return new ActivityCorrectionRequest(model, ActivityEquilibriumSystemType.POLYPROTIC, null, concentration, temperature,
                solventCode, spectatorIonCode, 0, spectatorStoichiometry, null, null, null,
                null, acidFamilyCode, initialForm, null);
    }

    public Optional<PolyproticAcidFamily> maybePolyproticFamily() {
        return Optional.ofNullable(polyproticFamily);
    }
}

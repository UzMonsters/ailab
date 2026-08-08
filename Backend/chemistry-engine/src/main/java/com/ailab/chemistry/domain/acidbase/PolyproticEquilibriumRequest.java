package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public record PolyproticEquilibriumRequest(
        PolyproticAcidFamily family,
        String acidFamilyCode,
        PolyproticInitialForm initialForm,
        MolarConcentration totalAnalyticalConcentration,
        Temperature temperature,
        String solventCode,
        String spectatorIonCode,
        int spectatorIonCharge,
        BigDecimal spectatorStoichiometry,
        BigDecimal kw
) {
    public PolyproticEquilibriumRequest {
        Objects.requireNonNull(initialForm, "initialForm must not be null");
        Objects.requireNonNull(totalAnalyticalConcentration, "totalAnalyticalConcentration must not be null");
        Objects.requireNonNull(temperature, "temperature must not be null");
        if (solventCode == null || solventCode.isBlank()) {
            throw new PolyproticException(PolyproticErrorCode.UNSUPPORTED_SOLVENT, "Solvent code must not be blank");
        }
        solventCode = solventCode.trim();
        if (totalAnalyticalConcentration.in(MolarConcentrationUnit.MOL_PER_LITER).compareTo(BigDecimal.ZERO) <= 0) {
            throw new PolyproticException(PolyproticErrorCode.NON_POSITIVE_CONCENTRATION, "Total analytical concentration must be positive");
        }
        spectatorStoichiometry = spectatorStoichiometry == null ? BigDecimal.ZERO : spectatorStoichiometry;
        if (spectatorStoichiometry.compareTo(BigDecimal.ZERO) < 0) {
            throw new PolyproticException(PolyproticErrorCode.INVALID_SPECTATOR_STOICHIOMETRY, "Spectator stoichiometry must not be negative");
        }
        if (requiresSpectator(initialForm) && (spectatorIonCode == null || spectatorIonCode.isBlank())) {
            throw new PolyproticException(PolyproticErrorCode.MISSING_SPECTATOR_ION, "Salt initial forms require explicit spectator ion information");
        }
        spectatorIonCode = spectatorIonCode == null || spectatorIonCode.isBlank() ? null : spectatorIonCode.trim();
        acidFamilyCode = acidFamilyCode == null || acidFamilyCode.isBlank() ? family == null ? null : family.familyCode() : acidFamilyCode.trim();
        if (family != null && (kw == null || kw.compareTo(BigDecimal.ZERO) <= 0)) {
            throw new PolyproticException(PolyproticErrorCode.INSUFFICIENT_REFERENCE_DATA, "Resolved polyprotic requests require positive Kw");
        }
    }

    public PolyproticEquilibriumRequest(
            PolyproticAcidFamily family,
            PolyproticInitialForm initialForm,
            MolarConcentration totalAnalyticalConcentration,
            Temperature temperature,
            String solventCode,
            String spectatorIonCode,
            int spectatorIonCharge,
            BigDecimal spectatorStoichiometry,
            BigDecimal kw) {
        this(family, family == null ? null : family.familyCode(), initialForm, totalAnalyticalConcentration, temperature, solventCode, spectatorIonCode, spectatorIonCharge, spectatorStoichiometry, kw);
    }

    public PolyproticEquilibriumRequest(
            String acidFamilyCode,
            PolyproticInitialForm initialForm,
            MolarConcentration totalAnalyticalConcentration,
            Temperature temperature,
            String solventCode,
            String spectatorIonCode,
            BigDecimal spectatorStoichiometry) {
        this(null, acidFamilyCode, initialForm, totalAnalyticalConcentration, temperature, solventCode, spectatorIonCode, 0, spectatorStoichiometry, null);
    }

    public Optional<PolyproticAcidFamily> maybeFamily() {
        return Optional.ofNullable(family);
    }

    private static boolean requiresSpectator(PolyproticInitialForm initialForm) {
        return initialForm == PolyproticInitialForm.INTERMEDIATE_AMPHIPROTIC_SALT
                || initialForm == PolyproticInitialForm.FULLY_DEPROTONATED_SALT;
    }
}

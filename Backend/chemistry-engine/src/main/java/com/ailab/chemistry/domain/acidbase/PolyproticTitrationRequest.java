package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public record PolyproticTitrationRequest(
        PolyproticAcidFamily family,
        String acidFamilyCode,
        PolyproticTitrationSystemType systemType,
        MolarConcentration analyteConcentration,
        Volume analyteVolume,
        MolarConcentration titrantConcentration,
        Temperature temperature,
        String solventCode,
        String analyteSpectatorIonCode,
        int analyteSpectatorIonCharge,
        String titrantSpectatorIonCode,
        int titrantSpectatorIonCharge,
        BigDecimal kw,
        BigDecimal volumeToleranceLiters
) {
    public PolyproticTitrationRequest {
        Objects.requireNonNull(systemType, "systemType must not be null");
        Objects.requireNonNull(analyteConcentration, "analyteConcentration must not be null");
        Objects.requireNonNull(analyteVolume, "analyteVolume must not be null");
        Objects.requireNonNull(titrantConcentration, "titrantConcentration must not be null");
        Objects.requireNonNull(temperature, "temperature must not be null");
        if (solventCode == null || solventCode.isBlank()) {
            throw new PolyproticTitrationException(PolyproticTitrationErrorCode.UNSUPPORTED_SOLVENT, "Solvent code must not be blank");
        }
        solventCode = solventCode.trim();
        if (analyteConcentration.in(MolarConcentrationUnit.MOL_PER_LITER).compareTo(BigDecimal.ZERO) <= 0
                || titrantConcentration.in(MolarConcentrationUnit.MOL_PER_LITER).compareTo(BigDecimal.ZERO) <= 0) {
            throw new PolyproticTitrationException(PolyproticTitrationErrorCode.NON_POSITIVE_CONCENTRATION, "Analyte and titrant concentrations must be positive");
        }
        if (analyteVolume.in(VolumeUnit.LITER).compareTo(BigDecimal.ZERO) <= 0) {
            throw new PolyproticTitrationException(PolyproticTitrationErrorCode.NON_POSITIVE_VOLUME, "Analyte volume must be positive");
        }
        volumeToleranceLiters = volumeToleranceLiters == null ? new BigDecimal("1e-8") : volumeToleranceLiters;
        if (volumeToleranceLiters.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PolyproticTitrationException(PolyproticTitrationErrorCode.NUMERICALLY_UNSAFE_REQUEST, "Volume tolerance must be positive");
        }
        acidFamilyCode = acidFamilyCode == null || acidFamilyCode.isBlank() ? family == null ? null : family.familyCode() : acidFamilyCode.trim();
        analyteSpectatorIonCode = analyteSpectatorIonCode == null || analyteSpectatorIonCode.isBlank() ? null : analyteSpectatorIonCode.trim();
        titrantSpectatorIonCode = titrantSpectatorIonCode == null || titrantSpectatorIonCode.isBlank() ? null : titrantSpectatorIonCode.trim();
        if (family != null && (kw == null || kw.compareTo(BigDecimal.ZERO) <= 0)) {
            throw new PolyproticTitrationException(PolyproticTitrationErrorCode.INSUFFICIENT_REFERENCE_DATA, "Resolved titration requests require positive Kw");
        }
        validateSpectatorShape(family != null, systemType, analyteSpectatorIonCode, analyteSpectatorIonCharge, titrantSpectatorIonCode, titrantSpectatorIonCharge);
    }

    public PolyproticTitrationRequest(
            PolyproticAcidFamily family,
            PolyproticTitrationSystemType systemType,
            MolarConcentration analyteConcentration,
            Volume analyteVolume,
            MolarConcentration titrantConcentration,
            Temperature temperature,
            String solventCode,
            String analyteSpectatorIonCode,
            int analyteSpectatorIonCharge,
            String titrantSpectatorIonCode,
            int titrantSpectatorIonCharge,
            BigDecimal kw,
            BigDecimal volumeToleranceLiters) {
        this(family, family == null ? null : family.familyCode(), systemType, analyteConcentration, analyteVolume, titrantConcentration,
                temperature, solventCode, analyteSpectatorIonCode, analyteSpectatorIonCharge, titrantSpectatorIonCode,
                titrantSpectatorIonCharge, kw, volumeToleranceLiters);
    }

    public PolyproticTitrationRequest(
            String acidFamilyCode,
            PolyproticTitrationSystemType systemType,
            MolarConcentration analyteConcentration,
            Volume analyteVolume,
            MolarConcentration titrantConcentration,
            Temperature temperature,
            String solventCode,
            String analyteSpectatorIonCode,
            String titrantSpectatorIonCode,
            BigDecimal volumeToleranceLiters) {
        this(null, acidFamilyCode, systemType, analyteConcentration, analyteVolume, titrantConcentration, temperature,
                solventCode, analyteSpectatorIonCode, 0, titrantSpectatorIonCode, 0, null, volumeToleranceLiters);
    }

    public Optional<PolyproticAcidFamily> maybeFamily() {
        return Optional.ofNullable(family);
    }

    private static void validateSpectatorShape(
            boolean resolved,
            PolyproticTitrationSystemType systemType,
            String analyteSpectatorIonCode,
            int analyteSpectatorIonCharge,
            String titrantSpectatorIonCode,
            int titrantSpectatorIonCharge) {
        if (requiresAnalyteSpectator(systemType) && (analyteSpectatorIonCode == null || resolved && analyteSpectatorIonCharge == 0)) {
            throw new PolyproticTitrationException(PolyproticTitrationErrorCode.INVALID_SPECTATOR_ION, "Salt analytes require explicit spectator ion charge");
        }
        if (!requiresAnalyteSpectator(systemType) && analyteSpectatorIonCharge != 0) {
            throw new PolyproticTitrationException(PolyproticTitrationErrorCode.INVALID_SPECTATOR_ION, "Neutral acid analytes must not include analyte spectator charge");
        }
        if (titrantSpectatorIonCode == null || resolved && titrantSpectatorIonCharge == 0) {
            throw new PolyproticTitrationException(PolyproticTitrationErrorCode.INVALID_SPECTATOR_ION, "Strong titrant requires explicit spectator ion charge");
        }
        if (!resolved) {
            return;
        }
        boolean acidTitrant = isAcidTitrant(systemType);
        if (acidTitrant && titrantSpectatorIonCharge >= 0) {
            throw new PolyproticTitrationException(PolyproticTitrationErrorCode.INVALID_SPECTATOR_ION, "Strong acid titrant requires an anion spectator");
        }
        if (!acidTitrant && titrantSpectatorIonCharge <= 0) {
            throw new PolyproticTitrationException(PolyproticTitrationErrorCode.INVALID_SPECTATOR_ION, "Strong base titrant requires a cation spectator");
        }
    }

    static boolean isAcidTitrant(PolyproticTitrationSystemType systemType) {
        return systemType == PolyproticTitrationSystemType.FULLY_DEPROTONATED_SALT_WITH_STRONG_MONOPROTIC_ACID
                || systemType == PolyproticTitrationSystemType.AMPHIPROTIC_SALT_WITH_STRONG_MONOPROTIC_ACID;
    }

    static boolean requiresAnalyteSpectator(PolyproticTitrationSystemType systemType) {
        return systemType == PolyproticTitrationSystemType.FULLY_DEPROTONATED_SALT_WITH_STRONG_MONOPROTIC_ACID
                || systemType == PolyproticTitrationSystemType.AMPHIPROTIC_SALT_WITH_STRONG_MONOPROTIC_ACID
                || systemType == PolyproticTitrationSystemType.AMPHIPROTIC_SALT_WITH_STRONG_MONOBASIC_BASE;
    }
}

package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record TitrationRequest(
        TitrationSystemType systemType,
        String analyteSpeciesCode,
        String titrantSpeciesCode,
        MolarConcentration analyteConcentration,
        Volume analyteVolume,
        MolarConcentration titrantConcentration,
        Temperature temperature,
        String solventCode,
        BigDecimal ka,
        BigDecimal kb,
        BigDecimal kw,
        List<String> sources
) {
    public TitrationRequest {
        if (systemType == TitrationSystemType.UNSUPPORTED_WEAK_ACID_WEAK_BASE) {
            throw new TitrationException(TitrationErrorCode.UNSUPPORTED_TITRATION_SYSTEM, "Weak acid plus weak base titration is not supported");
        }
        analyteSpeciesCode = requireText(analyteSpeciesCode, "analyteSpeciesCode");
        titrantSpeciesCode = requireText(titrantSpeciesCode, "titrantSpeciesCode");
        Objects.requireNonNull(analyteConcentration, "analyteConcentration must not be null");
        Objects.requireNonNull(analyteVolume, "analyteVolume must not be null");
        Objects.requireNonNull(titrantConcentration, "titrantConcentration must not be null");
        Objects.requireNonNull(temperature, "temperature must not be null");
        solventCode = requireText(solventCode, "solventCode");
        validatePositive(analyteConcentration.in(MolarConcentrationUnit.MOL_PER_LITER), TitrationErrorCode.NON_POSITIVE_CONCENTRATION, "Analyte concentration must be positive");
        validatePositive(titrantConcentration.in(MolarConcentrationUnit.MOL_PER_LITER), TitrationErrorCode.NON_POSITIVE_CONCENTRATION, "Titrant concentration must be positive");
        validatePositive(analyteVolume.in(VolumeUnit.LITER), TitrationErrorCode.NON_POSITIVE_VOLUME, "Analyte volume must be positive");
        if (systemType != null) {
            kw = requirePositive(kw, "Kw");
            if (systemType == TitrationSystemType.WEAK_ACID_STRONG_BASE) {
                ka = requirePositive(ka, "Ka");
            }
            if (systemType == TitrationSystemType.WEAK_BASE_STRONG_ACID) {
                kb = requirePositive(kb, "Kb");
            }
        }
        sources = List.copyOf(sources == null ? List.of() : sources);
    }

    public TitrationRequest(
            String analyteSpeciesCode,
            String titrantSpeciesCode,
            MolarConcentration analyteConcentration,
            Volume analyteVolume,
            MolarConcentration titrantConcentration,
            Temperature temperature,
            String solventCode) {
        this(null, analyteSpeciesCode, titrantSpeciesCode, analyteConcentration, analyteVolume, titrantConcentration, temperature, solventCode, null, null, null, List.of());
    }

    public TitrationRequest withResolvedSystem(TitrationSystemType resolvedType, BigDecimal resolvedKa, BigDecimal resolvedKb, BigDecimal resolvedKw, List<String> resolvedSources) {
        return new TitrationRequest(resolvedType, analyteSpeciesCode, titrantSpeciesCode, analyteConcentration, analyteVolume, titrantConcentration, temperature, solventCode, resolvedKa, resolvedKb, resolvedKw, resolvedSources);
    }

    public Optional<TitrationSystemType> maybeSystemType() {
        return Optional.ofNullable(systemType);
    }

    public TitrationSystemType getSystemType() {
        return systemType;
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new TitrationException(TitrationErrorCode.MISMATCHED_SPECIES_ROLES, name + " must not be blank");
        }
        return value.trim();
    }

    private static void validatePositive(BigDecimal value, TitrationErrorCode code, String message) {
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TitrationException(code, message);
        }
    }

    private static BigDecimal requirePositive(BigDecimal value, String name) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TitrationException(TitrationErrorCode.INSUFFICIENT_REFERENCE_DATA, name + " must be positive");
        }
        return value;
    }
}

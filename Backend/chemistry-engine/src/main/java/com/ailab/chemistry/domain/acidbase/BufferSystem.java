package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.Temperature;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record BufferSystem(
        String pairCode,
        String acidSpeciesCode,
        String baseSpeciesCode,
        BufferSystemType systemType,
        BigDecimal ka,
        BigDecimal kb,
        BigDecimal kw,
        Temperature temperature,
        String solventCode,
        List<String> sources
) {
    public BufferSystem {
        pairCode = requireText(pairCode, "pairCode");
        acidSpeciesCode = requireText(acidSpeciesCode, "acidSpeciesCode");
        baseSpeciesCode = requireText(baseSpeciesCode, "baseSpeciesCode");
        Objects.requireNonNull(systemType, "systemType must not be null");
        ka = requirePositive(ka, "Ka");
        kb = requirePositive(kb, "Kb");
        kw = requirePositive(kw, "Kw");
        Objects.requireNonNull(temperature, "temperature must not be null");
        solventCode = requireText(solventCode, "solventCode");
        sources = List.copyOf(sources == null ? List.of() : sources);
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new BufferException(BufferErrorCode.INSUFFICIENT_REFERENCE_DATA, name + " must not be blank");
        }
        return value.trim();
    }

    private static BigDecimal requirePositive(BigDecimal value, String name) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BufferException(BufferErrorCode.INSUFFICIENT_REFERENCE_DATA, name + " must be positive");
        }
        return value;
    }

    public BufferSystemType getSystemType() {
        return systemType;
    }
}

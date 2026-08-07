package com.ailab.chemistry.domain.thermodynamics;

import java.math.BigDecimal;
import java.util.Objects;

public record PolynomialCoefficientSet(
        BigDecimal a,
        BigDecimal b,
        BigDecimal c,
        BigDecimal d,
        BigDecimal e,
        BigDecimal f,
        BigDecimal g,
        BigDecimal h) {

    public PolynomialCoefficientSet(String a, String b, String c, String d, String e, String f, String g, String h) {
        this(decimal(a), decimal(b), decimal(c), decimal(d), decimal(e), decimal(f), decimal(g), decimal(h));
    }

    public PolynomialCoefficientSet {
        Objects.requireNonNull(a, "a must not be null");
        Objects.requireNonNull(b, "b must not be null");
        Objects.requireNonNull(c, "c must not be null");
        Objects.requireNonNull(d, "d must not be null");
        Objects.requireNonNull(e, "e must not be null");
        Objects.requireNonNull(f, "f must not be null");
        Objects.requireNonNull(g, "g must not be null");
        Objects.requireNonNull(h, "h must not be null");
    }

    private static BigDecimal decimal(String value) {
        if (value == null || value.isBlank()) {
            throw new TemperatureCorrectionException(TemperatureCorrectionErrorCode.INVALID_CORRELATION,
                    "Coefficient values must be present");
        }
        return new BigDecimal(value);
    }
}

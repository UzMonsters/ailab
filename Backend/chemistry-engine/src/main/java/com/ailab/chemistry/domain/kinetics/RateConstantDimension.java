package com.ailab.chemistry.domain.kinetics;

import java.math.BigDecimal;
import java.util.Objects;

public record RateConstantDimension(BigDecimal order, String canonicalUnitSymbol) {
    public RateConstantDimension {
        Objects.requireNonNull(order, "order must not be null");
        canonicalUnitSymbol = canonicalUnitSymbol == null ? deriveUnitSymbol(order) : canonicalUnitSymbol;
    }

    public static RateConstantDimension ZERO_ORDER = new RateConstantDimension(BigDecimal.ZERO, "mol/(L*s)");
    public static RateConstantDimension FIRST_ORDER = new RateConstantDimension(BigDecimal.ONE, "1/s");
    public static RateConstantDimension SECOND_ORDER = new RateConstantDimension(new BigDecimal("2"), "L/(mol*s)");

    public static RateConstantDimension ofOrder(BigDecimal order) {
        if (order.compareTo(BigDecimal.ZERO) == 0) return ZERO_ORDER;
        if (order.compareTo(BigDecimal.ONE) == 0) return FIRST_ORDER;
        if (order.compareTo(new BigDecimal("2")) == 0) return SECOND_ORDER;
        return new RateConstantDimension(order, deriveUnitSymbol(order));
    }

    private static String deriveUnitSymbol(BigDecimal n) {
        if (n.compareTo(BigDecimal.ZERO) == 0) return "mol/(L*s)";
        if (n.compareTo(BigDecimal.ONE) == 0) return "1/s";
        if (n.compareTo(new BigDecimal("2")) == 0) return "L/(mol*s)";
        BigDecimal power = BigDecimal.ONE.subtract(n);
        return "(mol/L)^(" + power.toPlainString() + ")/s";
    }
}

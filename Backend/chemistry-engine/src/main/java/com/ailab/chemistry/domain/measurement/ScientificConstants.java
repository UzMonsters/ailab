package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;

public final class ScientificConstants {
    private ScientificConstants() {}

    /**
     * Avogadro constant: 6.02214076 × 10²³ mol⁻¹
     * Defined as exact.
     */
    public static final BigDecimal AVOGADRO_CONSTANT = new BigDecimal("6.02214076e23");

    /**
     * Ideal gas constant: 8.31446261815324 J·mol⁻¹·K⁻¹
     * SI units.
     */
    public static final BigDecimal IDEAL_GAS_CONSTANT_SI = new BigDecimal("8.31446261815324");

    /**
     * Standard atmospheric pressure: 101325 Pa
     * Exact definition.
     */
    public static final BigDecimal STANDARD_ATMOSPHERIC_PRESSURE = new BigDecimal("101325");

    /**
     * Celsius-Kelvin temperature offset: 273.15
     * Exact.
     */
    public static final BigDecimal CELSIUS_TO_KELVIN_OFFSET = new BigDecimal("273.15");
}

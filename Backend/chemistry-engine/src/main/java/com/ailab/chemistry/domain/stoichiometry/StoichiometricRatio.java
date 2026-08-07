package com.ailab.chemistry.domain.stoichiometry;

import com.ailab.chemistry.domain.measurement.ScientificMath;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public final class StoichiometricRatio {

    private final String fromCompoundCode;
    private final String toCompoundCode;
    private final BigInteger fromCoefficient;
    private final BigInteger toCoefficient;

    public StoichiometricRatio(String fromCompoundCode, String toCompoundCode, BigInteger fromCoefficient, BigInteger toCoefficient) {
        if (fromCompoundCode == null || fromCompoundCode.isBlank() || toCompoundCode == null || toCompoundCode.isBlank()) {
            throw new StoichiometryException(StoichiometryErrorCode.COMPOUND_NOT_FOUND, "Compound codes must not be blank");
        }
        if (fromCoefficient == null || fromCoefficient.compareTo(BigInteger.ZERO) <= 0 || toCoefficient == null || toCoefficient.compareTo(BigInteger.ZERO) <= 0) {
            throw new StoichiometryException(StoichiometryErrorCode.INVALID_COEFFICIENT, "Coefficients must be strictly positive whole numbers");
        }
        this.fromCompoundCode = fromCompoundCode.trim();
        this.toCompoundCode = toCompoundCode.trim();
        this.fromCoefficient = fromCoefficient;
        this.toCoefficient = toCoefficient;
    }

    public String getFromCompoundCode() {
        return fromCompoundCode;
    }

    public String getToCompoundCode() {
        return toCompoundCode;
    }

    public BigInteger getFromCoefficient() {
        return fromCoefficient;
    }

    public BigInteger getToCoefficient() {
        return toCoefficient;
    }

    public BigDecimal getRatio() {
        return new BigDecimal(toCoefficient).divide(new BigDecimal(fromCoefficient), ScientificMath.CALCULATION_CONTEXT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoichiometricRatio that = (StoichiometricRatio) o;
        return fromCompoundCode.equalsIgnoreCase(that.fromCompoundCode) &&
                toCompoundCode.equalsIgnoreCase(that.toCompoundCode) &&
                fromCoefficient.equals(that.fromCoefficient) &&
                toCoefficient.equals(that.toCoefficient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromCompoundCode.toUpperCase(), toCompoundCode.toUpperCase(), fromCoefficient, toCoefficient);
    }

    @Override
    public String toString() {
        return fromCompoundCode + " -> " + toCompoundCode + " (" + toCoefficient + ":" + fromCoefficient + ")";
    }
}

package com.ailab.chemistry.domain.equation;

import java.math.BigInteger;
import java.util.Objects;

public final class RationalNumber extends Number implements Comparable<RationalNumber> {
    public static final RationalNumber ZERO = new RationalNumber(BigInteger.ZERO, BigInteger.ONE);
    public static final RationalNumber ONE = new RationalNumber(BigInteger.ONE, BigInteger.ONE);

    private final BigInteger numerator;
    private final BigInteger denominator;

    public RationalNumber(BigInteger numerator, BigInteger denominator) {
        Objects.requireNonNull(numerator, "Numerator must not be null");
        Objects.requireNonNull(denominator, "Denominator must not be null");
        if (denominator.equals(BigInteger.ZERO)) {
            throw new ArithmeticException("Denominator cannot be zero");
        }

        // Keep denominator positive
        BigInteger num = numerator;
        BigInteger den = denominator;
        if (den.compareTo(BigInteger.ZERO) < 0) {
            num = num.negate();
            den = den.negate();
        }

        // Reduce by GCD
        BigInteger gcd = num.abs().gcd(den);
        if (gcd.compareTo(BigInteger.ONE) > 0) {
            num = num.divide(gcd);
            den = den.divide(gcd);
        }

        // Normalize zero
        if (num.equals(BigInteger.ZERO)) {
            den = BigInteger.ONE;
        }

        this.numerator = num;
        this.denominator = den;
    }

    public static RationalNumber of(long numerator, long denominator) {
        return new RationalNumber(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
    }

    public static RationalNumber of(BigInteger numerator, BigInteger denominator) {
        return new RationalNumber(numerator, denominator);
    }

    public static RationalNumber of(BigInteger value) {
        return new RationalNumber(value, BigInteger.ONE);
    }

    public BigInteger getNumerator() {
        return numerator;
    }

    public BigInteger getDenominator() {
        return denominator;
    }

    public RationalNumber add(RationalNumber other) {
        Objects.requireNonNull(other, "Other rational must not be null");
        BigInteger num = this.numerator.multiply(other.denominator)
                .add(other.numerator.multiply(this.denominator));
        BigInteger den = this.denominator.multiply(other.denominator);
        return new RationalNumber(num, den);
    }

    public RationalNumber subtract(RationalNumber other) {
        Objects.requireNonNull(other, "Other rational must not be null");
        BigInteger num = this.numerator.multiply(other.denominator)
                .subtract(other.numerator.multiply(this.denominator));
        BigInteger den = this.denominator.multiply(other.denominator);
        return new RationalNumber(num, den);
    }

    public RationalNumber multiply(RationalNumber other) {
        Objects.requireNonNull(other, "Other rational must not be null");
        BigInteger num = this.numerator.multiply(other.numerator);
        BigInteger den = this.denominator.multiply(other.denominator);
        return new RationalNumber(num, den);
    }

    public RationalNumber divide(RationalNumber other) {
        Objects.requireNonNull(other, "Other rational must not be null");
        if (other.numerator.equals(BigInteger.ZERO)) {
            throw new ArithmeticException("Division by zero");
        }
        BigInteger num = this.numerator.multiply(other.denominator);
        BigInteger den = this.denominator.multiply(other.numerator);
        return new RationalNumber(num, den);
    }

    public RationalNumber negate() {
        return new RationalNumber(this.numerator.negate(), this.denominator);
    }

    public RationalNumber reciprocal() {
        if (numerator.equals(BigInteger.ZERO)) {
            throw new ArithmeticException("Reciprocal of zero is undefined");
        }
        return new RationalNumber(this.denominator, this.numerator);
    }

    public boolean isZero() {
        return numerator.equals(BigInteger.ZERO);
    }

    @Override
    public int intValue() {
        return numerator.divide(denominator).intValue();
    }

    @Override
    public long longValue() {
        return numerator.divide(denominator).longValue();
    }

    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    @Override
    public double doubleValue() {
        return numerator.doubleValue() / denominator.doubleValue();
    }

    @Override
    public int compareTo(RationalNumber other) {
        Objects.requireNonNull(other, "Other rational must not be null");
        return this.numerator.multiply(other.denominator)
                .compareTo(other.numerator.multiply(this.denominator));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RationalNumber that = (RationalNumber) o;
        return numerator.equals(that.numerator) && denominator.equals(that.denominator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }

    @Override
    public String toString() {
        if (denominator.equals(BigInteger.ONE)) {
            return numerator.toString();
        }
        return numerator + "/" + denominator;
    }
}

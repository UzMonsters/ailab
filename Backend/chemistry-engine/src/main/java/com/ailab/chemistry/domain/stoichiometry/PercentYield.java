package com.ailab.chemistry.domain.stoichiometry;

import com.ailab.chemistry.domain.measurement.ScientificMath;

import java.math.BigDecimal;
import java.util.Objects;

public final class PercentYield {

    private final BigDecimal percentage;
    private final YieldStatus status;

    public PercentYield(BigDecimal percentage) {
        Objects.requireNonNull(percentage, "Percentage must not be null");
        if (percentage.compareTo(BigDecimal.ZERO) < 0) {
            throw new StoichiometryException(StoichiometryErrorCode.INVALID_YIELD, "Percent yield cannot be negative: " + percentage);
        }
        this.percentage = percentage;

        if (percentage.compareTo(BigDecimal.ZERO) == 0) {
            this.status = YieldStatus.ZERO_YIELD;
        } else if (percentage.compareTo(new BigDecimal("100")) > 0) {
            this.status = YieldStatus.ABOVE_THEORETICAL;
        } else {
            this.status = YieldStatus.NORMAL;
        }
    }

    public static PercentYield of(BigDecimal actual, BigDecimal theoretical) {
        Objects.requireNonNull(actual, "Actual mass must not be null");
        Objects.requireNonNull(theoretical, "Theoretical mass must not be null");
        if (theoretical.compareTo(BigDecimal.ZERO) <= 0) {
            throw new StoichiometryException(StoichiometryErrorCode.INVALID_YIELD, "Theoretical yield must be positive for percent yield calculation");
        }
        BigDecimal pct = actual.divide(theoretical, ScientificMath.CALCULATION_CONTEXT)
                .multiply(new BigDecimal("100"), ScientificMath.CALCULATION_CONTEXT);
        return new PercentYield(pct);
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public YieldStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PercentYield that = (PercentYield) o;
        return percentage.compareTo(that.percentage) == 0 && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ScientificMath.scaleIndependentHashCode(percentage), status);
    }

    @Override
    public String toString() {
        return percentage.stripTrailingZeros().toPlainString() + "% (" + status + ")";
    }
}

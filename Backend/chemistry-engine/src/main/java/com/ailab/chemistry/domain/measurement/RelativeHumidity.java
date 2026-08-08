package com.ailab.chemistry.domain.measurement;

import java.math.BigDecimal;
import java.util.Objects;

public record RelativeHumidity(BigDecimal valuePercent) implements Comparable<RelativeHumidity> {
    public RelativeHumidity {
        Objects.requireNonNull(valuePercent, "valuePercent must not be null");
        if (valuePercent.compareTo(BigDecimal.ZERO) < 0 || valuePercent.compareTo(new BigDecimal("100")) > 0) {
            throw new com.ailab.chemistry.domain.labenvironment.EnvironmentException(
                    com.ailab.chemistry.domain.labenvironment.EnvironmentErrorCode.INVALID_HUMIDITY,
                    "Relative humidity must be from 0% through 100%: " + valuePercent);
        }
    }

    public static RelativeHumidity percent(String value) {
        return new RelativeHumidity(new BigDecimal(value));
    }

    @Override
    public int compareTo(RelativeHumidity other) {
        return valuePercent.compareTo(other.valuePercent);
    }
}

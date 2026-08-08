package com.ailab.chemistry.domain.acidbase;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AcidBaseDecimalMathGovernanceTest {

    @Test
    void logarithmAndPowerHaveAnchoredValues() {
        assertThat(AcidBaseDecimalMath.log10(BigDecimal.ONE)).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(AcidBaseDecimalMath.log10(BigDecimal.TEN)).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(AcidBaseDecimalMath.tenPower(BigDecimal.ZERO)).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(AcidBaseDecimalMath.tenPower(BigDecimal.ONE)).isEqualByComparingTo(BigDecimal.TEN);
    }

    @Test
    void roundTripAcrossDocumentedRatioRangeIsWithinTolerance() {
        for (BigDecimal ratio : representativeRatios()) {
            BigDecimal actual = AcidBaseDecimalMath.tenPower(AcidBaseDecimalMath.log10(ratio));
            assertThat(actual).isCloseTo(ratio, org.assertj.core.data.Offset.offset(ratio.multiply(new BigDecimal("1e-12"))));
        }
    }

    @Test
    void logarithmAndPowerAreMonotonicAcrossDocumentedRange() {
        BigDecimal previousLog = null;
        BigDecimal previousPower = null;
        for (BigDecimal ratio : representativeRatios()) {
            BigDecimal log = AcidBaseDecimalMath.log10(ratio);
            BigDecimal power = AcidBaseDecimalMath.tenPower(log);
            assertThat(log).isNotNull();
            assertThat(power).isNotNull();
            if (previousLog != null) {
                assertThat(log).isGreaterThan(previousLog);
                assertThat(power).isGreaterThan(previousPower);
            }
            previousLog = log;
            previousPower = power;
        }
    }

    @Test
    void repeatedExecutionIsDeterministicAndPreparationToleranceIsStable() {
        BigDecimal first = AcidBaseDecimalMath.tenPower(AcidBaseDecimalMath.log10(new BigDecimal("12345.6789")));
        BigDecimal second = AcidBaseDecimalMath.tenPower(AcidBaseDecimalMath.log10(new BigDecimal("12345.6789")));
        assertThat(second).isEqualByComparingTo(first);

        BigDecimal targetRatio = AcidBaseDecimalMath.tenPower(new BigDecimal("1.0000"));
        assertThat(targetRatio).isCloseTo(BigDecimal.TEN, org.assertj.core.data.Offset.offset(new BigDecimal("1e-12")));
    }

    @Test
    void rejectsUnsafeInputsInsteadOfReturningNanOrInfinity() {
        assertThatThrownBy(() -> AcidBaseDecimalMath.log10(BigDecimal.ZERO))
                .isInstanceOf(BufferException.class)
                .extracting("errorCode")
                .isEqualTo(BufferErrorCode.NUMERICALLY_UNSAFE_REQUEST);

        assertThatThrownBy(() -> AcidBaseDecimalMath.tenPower(new BigDecimal("1000000")))
                .isInstanceOf(BufferException.class)
                .extracting("errorCode")
                .isEqualTo(BufferErrorCode.NUMERICALLY_UNSAFE_REQUEST);
    }

    private static List<BigDecimal> representativeRatios() {
        return List.of(
                new BigDecimal("1e-12"),
                new BigDecimal("1e-9"),
                new BigDecimal("1e-6"),
                new BigDecimal("1e-3"),
                BigDecimal.ONE,
                new BigDecimal("1e3"),
                new BigDecimal("1e6"),
                new BigDecimal("1e9"),
                new BigDecimal("1e12")
        );
    }
}

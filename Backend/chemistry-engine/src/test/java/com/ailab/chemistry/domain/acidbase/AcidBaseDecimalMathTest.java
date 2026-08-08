package com.ailab.chemistry.domain.acidbase;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AcidBaseDecimalMathTest {

    @Test
    void acidBaseCalculatorsShareSingleDecimalTranscendentalUtility() {
        assertThat(AcidBaseDecimalMath.log10(BigDecimal.ONE)).isEqualByComparingTo("0");
        assertThat(AcidBaseDecimalMath.log10(BigDecimal.TEN)).isEqualByComparingTo("1");
        assertThat(AcidBaseDecimalMath.tenPower(BigDecimal.ZERO)).isEqualByComparingTo("1");
        assertThat(AcidBaseDecimalMath.tenPower(BigDecimal.ONE)).isEqualByComparingTo("10");
    }
}

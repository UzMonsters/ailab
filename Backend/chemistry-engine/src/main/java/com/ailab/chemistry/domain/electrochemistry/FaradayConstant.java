package com.ailab.chemistry.domain.electrochemistry;

import java.math.BigDecimal;

public record FaradayConstant(BigDecimal coulombsPerMole, String unit, String source, String version) {
    public static final FaradayConstant CODATA_2018_EXACT = new FaradayConstant(
            new BigDecimal("96485.3321233100184"),
            "C mol^-1",
            "2019 SI exact e*N_A",
            "CODATA-2018-EXACT");
}

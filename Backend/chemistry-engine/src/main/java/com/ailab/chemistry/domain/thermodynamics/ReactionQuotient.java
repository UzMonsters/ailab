package com.ailab.chemistry.domain.thermodynamics;

import java.math.BigDecimal;
import java.util.Optional;

public record ReactionQuotient(
        BigDecimal lnQ,
        BigDecimal log10Q,
        Optional<BigDecimal> directQ) {

    public ReactionQuotient {
        directQ = directQ == null ? Optional.empty() : directQ;
    }
}

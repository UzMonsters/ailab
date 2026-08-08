package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.Temperature;

import java.math.BigDecimal;
import java.util.Objects;

public record ActivityParameterSet(
        ActivityModel model,
        String solventCode,
        Temperature temperature,
        BigDecimal daviesA,
        BigDecimal minimumIonicStrength,
        BigDecimal maximumIonicStrength,
        String sourceDocument,
        String evidence,
        String license
) {
    public ActivityParameterSet {
        Objects.requireNonNull(model, "model must not be null");
        if (solventCode == null || solventCode.isBlank()) {
            throw new ActivityException(ActivityErrorCode.UNSUPPORTED_SOLVENT, "Solvent code must not be blank");
        }
        solventCode = solventCode.trim();
        Objects.requireNonNull(temperature, "temperature must not be null");
        Objects.requireNonNull(daviesA, "daviesA must not be null");
        Objects.requireNonNull(minimumIonicStrength, "minimumIonicStrength must not be null");
        Objects.requireNonNull(maximumIonicStrength, "maximumIonicStrength must not be null");
        if (model == ActivityModel.DAVIES && daviesA.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ActivityException(ActivityErrorCode.NON_POSITIVE_PARAMETER, "Davies A parameter must be positive");
        }
        if (minimumIonicStrength.compareTo(BigDecimal.ZERO) < 0 || maximumIonicStrength.compareTo(minimumIonicStrength) < 0) {
            throw new ActivityException(ActivityErrorCode.OUTSIDE_MODEL_VALIDITY_RANGE, "Invalid ionic-strength validity range");
        }
        sourceDocument = sourceDocument == null ? "" : sourceDocument.trim();
        evidence = evidence == null ? "" : evidence.trim();
        license = license == null ? "" : license.trim();
    }
}

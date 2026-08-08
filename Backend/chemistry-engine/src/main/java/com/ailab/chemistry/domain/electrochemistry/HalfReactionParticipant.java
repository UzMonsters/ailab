package com.ailab.chemistry.domain.electrochemistry;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public record HalfReactionParticipant(
        String speciesCode,
        String displayFormula,
        Map<String, BigDecimal> formula,
        BigDecimal coefficient,
        String phase,
        int charge,
        HalfReactionParticipantSide side
) {
    public HalfReactionParticipant {
        if (speciesCode == null || speciesCode.isBlank()) {
            throw new ElectrochemicalException(ElectrochemicalErrorCode.INVALID_HALF_REACTION, "Participant species code is required");
        }
        if (displayFormula == null || displayFormula.isBlank()) {
            throw new ElectrochemicalException(ElectrochemicalErrorCode.INVALID_HALF_REACTION, "Participant formula label is required");
        }
        formula = Map.copyOf(Objects.requireNonNull(formula, "formula must not be null"));
        if (formula.isEmpty()) {
            throw new ElectrochemicalException(ElectrochemicalErrorCode.INVALID_HALF_REACTION, "Participant formula is required");
        }
        Objects.requireNonNull(coefficient, "coefficient must not be null");
        if (coefficient.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ElectrochemicalException(ElectrochemicalErrorCode.INVALID_HALF_REACTION, "Participant coefficient must be positive");
        }
        if (phase == null || phase.isBlank()) {
            throw new ElectrochemicalException(ElectrochemicalErrorCode.INVALID_HALF_REACTION, "Participant phase is required");
        }
        Objects.requireNonNull(side, "side must not be null");
    }

    public String speciesKey() {
        return speciesCode + "|" + phase;
    }
}

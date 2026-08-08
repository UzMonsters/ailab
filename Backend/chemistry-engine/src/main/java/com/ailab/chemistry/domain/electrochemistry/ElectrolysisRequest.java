package com.ailab.chemistry.domain.electrochemistry;

import com.ailab.chemistry.domain.measurement.Duration;

import java.math.BigDecimal;

public record ElectrolysisRequest(
        String halfReactionRecordId,
        String substanceCode,
        String substancePhase,
        ElectricCurrent current,
        Duration duration,
        ElectricCharge charge,
        CurrentEfficiency efficiency,
        BigDecimal molarMassGramsPerMole
) {
    public static ElectrolysisRequest forCurrentAndDuration(
            String halfReactionRecordId,
            String substanceCode,
            String substancePhase,
            ElectricCurrent current,
            Duration duration,
            CurrentEfficiency efficiency,
            BigDecimal molarMassGramsPerMole) {
        return new ElectrolysisRequest(halfReactionRecordId, substanceCode, substancePhase, current, duration, null, efficiency, molarMassGramsPerMole);
    }

    public static ElectrolysisRequest forCharge(
            String halfReactionRecordId,
            String substanceCode,
            String substancePhase,
            ElectricCharge charge,
            CurrentEfficiency efficiency,
            BigDecimal molarMassGramsPerMole) {
        return new ElectrolysisRequest(halfReactionRecordId, substanceCode, substancePhase, null, null, charge, efficiency, molarMassGramsPerMole);
    }
}

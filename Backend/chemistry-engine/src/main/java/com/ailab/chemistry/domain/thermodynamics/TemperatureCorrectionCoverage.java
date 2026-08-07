package com.ailab.chemistry.domain.thermodynamics;

import java.util.List;

public record TemperatureCorrectionCoverage(
        String reactionCode,
        TemperatureCorrectionStatus status,
        List<String> missingCorrelations,
        List<String> outOfRangeCorrelations,
        List<String> missingPhysicalStates,
        List<String> unsupportedStates) {

    public TemperatureCorrectionCoverage {
        missingCorrelations = List.copyOf(missingCorrelations);
        outOfRangeCorrelations = List.copyOf(outOfRangeCorrelations);
        missingPhysicalStates = List.copyOf(missingPhysicalStates);
        unsupportedStates = List.copyOf(unsupportedStates);
    }

    public static TemperatureCorrectionCoverage complete(String reactionCode) {
        return new TemperatureCorrectionCoverage(reactionCode, TemperatureCorrectionStatus.CALCULABLE,
                List.of(), List.of(), List.of(), List.of());
    }

    public static TemperatureCorrectionCoverage incomplete(String reactionCode, List<String> missingCorrelations,
                                                           List<String> outOfRangeCorrelations,
                                                           List<String> missingPhysicalStates,
                                                           List<String> unsupportedStates) {
        return new TemperatureCorrectionCoverage(reactionCode, TemperatureCorrectionStatus.INCOMPLETE_COVERAGE,
                missingCorrelations, outOfRangeCorrelations, missingPhysicalStates, unsupportedStates);
    }
}

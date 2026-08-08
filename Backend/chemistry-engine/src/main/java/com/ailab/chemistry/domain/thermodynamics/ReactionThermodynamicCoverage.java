package com.ailab.chemistry.domain.thermodynamics;

import java.util.List;

public record ReactionThermodynamicCoverage(
        String reactionCode,
        ReactionThermodynamicStatus status,
        List<String> missingCompounds,
        List<String> missingPropertyTypes,
        List<String> missingPhaseSpecificRecords,
        List<String> missingPhysicalStates,
        List<String> unsupportedStates) {

    public ReactionThermodynamicCoverage {
        missingCompounds = List.copyOf(missingCompounds);
        missingPropertyTypes = List.copyOf(missingPropertyTypes);
        missingPhaseSpecificRecords = List.copyOf(missingPhaseSpecificRecords);
        missingPhysicalStates = List.copyOf(missingPhysicalStates);
        unsupportedStates = List.copyOf(unsupportedStates);
    }

    public static ReactionThermodynamicCoverage complete(String reactionCode) {
        return new ReactionThermodynamicCoverage(reactionCode, ReactionThermodynamicStatus.CALCULABLE,
                List.of(), List.of(), List.of(), List.of(), List.of());
    }

    public static ReactionThermodynamicCoverage incomplete(String reactionCode, List<String> missingCompounds,
                                                           List<String> missingPropertyTypes,
                                                           List<String> missingPhaseSpecificRecords,
                                                           List<String> missingPhysicalStates,
                                                           List<String> unsupportedStates) {
        return new ReactionThermodynamicCoverage(reactionCode, ReactionThermodynamicStatus.INCOMPLETE_COVERAGE,
                missingCompounds, missingPropertyTypes, missingPhaseSpecificRecords, missingPhysicalStates, unsupportedStates);
    }
}

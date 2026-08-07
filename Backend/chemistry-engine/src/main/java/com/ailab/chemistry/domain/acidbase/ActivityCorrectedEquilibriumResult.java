package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.PhValue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record ActivityCorrectedEquilibriumResult(
        ActivityModel model,
        ActivityEquilibriumSystemType systemType,
        PhValue idealPh,
        PhValue activityPh,
        PhValue activityPoh,
        BigDecimal hydroniumConcentration,
        BigDecimal hydroxideConcentration,
        IonicStrength ionicStrength,
        Map<String, BigDecimal> coefficients,
        List<ChemicalActivity> activities,
        SpeciesDistribution distribution,
        Map<String, BigDecimal> constants,
        ActivityIterationResult iteration,
        PolyproticResidual residual,
        ActivitySolverStatus solverStatus
) {
    public ActivityCorrectedEquilibriumResult {
        Objects.requireNonNull(model, "model must not be null");
        Objects.requireNonNull(systemType, "systemType must not be null");
        Objects.requireNonNull(idealPh, "idealPh must not be null");
        Objects.requireNonNull(activityPh, "activityPh must not be null");
        Objects.requireNonNull(activityPoh, "activityPoh must not be null");
        Objects.requireNonNull(hydroniumConcentration, "hydroniumConcentration must not be null");
        Objects.requireNonNull(hydroxideConcentration, "hydroxideConcentration must not be null");
        Objects.requireNonNull(ionicStrength, "ionicStrength must not be null");
        coefficients = Map.copyOf(Objects.requireNonNull(coefficients, "coefficients must not be null"));
        activities = List.copyOf(Objects.requireNonNull(activities, "activities must not be null"));
        Objects.requireNonNull(iteration, "iteration must not be null");
        Objects.requireNonNull(residual, "residual must not be null");
        Objects.requireNonNull(solverStatus, "solverStatus must not be null");
        constants = Map.copyOf(Objects.requireNonNull(constants, "constants must not be null"));
    }
}

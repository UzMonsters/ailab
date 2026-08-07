package com.ailab.chemistry.domain.acidbase;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public record ActivityCorrectionResult(
        ActivityModel model,
        IonicStrength ionicStrength,
        List<ActivityCoefficient> coefficients,
        List<ChemicalActivity> activities
) {
    public ActivityCorrectionResult {
        Objects.requireNonNull(model, "model must not be null");
        Objects.requireNonNull(ionicStrength, "ionicStrength must not be null");
        coefficients = List.copyOf(Objects.requireNonNull(coefficients, "coefficients must not be null"));
        activities = List.copyOf(Objects.requireNonNull(activities, "activities must not be null"));
    }

    public ActivityCoefficient coefficientFor(String speciesCode) {
        return coefficients.stream()
                .filter(coefficient -> coefficient.speciesCode().equalsIgnoreCase(speciesCode))
                .findFirst()
                .orElseThrow();
    }

    public ActivityCoefficient coefficientForCharge(int charge) {
        return coefficients.stream()
                .filter(coefficient -> coefficient.charge() == charge)
                .findFirst()
                .orElseGet(() -> new ActivityCoefficient("CHARGE-" + charge, charge, BigDecimal.ONE));
    }

    public Map<String, BigDecimal> coefficientMap() {
        return coefficients.stream().collect(Collectors.toMap(ActivityCoefficient::speciesCode, ActivityCoefficient::value, (a, b) -> a));
    }
}

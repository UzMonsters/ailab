package com.ailab.chemistry.domain.labenvironment;

import java.util.ArrayList;
import java.util.List;

public final class EnvironmentSuitabilityCalculator {
    public EnvironmentSuitabilityResult evaluate(EnvironmentSuitabilityRequest request) {
        List<EnvironmentViolation> violations = new ArrayList<>();
        LaboratoryEnvironmentSnapshot snapshot = request.snapshot();
        EnvironmentalRequirement requirement = request.requirement();

        if (requirement.acceptableTemperatureRange() != null) {
            if (snapshot.ambientTemperature() == null) {
                missing(violations, "ambient temperature");
            } else if (!requirement.acceptableTemperatureRange().contains(snapshot.ambientTemperature())) {
                violations.add(new EnvironmentViolation(EnvironmentErrorCode.TEMPERATURE_OUTSIDE_RANGE, "Ambient temperature is outside required range"));
            }
        }
        if (requirement.acceptablePressureRange() != null) {
            if (snapshot.ambientPressure() == null) {
                missing(violations, "ambient pressure");
            } else if (!requirement.acceptablePressureRange().contains(snapshot.ambientPressure())) {
                violations.add(new EnvironmentViolation(EnvironmentErrorCode.PRESSURE_OUTSIDE_RANGE, "Ambient pressure is outside required range"));
            }
        }
        if (requirement.acceptableHumidityRange() != null) {
            if (snapshot.relativeHumidity() == null) {
                missing(violations, "relative humidity");
            } else if (!requirement.acceptableHumidityRange().contains(snapshot.relativeHumidity())) {
                violations.add(new EnvironmentViolation(EnvironmentErrorCode.HUMIDITY_OUTSIDE_RANGE, "Relative humidity is outside required range"));
            }
        }
        if (requirement.requiredVentilationMode() != null) {
            if (snapshot.ventilationMode() == null) {
                missing(violations, "ventilation mode");
            } else if (snapshot.ventilationMode() != requirement.requiredVentilationMode()) {
                violations.add(new EnvironmentViolation(EnvironmentErrorCode.VENTILATION_MODE_MISMATCH, "Ventilation mode does not match requirement"));
            }
        }
        if (requirement.fumeHoodRequired() && snapshot.fumeHoodState() != FumeHoodState.OPERATING) {
            violations.add(new EnvironmentViolation(EnvironmentErrorCode.FUME_HOOD_NOT_OPERATING, "A fume hood must be operating; available is not sufficient"));
        }
        if (requirement.isolatedEnclosureRequired() && snapshot.ventilationMode() != VentilationMode.ISOLATED_ENCLOSURE) {
            violations.add(new EnvironmentViolation(EnvironmentErrorCode.ISOLATED_ENCLOSURE_REQUIRED, "Isolated enclosure is required"));
        }
        if (snapshot.ventilationMode() != null && requirement.prohibitedVentilationModes().contains(snapshot.ventilationMode())) {
            violations.add(new EnvironmentViolation(EnvironmentErrorCode.PROHIBITED_ENVIRONMENTAL_STATE, "Ventilation mode is prohibited"));
        }

        EnvironmentSuitabilityStatus status = violations.isEmpty() ? EnvironmentSuitabilityStatus.SUITABLE : EnvironmentSuitabilityStatus.UNSUITABLE;
        return new EnvironmentSuitabilityResult(status, violations, List.of(), List.of("explicit environment snapshot"));
    }

    private static void missing(List<EnvironmentViolation> violations, String value) {
        violations.add(new EnvironmentViolation(EnvironmentErrorCode.MISSING_REQUIRED_ENVIRONMENT_VALUE, "Missing required environment value: " + value));
    }
}

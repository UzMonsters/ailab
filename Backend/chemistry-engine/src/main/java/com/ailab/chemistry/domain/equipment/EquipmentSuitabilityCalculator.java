package com.ailab.chemistry.domain.equipment;

import com.ailab.chemistry.domain.measurement.DurationUnit;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public final class EquipmentSuitabilityCalculator {
    public EquipmentSuitabilityResult evaluate(EquipmentSuitabilityRequest request) {
        List<EquipmentViolation> violations = new ArrayList<>();
        List<EquipmentWarning> warnings = new ArrayList<>();

        if (request.profile().condition() != EquipmentCondition.OPERATIONAL) {
            violations.add(new EquipmentViolation(EquipmentErrorCode.UNSUITABLE_CONDITION, "Equipment condition is not operational"));
        }

        for (EquipmentRequirement requirement : request.requirements()) {
            EquipmentCapability capability = request.profile().capabilities().stream()
                    .filter(c -> c.capabilityType().equals(requirement.capabilityType()) && c.quantity().equals(requirement.quantity()))
                    .findFirst()
                    .orElse(null);
            if (capability == null) {
                violations.add(new EquipmentViolation(EquipmentErrorCode.MISSING_CAPABILITY,
                        "Required capability is absent: " + requirement.capabilityType() + " " + requirement.quantity()));
                continue;
            }
            if (!capability.operatingRange().contains(requirement.requestedValue(), requirement.unit())) {
                violations.add(new EquipmentViolation(EquipmentErrorCode.VALUE_OUTSIDE_OPERATING_RANGE,
                        "Requested value is outside explicit operating range"));
            }
            if (requirement.requiredResolution() != null) {
                if (capability.resolution() == null || !capability.resolution().unit().equals(requirement.requiredResolution().unit())
                        || capability.resolution().value().compareTo(requirement.requiredResolution().value()) > 0) {
                    violations.add(new EquipmentViolation(EquipmentErrorCode.INSUFFICIENT_RESOLUTION,
                            "Equipment resolution is coarser than required precision"));
                }
            }
            if (requirement.requireAccuracyOrUncertainty() && capability.accuracy() == null && capability.uncertainty() == null) {
                violations.add(new EquipmentViolation(EquipmentErrorCode.MISSING_ACCURACY_OR_UNCERTAINTY,
                        "Required accuracy or uncertainty is unavailable"));
            }
            CalibrationStatus status = calibrationStatus(capability.calibrationRequirement(), request.calibrationRecords(), request.evaluationTimestamp());
            if (status == CalibrationStatus.MISSING) {
                violations.add(new EquipmentViolation(EquipmentErrorCode.MISSING_CALIBRATION, "Required calibration record is missing"));
            } else if (status == CalibrationStatus.EXPIRED) {
                violations.add(new EquipmentViolation(EquipmentErrorCode.EXPIRED_CALIBRATION, "Required calibration has expired"));
            } else if (status == CalibrationStatus.DUE_SOON) {
                warnings.add(new EquipmentWarning("Calibration is due soon for " + capability.quantity()));
            }
        }

        EquipmentSuitabilityStatus status = violations.isEmpty()
                ? (warnings.isEmpty() ? EquipmentSuitabilityStatus.SUITABLE : EquipmentSuitabilityStatus.SUITABLE_WITH_WARNINGS)
                : EquipmentSuitabilityStatus.UNSUITABLE;
        return new EquipmentSuitabilityResult(status, List.of(request.profile().profileId()), violations, warnings,
                List.of(request.profile().datasetId(), request.profile().provenance()));
    }

    private CalibrationStatus calibrationStatus(CalibrationRequirement requirement, List<CalibrationRecord> records, Instant evaluationTime) {
        if (!requirement.required()) {
            return CalibrationStatus.NOT_REQUIRED;
        }
        CalibrationRecord latest = records.stream()
                .max((a, b) -> a.calibratedAt().compareTo(b.calibratedAt()))
                .orElse(null);
        if (latest == null) {
            return CalibrationStatus.MISSING;
        }
        long intervalSeconds = requirement.interval().in(DurationUnit.SECOND).longValueExact();
        Instant expiry = latest.calibratedAt().plusSeconds(intervalSeconds);
        if (!evaluationTime.isBefore(expiry)) {
            return CalibrationStatus.EXPIRED;
        }
        if (requirement.dueSoonWindow() != null) {
            long warningSeconds = requirement.dueSoonWindow().in(DurationUnit.SECOND).longValueExact();
            if (!evaluationTime.isBefore(expiry.minusSeconds(warningSeconds))) {
                return CalibrationStatus.DUE_SOON;
            }
        }
        return CalibrationStatus.VALID;
    }
}

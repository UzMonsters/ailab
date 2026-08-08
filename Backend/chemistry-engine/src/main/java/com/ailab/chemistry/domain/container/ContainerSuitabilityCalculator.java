package com.ailab.chemistry.domain.container;

import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.ScientificMath;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;

import java.util.ArrayList;
import java.util.List;

public final class ContainerSuitabilityCalculator {
    public ContainerSuitabilityResult evaluate(ContainerSuitabilityRequest request) {
        List<ContainerViolation> violations = new ArrayList<>();
        ContainerProfile profile = request.profile();
        Volume nominal = profile.nominalCapacity().volume();
        Volume headspaceVolume = nominal.subtract(request.actualContentVolume());
        FillFraction fillFraction = new FillFraction(request.actualContentVolume().in(VolumeUnit.LITER)
                .divide(nominal.in(VolumeUnit.LITER), ScientificMath.CALCULATION_CONTEXT));

        if (request.actualContentVolume().compareTo(profile.maximumWorkingVolume().volume()) > 0) {
            violations.add(new ContainerViolation(ContainerErrorCode.CONTENT_VOLUME_EXCEEDS_WORKING_LIMIT,
                    "Actual content volume exceeds maximum working volume"));
        }
        if (request.sealedOperation() && request.requiredHeadspace() != null
                && headspaceVolume.compareTo(request.requiredHeadspace()) < 0) {
            violations.add(new ContainerViolation(ContainerErrorCode.INSUFFICIENT_HEADSPACE,
                    "Sealed operation does not have required headspace"));
        }
        if (request.operatingPressure() != null) {
            if (!request.sealedOperation() || profile.closureType() == ContainerClosureType.OPEN) {
                violations.add(new ContainerViolation(ContainerErrorCode.OPEN_CONTAINER_NOT_PRESSURE_RATED,
                        "An open container must not be treated as pressure-rated"));
            } else if (profile.pressureLimit() == null) {
                violations.add(new ContainerViolation(ContainerErrorCode.MISSING_PRESSURE_LIMIT,
                        "Pressure limit is required for pressure operation"));
            } else if (request.operatingPressure().compareTo(profile.pressureLimit().maximum()) > 0) {
                violations.add(new ContainerViolation(ContainerErrorCode.PRESSURE_LIMIT_EXCEEDED,
                        "Operating pressure exceeds explicit pressure limit"));
            }
        }
        if (request.operatingTemperature() != null) {
            if (profile.temperatureLimit() == null) {
                violations.add(new ContainerViolation(ContainerErrorCode.MISSING_TEMPERATURE_LIMIT,
                        "Temperature limit is required for heating operation"));
            } else if (!profile.temperatureLimit().contains(request.operatingTemperature())) {
                violations.add(new ContainerViolation(ContainerErrorCode.TEMPERATURE_LIMIT_EXCEEDED,
                        "Operating temperature exceeds explicit temperature limit"));
            }
        }
        evaluateCompatibility(request, violations);

        return new ContainerSuitabilityResult(violations.isEmpty() ? ContainerSuitabilityStatus.SUITABLE : ContainerSuitabilityStatus.UNSUITABLE,
                profile.profileId(), fillFraction, new Headspace(headspaceVolume), violations,
                List.of(profile.datasetId(), profile.provenance()));
    }

    private void evaluateCompatibility(ContainerSuitabilityRequest request, List<ContainerViolation> violations) {
        if (request.compoundOrFamily() == null || request.physicalState() == null) {
            return;
        }
        ContainerCompatibilityRecord record = request.profile().compatibilityRecords().stream()
                .filter(r -> r.compoundOrFamily().equals(request.compoundOrFamily()))
                .filter(r -> r.physicalState().equals(request.physicalState()))
                .filter(r -> r.containerMaterial() == request.profile().material())
                .filter(r -> r.closureMaterial() == null || r.closureMaterial() == request.profile().closureMaterial())
                .findFirst()
                .orElse(null);
        if (record == null || record.status() == CompatibilityStatus.UNKNOWN) {
            violations.add(new ContainerViolation(ContainerErrorCode.UNKNOWN_COMPATIBILITY,
                    "No explicit compatible material-chemical record is available"));
            return;
        }
        if (record.status() == CompatibilityStatus.INCOMPATIBLE) {
            violations.add(new ContainerViolation(ContainerErrorCode.INCOMPATIBLE_MATERIAL,
                    "Explicit compatibility record marks material as incompatible"));
            return;
        }
        if (record.status() == CompatibilityStatus.COMPATIBLE_WITH_LIMITS
                && record.temperatureLimit() != null
                && request.operatingTemperature() != null
                && !record.temperatureLimit().contains(request.operatingTemperature())) {
            violations.add(new ContainerViolation(ContainerErrorCode.COMPATIBILITY_LIMIT_VIOLATED,
                    "Compatibility record temperature limit is violated"));
        }
    }
}

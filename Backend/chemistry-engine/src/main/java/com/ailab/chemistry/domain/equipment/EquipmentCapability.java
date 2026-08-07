package com.ailab.chemistry.domain.equipment;

import com.ailab.chemistry.domain.measurement.MeasurementResolution;

import java.util.List;
import java.util.Objects;

public record EquipmentCapability(
        String capabilityType,
        String quantity,
        OperatingRange operatingRange,
        MeasurementResolution resolution,
        AccuracySpecification accuracy,
        UncertaintySpecification uncertainty,
        CapacityLimit capacity,
        CalibrationRequirement calibrationRequirement,
        List<String> environmentalRestrictions,
        String provenance
) {
    public EquipmentCapability {
        Objects.requireNonNull(capabilityType, "capabilityType must not be null");
        Objects.requireNonNull(quantity, "quantity must not be null");
        Objects.requireNonNull(operatingRange, "operatingRange must not be null");
        calibrationRequirement = calibrationRequirement == null ? CalibrationRequirement.notRequired() : calibrationRequirement;
        environmentalRestrictions = environmentalRestrictions == null ? List.of() : List.copyOf(environmentalRestrictions);
        Objects.requireNonNull(provenance, "provenance must not be null");
    }
}

package com.ailab.chemistry.laboratory;

import com.ailab.chemistry.domain.container.*;
import com.ailab.chemistry.domain.equipment.*;
import com.ailab.chemistry.domain.labenvironment.*;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;

import java.math.BigDecimal;
import java.util.List;

final class SupportFixtures {
    private SupportFixtures() {
    }

    static EquipmentSuitabilityResult suitableEquipmentResult() {
        return new EquipmentSuitabilityResult(EquipmentSuitabilityStatus.SUITABLE, List.of("BAL-ANALYTICAL-120G"), List.of(), List.of(), List.of("equipment provenance"));
    }

    static EquipmentSuitabilityResult unsuitableEquipmentResult(EquipmentErrorCode code) {
        return new EquipmentSuitabilityResult(EquipmentSuitabilityStatus.UNSUITABLE, List.of("BAL-ANALYTICAL-120G"),
                List.of(new EquipmentViolation(code, "equipment blocked")), List.of(), List.of());
    }

    static ContainerSuitabilityResult suitableContainerResult() {
        return new ContainerSuitabilityResult(ContainerSuitabilityStatus.SUITABLE, "CON-BORO-BEAKER-100ML",
                new FillFraction(new BigDecimal("0.8")), new Headspace(Volume.of("20", VolumeUnit.MILLILITER)),
                List.of(), List.of("container provenance"));
    }

    static ContainerSuitabilityResult unsuitableContainerResult(ContainerErrorCode code) {
        return new ContainerSuitabilityResult(ContainerSuitabilityStatus.UNSUITABLE, "CON-HDPE-BOTTLE-500ML",
                new FillFraction(BigDecimal.ZERO), new Headspace(Volume.of("0", VolumeUnit.MILLILITER)),
                List.of(new ContainerViolation(code, "container blocked")), List.of());
    }

    static EnvironmentSuitabilityResult suitableEnvironmentResult() {
        return new EnvironmentSuitabilityResult(EnvironmentSuitabilityStatus.SUITABLE, List.of(), List.of(), List.of("environment observation"));
    }

    static EnvironmentSuitabilityResult unsuitableEnvironmentResult(EnvironmentErrorCode code) {
        return new EnvironmentSuitabilityResult(EnvironmentSuitabilityStatus.UNSUITABLE,
                List.of(new EnvironmentViolation(code, "environment blocked")), List.of(), List.of());
    }
}

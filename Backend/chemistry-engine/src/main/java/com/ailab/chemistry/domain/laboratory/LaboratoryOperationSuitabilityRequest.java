package com.ailab.chemistry.domain.laboratory;

import com.ailab.chemistry.domain.container.ContainerSuitabilityResult;
import com.ailab.chemistry.domain.equipment.EquipmentSuitabilityResult;
import com.ailab.chemistry.domain.labenvironment.EnvironmentSuitabilityResult;

import java.util.List;
import java.util.Objects;

public record LaboratoryOperationSuitabilityRequest(
        String operationName,
        EquipmentSuitabilityResult equipmentResult,
        ContainerSuitabilityResult containerResult,
        EnvironmentSuitabilityResult environmentResult,
        List<String> assumptions,
        List<String> provenance
) {
    public LaboratoryOperationSuitabilityRequest {
        Objects.requireNonNull(operationName, "operationName must not be null");
        Objects.requireNonNull(equipmentResult, "equipmentResult must not be null");
        Objects.requireNonNull(containerResult, "containerResult must not be null");
        Objects.requireNonNull(environmentResult, "environmentResult must not be null");
        assumptions = assumptions == null ? List.of() : List.copyOf(assumptions);
        provenance = provenance == null ? List.of() : List.copyOf(provenance);
    }
}

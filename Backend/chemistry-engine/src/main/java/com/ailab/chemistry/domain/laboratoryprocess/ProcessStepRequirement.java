package com.ailab.chemistry.domain.laboratoryprocess;

import java.util.List;

public record ProcessStepRequirement(
        List<ProcessMaterialRequirement> materialRequirements,
        List<ProcessEquipmentRequirement> equipmentRequirements,
        List<ProcessContainerRequirement> containerRequirements,
        List<ProcessEnvironmentRequirement> environmentRequirements
) {
    public ProcessStepRequirement {
        materialRequirements = List.copyOf(materialRequirements == null ? List.of() : materialRequirements);
        equipmentRequirements = List.copyOf(equipmentRequirements == null ? List.of() : equipmentRequirements);
        containerRequirements = List.copyOf(containerRequirements == null ? List.of() : containerRequirements);
        environmentRequirements = List.copyOf(environmentRequirements == null ? List.of() : environmentRequirements);
    }
}

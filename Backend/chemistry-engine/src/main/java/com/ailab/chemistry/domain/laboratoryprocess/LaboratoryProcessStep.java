package com.ailab.chemistry.domain.laboratoryprocess;

import com.ailab.chemistry.domain.measurement.Duration;
import com.ailab.chemistry.domain.simulationengine.ScientificOperationSpecification;

import java.util.List;

public record LaboratoryProcessStep(
        ProcessStepId id,
        ProcessStepType type,
        boolean optional,
        Duration expectedDuration,
        List<ProcessStepDependency> dependencies,
        List<ProcessMaterialRequirement> materialRequirements,
        List<ProcessEquipmentRequirement> equipmentRequirements,
        List<ProcessContainerRequirement> containerRequirements,
        List<ProcessEnvironmentRequirement> environmentRequirements,
        List<String> inputPortIds,
        List<String> outputPortIds,
        List<ScientificOperationSpecification> scientificOperationSpecifications
) {
    public LaboratoryProcessStep {
        if (id == null || type == null || expectedDuration == null) {
            throw new IllegalArgumentException("Step id, type, and expected duration are required");
        }
        dependencies = List.copyOf(dependencies == null ? List.of() : dependencies);
        materialRequirements = List.copyOf(materialRequirements == null ? List.of() : materialRequirements);
        equipmentRequirements = List.copyOf(equipmentRequirements == null ? List.of() : equipmentRequirements);
        containerRequirements = List.copyOf(containerRequirements == null ? List.of() : containerRequirements);
        environmentRequirements = List.copyOf(environmentRequirements == null ? List.of() : environmentRequirements);
        inputPortIds = List.copyOf(inputPortIds == null ? List.of() : inputPortIds);
        outputPortIds = List.copyOf(outputPortIds == null ? List.of() : outputPortIds);
        scientificOperationSpecifications = List.copyOf(
                scientificOperationSpecifications == null ? List.of() : scientificOperationSpecifications);
    }

    public LaboratoryProcessStep(ProcessStepId id,
                                 ProcessStepType type,
                                 boolean optional,
                                 Duration expectedDuration,
                                 List<ProcessStepDependency> dependencies,
                                 List<ProcessMaterialRequirement> materialRequirements,
                                 List<ProcessEquipmentRequirement> equipmentRequirements,
                                 List<ProcessContainerRequirement> containerRequirements,
                                 List<ProcessEnvironmentRequirement> environmentRequirements,
                                 List<String> inputPortIds,
                                 List<String> outputPortIds) {
        this(id, type, optional, expectedDuration, dependencies, materialRequirements, equipmentRequirements,
                containerRequirements, environmentRequirements, inputPortIds, outputPortIds, List.of());
    }

    public LaboratoryProcessStep withMaterialRequirements(List<ProcessMaterialRequirement> materialRequirements) {
        return new LaboratoryProcessStep(id, type, optional, expectedDuration, dependencies, materialRequirements,
                equipmentRequirements, containerRequirements, environmentRequirements, inputPortIds, outputPortIds,
                scientificOperationSpecifications);
    }

    public LaboratoryProcessStep withExpectedDuration(Duration expectedDuration) {
        return new LaboratoryProcessStep(id, type, optional, expectedDuration, dependencies, materialRequirements,
                equipmentRequirements, containerRequirements, environmentRequirements, inputPortIds, outputPortIds,
                scientificOperationSpecifications);
    }

    public LaboratoryProcessStep withScientificOperationSpecifications(
            List<ScientificOperationSpecification> scientificOperationSpecifications) {
        return new LaboratoryProcessStep(id, type, optional, expectedDuration, dependencies, materialRequirements,
                equipmentRequirements, containerRequirements, environmentRequirements, inputPortIds, outputPortIds,
                scientificOperationSpecifications);
    }
}

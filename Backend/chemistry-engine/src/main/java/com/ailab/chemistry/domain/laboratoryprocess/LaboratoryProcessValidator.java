package com.ailab.chemistry.domain.laboratoryprocess;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LaboratoryProcessValidator {
    public ProcessValidationResult validate(LaboratoryProcessDefinition definition) {
        List<ProcessValidationError> errors = new ArrayList<>();
        if (definition.steps().isEmpty()) {
            errors.add(error("NO_STEPS", "Process must declare at least one step", null));
            return new ProcessValidationResult(errors);
        }

        Map<String, LaboratoryProcessStep> byId = new HashMap<>();
        Set<String> duplicateIds = new HashSet<>();
        for (LaboratoryProcessStep step : definition.steps()) {
            if (byId.put(step.id().value(), step) != null) {
                duplicateIds.add(step.id().value());
                errors.add(error("DUPLICATE_STEP_ID", "Duplicate step id: " + step.id().value(), step.id()));
            }
            validateStepRequirements(step, errors);
        }

        boolean hasInitial = false;
        for (LaboratoryProcessStep step : definition.steps()) {
            if (step.dependencies().isEmpty()) {
                hasInitial = true;
            }
            for (ProcessStepDependency dependency : step.dependencies()) {
                if (!byId.containsKey(dependency.stepId().value())) {
                    errors.add(error("MISSING_DEPENDENCY",
                            "Dependency references missing step: " + dependency.stepId().value(), step.id()));
                }
            }
        }
        if (!hasInitial) {
            errors.add(error("NO_INITIAL_STEP", "Process must have at least one initial step", null));
        }

        Set<String> cycleMembers = findCycleMembers(definition.steps(), byId);
        for (String cycleMember : cycleMembers) {
            errors.add(error("CIRCULAR_DEPENDENCY", "Circular dependency includes step: " + cycleMember,
                    new ProcessStepId(cycleMember)));
        }

        Set<String> reachable = reachableFromInitialSteps(definition.steps(), byId, duplicateIds);
        for (LaboratoryProcessStep step : definition.steps()) {
            if (!reachable.contains(step.id().value())) {
                errors.add(error("UNREACHABLE_STEP", "Step is not reachable from any initial step", step.id()));
            }
        }

        return new ProcessValidationResult(errors);
    }

    private void validateStepRequirements(LaboratoryProcessStep step, List<ProcessValidationError> errors) {
        if (step.inputPortIds().isEmpty() || step.outputPortIds().isEmpty()) {
            errors.add(error("MISSING_IO", "Step inputs and outputs must be explicit", step.id()));
        }
        boolean hasRequirement = !step.materialRequirements().isEmpty()
                || !step.equipmentRequirements().isEmpty()
                || !step.containerRequirements().isEmpty()
                || !step.environmentRequirements().isEmpty();
        if (!hasRequirement) {
            errors.add(error("MISSING_REQUIREMENT", "Step requirements must be explicit", step.id()));
        }
        for (ProcessMaterialRequirement material : step.materialRequirements()) {
            if (material.unit() == null || material.unit().isBlank()) {
                errors.add(error("MISSING_UNIT", "Material requirement unit is required", step.id()));
            }
            if (material.compoundCode() == null || material.compoundCode().isBlank()
                    || material.physicalState() == null || material.physicalState().isBlank()) {
                errors.add(error("MISSING_MATERIAL_IDENTITY", "Material identity and state are required", step.id()));
            }
        }
        for (ProcessEquipmentRequirement equipment : step.equipmentRequirements()) {
            if (equipment.profileId() == null || equipment.profileId().isBlank()) {
                errors.add(error("MISSING_REQUIREMENT", "Equipment profile id is required", step.id()));
            }
        }
        for (ProcessContainerRequirement container : step.containerRequirements()) {
            if (container.profileId() == null || container.profileId().isBlank()
                    || container.compoundOrFamily() == null || container.compoundOrFamily().isBlank()
                    || container.physicalState() == null || container.physicalState().isBlank()) {
                errors.add(error("MISSING_REQUIREMENT", "Container profile, material identity, and state are required", step.id()));
            }
        }
    }

    private Set<String> findCycleMembers(List<LaboratoryProcessStep> steps, Map<String, LaboratoryProcessStep> byId) {
        Set<String> visiting = new HashSet<>();
        Set<String> visited = new HashSet<>();
        Set<String> cycleMembers = new HashSet<>();
        for (LaboratoryProcessStep step : steps) {
            visitForCycle(step.id().value(), byId, visiting, visited, cycleMembers);
        }
        return cycleMembers;
    }

    private void visitForCycle(String stepId, Map<String, LaboratoryProcessStep> byId, Set<String> visiting,
                               Set<String> visited, Set<String> cycleMembers) {
        if (visited.contains(stepId) || !byId.containsKey(stepId)) {
            return;
        }
        if (!visiting.add(stepId)) {
            cycleMembers.add(stepId);
            return;
        }
        for (ProcessStepDependency dependency : byId.get(stepId).dependencies()) {
            if (visiting.contains(dependency.stepId().value())) {
                cycleMembers.add(stepId);
                cycleMembers.add(dependency.stepId().value());
            }
            visitForCycle(dependency.stepId().value(), byId, visiting, visited, cycleMembers);
        }
        visiting.remove(stepId);
        visited.add(stepId);
    }

    private Set<String> reachableFromInitialSteps(List<LaboratoryProcessStep> steps, Map<String, LaboratoryProcessStep> byId,
                                                  Set<String> duplicateIds) {
        Map<String, List<String>> dependents = steps.stream()
                .filter(step -> !duplicateIds.contains(step.id().value()))
                .collect(Collectors.toMap(
                        step -> step.id().value(),
                        step -> new ArrayList<>(),
                        (left, right) -> left));
        for (LaboratoryProcessStep step : steps) {
            for (ProcessStepDependency dependency : step.dependencies()) {
                if (dependents.containsKey(dependency.stepId().value())) {
                    dependents.get(dependency.stepId().value()).add(step.id().value());
                }
            }
        }

        Set<String> reachable = new HashSet<>();
        ArrayDeque<String> queue = new ArrayDeque<>();
        for (LaboratoryProcessStep step : steps) {
            if (step.dependencies().isEmpty() && !duplicateIds.contains(step.id().value())) {
                reachable.add(step.id().value());
                queue.add(step.id().value());
            }
        }
        while (!queue.isEmpty()) {
            String current = queue.removeFirst();
            for (String dependent : dependents.getOrDefault(current, List.of())) {
                if (byId.containsKey(dependent) && reachable.add(dependent)) {
                    queue.add(dependent);
                }
            }
        }
        return reachable;
    }

    private ProcessValidationError error(String code, String message, ProcessStepId stepId) {
        return new ProcessValidationError(code, message, stepId);
    }
}

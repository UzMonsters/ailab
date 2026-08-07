package com.ailab.chemistry.domain.laboratoryprocess;

import java.util.List;

public record LaboratoryProcessDefinition(
        String code,
        LaboratoryProcessVersion version,
        LaboratoryProcessStatus status,
        List<LaboratoryProcessStep> steps
) {
    public LaboratoryProcessDefinition {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Process code is required");
        }
        if (version == null || status == null) {
            throw new IllegalArgumentException("Process version and status are required");
        }
        steps = List.copyOf(steps == null ? List.of() : steps);
    }

    public LaboratoryProcessDefinition withStatus(LaboratoryProcessStatus status) {
        return new LaboratoryProcessDefinition(code, version, status, steps);
    }

    public LaboratoryProcessDefinition replaceSteps(List<LaboratoryProcessStep> replacementSteps) {
        if (status.immutable()) {
            throw new IllegalStateException("Published, terminal, and archived process definitions are immutable");
        }
        return new LaboratoryProcessDefinition(code, version, status, replacementSteps);
    }

    public LaboratoryProcessDefinition nextDraftVersion() {
        return new LaboratoryProcessDefinition(code, new LaboratoryProcessVersion(version.value() + 1),
                LaboratoryProcessStatus.DRAFT, steps);
    }
}

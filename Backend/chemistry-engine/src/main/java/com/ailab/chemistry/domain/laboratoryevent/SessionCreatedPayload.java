package com.ailab.chemistry.domain.laboratoryevent;

import java.util.List;

public record SessionCreatedPayload(String processCode, int processVersion, List<StepDefinitionSnapshot> steps)
        implements LaboratoryEventPayload {
    public SessionCreatedPayload {
        if (processCode == null || processCode.isBlank() || processVersion < 1) {
            throw new IllegalArgumentException("Process code and version are required");
        }
        steps = List.copyOf(steps == null ? List.of() : steps);
    }

    @Override
    public LaboratoryEventType eventType() {
        return LaboratoryEventType.SESSION_CREATED;
    }
}

package com.ailab.chemistry.domain.laboratoryevent;

public record LaboratoryEventSequence(long value) {
    public LaboratoryEventSequence {
        if (value < 1) {
            throw new IllegalArgumentException("Event sequence must be positive");
        }
    }
}

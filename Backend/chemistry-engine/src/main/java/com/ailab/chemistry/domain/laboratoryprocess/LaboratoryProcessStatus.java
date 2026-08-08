package com.ailab.chemistry.domain.laboratoryprocess;

public enum LaboratoryProcessStatus {
    DRAFT,
    PUBLISHED,
    TERMINAL,
    ARCHIVED;

    public boolean immutable() {
        return this == PUBLISHED || this == TERMINAL || this == ARCHIVED;
    }
}

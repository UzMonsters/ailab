package com.ailab.chemistry.domain.laboratoryprocess;

public record LaboratoryProcessVersion(int value) {
    public LaboratoryProcessVersion {
        if (value < 1) {
            throw new IllegalArgumentException("Process version must be positive");
        }
    }
}

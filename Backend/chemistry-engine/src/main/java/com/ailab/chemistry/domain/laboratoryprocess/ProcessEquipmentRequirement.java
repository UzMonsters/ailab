package com.ailab.chemistry.domain.laboratoryprocess;

public record ProcessEquipmentRequirement(String requirementId, String profileId, boolean exclusive) {
    public ProcessEquipmentRequirement {
        if (requirementId == null || requirementId.isBlank()) {
            throw new IllegalArgumentException("Equipment requirement id is required");
        }
    }
}

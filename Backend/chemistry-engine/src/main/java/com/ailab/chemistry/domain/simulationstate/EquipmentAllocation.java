package com.ailab.chemistry.domain.simulationstate;

public record EquipmentAllocation(String equipmentProfileId, String stepId, boolean exclusive) {
    public EquipmentAllocation {
        if (equipmentProfileId == null || equipmentProfileId.isBlank() || stepId == null || stepId.isBlank()) {
            throw new IllegalArgumentException("Equipment allocation profile and step are required");
        }
    }
}

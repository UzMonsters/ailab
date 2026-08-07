package com.ailab.chemistry.domain.electrochemistry;

import java.util.List;

public record StandardReductionPotential(
        String recordId,
        ElectrochemicalDatasetVersion datasetVersion,
        HalfReaction halfReaction,
        ElectrodePotential standardPotential,
        ElectrochemicalReferenceConditions conditions,
        ElectrochemicalProvenance provenance,
        boolean active
) {
    public StandardReductionPotential {
        if (recordId == null || recordId.isBlank()) {
            throw new ElectrochemicalException(ElectrochemicalErrorCode.INVALID_HALF_REACTION, "Record id is required");
        }
    }

    public List<HalfReactionParticipant> participants() {
        return halfReaction.participants();
    }

    public ElectronCount electronCount() {
        return halfReaction.electronCount();
    }

    public HalfReactionValidation validate() {
        return halfReaction.validate();
    }
}

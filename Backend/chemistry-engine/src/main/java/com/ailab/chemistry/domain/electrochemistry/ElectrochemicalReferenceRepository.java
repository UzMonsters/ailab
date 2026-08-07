package com.ailab.chemistry.domain.electrochemistry;

import java.util.List;
import java.util.Optional;

public interface ElectrochemicalReferenceRepository {
    Optional<StandardReductionPotential> findByRecordId(String recordId);

    List<StandardReductionPotential> findActive();
}

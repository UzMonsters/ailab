package com.ailab.chemistry.domain.laboratoryprocess;

import java.util.Optional;

public interface LaboratoryProcessRepository {
    LaboratoryProcessDefinition save(LaboratoryProcessDefinition definition);

    Optional<LaboratoryProcessDefinition> findByCodeAndVersion(String code, int version);
}

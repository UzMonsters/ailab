package com.ailab.chemistry.infrastructure.persistence.simulation;

import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessDefinition;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Profile("!local & !prod & !migration-test")
public class UnavailableLaboratoryProcessRepository implements LaboratoryProcessRepository {
    @Override
    public LaboratoryProcessDefinition save(LaboratoryProcessDefinition definition) {
        throw unavailable();
    }

    @Override
    public Optional<LaboratoryProcessDefinition> findByCodeAndVersion(String code, int version) {
        throw unavailable();
    }

    private IllegalStateException unavailable() {
        return new IllegalStateException("Production laboratory process repository is unavailable for this profile");
    }
}

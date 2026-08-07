package com.ailab.chemistry.service;

import com.ailab.chemistry.api.LaboratoryProcessService;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessDefinition;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessException;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessRepository;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessStatus;
import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessValidator;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessValidationResult;
import org.springframework.stereotype.Service;

@Service
public class LaboratoryProcessServiceImpl implements LaboratoryProcessService {
    private final LaboratoryProcessRepository repository;
    private final LaboratoryProcessValidator validator = new LaboratoryProcessValidator();

    public LaboratoryProcessServiceImpl(LaboratoryProcessRepository repository) {
        this.repository = repository;
    }

    @Override
    public LaboratoryProcessDefinition create(LaboratoryProcessDefinition definition) {
        assertValid(definition);
        repository.findByCodeAndVersion(definition.code(), definition.version().value())
                .ifPresent(existing -> {
                    throw new LaboratoryProcessException("Process version already exists: "
                            + existing.code() + " v" + existing.version().value());
                });
        return repository.save(definition);
    }

    @Override
    public LaboratoryProcessDefinition publish(LaboratoryProcessDefinition definition) {
        assertValid(definition);
        LaboratoryProcessDefinition published = definition.withStatus(LaboratoryProcessStatus.PUBLISHED);
        return repository.save(published);
    }

    @Override
    public ProcessValidationResult validate(LaboratoryProcessDefinition definition) {
        return validator.validate(definition);
    }

    @Override
    public LaboratoryProcessDefinition get(String code, int version) {
        return repository.findByCodeAndVersion(code, version)
                .orElseThrow(() -> new LaboratoryProcessException("Process definition not found: " + code + " v" + version));
    }

    private void assertValid(LaboratoryProcessDefinition definition) {
        ProcessValidationResult result = validator.validate(definition);
        if (!result.valid()) {
            throw new LaboratoryProcessException("Invalid process definition: " + result.errorCodes());
        }
    }
}

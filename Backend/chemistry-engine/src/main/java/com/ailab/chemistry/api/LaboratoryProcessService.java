package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.laboratoryprocess.LaboratoryProcessDefinition;
import com.ailab.chemistry.domain.laboratoryprocess.ProcessValidationResult;

public interface LaboratoryProcessService {
    LaboratoryProcessDefinition create(LaboratoryProcessDefinition definition);

    LaboratoryProcessDefinition publish(LaboratoryProcessDefinition definition);

    ProcessValidationResult validate(LaboratoryProcessDefinition definition);

    LaboratoryProcessDefinition get(String code, int version);
}

package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.laboratory.LaboratoryOperationSuitabilityRequest;
import com.ailab.chemistry.domain.laboratory.LaboratoryOperationSuitabilityResult;

public interface LaboratoryOperationService {
    LaboratoryOperationSuitabilityResult evaluate(LaboratoryOperationSuitabilityRequest request);
}

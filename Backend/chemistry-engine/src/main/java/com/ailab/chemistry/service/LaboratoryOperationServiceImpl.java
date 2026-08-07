package com.ailab.chemistry.service;

import com.ailab.chemistry.api.LaboratoryOperationService;
import com.ailab.chemistry.domain.laboratory.LaboratoryOperationSuitabilityCalculator;
import com.ailab.chemistry.domain.laboratory.LaboratoryOperationSuitabilityRequest;
import com.ailab.chemistry.domain.laboratory.LaboratoryOperationSuitabilityResult;

@org.springframework.stereotype.Service
public class LaboratoryOperationServiceImpl implements LaboratoryOperationService {
    private final LaboratoryOperationSuitabilityCalculator calculator = new LaboratoryOperationSuitabilityCalculator();

    @Override
    public LaboratoryOperationSuitabilityResult evaluate(LaboratoryOperationSuitabilityRequest request) {
        return calculator.evaluate(request);
    }
}

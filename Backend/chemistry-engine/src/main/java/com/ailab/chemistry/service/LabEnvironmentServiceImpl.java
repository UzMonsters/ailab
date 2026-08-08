package com.ailab.chemistry.service;

import com.ailab.chemistry.api.LabEnvironmentService;
import com.ailab.chemistry.domain.labenvironment.EnvironmentSuitabilityCalculator;
import com.ailab.chemistry.domain.labenvironment.EnvironmentSuitabilityRequest;
import com.ailab.chemistry.domain.labenvironment.EnvironmentSuitabilityResult;

@org.springframework.stereotype.Service
public class LabEnvironmentServiceImpl implements LabEnvironmentService {
    private final EnvironmentSuitabilityCalculator calculator = new EnvironmentSuitabilityCalculator();

    @Override
    public EnvironmentSuitabilityResult evaluate(EnvironmentSuitabilityRequest request) {
        return calculator.evaluate(request);
    }
}

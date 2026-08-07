package com.ailab.chemistry.domain.labenvironment;

public interface LabEnvironmentService {
    EnvironmentSuitabilityResult evaluate(EnvironmentSuitabilityRequest request);
}

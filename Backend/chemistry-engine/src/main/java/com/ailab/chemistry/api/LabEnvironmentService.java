package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.labenvironment.EnvironmentSuitabilityRequest;
import com.ailab.chemistry.domain.labenvironment.EnvironmentSuitabilityResult;

public interface LabEnvironmentService {
    EnvironmentSuitabilityResult evaluate(EnvironmentSuitabilityRequest request);
}

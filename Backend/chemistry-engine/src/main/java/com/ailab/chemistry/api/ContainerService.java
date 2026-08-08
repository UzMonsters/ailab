package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.container.ContainerSuitabilityRequest;
import com.ailab.chemistry.domain.container.ContainerSuitabilityResult;
import com.ailab.chemistry.domain.container.ContainerProfileSuitabilityRequest;

public interface ContainerService {
    ContainerSuitabilityResult evaluate(ContainerSuitabilityRequest request);

    ContainerSuitabilityResult evaluate(ContainerProfileSuitabilityRequest request);
}

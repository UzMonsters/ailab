package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionRequest;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionResult;

public interface EquilibriumCompositionService {

    EquilibriumCompositionResult calculate(
            EquilibriumCompositionRequest request
    );
}

package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumConstantResult;
import com.ailab.chemistry.domain.thermodynamics.NonstandardGibbsRequest;
import com.ailab.chemistry.domain.thermodynamics.NonstandardGibbsResult;

import java.util.Map;

public interface ThermodynamicEquilibriumService {
    EquilibriumConstantResult calculateStandardConstant(
            String reactionCode,
            Temperature temperature,
            Pressure standardPressure,
            Map<String, MatterState> stateOverrides);

    NonstandardGibbsResult calculateNonstandardGibbs(NonstandardGibbsRequest request);
}

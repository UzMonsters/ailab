package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.thermodynamics.AdiabaticTemperatureRequest;
import com.ailab.chemistry.domain.thermodynamics.AdiabaticTemperatureResult;
import com.ailab.chemistry.domain.thermodynamics.ReactionCalorimetryRequest;
import com.ailab.chemistry.domain.thermodynamics.ReactionCalorimetryResult;
import com.ailab.chemistry.domain.thermodynamics.SensibleHeatRequest;
import com.ailab.chemistry.domain.thermodynamics.SensibleHeatResult;
import com.ailab.chemistry.domain.thermodynamics.ThermalMixingRequest;
import com.ailab.chemistry.domain.thermodynamics.ThermalMixingResult;

public interface CalorimetryService {

    SensibleHeatResult calculateSensibleHeat(
            SensibleHeatRequest request
    );

    ThermalMixingResult calculateFinalTemperature(
            ThermalMixingRequest request
    );

    ReactionCalorimetryResult calculateReactionHeat(
            ReactionCalorimetryRequest request
    );

    AdiabaticTemperatureResult calculateAdiabaticFinalTemperature(
            AdiabaticTemperatureRequest request
    );
}

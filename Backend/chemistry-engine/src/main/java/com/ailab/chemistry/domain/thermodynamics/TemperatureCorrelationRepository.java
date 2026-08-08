package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.List;
import java.util.Optional;

public interface TemperatureCorrelationRepository {
    Optional<HeatCapacityCorrelation> find(String compoundCode, MatterState state, Temperature targetTemperature);

    List<HeatCapacityCorrelation> findAll();
}

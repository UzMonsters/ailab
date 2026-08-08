package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Temperature;

public interface VaporPressureCorrelation {
    String correlationId();
    String compoundCode();
    MatterState initialPhase();
    MatterState finalPhase();
    Temperature minTemperature();
    Temperature maxTemperature();
    String temperatureUnit();
    String pressureUnit();
}

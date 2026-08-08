package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;

public record PhaseTransitionConditions(Temperature temperature, Pressure pressure) {
}

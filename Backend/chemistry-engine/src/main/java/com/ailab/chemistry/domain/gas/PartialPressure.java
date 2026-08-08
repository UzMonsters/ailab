package com.ailab.chemistry.domain.gas;

import com.ailab.chemistry.domain.measurement.Pressure;

public record PartialPressure(String compoundCode, Pressure pressure) {
}

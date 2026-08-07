package com.ailab.chemistry.domain.electrochemistry;

import com.ailab.chemistry.domain.measurement.Temperature;

public record ElectrochemicalReferenceConditions(
        Temperature temperature,
        String solventOrMedium,
        String standardStateConvention
) {
}

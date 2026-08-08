package com.ailab.chemistry.domain.electrochemistry;

import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.List;

public record NernstRequest(
        String cathodeReductionRecordId,
        String anodeReductionRecordId,
        Temperature temperature,
        List<ElectrochemicalActivity> activities
) {
    public NernstRequest {
        activities = List.copyOf(activities);
    }
}

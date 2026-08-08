package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.Volume;

import java.util.Objects;

public record TitrationPointRequest(
        TitrationRequest request,
        Volume addedTitrantVolume
) {
    public TitrationPointRequest {
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(addedTitrantVolume, "addedTitrantVolume must not be null");
    }
}

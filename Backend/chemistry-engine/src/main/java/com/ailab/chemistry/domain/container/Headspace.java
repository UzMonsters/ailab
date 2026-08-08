package com.ailab.chemistry.domain.container;

import com.ailab.chemistry.domain.measurement.Volume;

import java.util.Objects;

public record Headspace(Volume volume) {
    public Headspace {
        Objects.requireNonNull(volume, "volume must not be null");
    }
}

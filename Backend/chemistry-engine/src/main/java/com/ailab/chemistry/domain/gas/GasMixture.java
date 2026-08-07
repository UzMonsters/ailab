package com.ailab.chemistry.domain.gas;

import com.ailab.chemistry.domain.measurement.Pressure;

import java.util.List;
import java.util.Objects;

public record GasMixture(Pressure totalPressure, List<GasMixtureComponent> components) {
    public GasMixture {
        Objects.requireNonNull(totalPressure, "totalPressure must not be null");
        components = List.copyOf(Objects.requireNonNull(components, "components must not be null"));
    }
}

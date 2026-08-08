package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicPropertyType;

import java.util.List;
import java.util.Optional;

public interface ThermodynamicReferenceService {
    ThermodynamicProfileDetails getProfile(String compoundCode);

    List<ThermodynamicPropertyDetails> findProperties(String compoundCode, ThermodynamicPropertyType type);

    Optional<ThermodynamicPropertyDetails> findExact(
            String compoundCode,
            ThermodynamicPropertyType type,
            MatterState state,
            Temperature temperature,
            Pressure pressure
    );
}

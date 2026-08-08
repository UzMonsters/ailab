package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.List;
import java.util.Optional;

public interface ThermodynamicReferenceRepository {
    Optional<ThermodynamicProfile> findProfile(String compoundCode);

    List<ThermodynamicProfile> findAllProfiles();

    default List<ThermodynamicPropertyRecord> findProperties(String compoundCode, ThermodynamicPropertyType type) {
        return findProfile(compoundCode).stream()
                .flatMap(profile -> profile.records().stream())
                .filter(record -> record.type() == type)
                .toList();
    }

    default Optional<ThermodynamicPropertyRecord> findExact(String compoundCode, ThermodynamicPropertyType type,
                                                            MatterState state, Temperature temperature, Pressure pressure) {
        return findProperties(compoundCode, type).stream()
                .filter(record -> record.conditions().state() == state)
                .filter(record -> record.conditions().temperature().equals(temperature))
                .filter(record -> record.conditions().pressure().equals(pressure))
                .findFirst();
    }
}

package com.ailab.chemistry.service;

import com.ailab.chemistry.api.ThermodynamicConditionDetails;
import com.ailab.chemistry.api.ThermodynamicProfileDetails;
import com.ailab.chemistry.api.ThermodynamicPropertyDetails;
import com.ailab.chemistry.api.ThermodynamicProvenanceDetails;
import com.ailab.chemistry.api.ThermodynamicReferenceService;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.MolarEnergyUnit;
import com.ailab.chemistry.domain.measurement.MolarEntropyUnit;
import com.ailab.chemistry.domain.measurement.MolarHeatCapacityUnit;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicErrorCode;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicException;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicProfile;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicPropertyRecord;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicPropertyType;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicReferenceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ThermodynamicReferenceServiceImpl implements ThermodynamicReferenceService {
    private final ThermodynamicReferenceRepository repository;

    public ThermodynamicReferenceServiceImpl(ThermodynamicReferenceRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    @Override
    public ThermodynamicProfileDetails getProfile(String compoundCode) {
        ThermodynamicProfile profile = repository.findProfile(compoundCode)
                .orElseThrow(() -> new ThermodynamicException(ThermodynamicErrorCode.MISSING_PROFILE,
                        "Missing thermodynamic profile for " + compoundCode));
        return toDetails(profile);
    }

    @Override
    public List<ThermodynamicPropertyDetails> findProperties(String compoundCode, ThermodynamicPropertyType type) {
        return repository.findProperties(compoundCode, type).stream()
                .map(record -> toDetails(compoundCode, record))
                .toList();
    }

    @Override
    public Optional<ThermodynamicPropertyDetails> findExact(String compoundCode, ThermodynamicPropertyType type, MatterState state,
                                                           Temperature temperature, Pressure pressure) {
        return repository.findExact(compoundCode, type, state, temperature, pressure)
                .map(record -> toDetails(compoundCode, record));
    }

    private ThermodynamicProfileDetails toDetails(ThermodynamicProfile profile) {
        return new ThermodynamicProfileDetails(profile.compoundCode(), profile.datasetVersion().value(),
                profile.records().stream().map(record -> toDetails(profile.compoundCode(), record)).toList());
    }

    private ThermodynamicPropertyDetails toDetails(String compoundCode, ThermodynamicPropertyRecord record) {
        BigDecimal value;
        String unit;
        if (record.energyValue().isPresent()) {
            value = record.energyValue().orElseThrow().in(MolarEnergyUnit.KILOJOULE_PER_MOLE);
            unit = MolarEnergyUnit.KILOJOULE_PER_MOLE.getSymbol();
        } else if (record.entropyValue().isPresent()) {
            value = record.entropyValue().orElseThrow().in(MolarEntropyUnit.JOULE_PER_MOLE_KELVIN);
            unit = MolarEntropyUnit.JOULE_PER_MOLE_KELVIN.getSymbol();
        } else {
            value = record.heatCapacityValue().orElseThrow().toCanonical().getValue();
            unit = MolarHeatCapacityUnit.JOULE_PER_MOLE_KELVIN.getSymbol();
        }
        return new ThermodynamicPropertyDetails(compoundCode, record.type(), value, unit, conditions(record), record.evidenceStatus(),
                new ThermodynamicProvenanceDetails(record.provenance().sourceIdentifier(), record.provenance().citation(),
                        record.provenance().reuseLimitations()));
    }

    private ThermodynamicConditionDetails conditions(ThermodynamicPropertyRecord record) {
        return new ThermodynamicConditionDetails(
                record.conditions().temperature().in(TemperatureUnit.KELVIN),
                record.conditions().pressure().in(PressureUnit.PASCAL),
                record.conditions().state(),
                record.conditions().standardStateConvention());
    }
}

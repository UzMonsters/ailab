package com.ailab.chemistry.infrastructure.persistence.physicalproperty;

import com.ailab.chemistry.domain.compound.Compound;
import com.ailab.chemistry.domain.compound.CompoundId;
import com.ailab.chemistry.domain.compound.ElementMassProvider;
import com.ailab.chemistry.domain.compound.KnownCompoundRegistry;
import com.ailab.chemistry.domain.physicalproperty.*;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Primary
@Profile({"test", "standalone-engine"})
public class InMemoryCompoundPhysicalPropertyProfileRepository implements CompoundPhysicalPropertyProfileRepository {

    private final List<CompoundPhysicalPropertyProfile> profiles;
    private final ElementMassProvider massProvider;

    public InMemoryCompoundPhysicalPropertyProfileRepository(ElementMassProvider massProvider) {
        this.massProvider = massProvider;
        this.profiles = new ArrayList<>(KnownCompoundPhysicalPropertyRegistry.buildAll55Profiles(massProvider));
    }

    @Override
    public Optional<CompoundPhysicalPropertyProfile> findByCompoundId(CompoundId compoundId) {
        return profiles.stream().filter(p -> p.getCompoundId().equals(compoundId)).findFirst();
    }

    @Override
    public Optional<CompoundPhysicalPropertyProfile> findByCompoundCode(String compoundCode) {
        return profiles.stream()
                .filter(p -> p.getCompoundId().equals(KnownCompoundRegistry.buildAll55CoreCompounds(massProvider).stream()
                        .filter(c -> c.getCode().getValue().equalsIgnoreCase(compoundCode))
                        .map(Compound::getId)
                        .findFirst()
                        .orElse(null)))
                .findFirst();
    }

    @Override
    public List<CompoundPhysicalPropertyProfile> findWithAvailableProperty(PhysicalPropertyType propertyType) {
        return profiles.stream()
                .filter(p -> p.getAvailabilityMap().getOrDefault(propertyType, PropertyAvailability.NOT_INCLUDED_IN_DATASET) == PropertyAvailability.AVAILABLE)
                .collect(Collectors.toList());
    }

    @Override
    public List<CompoundPhysicalPropertyProfile> findAll() {
        return new ArrayList<>(profiles);
    }

    @Override
    public long count() {
        return profiles.size();
    }
}

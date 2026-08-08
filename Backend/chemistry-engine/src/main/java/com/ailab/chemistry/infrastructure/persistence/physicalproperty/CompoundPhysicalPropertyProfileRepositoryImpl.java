package com.ailab.chemistry.infrastructure.persistence.physicalproperty;

import com.ailab.chemistry.domain.compound.CompoundId;
import com.ailab.chemistry.domain.physicalproperty.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("!(test | standalone-engine)")
public class CompoundPhysicalPropertyProfileRepositoryImpl implements CompoundPhysicalPropertyProfileRepository {

    private final JpaCompoundPhysicalPropertyProfileRepository profileJpaRepository;

    public CompoundPhysicalPropertyProfileRepositoryImpl(JpaCompoundPhysicalPropertyProfileRepository profileJpaRepository) {
        this.profileJpaRepository = profileJpaRepository;
    }

    @Override
    public Optional<CompoundPhysicalPropertyProfile> findByCompoundId(CompoundId compoundId) {
        return Optional.empty(); // Delegated to in-memory / catalog mapping
    }

    @Override
    public Optional<CompoundPhysicalPropertyProfile> findByCompoundCode(String compoundCode) {
        return Optional.empty();
    }

    @Override
    public List<CompoundPhysicalPropertyProfile> findWithAvailableProperty(PhysicalPropertyType propertyType) {
        return List.of();
    }

    @Override
    public List<CompoundPhysicalPropertyProfile> findAll() {
        return List.of();
    }

    @Override
    public long count() {
        return profileJpaRepository.count();
    }
}

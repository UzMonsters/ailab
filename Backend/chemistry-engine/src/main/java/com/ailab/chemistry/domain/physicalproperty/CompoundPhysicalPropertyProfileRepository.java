package com.ailab.chemistry.domain.physicalproperty;

import com.ailab.chemistry.domain.compound.CompoundId;

import java.util.List;
import java.util.Optional;

public interface CompoundPhysicalPropertyProfileRepository {
    Optional<CompoundPhysicalPropertyProfile> findByCompoundId(CompoundId compoundId);
    Optional<CompoundPhysicalPropertyProfile> findByCompoundCode(String compoundCode);
    List<CompoundPhysicalPropertyProfile> findWithAvailableProperty(PhysicalPropertyType propertyType);
    List<CompoundPhysicalPropertyProfile> findAll();
    long count();
}

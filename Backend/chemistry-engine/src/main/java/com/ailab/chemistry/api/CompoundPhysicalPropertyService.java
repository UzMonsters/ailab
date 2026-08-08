package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.physicalproperty.PhysicalPropertyType;

import java.util.List;
import java.util.UUID;

public interface CompoundPhysicalPropertyService {

    CompoundPhysicalPropertyDetails getByCompoundId(UUID compoundId);

    CompoundPhysicalPropertyDetails getByCompoundCode(String compoundCode);

    List<CompoundSummary> findWithAvailableProperty(PhysicalPropertyType propertyType);
}

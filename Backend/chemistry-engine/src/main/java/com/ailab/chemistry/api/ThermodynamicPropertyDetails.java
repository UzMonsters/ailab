package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.thermodynamics.ThermodynamicEvidenceStatus;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicPropertyType;

import java.math.BigDecimal;

public record ThermodynamicPropertyDetails(
        String compoundCode,
        ThermodynamicPropertyType type,
        BigDecimal value,
        String unitSymbol,
        ThermodynamicConditionDetails conditions,
        ThermodynamicEvidenceStatus evidenceStatus,
        ThermodynamicProvenanceDetails provenance) {
}

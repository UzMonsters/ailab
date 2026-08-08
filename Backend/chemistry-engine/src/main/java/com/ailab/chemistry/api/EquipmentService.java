package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.equipment.EquipmentSuitabilityRequest;
import com.ailab.chemistry.domain.equipment.EquipmentSuitabilityResult;
import com.ailab.chemistry.domain.equipment.EquipmentProfileSuitabilityRequest;

public interface EquipmentService {
    EquipmentSuitabilityResult evaluate(EquipmentSuitabilityRequest request);

    EquipmentSuitabilityResult evaluate(EquipmentProfileSuitabilityRequest request);
}

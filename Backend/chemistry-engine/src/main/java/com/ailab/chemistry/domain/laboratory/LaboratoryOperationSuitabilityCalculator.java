package com.ailab.chemistry.domain.laboratory;

import com.ailab.chemistry.domain.container.ContainerSuitabilityStatus;
import com.ailab.chemistry.domain.equipment.EquipmentSuitabilityStatus;
import com.ailab.chemistry.domain.labenvironment.EnvironmentSuitabilityStatus;

import java.util.ArrayList;
import java.util.List;

public final class LaboratoryOperationSuitabilityCalculator {
    public LaboratoryOperationSuitabilityResult evaluate(LaboratoryOperationSuitabilityRequest request) {
        List<LaboratoryOperationViolation> violations = new ArrayList<>();
        List<LaboratoryOperationWarning> warnings = new ArrayList<>();

        request.equipmentResult().violations().forEach(v ->
                violations.add(new LaboratoryOperationViolation("EQUIPMENT:" + v.code().name(), v.message())));
        request.containerResult().violations().forEach(v ->
                violations.add(new LaboratoryOperationViolation("CONTAINER:" + v.code().name(), v.message())));
        request.environmentResult().violations().forEach(v ->
                violations.add(new LaboratoryOperationViolation("ENVIRONMENT:" + v.code().name(), v.message())));

        request.equipmentResult().warnings().forEach(w -> warnings.add(new LaboratoryOperationWarning("Equipment: " + w.message())));
        request.environmentResult().warnings().forEach(w -> warnings.add(new LaboratoryOperationWarning("Environment: " + w.message())));

        boolean unsuitable = request.equipmentResult().status() == EquipmentSuitabilityStatus.UNSUITABLE
                || request.containerResult().status() == ContainerSuitabilityStatus.UNSUITABLE
                || request.environmentResult().status() == EnvironmentSuitabilityStatus.UNSUITABLE
                || !violations.isEmpty();
        LaboratoryOperationStatus status = unsuitable
                ? LaboratoryOperationStatus.UNSUITABLE
                : (warnings.isEmpty() ? LaboratoryOperationStatus.SUITABLE : LaboratoryOperationStatus.SUITABLE_WITH_WARNINGS);

        List<String> provenance = new ArrayList<>();
        provenance.addAll(request.equipmentResult().provenance());
        provenance.addAll(request.containerResult().provenance());
        provenance.addAll(request.environmentResult().provenance());
        provenance.addAll(request.provenance());

        return new LaboratoryOperationSuitabilityResult(
                status,
                request.equipmentResult().selectedProfileIds(),
                request.containerResult().selectedProfileId(),
                violations,
                warnings,
                request.assumptions(),
                provenance);
    }
}

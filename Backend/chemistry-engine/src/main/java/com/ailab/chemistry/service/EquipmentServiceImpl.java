package com.ailab.chemistry.service;

import com.ailab.chemistry.api.EquipmentService;
import com.ailab.chemistry.domain.equipment.EquipmentErrorCode;
import com.ailab.chemistry.domain.equipment.EquipmentProfileSuitabilityRequest;
import com.ailab.chemistry.domain.equipment.EquipmentReferenceRepository;
import com.ailab.chemistry.domain.equipment.EquipmentSuitabilityCalculator;
import com.ailab.chemistry.domain.equipment.EquipmentSuitabilityRequest;
import com.ailab.chemistry.domain.equipment.EquipmentSuitabilityResult;
import com.ailab.chemistry.domain.equipment.EquipmentSuitabilityStatus;
import com.ailab.chemistry.domain.equipment.EquipmentViolation;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;

@org.springframework.stereotype.Service
public class EquipmentServiceImpl implements EquipmentService {
    private final EquipmentSuitabilityCalculator calculator = new EquipmentSuitabilityCalculator();
    private final ObjectProvider<EquipmentReferenceRepository> repositoryProvider;

    public EquipmentServiceImpl(ObjectProvider<EquipmentReferenceRepository> repositoryProvider) {
        this.repositoryProvider = repositoryProvider;
    }

    @Override
    public EquipmentSuitabilityResult evaluate(EquipmentSuitabilityRequest request) {
        return calculator.evaluate(request);
    }

    @Override
    public EquipmentSuitabilityResult evaluate(EquipmentProfileSuitabilityRequest request) {
        EquipmentReferenceRepository repository = repositoryProvider.getIfAvailable();
        if (repository == null) {
            throw new IllegalStateException("Production equipment reference repository is unavailable");
        }
        return repository.findByProfileId(request.profileId())
                .map(profile -> calculator.evaluate(new EquipmentSuitabilityRequest(
                        profile, request.requirements(), request.calibrationRecords(), request.evaluationTimestamp())))
                .orElseGet(() -> new EquipmentSuitabilityResult(
                        EquipmentSuitabilityStatus.UNSUITABLE,
                        List.of(request.profileId()),
                        List.of(new EquipmentViolation(EquipmentErrorCode.PROFILE_UNAVAILABLE,
                                "Equipment reference profile is not active or not present: " + request.profileId())),
                        List.of(),
                        List.of()));
    }
}

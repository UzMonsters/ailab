package com.ailab.chemistry.service;

import com.ailab.chemistry.api.ContainerService;
import com.ailab.chemistry.domain.container.ContainerErrorCode;
import com.ailab.chemistry.domain.container.ContainerProfileSuitabilityRequest;
import com.ailab.chemistry.domain.container.ContainerReferenceRepository;
import com.ailab.chemistry.domain.container.ContainerSuitabilityCalculator;
import com.ailab.chemistry.domain.container.ContainerSuitabilityRequest;
import com.ailab.chemistry.domain.container.ContainerSuitabilityResult;
import com.ailab.chemistry.domain.container.ContainerSuitabilityStatus;
import com.ailab.chemistry.domain.container.ContainerViolation;
import com.ailab.chemistry.domain.container.FillFraction;
import com.ailab.chemistry.domain.container.Headspace;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;
import org.springframework.beans.factory.ObjectProvider;

import java.math.BigDecimal;
import java.util.List;

@org.springframework.stereotype.Service
public class ContainerServiceImpl implements ContainerService {
    private final ContainerSuitabilityCalculator calculator = new ContainerSuitabilityCalculator();
    private final ObjectProvider<ContainerReferenceRepository> repositoryProvider;

    public ContainerServiceImpl(ObjectProvider<ContainerReferenceRepository> repositoryProvider) {
        this.repositoryProvider = repositoryProvider;
    }

    @Override
    public ContainerSuitabilityResult evaluate(ContainerSuitabilityRequest request) {
        return calculator.evaluate(request);
    }

    @Override
    public ContainerSuitabilityResult evaluate(ContainerProfileSuitabilityRequest request) {
        ContainerReferenceRepository repository = repositoryProvider.getIfAvailable();
        if (repository == null) {
            throw new IllegalStateException("Production container reference repository is unavailable");
        }
        return repository.findByProfileId(request.profileId())
                .map(profile -> calculator.evaluate(new ContainerSuitabilityRequest(
                        profile,
                        request.actualContentVolume(),
                        request.sealedOperation(),
                        request.operatingTemperature(),
                        request.operatingPressure(),
                        request.requiredHeadspace(),
                        request.compoundOrFamily(),
                        request.physicalState(),
                        request.concentration(),
                        request.contactDuration())))
                .orElseGet(() -> new ContainerSuitabilityResult(
                        ContainerSuitabilityStatus.UNSUITABLE,
                        request.profileId(),
                        new FillFraction(BigDecimal.ZERO),
                        new Headspace(Volume.of("0", VolumeUnit.MILLILITER)),
                        List.of(new ContainerViolation(ContainerErrorCode.PROFILE_UNAVAILABLE,
                                "Container reference profile is not active or not present: " + request.profileId())),
                        List.of()));
    }
}

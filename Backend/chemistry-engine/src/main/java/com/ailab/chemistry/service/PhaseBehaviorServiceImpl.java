package com.ailab.chemistry.service;

import com.ailab.chemistry.api.PhaseBehaviorService;
import com.ailab.chemistry.domain.phasebehavior.BoilingPointRequest;
import com.ailab.chemistry.domain.phasebehavior.BoilingPointResult;
import com.ailab.chemistry.domain.phasebehavior.HeatingPathRequest;
import com.ailab.chemistry.domain.phasebehavior.HeatingPathResult;
import com.ailab.chemistry.domain.phasebehavior.PhaseBehaviorCalculator;
import com.ailab.chemistry.domain.phasebehavior.PhaseBehaviorRepository;
import com.ailab.chemistry.domain.phasebehavior.PhaseTransitionRequest;
import com.ailab.chemistry.domain.phasebehavior.PhaseTransitionResult;
import com.ailab.chemistry.domain.phasebehavior.SaturationPressureRequest;
import com.ailab.chemistry.domain.phasebehavior.SaturationPressureResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class PhaseBehaviorServiceImpl implements PhaseBehaviorService {
    private final PhaseBehaviorRepository repository;
    private final PhaseBehaviorCalculator calculator = new PhaseBehaviorCalculator();

    @Autowired
    public PhaseBehaviorServiceImpl(PhaseBehaviorRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository must not be null");
    }

    @Override
    public PhaseTransitionResult calculateTransition(PhaseTransitionRequest request) {
        return calculator.calculateTransition(request, repository);
    }

    @Override
    public SaturationPressureResult calculateSaturationPressure(SaturationPressureRequest request) {
        return calculator.calculateSaturationPressure(request, repository);
    }

    @Override
    public BoilingPointResult calculateBoilingPoint(BoilingPointRequest request) {
        return calculator.calculateBoilingPoint(request, repository);
    }

    @Override
    public HeatingPathResult calculateHeatingPath(HeatingPathRequest request) {
        return calculator.calculateHeatingPath(request, repository);
    }
}

package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.phasebehavior.BoilingPointRequest;
import com.ailab.chemistry.domain.phasebehavior.BoilingPointResult;
import com.ailab.chemistry.domain.phasebehavior.HeatingPathRequest;
import com.ailab.chemistry.domain.phasebehavior.HeatingPathResult;
import com.ailab.chemistry.domain.phasebehavior.PhaseTransitionRequest;
import com.ailab.chemistry.domain.phasebehavior.PhaseTransitionResult;
import com.ailab.chemistry.domain.phasebehavior.SaturationPressureRequest;
import com.ailab.chemistry.domain.phasebehavior.SaturationPressureResult;

public interface PhaseBehaviorService {
    PhaseTransitionResult calculateTransition(PhaseTransitionRequest request);
    SaturationPressureResult calculateSaturationPressure(SaturationPressureRequest request);
    BoilingPointResult calculateBoilingPoint(BoilingPointRequest request);
    HeatingPathResult calculateHeatingPath(HeatingPathRequest request);
}

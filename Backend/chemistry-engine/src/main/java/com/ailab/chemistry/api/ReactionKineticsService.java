package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.kinetics.ArrheniusRequest;
import com.ailab.chemistry.domain.kinetics.ArrheniusResult;
import com.ailab.chemistry.domain.kinetics.HalfLifeResult;
import com.ailab.chemistry.domain.kinetics.IntegratedRateLawRequest;
import com.ailab.chemistry.domain.kinetics.IntegratedRateLawResult;
import com.ailab.chemistry.domain.kinetics.KineticProgressRequest;
import com.ailab.chemistry.domain.kinetics.KineticProgressResult;
import com.ailab.chemistry.domain.kinetics.RateEvaluationRequest;
import com.ailab.chemistry.domain.kinetics.RateEvaluationResult;

public interface ReactionKineticsService {

    RateEvaluationResult calculateRate(
            RateEvaluationRequest request
    );

    IntegratedRateLawResult calculateIntegratedLaw(
            IntegratedRateLawRequest request
    );

    HalfLifeResult calculateHalfLife(
            IntegratedRateLawRequest request
    );

    ArrheniusResult calculateRateConstant(
            ArrheniusRequest request
    );

    KineticProgressResult simulateProgress(
            KineticProgressRequest request
    );
}

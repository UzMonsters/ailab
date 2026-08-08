package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.thermodynamics.HessLawRequest;
import com.ailab.chemistry.domain.thermodynamics.HessLawResult;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicCoverage;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicsResult;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicReferenceConditions;

import java.util.List;
import java.util.Map;

public interface ReactionThermodynamicsService {

    ReactionThermodynamicsResult calculate(
            String reactionCode,
            ThermodynamicReferenceConditions conditions,
            Map<String, MatterState> stateOverrides
    );

    HessLawResult calculateHessLaw(HessLawRequest request);

    List<ReactionThermodynamicCoverage> evaluateCatalogueCoverage(ThermodynamicReferenceConditions conditions);
}

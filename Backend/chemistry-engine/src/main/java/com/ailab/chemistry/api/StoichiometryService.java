package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.Mass;
import com.ailab.chemistry.domain.stoichiometry.*;

import java.util.List;

public interface StoichiometryService {

    AmountOfSubstance convertMassToMoles(
            String compoundCode,
            Mass mass
    );

    Mass convertMolesToMass(
            String compoundCode,
            AmountOfSubstance amount
    );

    StoichiometryCalculationResult calculateFromReactant(
            String reactionCode,
            String reactantCompoundCode,
            StoichiometricQuantity quantity
    );

    LimitingReagentResult determineLimitingReagent(
            String reactionCode,
            List<ReactionParticipantQuantity> reactants
    );

    TheoreticalYieldResult calculateTheoreticalYield(
            String reactionCode,
            List<ReactionParticipantQuantity> reactants,
            String productCompoundCode
    );

    ActualYieldResult evaluateActualYield(
            TheoreticalYieldResult theoreticalYield,
            Mass actualYield
    );
}

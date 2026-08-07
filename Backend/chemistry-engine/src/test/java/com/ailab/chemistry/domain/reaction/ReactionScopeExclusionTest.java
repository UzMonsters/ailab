package com.ailab.chemistry.domain.reaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ReactionScopeExclusionTest {

    @Test
    @DisplayName("Reaction Database Core contains zero stoichiometry, yield, kinetic or thermodynamic methods")
    void testScopeExclusion() {
        Class<Reaction> reactionClass = Reaction.class;
        Method[] methods = reactionClass.getDeclaredMethods();

        boolean hasStoichiometricAmountCalc = Arrays.stream(methods).anyMatch(m -> m.getName().toLowerCase().contains("calculateyield") || m.getName().toLowerCase().contains("limitingreagent"));
        boolean hasThermodynamics = Arrays.stream(methods).anyMatch(m -> m.getName().toLowerCase().contains("enthalpy") || m.getName().toLowerCase().contains("gibbs") || m.getName().toLowerCase().contains("spontaneous"));
        boolean hasKinetics = Arrays.stream(methods).anyMatch(m -> m.getName().toLowerCase().contains("ratelaw") || m.getName().toLowerCase().contains("half-life") || m.getName().toLowerCase().contains("activationenergy"));

        assertFalse(hasStoichiometricAmountCalc, "Reaction class must not implement stoichiometric yield calculations in Phase 6A");
        assertFalse(hasThermodynamics, "Reaction class must not implement thermodynamic feasibility in Phase 6A");
        assertFalse(hasKinetics, "Reaction class must not implement kinetic simulation in Phase 6A");
    }
}

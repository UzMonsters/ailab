package com.ailab.chemistry.domain.reaction;

import com.ailab.chemistry.domain.equation.DefaultEquationBalancer;
import com.ailab.chemistry.domain.formula.DefaultFormulaParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReactionHydratesTest {

    @Test
    @DisplayName("Hydrate CuSO4·5H2O retains full composition during balancing and dehydration reactions")
    void testHydrateIntegrity() {
        List<Reaction> reactions = KnownReactionRegistry.buildAll26Reactions();

        Reaction hydration = reactions.stream()
                .filter(r -> r.getReactionCode().getValue().equals("RXN-CUSO4-HYDRATION"))
                .findFirst().orElseThrow();

        Reaction dehydration = reactions.stream()
                .filter(r -> r.getReactionCode().getValue().equals("RXN-CUSO4-DEHYDRATION"))
                .findFirst().orElseThrow();

        // Check hydration
        assertEquals("CuSO4 + 5H2O -> CuSO4·5H2O", hydration.getEquation().getCanonicalBalancedEquation());
        ReactionTerm hydrateProduct = hydration.getProducts().get(0);
        assertEquals("COMP-CUSO4-5H2O", hydrateProduct.getCompoundCode());
        assertEquals("CuSO4·5H2O", hydrateProduct.getFormula());

        // Check dehydration
        assertEquals("CuSO4·5H2O -> CuSO4 + 5H2O", dehydration.getEquation().getCanonicalBalancedEquation());
        ReactionTerm hydrateReactant = dehydration.getReactants().get(0);
        assertEquals("COMP-CUSO4-5H2O", hydrateReactant.getCompoundCode());
        assertEquals("CuSO4·5H2O", hydrateReactant.getFormula());

        // Validate atom balance on full hydrate
        ReactionValidator validator = new ReactionValidator(new DefaultFormulaParser(), new DefaultEquationBalancer());
        assertDoesNotThrow(() -> validator.validateAndVerify(hydration));
        assertDoesNotThrow(() -> validator.validateAndVerify(dehydration));
    }
}

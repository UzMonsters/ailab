package com.ailab.chemistry.domain.reaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReactionIsomersTest {

    @Test
    @DisplayName("Ethanol and Dimethyl Ether remain distinct reaction participants with unique compound identity and signatures")
    void testIsomerIntegrity() {
        List<Reaction> reactions = KnownReactionRegistry.buildAll26Reactions();

        Reaction ethanolComb = reactions.stream()
                .filter(r -> r.getReactionCode().getValue().equals("RXN-ETHANOL-COMBUSTION"))
                .findFirst().orElseThrow();

        Reaction dmeComb = reactions.stream()
                .filter(r -> r.getReactionCode().getValue().equals("RXN-DIMETHYL-ETHER-COMBUSTION"))
                .findFirst().orElseThrow();

        // Distinct reaction codes and IDs
        assertNotEquals(ethanolComb.getReactionCode(), dmeComb.getReactionCode());
        assertNotEquals(ethanolComb.getId(), dmeComb.getId());

        // Distinct compound references
        String ethanolCompound = ethanolComb.getReactants().get(0).getCompoundCode();
        String dmeCompound = dmeComb.getReactants().get(0).getCompoundCode();
        assertEquals("COMP-ETHANOL", ethanolCompound);
        assertEquals("COMP-DIMETHYL-ETHER", dmeCompound);

        // Distinct reaction signatures
        assertNotEquals(ethanolComb.getEquation().getReactionSignature(), dmeComb.getEquation().getReactionSignature());
        assertTrue(ethanolComb.getEquation().getReactionSignature().contains("COMP-ETHANOL"));
        assertTrue(dmeComb.getEquation().getReactionSignature().contains("COMP-DIMETHYL-ETHER"));

        // Formulas in canonical equations preserve respective normalized formulas
        assertEquals("C2H5OH", ethanolComb.getReactants().get(0).getFormula());
        assertEquals("CH3OCH3", dmeComb.getReactants().get(0).getFormula());
    }
}

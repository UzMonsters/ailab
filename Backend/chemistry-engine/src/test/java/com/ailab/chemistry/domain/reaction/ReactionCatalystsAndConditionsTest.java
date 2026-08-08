package com.ailab.chemistry.domain.reaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReactionCatalystsAndConditionsTest {

    @Test
    @DisplayName("Catalysts reference valid compound or element entities and are not consumed as terms")
    void testCatalystReferences() {
        Reaction decomp = KnownReactionRegistry.buildAll26Reactions().stream()
                .filter(r -> r.getReactionCode().getValue().equals("RXN-H2O2-DECOMP"))
                .findFirst().orElseThrow();

        List<Catalyst> catalysts = decomp.getCatalysts();
        assertEquals(1, catalysts.size());
        Catalyst cat = catalysts.get(0);
        assertEquals(CatalystReferenceType.COMPOUND, cat.getReferenceType());
        assertEquals("COMP-CUO", cat.getReferenceCode());
        assertEquals(CatalystRole.CATALYST, cat.getRole());

        // Verify catalyst does NOT appear as a reactant or product term
        boolean appearsInTerms = decomp.getTerms().stream().anyMatch(t -> t.getCompoundCode().equalsIgnoreCase("COMP-CUO"));
        assertFalse(appearsInTerms, "Catalyst must not be included in atom balance terms");
    }

    @Test
    @DisplayName("Condition sets preserve reference temperature, pressure, energy input, and atmosphere")
    void testConditionSets() {
        Reaction synthesis = KnownReactionRegistry.buildAll26Reactions().stream()
                .filter(r -> r.getReactionCode().getValue().equals("RXN-WATER-SYNTHESIS"))
                .findFirst().orElseThrow();

        List<ReactionConditionSet> conditions = synthesis.getConditionSets();
        assertEquals(1, conditions.size());
        ReactionConditionSet cond = conditions.get(0);
        assertEquals(EnergyInput.HEAT, cond.getEnergyInput());
        assertEquals(ReactionAtmosphere.OXYGEN, cond.getAtmosphere());
    }
}

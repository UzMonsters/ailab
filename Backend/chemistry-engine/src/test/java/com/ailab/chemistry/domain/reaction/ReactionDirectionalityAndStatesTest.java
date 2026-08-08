package com.ailab.chemistry.domain.reaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReactionDirectionalityAndStatesTest {

    @Test
    @DisplayName("Directionality is decoupled from raw equation string and supports reversible reactions")
    void testDirectionality() {
        List<Reaction> reactions = KnownReactionRegistry.buildAll26Reactions();
        long reversibleCount = reactions.stream().filter(r -> r.getDirectionality() == ReactionDirectionality.REVERSIBLE).count();
        long irreversibleCount = reactions.stream().filter(r -> r.getDirectionality() == ReactionDirectionality.IRREVERSIBLE).count();

        assertTrue(reversibleCount >= 4, "Should have at least 4 reversible reactions");
        assertTrue(irreversibleCount >= 15, "Should have at least 15 irreversible reactions");

        Reaction haber = reactions.stream().filter(r -> r.getReactionCode().getValue().equals("RXN-HABER-PROCESS")).findFirst().orElseThrow();
        assertEquals(ReactionDirectionality.REVERSIBLE, haber.getDirectionality());
    }

    @Test
    @DisplayName("Species states are annotated on terms without altering chemical formula representation")
    void testSpeciesStateAnnotations() {
        Reaction r = KnownReactionRegistry.buildAll26Reactions().stream()
                .filter(rxn -> rxn.getReactionCode().getValue().equals("RXN-NEUT-HCL-NAOH"))
                .findFirst().orElseThrow();

        ReactionTerm hclTerm = r.getReactants().stream().filter(t -> t.getCompoundCode().equals("COMP-HCL")).findFirst().orElseThrow();
        assertEquals("HCl", hclTerm.getFormula());
        assertEquals(ReactionSpeciesState.AQUEOUS, hclTerm.getSpeciesState());
    }
}

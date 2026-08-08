package com.ailab.chemistry.domain.reaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReactionTypesTest {

    @Test
    @DisplayName("Multi-label reaction taxonomy supports multiple curated and derived types")
    void testMultiLabelTaxonomy() {
        Reaction r = KnownReactionRegistry.buildAll26Reactions().stream()
                .filter(rxn -> rxn.getReactionCode().getValue().equals("RXN-WATER-SYNTHESIS"))
                .findFirst().orElseThrow();

        List<ReactionTypeAssignment> types = r.getTypeAssignments();
        assertEquals(3, types.size());
        assertTrue(types.stream().anyMatch(t -> t.getTypeCode() == ReactionTypeCode.SYNTHESIS));
        assertTrue(types.stream().anyMatch(t -> t.getTypeCode() == ReactionTypeCode.COMBUSTION));
        assertTrue(types.stream().anyMatch(t -> t.getTypeCode() == ReactionTypeCode.REDOX));
    }

    @Test
    @DisplayName("Safe rule derivation engine generates REVERSIBLE_REACTION and GAS_EVOLUTION when explicit evidence exists")
    void testSafeRuleDerivation() {
        List<ReactionTerm> terms = List.of(
                new ReactionTerm(java.util.UUID.randomUUID(), "COMP-NAHCO3", "NaHCO3", ReactionSide.REACTANT, java.math.BigInteger.TWO, ReactionSpeciesState.SOLID, 1),
                new ReactionTerm(java.util.UUID.randomUUID(), "COMP-CO2", "CO2", ReactionSide.PRODUCT, java.math.BigInteger.ONE, ReactionSpeciesState.GAS, 2)
        );

        List<ReactionTypeAssignment> derived = ReactionTypeDerivationEngine.deriveSafeTypes(ReactionDirectionality.REVERSIBLE, terms);
        assertEquals(2, derived.size());
        assertTrue(derived.stream().anyMatch(d -> d.getTypeCode() == ReactionTypeCode.REVERSIBLE_REACTION));
        assertTrue(derived.stream().anyMatch(d -> d.getTypeCode() == ReactionTypeCode.GAS_EVOLUTION));
    }
}

package com.ailab.chemistry.domain.reaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReactionTest {

    @Test
    @DisplayName("Reaction code validation rejects blank or null code")
    void testReactionCodeValidation() {
        assertThrows(NullPointerException.class, () -> new ReactionCode(null));
        assertThrows(ReactionException.class, () -> new ReactionCode("   "));
        ReactionCode code = new ReactionCode("RXN-TEST-01");
        assertEquals("RXN-TEST-01", code.getValue());
    }

    @Test
    @DisplayName("Reaction name and alias validation rejects blank name and duplicate alias")
    void testReactionNameAndAliasValidation() {
        assertThrows(ReactionException.class, () -> new ReactionName(""));
        ReactionName name = new ReactionName("Water Synthesis");
        assertEquals("Water Synthesis", name.getValue());

        ReactionAlias alias1 = new ReactionAlias("H2 Combustion", "COMMON");
        ReactionAlias alias2 = new ReactionAlias("H2 Combustion", "SCIENTIFIC"); // case-insensitive duplicate name

        ReactionId id = ReactionId.generate();
        ReactionCode code = new ReactionCode("RXN-001");
        ReactionEquation eq = new ReactionEquation("2H2 + O2 -> 2H2O", "2H2 + O2 -> 2H2O", "2H2 + O2 -> 2H2O", "2*COMP-H2+1*COMP-O2->2*COMP-H2O[IRREVERSIBLE]");
        List<ReactionTerm> terms = List.of(
                new ReactionTerm(UUID.randomUUID(), "COMP-H2", "H2", ReactionSide.REACTANT, BigInteger.TWO, ReactionSpeciesState.GAS, 1),
                new ReactionTerm(UUID.randomUUID(), "COMP-O2", "O2", ReactionSide.REACTANT, BigInteger.ONE, ReactionSpeciesState.GAS, 2),
                new ReactionTerm(UUID.randomUUID(), "COMP-H2O", "H2O", ReactionSide.PRODUCT, BigInteger.TWO, ReactionSpeciesState.GAS, 3)
        );
        ReactionProvenance prov = new ReactionProvenance("CRC-HANDBOOK-104", List.of("equation"), "");

        assertThrows(ReactionException.class, () -> new Reaction(
                id, code, name, List.of(alias1, alias2), eq, terms, ReactionDirectionality.IRREVERSIBLE, List.of(), List.of(), List.of(), "v1.0", prov
        ));
    }

    @Test
    @DisplayName("Reaction collections are strictly immutable")
    void testImmutableCollections() {
        Reaction r = KnownReactionRegistry.buildAll26Reactions().get(0);
        assertThrows(UnsupportedOperationException.class, () -> r.getAliases().add(new ReactionAlias("New", "COMMON")));
        assertThrows(UnsupportedOperationException.class, () -> r.getTerms().add(new ReactionTerm(UUID.randomUUID(), "COMP-X", "X", ReactionSide.REACTANT, BigInteger.ONE, ReactionSpeciesState.GAS, 99)));
        assertThrows(UnsupportedOperationException.class, () -> r.getTypeAssignments().clear());
    }
}

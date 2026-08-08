package com.ailab.chemistry.domain.reaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReactionTermsTest {

    @Test
    @DisplayName("Zero or negative coefficients are rejected")
    void testInvalidCoefficients() {
        UUID cid = UUID.randomUUID();
        assertThrows(ReactionException.class, () -> new ReactionTerm(cid, "COMP-H2", "H2", ReactionSide.REACTANT, BigInteger.ZERO, ReactionSpeciesState.GAS, 1));
        assertThrows(ReactionException.class, () -> new ReactionTerm(cid, "COMP-H2", "H2", ReactionSide.REACTANT, BigInteger.valueOf(-1), ReactionSpeciesState.GAS, 1));
    }

    @Test
    @DisplayName("Free electron e- is rejected as compound term")
    void testElectronRejection() {
        UUID cid = UUID.randomUUID();
        assertThrows(ReactionException.class, () -> new ReactionTerm(cid, "e-", "e-", ReactionSide.REACTANT, BigInteger.ONE, ReactionSpeciesState.UNKNOWN, 1));
    }

    @Test
    @DisplayName("Duplicate reactant or product compound terms on one side are rejected")
    void testDuplicateTermsRejection() {
        ReactionId id = ReactionId.generate();
        ReactionCode code = new ReactionCode("RXN-DUP");
        ReactionName name = new ReactionName("Duplicate Term Reaction");
        ReactionEquation eq = new ReactionEquation("H2 + H2 -> H2O", "H2 + H2 -> H2O", "H2 + H2 -> H2O", "1*COMP-H2+1*COMP-H2->1*COMP-H2O[IRREVERSIBLE]");
        ReactionProvenance prov = new ReactionProvenance("CRC-HANDBOOK-104", List.of("equation"), "");

        UUID cid = UUID.nameUUIDFromBytes("compound-COMP-H2".getBytes());
        List<ReactionTerm> duplicateTerms = List.of(
                new ReactionTerm(cid, "COMP-H2", "H2", ReactionSide.REACTANT, BigInteger.ONE, ReactionSpeciesState.GAS, 1),
                new ReactionTerm(cid, "COMP-H2", "H2", ReactionSide.REACTANT, BigInteger.ONE, ReactionSpeciesState.GAS, 2),
                new ReactionTerm(UUID.randomUUID(), "COMP-H2O", "H2O", ReactionSide.PRODUCT, BigInteger.ONE, ReactionSpeciesState.GAS, 3)
        );

        assertThrows(ReactionException.class, () -> new Reaction(id, code, name, List.of(), eq, duplicateTerms, ReactionDirectionality.IRREVERSIBLE, List.of(), List.of(), List.of(), "v1.0", prov));
    }

    @Test
    @DisplayName("Terms order is deterministic according to termOrder")
    void testDeterministicTermOrdering() {
        Reaction r = KnownReactionRegistry.buildAll26Reactions().get(0);
        List<ReactionTerm> terms = r.getTerms();
        assertEquals(1, terms.get(0).getTermOrder());
        assertEquals(2, terms.get(1).getTermOrder());
        assertEquals(3, terms.get(2).getTermOrder());
    }
}

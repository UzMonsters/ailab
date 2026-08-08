package com.ailab.chemistry.domain.reaction;

import com.ailab.chemistry.domain.equation.DefaultEquationBalancer;
import com.ailab.chemistry.domain.formula.DefaultFormulaParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ReactionBalancingTest {

    private ReactionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ReactionValidator(new DefaultFormulaParser(), new DefaultEquationBalancer());
    }

    @Test
    @DisplayName("All 26 catalogue reactions validate exactly with atom and charge conservation")
    void testAll26ReactionsValidation() {
        List<Reaction> reactions = KnownReactionRegistry.buildAll26Reactions();
        assertEquals(26, reactions.size());
        for (Reaction r : reactions) {
            assertDoesNotThrow(() -> validator.validateAndVerify(r), "Reaction failed validation: " + r.getReactionCode());
        }
    }

    @Test
    @DisplayName("Unbalanced reaction is rejected by validator")
    void testUnbalancedReactionRejection() {
        ReactionId id = ReactionId.generate();
        ReactionCode code = new ReactionCode("RXN-UNBALANCED");
        ReactionName name = new ReactionName("Unbalanced Water Synthesis");
        ReactionEquation eq = new ReactionEquation("H2 + O2 -> H2O", "H2 + O2 -> H2O", "H2 + O2 -> H2O", "1*COMP-H2+1*COMP-O2->1*COMP-H2O[IRREVERSIBLE]");
        ReactionProvenance prov = new ReactionProvenance("CRC-HANDBOOK-104", List.of("equation"), "");

        // H2 + O2 -> H2O with 1, 1 -> 1 is unbalanced
        List<ReactionTerm> terms = List.of(
                new ReactionTerm(UUID.randomUUID(), "COMP-H2", "H2", ReactionSide.REACTANT, BigInteger.ONE, ReactionSpeciesState.GAS, 1),
                new ReactionTerm(UUID.randomUUID(), "COMP-O2", "O2", ReactionSide.REACTANT, BigInteger.ONE, ReactionSpeciesState.GAS, 2),
                new ReactionTerm(UUID.randomUUID(), "COMP-H2O", "H2O", ReactionSide.PRODUCT, BigInteger.ONE, ReactionSpeciesState.GAS, 3)
        );

        Reaction unbalReaction = new Reaction(id, code, name, List.of(), eq, terms, ReactionDirectionality.IRREVERSIBLE, List.of(), List.of(), List.of(), "v1.0", prov);
        assertThrows(ReactionException.class, () -> validator.validateAndVerify(unbalReaction));
    }

    @Test
    @DisplayName("Non-minimal coefficients are rejected (e.g. 4H2 + 2O2 -> 4H2O)")
    void testNonMinimalCoefficientsRejection() {
        ReactionId id = ReactionId.generate();
        ReactionCode code = new ReactionCode("RXN-NON-MINIMAL");
        ReactionName name = new ReactionName("Non Minimal Water Synthesis");
        ReactionEquation eq = new ReactionEquation("4H2 + 2O2 -> 4H2O", "4H2 + 2O2 -> 4H2O", "4H2 + 2O2 -> 4H2O", "4*COMP-H2+2*COMP-O2->4*COMP-H2O[IRREVERSIBLE]");
        ReactionProvenance prov = new ReactionProvenance("CRC-HANDBOOK-104", List.of("equation"), "");

        List<ReactionTerm> terms = List.of(
                new ReactionTerm(UUID.randomUUID(), "COMP-H2", "H2", ReactionSide.REACTANT, BigInteger.valueOf(4), ReactionSpeciesState.GAS, 1),
                new ReactionTerm(UUID.randomUUID(), "COMP-O2", "O2", ReactionSide.REACTANT, BigInteger.valueOf(2), ReactionSpeciesState.GAS, 2),
                new ReactionTerm(UUID.randomUUID(), "COMP-H2O", "H2O", ReactionSide.PRODUCT, BigInteger.valueOf(4), ReactionSpeciesState.GAS, 3)
        );

        Reaction nonMinReaction = new Reaction(id, code, name, List.of(), eq, terms, ReactionDirectionality.IRREVERSIBLE, List.of(), List.of(), List.of(), "v1.0", prov);
        assertThrows(ReactionException.class, () -> validator.validateAndVerify(nonMinReaction));
    }
}

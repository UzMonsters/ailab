package com.ailab.chemistry.service;

import com.ailab.chemistry.api.BalancedReactionDetails;
import com.ailab.chemistry.api.ReactionCatalogService;
import com.ailab.chemistry.api.ReactionDetails;
import com.ailab.chemistry.api.ReactionSummary;
import com.ailab.chemistry.domain.reaction.ReactionException;
import com.ailab.chemistry.infrastructure.persistence.reaction.InMemoryReactionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReactionCatalogServiceTest {

    private ReactionCatalogService reactionCatalogService;

    @BeforeEach
    void setUp() {
        reactionCatalogService = new ReactionCatalogServiceImpl(new InMemoryReactionRepository());
    }

    @Test
    @DisplayName("Lookup reaction by code succeeds")
    void testGetByCode() {
        ReactionDetails details = reactionCatalogService.getByCode("RXN-WATER-SYNTHESIS");
        assertNotNull(details);
        assertEquals("RXN-WATER-SYNTHESIS", details.reactionCode());
        assertEquals("Synthesis of Water", details.primaryName());
        assertEquals("2H2 + O2 -> 2H2O", details.canonicalEquation());
    }

    @Test
    @DisplayName("Find reactions by reactant compound code succeeds")
    void testFindByReactant() {
        List<ReactionSummary> results = reactionCatalogService.findByReactant("COMP-H2");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(r -> r.reactionCode().equals("RXN-WATER-SYNTHESIS")));
    }

    @Test
    @DisplayName("Find reactions by product compound code succeeds")
    void testFindByProduct() {
        List<ReactionSummary> results = reactionCatalogService.findByProduct("COMP-H2O");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(r -> r.reactionCode().equals("RXN-WATER-SYNTHESIS")));
    }

    @Test
    @DisplayName("Find reactions involving compound code succeeds")
    void testFindInvolvingCompound() {
        List<ReactionSummary> results = reactionCatalogService.findInvolvingCompound("COMP-CO2");
        assertTrue(results.size() >= 10, "Should find multiple reactions involving CO2");
    }

    @Test
    @DisplayName("Find reactions by type code succeeds")
    void testFindByReactionType() {
        List<ReactionSummary> combustionRxns = reactionCatalogService.findByReactionType("COMBUSTION");
        assertFalse(combustionRxns.isEmpty());
        assertTrue(combustionRxns.stream().anyMatch(r -> r.reactionCode().equals("RXN-METHANE-COMBUSTION")));
    }

    @Test
    @DisplayName("Find reversible reactions succeeds")
    void testFindReversible() {
        List<ReactionSummary> reversibleRxns = reactionCatalogService.findReversible();
        assertFalse(reversibleRxns.isEmpty());
        assertTrue(reversibleRxns.stream().anyMatch(r -> r.reactionCode().equals("RXN-HABER-PROCESS")));
    }

    @Test
    @DisplayName("Validate and balance user input equation returns balanced DTO")
    void testValidateAndBalance() {
        BalancedReactionDetails details = reactionCatalogService.validateAndBalance("H2 + O2 -> H2O");
        assertNotNull(details);
        assertTrue(details.isBalanced());
        assertEquals("2H2 + O2 -> 2H2O", details.canonicalBalancedEquation());
    }

    @Test
    @DisplayName("Lookup for non-existent reaction code throws ReactionException")
    void testNonExistentCodeThrows() {
        assertThrows(ReactionException.class, () -> reactionCatalogService.getByCode("RXN-NONEXISTENT"));
    }
}

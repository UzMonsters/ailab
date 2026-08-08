package com.ailab.chemistry.service;

import com.ailab.chemistry.api.*;
import com.ailab.chemistry.compound.TestElementMassProvider;
import com.ailab.chemistry.domain.measurement.*;
import com.ailab.chemistry.domain.stoichiometry.*;
import com.ailab.chemistry.infrastructure.persistence.compound.InMemoryCompoundRepository;
import com.ailab.chemistry.infrastructure.persistence.reaction.InMemoryReactionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StoichiometryServiceTest {

    private StoichiometryService stoichiometryService;

    @BeforeEach
    void setUp() {
        InMemoryReactionRepository reactionRepository = new InMemoryReactionRepository();
        InMemoryCompoundRepository compoundRepository = new InMemoryCompoundRepository(new TestElementMassProvider());
        CompoundCatalogService compoundCatalogService = new CompoundCatalogServiceImpl(compoundRepository);
        stoichiometryService = new StoichiometryServiceImpl(reactionRepository, compoundCatalogService);
    }

    @Test
    @DisplayName("Convert mass to moles and moles to mass for catalogue compound H2O")
    void testMassMolesConversions() {
        Mass mass = Mass.of("36.03", MassUnit.GRAM);
        AmountOfSubstance moles = stoichiometryService.convertMassToMoles("COMP-H2O", mass);

        // ~ 2.0 mol H2O
        assertThat(moles.in(AmountOfSubstanceUnit.MOLE)).isCloseTo(new BigDecimal("2.0"), org.assertj.core.data.Offset.offset(new BigDecimal("0.05")));

        Mass reconvertedMass = stoichiometryService.convertMolesToMass("COMP-H2O", moles);
        assertThat(reconvertedMass.in(MassUnit.GRAM)).isCloseTo(new BigDecimal("36.03"), org.assertj.core.data.Offset.offset(new BigDecimal("0.05")));
    }

    @Test
    @DisplayName("Calculate from reactant for 2H2 + O2 -> 2H2O given H2 input")
    void testCalculateFromReactant() {
        StoichiometricQuantity qty = StoichiometricQuantity.fromMoles(AmountOfSubstance.of("2.0", AmountOfSubstanceUnit.MOLE));
        StoichiometryCalculationResult result = stoichiometryService.calculateFromReactant("RXN-WATER-SYNTHESIS", "COMP-H2", qty);

        assertNotNull(result);
        assertEquals("RXN-WATER-SYNTHESIS", result.getReactionCode());
        assertEquals("COMP-H2", result.getSourceReactantCode());

        // 2 mol H2 requires 1 mol O2 and produces 2 mol H2O
        AmountOfSubstance o2Req = result.getRequiredReactantMoles().get("COMP-O2");
        assertThat(o2Req.in(AmountOfSubstanceUnit.MOLE)).isCloseTo(new BigDecimal("1.0"), org.assertj.core.data.Offset.offset(new BigDecimal("0.01")));

        AmountOfSubstance h2oExp = result.getExpectedProductMoles().get("COMP-H2O");
        assertThat(h2oExp.in(AmountOfSubstanceUnit.MOLE)).isCloseTo(new BigDecimal("2.0"), org.assertj.core.data.Offset.offset(new BigDecimal("0.01")));
    }

    @Test
    @DisplayName("Determine limiting reagent and calculate theoretical yield for methane combustion")
    void testLimitingReagentAndTheoreticalYield() {
        List<ReactionParticipantQuantity> reactants = List.of(
                ReactionParticipantQuantity.ofMoles("COMP-CH4", AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE)),
                ReactionParticipantQuantity.ofMoles("COMP-O2", AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE)) // Limiting (requires 2.0)
        );

        LimitingReagentResult lr = stoichiometryService.determineLimitingReagent("RXN-METHANE-COMBUSTION", reactants);
        assertEquals("COMP-O2", lr.getPrimaryLimitingCompoundCode());

        TheoreticalYieldResult theo = stoichiometryService.calculateTheoreticalYield("RXN-METHANE-COMBUSTION", reactants, "COMP-CO2");
        assertEquals("COMP-CO2", theo.getProductCompoundCode());
        assertThat(theo.getTheoreticalMoles().in(AmountOfSubstanceUnit.MOLE)).isCloseTo(new BigDecimal("0.5"), org.assertj.core.data.Offset.offset(new BigDecimal("0.01")));
    }

    @Test
    @DisplayName("Evaluate actual yield and percent yield calculation")
    void testEvaluateActualYield() {
        List<ReactionParticipantQuantity> reactants = List.of(
                ReactionParticipantQuantity.ofMoles("COMP-H2", AmountOfSubstance.of("2.0", AmountOfSubstanceUnit.MOLE)),
                ReactionParticipantQuantity.ofMoles("COMP-O2", AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE))
        );

        TheoreticalYieldResult theo = stoichiometryService.calculateTheoreticalYield("RXN-WATER-SYNTHESIS", reactants, "COMP-H2O");
        BigDecimal theoGrams = theo.getTheoreticalMass().in(MassUnit.GRAM);
        BigDecimal halfTheoGrams = theoGrams.divide(new BigDecimal("2"), com.ailab.chemistry.domain.measurement.ScientificMath.CALCULATION_CONTEXT);
        Mass actualMass = Mass.of(halfTheoGrams, MassUnit.GRAM); // 50% yield

        ActualYieldResult actual = stoichiometryService.evaluateActualYield(theo, actualMass);
        assertEquals(YieldStatus.NORMAL, actual.getPercentYield().getStatus());
        assertThat(actual.getPercentYield().getPercentage()).isCloseTo(new BigDecimal("50.0"), org.assertj.core.data.Offset.offset(new BigDecimal("0.1")));
    }
}

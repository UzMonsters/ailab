package com.ailab.chemistry.domain.stoichiometry;

import com.ailab.chemistry.domain.compound.MolarMass;
import com.ailab.chemistry.domain.compound.MolarMassCalculationBasis;
import com.ailab.chemistry.domain.measurement.*;
import com.ailab.chemistry.domain.reaction.KnownReactionRegistry;
import com.ailab.chemistry.domain.reaction.Reaction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StoichiometryCalculatorTest {

    private StoichiometryCalculator calculator;
    private MolarMassCalculationBasis basis;

    // Approximate Molar Masses for verification tests
    private MolarMass h2Mass;
    private MolarMass o2Mass;
    private MolarMass h2oMass;
    private MolarMass ch4Mass;
    private MolarMass co2Mass;
    private MolarMass hclMass;
    private MolarMass naohMass;
    private MolarMass naclMass;
    private MolarMass h2o2Mass;
    private MolarMass nahco3Mass;
    private MolarMass na2co3Mass;

    @BeforeEach
    void setUp() {
        calculator = new StoichiometryCalculator();
        basis = new MolarMassCalculationBasis("IUPAC-2021", "1.0");

        h2Mass = MolarMass.exact(new BigDecimal("2.01588"), basis);
        o2Mass = MolarMass.exact(new BigDecimal("31.9988"), basis);
        h2oMass = MolarMass.exact(new BigDecimal("18.01528"), basis);
        ch4Mass = MolarMass.exact(new BigDecimal("16.0425"), basis);
        co2Mass = MolarMass.exact(new BigDecimal("44.0095"), basis);
        hclMass = MolarMass.exact(new BigDecimal("36.461"), basis);
        naohMass = MolarMass.exact(new BigDecimal("39.9971"), basis);
        naclMass = MolarMass.exact(new BigDecimal("58.443"), basis);
        h2o2Mass = MolarMass.exact(new BigDecimal("34.0147"), basis);
        nahco3Mass = MolarMass.exact(new BigDecimal("84.007"), basis);
        na2co3Mass = MolarMass.exact(new BigDecimal("105.988"), basis);
    }

    @Test
    @DisplayName("2H2 + O2 -> 2H2O: Mass and mole conversion, limiting reagents, exact mixture, and excess calculation")
    void testWaterSynthesisCalculations() {
        Reaction reaction = KnownReactionRegistry.buildAll26Reactions().get(0); // RXN-WATER-SYNTHESIS
        Map<String, MolarMass> masses = Map.of(
                "COMP-H2", h2Mass,
                "COMP-O2", o2Mass,
                "COMP-H2O", h2oMass
        );

        // 1. Limiting Oxygen case: 4g H2 (~1.984 mol) + 16g O2 (~0.500 mol) -> O2 limiting
        Map<String, StoichiometricQuantity> inputs1 = Map.of(
                "COMP-H2", StoichiometricQuantity.fromMass(Mass.of("4.0", MassUnit.GRAM)),
                "COMP-O2", StoichiometricQuantity.fromMass(Mass.of("16.0", MassUnit.GRAM))
        );

        LimitingReagentResult lr1 = calculator.determineLimitingReagent(reaction, inputs1, masses);
        assertFalse(lr1.isTied());
        assertEquals("COMP-O2", lr1.getPrimaryLimitingCompoundCode());

        TheoreticalYieldResult theo1 = calculator.calculateTheoreticalYield(reaction, inputs1, masses, "COMP-H2O");
        assertEquals("COMP-O2", theo1.getLimitingReagentResult().getPrimaryLimitingCompoundCode());
        // 0.5 mol O2 produces 1.0 mol H2O (~18.015g)
        assertTrue(theo1.getTheoreticalMoles().in(AmountOfSubstanceUnit.MOLE).compareTo(new BigDecimal("0.99")) > 0);
        assertTrue(theo1.getTheoreticalMass().in(MassUnit.GRAM).compareTo(new BigDecimal("17.5")) > 0);

        // 2. Limiting Hydrogen case: 1g H2 (~0.496 mol) + 32g O2 (1 mol) -> H2 limiting
        Map<String, StoichiometricQuantity> inputs2 = Map.of(
                "COMP-H2", StoichiometricQuantity.fromMass(Mass.of("1.0", MassUnit.GRAM)),
                "COMP-O2", StoichiometricQuantity.fromMass(Mass.of("32.0", MassUnit.GRAM))
        );

        LimitingReagentResult lr2 = calculator.determineLimitingReagent(reaction, inputs2, masses);
        assertEquals("COMP-H2", lr2.getPrimaryLimitingCompoundCode());

        // 3. Exact Stoichiometric mixture: 2.01588 mol H2 + 1.00000 mol O2 -> Tied limiting reagents
        Map<String, StoichiometricQuantity> inputs3 = Map.of(
                "COMP-H2", StoichiometricQuantity.fromMoles(AmountOfSubstance.of("2.0", AmountOfSubstanceUnit.MOLE)),
                "COMP-O2", StoichiometricQuantity.fromMoles(AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE))
        );

        LimitingReagentResult lr3 = calculator.determineLimitingReagent(reaction, inputs3, masses);
        assertTrue(lr3.isTied());
        assertEquals(2, lr3.getLimitingCompoundCodes().size());
    }

    @Test
    @DisplayName("CH4 + 2O2 -> CO2 + 2H2O: Hydrocarbon combustion with multiple products")
    void testMethaneCombustionCalculations() {
        Reaction reaction = KnownReactionRegistry.buildAll26Reactions().stream()
                .filter(r -> r.getReactionCode().getValue().equals("RXN-METHANE-COMBUSTION"))
                .findFirst().orElseThrow();

        Map<String, MolarMass> masses = Map.of(
                "COMP-CH4", ch4Mass,
                "COMP-O2", o2Mass,
                "COMP-CO2", co2Mass,
                "COMP-H2O", h2oMass
        );

        // 16.0425g CH4 (1.0 mol) + 63.9976g O2 (2.0 mol)
        Map<String, StoichiometricQuantity> inputs = Map.of(
                "COMP-CH4", StoichiometricQuantity.fromMass(Mass.of("16.0425", MassUnit.GRAM)),
                "COMP-O2", StoichiometricQuantity.fromMass(Mass.of("63.9976", MassUnit.GRAM))
        );

        TheoreticalYieldResult theoCO2 = calculator.calculateTheoreticalYield(reaction, inputs, masses, "COMP-CO2");
        assertEquals("COMP-CO2", theoCO2.getProductCompoundCode());
        // Produces 1 mol CO2 (~44.01g) and 2 mol H2O (~36.03g)
        assertEquals(1, theoCO2.getTheoreticalMoles().in(AmountOfSubstanceUnit.MOLE).intValue());
        assertTrue(theoCO2.getAllProductYields().containsKey("COMP-H2O"));
        assertTrue(theoCO2.getAllProductYields().get("COMP-H2O").in(MassUnit.GRAM).compareTo(new BigDecimal("35.0")) > 0);
    }

    @Test
    @DisplayName("HCl + NaOH -> NaCl + H2O: 1:1 Acid-Base neutralization equivalence")
    void testAcidBaseNeutralizationCalculations() {
        Reaction reaction = KnownReactionRegistry.buildAll26Reactions().stream()
                .filter(r -> r.getReactionCode().getValue().equals("RXN-NEUT-HCL-NAOH"))
                .findFirst().orElseThrow();

        Map<String, MolarMass> masses = Map.of(
                "COMP-HCL", hclMass,
                "COMP-NAOH", naohMass,
                "COMP-NACL", naclMass,
                "COMP-H2O", h2oMass
        );

        Map<String, StoichiometricQuantity> inputs = Map.of(
                "COMP-HCL", StoichiometricQuantity.fromMoles(AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE)),
                "COMP-NAOH", StoichiometricQuantity.fromMoles(AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE))
        );

        TheoreticalYieldResult theo = calculator.calculateTheoreticalYield(reaction, inputs, masses, "COMP-NACL");
        assertTrue(theo.getLimitingReagentResult().isTied());
        assertEquals(1, theo.getTheoreticalMoles().in(AmountOfSubstanceUnit.MOLE).intValue());
    }

    @Test
    @DisplayName("2H2O2 -> 2H2O + O2: Single reactant decomposition")
    void testDecompositionCalculations() {
        Reaction reaction = KnownReactionRegistry.buildAll26Reactions().stream()
                .filter(r -> r.getReactionCode().getValue().equals("RXN-H2O2-DECOMP"))
                .findFirst().orElseThrow();

        Map<String, MolarMass> masses = Map.of(
                "COMP-H2O2", h2o2Mass,
                "COMP-H2O", h2oMass,
                "COMP-O2", o2Mass
        );

        Map<String, StoichiometricQuantity> inputs = Map.of(
                "COMP-H2O2", StoichiometricQuantity.fromMoles(AmountOfSubstance.of("2.0", AmountOfSubstanceUnit.MOLE))
        );

        TheoreticalYieldResult theoO2 = calculator.calculateTheoreticalYield(reaction, inputs, masses, "COMP-O2");
        assertEquals(1, theoO2.getTheoreticalMoles().in(AmountOfSubstanceUnit.MOLE).intValue());
    }

    @Test
    @DisplayName("2NaHCO3 -> Na2CO3 + CO2 + H2O: Single reactant producing three products")
    void testBakingSodaDecompositionCalculations() {
        Reaction reaction = KnownReactionRegistry.buildAll26Reactions().stream()
                .filter(r -> r.getReactionCode().getValue().equals("RXN-NAHCO3-DECOMP"))
                .findFirst().orElseThrow();

        Map<String, MolarMass> masses = Map.of(
                "COMP-NAHCO3", nahco3Mass,
                "COMP-NA2CO3", na2co3Mass,
                "COMP-CO2", co2Mass,
                "COMP-H2O", h2oMass
        );

        Map<String, StoichiometricQuantity> inputs = Map.of(
                "COMP-NAHCO3", StoichiometricQuantity.fromMoles(AmountOfSubstance.of("2.0", AmountOfSubstanceUnit.MOLE))
        );

        TheoreticalYieldResult theo = calculator.calculateTheoreticalYield(reaction, inputs, masses, "COMP-NA2CO3");
        assertEquals(3, theo.getAllProductYields().size());
        assertEquals(1, theo.getTheoreticalMoles().in(AmountOfSubstanceUnit.MOLE).intValue());
    }

    @Test
    @DisplayName("Purity-adjusted reactant input reduces pure active moles correctly")
    void testPurityAdjustedInput() {
        Reaction reaction = KnownReactionRegistry.buildAll26Reactions().get(0);
        Map<String, MolarMass> masses = Map.of("COMP-H2", h2Mass, "COMP-O2", o2Mass, "COMP-H2O", h2oMass);

        // 100g of 50% pure H2 = 50g pure H2 (~24.8 mol)
        StoichiometricQuantity impureH2 = StoichiometricQuantity.fromMass(Mass.of("100.0", MassUnit.GRAM), Purity.ofPercentage("50"));
        AmountOfSubstance pureMoles = impureH2.toPureMoles(h2Mass);

        assertTrue(pureMoles.in(AmountOfSubstanceUnit.MOLE).compareTo(new BigDecimal("24.0")) > 0);
    }

    @Test
    @DisplayName("Actual yield evaluation handles normal yield and yield > 100% (marked ABOVE_THEORETICAL)")
    void testActualAndPercentYield() {
        Reaction reaction = KnownReactionRegistry.buildAll26Reactions().get(0);
        Map<String, MolarMass> masses = Map.of("COMP-H2", h2Mass, "COMP-O2", o2Mass, "COMP-H2O", h2oMass);

        Map<String, StoichiometricQuantity> inputs = Map.of(
                "COMP-H2", StoichiometricQuantity.fromMoles(AmountOfSubstance.of("2.0", AmountOfSubstanceUnit.MOLE)),
                "COMP-O2", StoichiometricQuantity.fromMoles(AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE))
        );

        TheoreticalYieldResult theo = calculator.calculateTheoreticalYield(reaction, inputs, masses, "COMP-H2O");
        // Theo mass ~ 36.03g

        // 1. Normal yield: 32.427g (~90%)
        ActualYieldResult actualNormal = calculator.evaluateActualYield(theo, Mass.of("32.427", MassUnit.GRAM), h2oMass);
        assertEquals(YieldStatus.NORMAL, actualNormal.getPercentYield().getStatus());
        assertTrue(actualNormal.getPercentYield().getPercentage().compareTo(new BigDecimal("89.0")) > 0);

        // 2. Yield > 100%: 40.0g (~111%)
        ActualYieldResult actualAbove = calculator.evaluateActualYield(theo, Mass.of("40.0", MassUnit.GRAM), h2oMass);
        assertEquals(YieldStatus.ABOVE_THEORETICAL, actualAbove.getPercentYield().getStatus());
    }

    @Test
    @DisplayName("Product supplied as reactant throws PRODUCT_SUPPLIED_AS_REACTANT")
    void testProductSuppliedAsReactantRejection() {
        Reaction reaction = KnownReactionRegistry.buildAll26Reactions().get(0);
        Map<String, MolarMass> masses = Map.of("COMP-H2", h2Mass, "COMP-O2", o2Mass, "COMP-H2O", h2oMass);

        Map<String, StoichiometricQuantity> invalidInputs = Map.of(
                "COMP-H2O", StoichiometricQuantity.fromMoles(AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE))
        );

        StoichiometryException ex = assertThrows(StoichiometryException.class, () ->
                calculator.calculateTheoreticalYield(reaction, invalidInputs, masses, "COMP-H2O"));
        assertEquals(StoichiometryErrorCode.PRODUCT_SUPPLIED_AS_REACTANT, ex.getErrorCode());
    }

    @Test
    @DisplayName("Interval-aware molar mass propagates lower and upper bounds for theoretical moles")
    void testIntervalMolarMassPropagation() {
        MolarMass intervalMass = MolarMass.interval(new BigDecimal("100.0"), new BigDecimal("98.0"), new BigDecimal("102.0"), basis);
        StoichiometricQuantity q = StoichiometricQuantity.fromMass(Mass.of("100.0", MassUnit.GRAM));

        AmountOfSubstance repMoles = q.toPureMoles(intervalMass);
        AmountOfSubstance lowMoles = q.toPureMolesLowerBound(intervalMass);
        AmountOfSubstance uppMoles = q.toPureMolesUpperBound(intervalMass);

        assertEquals(0, repMoles.in(AmountOfSubstanceUnit.MOLE).compareTo(new BigDecimal("1.0")));
        // Lower moles: 100 / 102 = ~0.9803
        // Upper moles: 100 / 98 = ~1.0204
        assertTrue(lowMoles.in(AmountOfSubstanceUnit.MOLE).compareTo(repMoles.in(AmountOfSubstanceUnit.MOLE)) < 0);
        assertTrue(uppMoles.in(AmountOfSubstanceUnit.MOLE).compareTo(repMoles.in(AmountOfSubstanceUnit.MOLE)) > 0);
    }
}

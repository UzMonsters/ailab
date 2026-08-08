package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.equation.RationalNumber;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HessLawCalculatorTest {

    private final HessLawCalculator calculator = new HessLawCalculator();

    @Test
    void combinesExactRationalVectorsAndCancelsIntermediates() {
        var request = new HessLawRequest(
                List.of(
                        component("methane-combustion", 1, 1,
                                vector(term("COMP-CH4", MatterState.GAS, -1), term("COMP-O2", MatterState.GAS, -2),
                                        term("COMP-CO2", MatterState.GAS, 1), term("COMP-H2O", MatterState.GAS, 2)),
                                props("-802.319", "-800.916", "-5.21", "-30.15")),
                        component("reverse-half-co-oxidation", -1, 2,
                                vector(term("COMP-CO", MatterState.GAS, -2), term("COMP-O2", MatterState.GAS, -1),
                                        term("COMP-CO2", MatterState.GAS, 2)),
                                props("-565.968", "-514.382", "-173.72", "15.77")),
                        component("reverse-water-formation", -3, 2,
                                vector(term("COMP-H2", MatterState.GAS, -2), term("COMP-O2", MatterState.GAS, -1),
                                        term("COMP-H2O", MatterState.GAS, 2)),
                                props("-483.652", "-457.144", "89.85", "-38.48"))
                ),
                vector(term("COMP-CH4", MatterState.GAS, -1), term("COMP-H2O", MatterState.GAS, -1),
                        term("COMP-CO", MatterState.GAS, 1), term("COMP-H2", MatterState.GAS, 3)));

        var result = calculator.calculate(request);

        assertThat(result.resultingVector()).isEqualTo(request.targetVector());
        assertThat(result.intermediateCancellations()).contains("COMP-CO2|GAS", "COMP-O2|GAS");
        assertThat(result.properties().get(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY).value())
                .isEqualByComparingTo("206.143");
        assertThat(result.properties().get(ReactionThermodynamicProperty.STANDARD_REACTION_GIBBS_ENERGY).value())
                .isEqualByComparingTo("141.991");
        assertThat(result.derivation()).hasSize(3);
    }

    @Test
    void rejectsTargetEquationMismatchWithoutDecimalApproximation() {
        var request = new HessLawRequest(
                List.of(component("water", 1, 1,
                        vector(term("COMP-H2", MatterState.GAS, -2), term("COMP-O2", MatterState.GAS, -1),
                                term("COMP-H2O", MatterState.GAS, 2)),
                        props("-483.652", "-457.144", "89.85", "-38.48"))),
                vector(term("COMP-H2", MatterState.GAS, -1), term("COMP-O2", MatterState.GAS, -1),
                        term("COMP-H2O", MatterState.GAS, 1)));

        assertThatThrownBy(() -> calculator.calculate(request))
                .isInstanceOf(ThermodynamicException.class)
                .extracting("errorCode")
                .isEqualTo(ThermodynamicErrorCode.HESS_TARGET_MISMATCH);
    }

    @Test
    void rejectsStateIncompatibleCancellation() {
        var request = new HessLawRequest(
                List.of(
                        component("produce-liquid-water", 1, 1,
                                vector(term("COMP-H2O", MatterState.LIQUID, 1)),
                                props("-285.830", "-237.129", "69.91", "75.38")),
                        component("consume-gas-water", 1, 1,
                                vector(term("COMP-H2O", MatterState.GAS, -1)),
                                props("241.826", "228.572", "-188.83", "-33.58"))
                ),
                vector());

        assertThatThrownBy(() -> calculator.calculate(request))
                .isInstanceOf(ThermodynamicException.class)
                .extracting("errorCode")
                .isEqualTo(ThermodynamicErrorCode.HESS_STATE_INCOMPATIBLE_CANCELLATION);
    }

    private static HessReactionTerm component(String code, long numerator, long denominator,
                                              ReactionThermodynamicVector vector,
                                              ReactionThermodynamicPropertySet properties) {
        return new HessReactionTerm(code, RationalNumber.of(numerator, denominator), vector, properties);
    }

    private static ReactionThermodynamicVector vector(ReactionThermodynamicVectorTerm... terms) {
        return ReactionThermodynamicVector.of(List.of(terms));
    }

    private static ReactionThermodynamicVectorTerm term(String compoundCode, MatterState state, long coefficient) {
        return new ReactionThermodynamicVectorTerm(compoundCode, state, RationalNumber.of(coefficient, 1));
    }

    private static ReactionThermodynamicPropertySet props(String enthalpy, String gibbs, String entropy, String cp) {
        return ReactionThermodynamicPropertySet.of(
                new ReactionThermodynamicResultProperty(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY, new BigDecimal(enthalpy), "kJ/mol"),
                new ReactionThermodynamicResultProperty(ReactionThermodynamicProperty.STANDARD_REACTION_GIBBS_ENERGY, new BigDecimal(gibbs), "kJ/mol"),
                new ReactionThermodynamicResultProperty(ReactionThermodynamicProperty.STANDARD_REACTION_ENTROPY, new BigDecimal(entropy), "J/(mol*K)"),
                new ReactionThermodynamicResultProperty(ReactionThermodynamicProperty.STANDARD_REACTION_HEAT_CAPACITY, new BigDecimal(cp), "J/(mol*K)")
        );
    }
}

package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.equation.RationalNumber;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.reaction.ReactionSide;
import com.ailab.chemistry.domain.reaction.ReactionSpeciesState;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReactionThermodynamicsCalculatorTest {

    private static final Temperature T25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);
    private static final Pressure P1BAR = Pressure.of("1.000", PressureUnit.BAR);

    private final ReactionThermodynamicsCalculator calculator = new ReactionThermodynamicsCalculator();

    @Test
    void waterLiquidFormationUsesProductPositiveReactantNegativeContributions() {
        var result = calculator.calculate(request(
                List.of(
                        term("COMP-H2", "H2", ReactionSide.REACTANT, 2, ReactionSpeciesState.GAS),
                        term("COMP-O2", "O2", ReactionSide.REACTANT, 1, ReactionSpeciesState.GAS),
                        term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 2, ReactionSpeciesState.LIQUID)
                ),
                Map.of(
                        "COMP-H2|GAS", values("0", "0", "130.68", "28.84", MatterState.GAS),
                        "COMP-O2|GAS", values("0", "0", "205.15", "29.36", MatterState.GAS),
                        "COMP-H2O|LIQUID", values("-285.830", "-237.129", "69.91", "75.38", MatterState.LIQUID)
                )));

        assertThat(result.status()).isEqualTo(ReactionThermodynamicStatus.CALCULABLE);
        assertThat(result.property(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY).value())
                .isEqualByComparingTo("-571.660");
        assertThat(result.property(ReactionThermodynamicProperty.STANDARD_REACTION_GIBBS_ENERGY).value())
                .isEqualByComparingTo("-474.258");
        assertThat(result.property(ReactionThermodynamicProperty.STANDARD_REACTION_ENTROPY).value())
                .isEqualByComparingTo("-326.69");
        assertThat(result.property(ReactionThermodynamicProperty.STANDARD_REACTION_HEAT_CAPACITY).value())
                .isEqualByComparingTo("63.72");
        assertThat(result.property(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY).sign())
                .isEqualTo(ThermodynamicSign.NEGATIVE);
        assertThat(result.terms()).filteredOn(term -> term.compoundCode().equals("COMP-H2O"))
                .extracting(ReactionThermodynamicTerm::signedCoefficient)
                .containsExactly(RationalNumber.of(BigInteger.TWO));
    }

    @Test
    void gasWaterFormationDiffersFromLiquidWaterFormation() {
        var liquid = calculator.calculate(request(
                List.of(
                        term("COMP-H2", "H2", ReactionSide.REACTANT, 2, ReactionSpeciesState.GAS),
                        term("COMP-O2", "O2", ReactionSide.REACTANT, 1, ReactionSpeciesState.GAS),
                        term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 2, ReactionSpeciesState.LIQUID)
                ),
                Map.of(
                        "COMP-H2|GAS", values("0", "0", "130.68", "28.84", MatterState.GAS),
                        "COMP-O2|GAS", values("0", "0", "205.15", "29.36", MatterState.GAS),
                        "COMP-H2O|LIQUID", values("-285.830", "-237.129", "69.91", "75.38", MatterState.LIQUID)
                )));
        var gas = calculator.calculate(request(
                List.of(
                        term("COMP-H2", "H2", ReactionSide.REACTANT, 2, ReactionSpeciesState.GAS),
                        term("COMP-O2", "O2", ReactionSide.REACTANT, 1, ReactionSpeciesState.GAS),
                        term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 2, ReactionSpeciesState.GAS)
                ),
                Map.of(
                        "COMP-H2|GAS", values("0", "0", "130.68", "28.84", MatterState.GAS),
                        "COMP-O2|GAS", values("0", "0", "205.15", "29.36", MatterState.GAS),
                        "COMP-H2O|GAS", values("-241.826", "-228.572", "188.83", "33.58", MatterState.GAS)
                )));

        assertThat(gas.property(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY).value())
                .isEqualByComparingTo("-483.652");
        assertThat(gas.property(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY).value())
                .isNotEqualByComparingTo(liquid.property(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY).value());
    }

    @Test
    void missingCoverageDoesNotDefaultToZero() {
        var result = calculator.calculate(request(
                List.of(
                        term("COMP-HCL", "HCl", ReactionSide.REACTANT, 1, ReactionSpeciesState.AQUEOUS),
                        term("COMP-NAOH", "NaOH", ReactionSide.REACTANT, 1, ReactionSpeciesState.AQUEOUS),
                        term("COMP-NACL", "NaCl", ReactionSide.PRODUCT, 1, ReactionSpeciesState.AQUEOUS),
                        term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 1, ReactionSpeciesState.LIQUID)
                ),
                Map.of("COMP-H2O|LIQUID", values("-285.830", "-237.129", "69.91", "75.38", MatterState.LIQUID))));

        assertThat(result.status()).isEqualTo(ReactionThermodynamicStatus.INCOMPLETE_COVERAGE);
        assertThat(result.coverage().missingPhaseSpecificRecords()).contains("COMP-HCL|AQUEOUS");
        assertThat(result.properties()).isEmpty();
    }

    @Test
    void reversingReactionNegatesEveryReactionProperty() {
        var forward = calculator.calculate(request(
                List.of(
                        term("COMP-CO", "CO", ReactionSide.REACTANT, 2, ReactionSpeciesState.GAS),
                        term("COMP-O2", "O2", ReactionSide.REACTANT, 1, ReactionSpeciesState.GAS),
                        term("COMP-CO2", "CO2", ReactionSide.PRODUCT, 2, ReactionSpeciesState.GAS)
                ),
                Map.of(
                        "COMP-CO|GAS", values("-110.525", "-137.168", "197.66", "29.14", MatterState.GAS),
                        "COMP-O2|GAS", values("0", "0", "205.15", "29.36", MatterState.GAS),
                        "COMP-CO2|GAS", values("-393.509", "-394.359", "213.79", "37.135", MatterState.GAS)
                )));
        var reverse = calculator.calculate(request(
                List.of(
                        term("COMP-CO2", "CO2", ReactionSide.REACTANT, 2, ReactionSpeciesState.GAS),
                        term("COMP-CO", "CO", ReactionSide.PRODUCT, 2, ReactionSpeciesState.GAS),
                        term("COMP-O2", "O2", ReactionSide.PRODUCT, 1, ReactionSpeciesState.GAS)
                ),
                Map.of(
                        "COMP-CO|GAS", values("-110.525", "-137.168", "197.66", "29.14", MatterState.GAS),
                        "COMP-O2|GAS", values("0", "0", "205.15", "29.36", MatterState.GAS),
                        "COMP-CO2|GAS", values("-393.509", "-394.359", "213.79", "37.135", MatterState.GAS)
                )));

        for (ReactionThermodynamicProperty property : ReactionThermodynamicProperty.values()) {
            assertThat(reverse.property(property).value())
                    .isEqualByComparingTo(forward.property(property).value().negate());
        }
    }

    @Test
    void multiplyingReactionScalesEveryReactionProperty() {
        var base = calculator.calculate(request(
                List.of(
                        term("COMP-CO", "CO", ReactionSide.REACTANT, 2, ReactionSpeciesState.GAS),
                        term("COMP-O2", "O2", ReactionSide.REACTANT, 1, ReactionSpeciesState.GAS),
                        term("COMP-CO2", "CO2", ReactionSide.PRODUCT, 2, ReactionSpeciesState.GAS)
                ),
                Map.of(
                        "COMP-CO|GAS", values("-110.525", "-137.168", "197.66", "29.14", MatterState.GAS),
                        "COMP-O2|GAS", values("0", "0", "205.15", "29.36", MatterState.GAS),
                        "COMP-CO2|GAS", values("-393.509", "-394.359", "213.79", "37.135", MatterState.GAS)
                )));
        var doubled = calculator.calculate(request(
                List.of(
                        term("COMP-CO", "CO", ReactionSide.REACTANT, 4, ReactionSpeciesState.GAS),
                        term("COMP-O2", "O2", ReactionSide.REACTANT, 2, ReactionSpeciesState.GAS),
                        term("COMP-CO2", "CO2", ReactionSide.PRODUCT, 4, ReactionSpeciesState.GAS)
                ),
                Map.of(
                        "COMP-CO|GAS", values("-110.525", "-137.168", "197.66", "29.14", MatterState.GAS),
                        "COMP-O2|GAS", values("0", "0", "205.15", "29.36", MatterState.GAS),
                        "COMP-CO2|GAS", values("-393.509", "-394.359", "213.79", "37.135", MatterState.GAS)
                )));

        for (ReactionThermodynamicProperty property : ReactionThermodynamicProperty.values()) {
            assertThat(doubled.property(property).value())
                    .isEqualByComparingTo(base.property(property).value().multiply(new BigDecimal("2")));
        }
    }

    @Test
    void unknownStateRequiresExplicitResolution() {
        var result = calculator.calculate(request(
                List.of(term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 1, ReactionSpeciesState.UNKNOWN)),
                Map.of()));

        assertThat(result.status()).isEqualTo(ReactionThermodynamicStatus.INCOMPLETE_COVERAGE);
        assertThat(result.coverage().missingPhysicalStates()).contains("COMP-H2O");
    }

    @Test
    void conflictingStandardStateConventionIsRejected() {
        var badRecord = new ReactionThermodynamicRecordSet(
                "COMP-H2O",
                MatterState.LIQUID,
                new ThermodynamicReferenceConditions(T25, P1BAR, MatterState.LIQUID, StandardStateConvention.IDEAL_GAS_STANDARD_STATE),
                sourceProperty("0", ThermodynamicPropertyType.STANDARD_ENTHALPY_OF_FORMATION),
                sourceProperty("0", ThermodynamicPropertyType.STANDARD_GIBBS_ENERGY_OF_FORMATION),
                sourceProperty("1", ThermodynamicPropertyType.STANDARD_MOLAR_ENTROPY),
                sourceProperty("1", ThermodynamicPropertyType.MOLAR_HEAT_CAPACITY));

        assertThatThrownBy(() -> calculator.calculate(request(
                List.of(term("COMP-H2O", "H2O", ReactionSide.PRODUCT, 1, ReactionSpeciesState.LIQUID)),
                Map.of("COMP-H2O|LIQUID", badRecord))))
                .isInstanceOf(ThermodynamicException.class)
                .extracting("errorCode")
                .isEqualTo(ThermodynamicErrorCode.CONFLICTING_STANDARD_STATE_CONVENTION);
    }

    private static ReactionThermodynamicsRequest request(List<ReactionThermodynamicRequestTerm> terms,
                                                         Map<String, ReactionThermodynamicRecordSet> records) {
        return new ReactionThermodynamicsRequest("TEST-RXN", "test equation", terms, T25, P1BAR, records);
    }

    private static ReactionThermodynamicRequestTerm term(String compoundCode, String formula, ReactionSide side,
                                                         int coefficient, ReactionSpeciesState state) {
        return new ReactionThermodynamicRequestTerm(compoundCode, formula, side, BigInteger.valueOf(coefficient), state);
    }

    private static ReactionThermodynamicRecordSet values(String enthalpy, String gibbs, String entropy, String cp, MatterState state) {
        return new ReactionThermodynamicRecordSet(
                state == MatterState.GAS ? "COMP-GAS" : "COMP-CONDENSED",
                state,
                new ThermodynamicReferenceConditions(T25, P1BAR, state, conventionFor(state)),
                sourceProperty(enthalpy, ThermodynamicPropertyType.STANDARD_ENTHALPY_OF_FORMATION),
                sourceProperty(gibbs, ThermodynamicPropertyType.STANDARD_GIBBS_ENERGY_OF_FORMATION),
                sourceProperty(entropy, ThermodynamicPropertyType.STANDARD_MOLAR_ENTROPY),
                sourceProperty(cp, ThermodynamicPropertyType.MOLAR_HEAT_CAPACITY));
    }

    private static ReactionThermodynamicSourceProperty sourceProperty(String value, ThermodynamicPropertyType sourceType) {
        return new ReactionThermodynamicSourceProperty(sourceType, new BigDecimal(value), "unit",
                new ThermodynamicProvenance("TEST", "hand checked fixture", "test only"));
    }

    private static StandardStateConvention conventionFor(MatterState state) {
        return switch (state) {
            case GAS -> StandardStateConvention.IDEAL_GAS_STANDARD_STATE;
            case LIQUID -> StandardStateConvention.PURE_SUBSTANCE_STANDARD_STATE;
            case SOLID -> StandardStateConvention.SOLID_REFERENCE_STATE;
            default -> throw new IllegalArgumentException("Unsupported fixture state " + state);
        };
    }
}

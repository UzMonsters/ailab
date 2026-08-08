package com.ailab.chemistry.service;

import com.ailab.chemistry.api.ReactionThermodynamicsService;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.reaction.ReactionException;
import com.ailab.chemistry.domain.thermodynamics.HessLawRequest;
import com.ailab.chemistry.domain.thermodynamics.HessReactionTerm;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicProperty;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicStatus;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicVector;
import com.ailab.chemistry.domain.thermodynamics.StandardStateConvention;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicException;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicReferenceConditions;
import com.ailab.chemistry.infrastructure.persistence.reaction.InMemoryReactionRepository;
import com.ailab.chemistry.infrastructure.persistence.thermodynamics.InMemoryThermodynamicReferenceRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReactionThermodynamicsServiceTest {

    private static final ThermodynamicReferenceConditions CONDITIONS = new ThermodynamicReferenceConditions(
            Temperature.of("25.0", TemperatureUnit.CELSIUS),
            Pressure.of("1.000", PressureUnit.BAR),
            MatterState.GAS,
            StandardStateConvention.IDEAL_GAS_STANDARD_STATE);

    private final ReactionThermodynamicsService service = new ReactionThermodynamicsServiceImpl(
            new ReactionCatalogServiceImpl(new InMemoryReactionRepository()),
            new ThermodynamicReferenceServiceImpl(new InMemoryThermodynamicReferenceRepository()));

    @Test
    void calculatesMethaneCombustionFromCatalogueAndPreservesTermContributions() {
        var result = service.calculate("RXN-METHANE-COMBUSTION", CONDITIONS, Map.of());

        assertThat(result.status()).isEqualTo(ReactionThermodynamicStatus.CALCULABLE);
        assertThat(result.property(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY).value())
                .isEqualByComparingTo("-802.288");
        assertThat(result.property(ReactionThermodynamicProperty.STANDARD_REACTION_GIBBS_ENERGY).value())
                .isEqualByComparingTo("-800.703");
        assertThat(result.terms()).filteredOn(term -> term.compoundCode().equals("COMP-CH4"))
                .first().satisfies(term -> {
                    assertThat(term.signedCoefficient().toString()).isEqualTo("-1");
                    assertThat(term.contributions().get(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY).value())
                            .isEqualByComparingTo("74.873");
                });
        assertThat(result.terms()).filteredOn(term -> term.compoundCode().equals("COMP-CO2"))
                .first().satisfies(term -> assertThat(term.state()).isEqualTo(MatterState.GAS));
        assertThat(result.terms()).filteredOn(term -> term.compoundCode().equals("COMP-H2O"))
                .first().satisfies(term -> assertThat(term.state()).isEqualTo(MatterState.GAS));
        assertThat(result.explanation()).contains("per canonical stoichiometric reaction");
    }

    @Test
    void carbonMonoxideOxidationAggregatesExactCatalogueRecords() {
        var result = service.calculate("RXN-CO-OXIDATION", CONDITIONS, Map.of());

        assertThat(result.property(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY).value())
                .isEqualByComparingTo("-565.968");
        assertThat(result.property(ReactionThermodynamicProperty.STANDARD_REACTION_GIBBS_ENERGY).value())
                .isEqualByComparingTo("-514.382");
    }

    @Test
    void evaluatesAllCatalogueCoverageWithoutFabricatingRecords() {
        var coverage = service.evaluateCatalogueCoverage(CONDITIONS);

        assertThat(coverage).hasSize(26);
        assertThat(coverage).filteredOn(c -> c.status() == ReactionThermodynamicStatus.CALCULABLE).hasSize(5);
        assertThat(coverage).filteredOn(c -> c.reactionCode().equals("RXN-DIMETHYL-ETHER-COMBUSTION"))
                .singleElement()
                .satisfies(c -> assertThat(c.missingCompounds()).contains("COMP-DIMETHYL-ETHER"));
    }

    @Test
    void unknownReactionFailsStructurally() {
        assertThatThrownBy(() -> service.calculate("RXN-NOT-PRESENT", CONDITIONS, Map.of()))
                .isInstanceOf(ReactionException.class);
    }

    @Test
    void mismatchedTemperatureOrPressureLeavesIncompleteCoverage() {
        var highTemperature = new ThermodynamicReferenceConditions(
                Temperature.of("350.0", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                MatterState.GAS,
                StandardStateConvention.IDEAL_GAS_STANDARD_STATE);

        var result = service.calculate("RXN-METHANE-COMBUSTION", highTemperature, Map.of());

        assertThat(result.status()).isEqualTo(ReactionThermodynamicStatus.INCOMPLETE_COVERAGE);
        assertThat(result.coverage().missingPhaseSpecificRecords()).contains("COMP-CH4|GAS");
    }

    @Test
    void deterministicRepeatedExecutionReturnsSameValues() {
        var first = service.calculate("RXN-CO-OXIDATION", CONDITIONS, Map.of());
        var second = service.calculate("RXN-CO-OXIDATION", CONDITIONS, Map.of());

        assertThat(second.property(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY).value())
                .isEqualByComparingTo(first.property(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY).value());
        assertThat(second.explanation()).isEqualTo(first.explanation());
    }

    @Test
    void hessCycleMatchesDirectCalculationWithinDecimalTolerance() {
        var methane = service.calculate("RXN-METHANE-COMBUSTION", CONDITIONS, Map.of()).toHessReactionTerm(1, 1);
        var reverseHalfCo = service.calculate("RXN-CO-OXIDATION", CONDITIONS, Map.of()).toHessReactionTerm(-1, 2);
        var reverseWater = service.calculate("RXN-WATER-SYNTHESIS", CONDITIONS, Map.of()).toHessReactionTerm(-3, 2);
        var hess = service.calculateHessLaw(new HessLawRequest(
                List.of(methane, reverseHalfCo, reverseWater),
                ReactionThermodynamicVector.parse("COMP-CH4|GAS:-1;COMP-H2O|GAS:-1;COMP-CO|GAS:1;COMP-H2|GAS:3")));

        BigDecimal directEnthalpy = new BigDecimal("-110.525")
                .subtract(new BigDecimal("-74.873"))
                .subtract(new BigDecimal("-241.826"));

        assertThat(hess.properties().get(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY).value())
                .isCloseTo(directEnthalpy, org.assertj.core.data.Offset.offset(new BigDecimal("0.001")));
        assertThat(hess.intermediateCancellations()).contains("COMP-CO2|GAS", "COMP-O2|GAS");
    }

    @Test
    void noTemperatureCorrectionOrEquilibriumConstantApiExists() {
        assertThat(ReactionThermodynamicsService.class.getMethods())
                .extracting(java.lang.reflect.Method::getName)
                .doesNotContain("calculateEquilibriumConstant", "calculateTemperatureCorrected");
    }
}

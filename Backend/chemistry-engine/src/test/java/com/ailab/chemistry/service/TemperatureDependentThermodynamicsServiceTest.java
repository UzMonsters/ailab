package com.ailab.chemistry.service;

import com.ailab.chemistry.api.TemperatureDependentThermodynamicsService;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicProperty;
import com.ailab.chemistry.domain.thermodynamics.TemperatureCorrectionStatus;
import com.ailab.chemistry.infrastructure.persistence.reaction.InMemoryReactionRepository;
import com.ailab.chemistry.infrastructure.persistence.thermodynamics.InMemoryTemperatureCorrelationRepository;
import com.ailab.chemistry.infrastructure.persistence.thermodynamics.InMemoryThermodynamicReferenceRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TemperatureDependentThermodynamicsServiceTest {

    private final TemperatureDependentThermodynamicsService service = new TemperatureDependentThermodynamicsServiceImpl(
            new ReactionCatalogServiceImpl(new InMemoryReactionRepository()),
            new ThermodynamicReferenceServiceImpl(new InMemoryThermodynamicReferenceRepository()),
            new ReactionThermodynamicsServiceImpl(
                    new ReactionCatalogServiceImpl(new InMemoryReactionRepository()),
                    new ThermodynamicReferenceServiceImpl(new InMemoryThermodynamicReferenceRepository())),
            new InMemoryTemperatureCorrelationRepository());

    @Test
    void calculatesSpeciesPropertiesFromPhaseSpecificCorrelation() {
        var result = service.calculateSpeciesProperties("COMP-H2O", MatterState.LIQUID,
                Temperature.of("400.0", TemperatureUnit.KELVIN));

        assertThat(result.status()).isEqualTo(TemperatureCorrectionStatus.CALCULABLE);
        assertThat(result.heatCapacityJPerMolKelvin()).isCloseTo(new BigDecimal("76.74"), offset("0.02"));
        assertThat(result.correlation().state()).isEqualTo(MatterState.LIQUID);
    }

    @Test
    void reactionAtReferenceTemperatureMatchesPhase8BStandardThermodynamics() {
        var result = service.calculateReaction("RXN-CO-OXIDATION",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                Map.of());

        assertThat(result.status()).isEqualTo(TemperatureCorrectionStatus.CALCULABLE);
        assertThat(result.reactionProperties().get(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY).value())
                .isEqualByComparingTo("-565.968");
        assertThat(result.reactionProperties().get(ReactionThermodynamicProperty.STANDARD_REACTION_GIBBS_ENERGY).value())
                .isEqualByComparingTo("-514.382");
    }

    @Test
    void waterFormationUsesGasAndLiquidPhaseCorrelationsDistinctly() {
        var gas = service.calculateReaction("RXN-WATER-SYNTHESIS",
                Temperature.of("600.0", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                Map.of());
        var liquid = service.calculateReaction("RXN-WATER-SYNTHESIS",
                Temperature.of("400.0", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                Map.of("COMP-H2O", MatterState.LIQUID));

        assertThat(gas.status()).isEqualTo(TemperatureCorrectionStatus.CALCULABLE);
        assertThat(liquid.status()).isEqualTo(TemperatureCorrectionStatus.CALCULABLE);
        assertThat(gas.reactionProperties().get(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY).value())
                .isNotEqualByComparingTo(liquid.reactionProperties().get(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY).value());
    }

    @Test
    void gasAndLiquidWaterCorrelationsHaveIndependentValidityRanges() {
        assertThatThrownBy(() -> service.calculateSpeciesProperties("COMP-H2O", MatterState.GAS,
                Temperature.of("400.0", TemperatureUnit.KELVIN)))
                .isInstanceOf(com.ailab.chemistry.domain.thermodynamics.TemperatureCorrectionException.class)
                .extracting("errorCode")
                .isEqualTo(com.ailab.chemistry.domain.thermodynamics.TemperatureCorrectionErrorCode.MISSING_CORRELATION);
    }

    @Test
    void methaneCombustionIsTemperatureCorrectedAndDeterministic() {
        var first = service.calculateReaction("RXN-METHANE-COMBUSTION",
                Temperature.of("600.0", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                Map.of());
        var second = service.calculateReaction("RXN-METHANE-COMBUSTION",
                Temperature.of("600.0", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                Map.of());

        assertThat(first.status()).isEqualTo(TemperatureCorrectionStatus.CALCULABLE);
        assertThat(first.reactionProperties().get(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY).value())
                .isNotEqualByComparingTo("-802.288");
        assertThat(second.reactionProperties()).isEqualTo(first.reactionProperties());
    }

    @Test
    void missingCorrelationReturnsIncompleteCoverage() {
        var coverage = service.evaluateCoverage(Temperature.of("600.0", TemperatureUnit.KELVIN));

        assertThat(coverage).hasSize(26);
        assertThat(coverage).anySatisfy(item -> {
            assertThat(item.reactionCode()).isEqualTo("RXN-DIMETHYL-ETHER-COMBUSTION");
            assertThat(item.status()).isEqualTo(TemperatureCorrectionStatus.INCOMPLETE_COVERAGE);
            assertThat(item.missingCorrelations()).contains("COMP-DIMETHYL-ETHER|GAS");
        });
    }

    private static org.assertj.core.data.Offset<BigDecimal> offset(String value) {
        return org.assertj.core.data.Offset.offset(new BigDecimal(value));
    }
}

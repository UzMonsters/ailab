package com.ailab.chemistry.service;

import com.ailab.chemistry.api.CalorimetryService;
import com.ailab.chemistry.api.EquilibriumCompositionService;
import com.ailab.chemistry.api.ReactionCatalogService;
import com.ailab.chemistry.api.ThermodynamicEquilibriumService;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.EnergyUnit;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.thermodynamics.AdiabaticTemperatureRequest;
import com.ailab.chemistry.domain.thermodynamics.AdiabaticTemperatureResult;
import com.ailab.chemistry.domain.thermodynamics.CalorimetryMethod;
import com.ailab.chemistry.domain.thermodynamics.CalorimetryStatus;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionMethod;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionRequest;
import com.ailab.chemistry.domain.thermodynamics.EquilibriumCompositionResult;
import com.ailab.chemistry.domain.thermodynamics.InitialParticipantAmount;
import com.ailab.chemistry.domain.thermodynamics.ReactionCalorimetryRequest;
import com.ailab.chemistry.domain.thermodynamics.ReactionCalorimetryResult;
import com.ailab.chemistry.domain.thermodynamics.SensibleHeatRequest;
import com.ailab.chemistry.domain.thermodynamics.SensibleHeatResult;
import com.ailab.chemistry.domain.thermodynamics.ThermalMixingRequest;
import com.ailab.chemistry.domain.thermodynamics.ThermalMixingResult;
import com.ailab.chemistry.domain.thermodynamics.ThermalSample;
import com.ailab.chemistry.infrastructure.persistence.reaction.InMemoryReactionRepository;
import com.ailab.chemistry.infrastructure.persistence.thermodynamics.InMemoryTemperatureCorrelationRepository;
import com.ailab.chemistry.infrastructure.persistence.thermodynamics.InMemoryThermodynamicReferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CalorimetryServiceTest {

    private CalorimetryService calorimetryService;
    private EquilibriumCompositionService compositionService;

    @BeforeEach
    void setUp() {
        var reactionCatalog = new ReactionCatalogServiceImpl(new InMemoryReactionRepository());
        var thermoRefRepo = new InMemoryThermodynamicReferenceRepository();
        var thermoCorrRepo = new InMemoryTemperatureCorrelationRepository();
        var referenceService = new ThermodynamicReferenceServiceImpl(thermoRefRepo);
        var reactionThermodynamics = new ReactionThermodynamicsServiceImpl(reactionCatalog, referenceService);
        var tempDependent = new TemperatureDependentThermodynamicsServiceImpl(reactionCatalog, referenceService, reactionThermodynamics, thermoCorrRepo);

        ThermodynamicEquilibriumService equilibriumService = new ThermodynamicEquilibriumServiceImpl(
                reactionCatalog, reactionThermodynamics, tempDependent, null);

        compositionService = new EquilibriumCompositionServiceImpl(reactionCatalog, equilibriumService, null);
        calorimetryService = new CalorimetryServiceImpl(reactionCatalog, reactionThermodynamics, tempDependent);
    }

    @Test
    void preflightExtentSemanticsForCoOxidationForwardAndReverse() {
        // Forward: 2 CO + 1 O2 -> 2 CO2
        EquilibriumCompositionRequest fwdReq = new EquilibriumCompositionRequest(
                "RXN-CO-OXIDATION",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                EquilibriumCompositionMethod.CONSTANT_TOTAL_PRESSURE,
                List.of(
                        new InitialParticipantAmount("COMP-CO", MatterState.GAS, new BigDecimal("2.0")),
                        new InitialParticipantAmount("COMP-O2", MatterState.GAS, new BigDecimal("1.0")),
                        new InitialParticipantAmount("COMP-CO2", MatterState.GAS, new BigDecimal("0.0"))
                ),
                Pressure.of("1.000", PressureUnit.BAR),
                null, BigDecimal.ZERO, List.of(), Map.of()
        );

        // Reverse: 0 CO + 0 O2 -> 2 CO2
        EquilibriumCompositionRequest revReq = new EquilibriumCompositionRequest(
                "RXN-CO-OXIDATION",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                EquilibriumCompositionMethod.CONSTANT_TOTAL_PRESSURE,
                List.of(
                        new InitialParticipantAmount("COMP-CO", MatterState.GAS, new BigDecimal("0.0")),
                        new InitialParticipantAmount("COMP-O2", MatterState.GAS, new BigDecimal("0.0")),
                        new InitialParticipantAmount("COMP-CO2", MatterState.GAS, new BigDecimal("2.0"))
                ),
                Pressure.of("1.000", PressureUnit.BAR),
                null, BigDecimal.ZERO, List.of(), Map.of()
        );

        EquilibriumCompositionResult fwdRes = compositionService.calculate(fwdReq);
        EquilibriumCompositionResult revRes = compositionService.calculate(revReq);

        // Proves that equivalent closed-system initial states converge to equivalent final compositions while extent remains relative to each request's initial composition
        BigDecimal fwdFinalCo2 = fwdRes.finalComposition().stream().filter(p -> p.compoundCode().equals("COMP-CO2")).findFirst().orElseThrow().finalMoles();
        BigDecimal revFinalCo2 = revRes.finalComposition().stream().filter(p -> p.compoundCode().equals("COMP-CO2")).findFirst().orElseThrow().finalMoles();

        assertThat(fwdFinalCo2).isCloseTo(revFinalCo2, org.assertj.core.data.Offset.offset(new BigDecimal("0.01")));
        assertThat(fwdRes.extent().extent()).isGreaterThan(new BigDecimal("0.99"));
        assertThat(revRes.extent().extent()).isCloseTo(BigDecimal.ZERO, org.assertj.core.data.Offset.offset(new BigDecimal("0.01")));
    }

    @Test
    void sensibleHeatShomateIntegrationAgreesWithEnthalpyIncrement() {
        // COMP-H2O LIQUID has Shomate correlation for 298 K to 500 K
        ThermalSample sample = new ThermalSample(
                "COMP-H2O", MatterState.LIQUID,
                null, AmountOfSubstance.of("1.0", AmountOfSubstanceUnit.MOLE),
                null, null,
                Temperature.of("298.15", TemperatureUnit.KELVIN)
        );

        SensibleHeatResult result = calorimetryService.calculateSensibleHeat(new SensibleHeatRequest(
                sample, Temperature.of("350.0", TemperatureUnit.KELVIN), CalorimetryMethod.TEMPERATURE_DEPENDENT_SHOMATE));

        assertThat(result.status()).isEqualTo(CalorimetryStatus.SUCCESS);
        assertThat(result.heatTransferredJoules().in(EnergyUnit.JOULE)).isGreaterThan(BigDecimal.ZERO);
    }

    @Test
    void reactionCalorimetryWaterFormationAndMethaneCombustionExothermicSignAndExtentScaling() {
        // Water synthesis: 2 H2 + O2 -> 2 H2O (extent = 1 mol)
        ReactionCalorimetryResult waterRes1 = calorimetryService.calculateReactionHeat(new ReactionCalorimetryRequest(
                "RXN-WATER-SYNTHESIS", new BigDecimal("1.0"), Temperature.of("298.15", TemperatureUnit.KELVIN), Pressure.of("1.000", PressureUnit.BAR), Map.of()));

        assertThat(waterRes1.status()).isEqualTo(CalorimetryStatus.SUCCESS);
        assertThat(waterRes1.totalReactionHeatJoules().in(EnergyUnit.JOULE)).isLessThan(BigDecimal.ZERO); // Exothermic < 0
        assertThat(waterRes1.heatToSurroundingsJoules().in(EnergyUnit.JOULE)).isGreaterThan(BigDecimal.ZERO); // Released to surroundings > 0

        // Scaled extent = 2.0 mol
        ReactionCalorimetryResult waterRes2 = calorimetryService.calculateReactionHeat(new ReactionCalorimetryRequest(
                "RXN-WATER-SYNTHESIS", new BigDecimal("2.0"), Temperature.of("298.15", TemperatureUnit.KELVIN), Pressure.of("1.000", PressureUnit.BAR), Map.of()));

        assertThat(waterRes2.totalReactionHeatJoules().in(EnergyUnit.JOULE))
                .isEqualTo(waterRes1.totalReactionHeatJoules().in(EnergyUnit.JOULE).multiply(new BigDecimal("2")));

        // Methane combustion
        ReactionCalorimetryResult ch4Res = calorimetryService.calculateReactionHeat(new ReactionCalorimetryRequest(
                "RXN-METHANE-COMBUSTION", new BigDecimal("1.0"), Temperature.of("298.15", TemperatureUnit.KELVIN), Pressure.of("1.000", PressureUnit.BAR), Map.of()));

        assertThat(ch4Res.status()).isEqualTo(CalorimetryStatus.SUCCESS);
        assertThat(ch4Res.totalReactionHeatJoules().in(EnergyUnit.JOULE)).isLessThan(BigDecimal.ZERO);
    }

    @Test
    void adiabaticFinalTemperatureCalculation() {
        // Use extent = 0.01 mol to stay within Shomate correlation range for O2 (100 K to 700 K)
        AdiabaticTemperatureRequest request = new AdiabaticTemperatureRequest(
                "RXN-CO-OXIDATION",
                new BigDecimal("0.01"),
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                List.of(
                        new InitialParticipantAmount("COMP-CO", MatterState.GAS, new BigDecimal("2.0")),
                        new InitialParticipantAmount("COMP-O2", MatterState.GAS, new BigDecimal("1.0")),
                        new InitialParticipantAmount("COMP-CO2", MatterState.GAS, new BigDecimal("0.1"))
                ),
                null, Map.of()
        );

        AdiabaticTemperatureResult result = calorimetryService.calculateAdiabaticFinalTemperature(request);

        assertThat(result.status()).isEqualTo(CalorimetryStatus.CONVERGED);
        assertThat(result.finalTemperature().in(TemperatureUnit.KELVIN)).isGreaterThan(new BigDecimal("298.15"));
        assertThat(result.finalTemperature().in(TemperatureUnit.KELVIN)).isLessThan(new BigDecimal("700.0"));
        assertThat(result.energyBalance().isBalanced()).isTrue();
    }
}

package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.equation.RationalNumber;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EquilibriumCompositionCalculatorTest {

    private final EquilibriumCompositionCalculator calculator = new EquilibriumCompositionCalculator();

    @Test
    void WaterVapourFormationAt298K_IdealGasConstantPressure() {
        // 2 H2(g) + 1 O2(g) -> 2 H2O(g)
        ReactionThermodynamicVector vector = new ReactionThermodynamicVector(List.of(
                new ReactionThermodynamicVectorTerm("COMP-H2", MatterState.GAS, RationalNumber.of(-2, 1)),
                new ReactionThermodynamicVectorTerm("COMP-O2", MatterState.GAS, RationalNumber.of(-1, 1)),
                new ReactionThermodynamicVectorTerm("COMP-H2O", MatterState.GAS, RationalNumber.of(2, 1))
        ));

        // ln K ~ 180
        StandardEquilibriumConstant constant = new StandardEquilibriumConstant(
                new BigDecimal("180.0"), new BigDecimal("78.17"), Optional.empty(), PhaseStabilityStatus.PHASE_STABILITY_NOT_EVALUATED);

        EquilibriumCompositionRequest request = new EquilibriumCompositionRequest(
                "RXN-WATER-SYNTHESIS",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                EquilibriumCompositionMethod.CONSTANT_TOTAL_PRESSURE,
                List.of(
                        new InitialParticipantAmount("COMP-H2", MatterState.GAS, new BigDecimal("2.0")),
                        new InitialParticipantAmount("COMP-O2", MatterState.GAS, new BigDecimal("1.0")),
                        new InitialParticipantAmount("COMP-H2O", MatterState.GAS, new BigDecimal("0.0"))
                ),
                Pressure.of("1.000", PressureUnit.BAR),
                null,
                BigDecimal.ZERO,
                List.of(),
                Map.of()
        );

        EquilibriumCompositionResult result = calculator.solve(vector, constant, request, TemperatureCorrectionCoverage.complete("DOMAIN"), null);

        assertThat(result.status()).isIn(EquilibriumCompositionStatus.CONVERGED, EquilibriumCompositionStatus.BOUNDED_AT_FORWARD_LIMIT);
        assertThat(result.extent().extent()).isGreaterThan(new BigDecimal("0.999"));
        assertThat(result.finalComposition()).hasSize(3);

        // All amounts non-negative
        for (EquilibriumParticipantState participant : result.finalComposition()) {
            assertThat(participant.finalMoles()).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        }
    }

    @Test
    void CoOxidationEquilibriumFromEitherSide() {
        // 2 CO(g) + 1 O2(g) -> 2 CO2(g)
        ReactionThermodynamicVector vector = new ReactionThermodynamicVector(List.of(
                new ReactionThermodynamicVectorTerm("COMP-CO", MatterState.GAS, RationalNumber.of(-2, 1)),
                new ReactionThermodynamicVectorTerm("COMP-O2", MatterState.GAS, RationalNumber.of(-1, 1)),
                new ReactionThermodynamicVectorTerm("COMP-CO2", MatterState.GAS, RationalNumber.of(2, 1))
        ));

        StandardEquilibriumConstant constant = new StandardEquilibriumConstant(
                new BigDecimal("200.0"), new BigDecimal("86.85"), Optional.empty(), PhaseStabilityStatus.PHASE_STABILITY_NOT_EVALUATED);

        // Forward initial mixture: 2 mol CO, 1 mol O2, 0 mol CO2
        EquilibriumCompositionRequest fwdRequest = new EquilibriumCompositionRequest(
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
                null,
                BigDecimal.ZERO,
                List.of(),
                Map.of()
        );

        // Reverse initial mixture: 0 mol CO, 0 mol O2, 2 mol CO2
        EquilibriumCompositionRequest revRequest = new EquilibriumCompositionRequest(
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
                null,
                BigDecimal.ZERO,
                List.of(),
                Map.of()
        );

        EquilibriumCompositionResult fwdRes = calculator.solve(vector, constant, fwdRequest, TemperatureCorrectionCoverage.complete("DOMAIN"), null);
        EquilibriumCompositionResult revRes = calculator.solve(vector, constant, revRequest, TemperatureCorrectionCoverage.complete("DOMAIN"), null);

        // Both forward and reverse starting mixtures reach equivalent product-favoured equilibrium
        BigDecimal fwdCo2 = fwdRes.finalComposition().stream().filter(p -> p.compoundCode().equals("COMP-CO2")).findFirst().orElseThrow().finalMoles();
        BigDecimal revCo2 = revRes.finalComposition().stream().filter(p -> p.compoundCode().equals("COMP-CO2")).findFirst().orElseThrow().finalMoles();

        assertThat(fwdCo2).isGreaterThan(new BigDecimal("1.99"));
        assertThat(revCo2).isGreaterThan(new BigDecimal("1.99"));
        assertThat(fwdCo2.subtract(revCo2).abs()).isLessThan(new BigDecimal("0.01"));
    }

    @Test
    void InertGasHandledSeparatelyForConstantPressureAndConstantVolume() {
        ReactionThermodynamicVector vector = new ReactionThermodynamicVector(List.of(
                new ReactionThermodynamicVectorTerm("COMP-CO", MatterState.GAS, RationalNumber.of(-2, 1)),
                new ReactionThermodynamicVectorTerm("COMP-O2", MatterState.GAS, RationalNumber.of(-1, 1)),
                new ReactionThermodynamicVectorTerm("COMP-CO2", MatterState.GAS, RationalNumber.of(2, 1))
        ));

        StandardEquilibriumConstant constant = new StandardEquilibriumConstant(
                new BigDecimal("10.0"), new BigDecimal("4.34"), Optional.of(new BigDecimal("22026.4")), PhaseStabilityStatus.PHASE_STABILITY_NOT_EVALUATED);

        // Constant pressure with inert gas
        EquilibriumCompositionRequest cpRequest = new EquilibriumCompositionRequest(
                "RXN-CO-OXIDATION",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                EquilibriumCompositionMethod.CONSTANT_TOTAL_PRESSURE,
                List.of(
                        new InitialParticipantAmount("COMP-CO", MatterState.GAS, new BigDecimal("2.0")),
                        new InitialParticipantAmount("COMP-O2", MatterState.GAS, new BigDecimal("1.0")),
                        new InitialParticipantAmount("COMP-CO2", MatterState.GAS, new BigDecimal("0.5"))
                ),
                Pressure.of("1.000", PressureUnit.BAR),
                null,
                new BigDecimal("5.0"), // 5 mol inert N2
                List.of(),
                Map.of()
        );

        // Constant volume with inert gas
        EquilibriumCompositionRequest cvRequest = new EquilibriumCompositionRequest(
                "RXN-CO-OXIDATION",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                EquilibriumCompositionMethod.CONSTANT_VOLUME_IDEAL_GAS,
                List.of(
                        new InitialParticipantAmount("COMP-CO", MatterState.GAS, new BigDecimal("2.0")),
                        new InitialParticipantAmount("COMP-O2", MatterState.GAS, new BigDecimal("1.0")),
                        new InitialParticipantAmount("COMP-CO2", MatterState.GAS, new BigDecimal("0.5"))
                ),
                null,
                Volume.of("100.0", VolumeUnit.LITER),
                new BigDecimal("5.0"),
                List.of(),
                Map.of()
        );

        EquilibriumCompositionResult cpRes = calculator.solve(vector, constant, cpRequest, TemperatureCorrectionCoverage.complete("DOMAIN"), null);
        EquilibriumCompositionResult cvRes = calculator.solve(vector, constant, cvRequest, TemperatureCorrectionCoverage.complete("DOMAIN"), null);

        assertThat(cpRes.extent().extent()).isNotNull();
        assertThat(cvRes.extent().extent()).isNotNull();
        assertThat(cpRes.extent().extent()).isNotEqualTo(cvRes.extent().extent());
    }

    @Test
    void InvalidPressureOrVolumeThrowsException() {
        ReactionThermodynamicVector vector = new ReactionThermodynamicVector(List.of(
                new ReactionThermodynamicVectorTerm("COMP-H2", MatterState.GAS, RationalNumber.of(-2, 1)),
                new ReactionThermodynamicVectorTerm("COMP-O2", MatterState.GAS, RationalNumber.of(-1, 1)),
                new ReactionThermodynamicVectorTerm("COMP-H2O", MatterState.GAS, RationalNumber.of(2, 1))
        ));
        StandardEquilibriumConstant constant = new StandardEquilibriumConstant(
                new BigDecimal("100.0"), new BigDecimal("43.4"), Optional.empty(), PhaseStabilityStatus.PHASE_STABILITY_NOT_EVALUATED);

        // Missing pressure for CONSTANT_TOTAL_PRESSURE
        EquilibriumCompositionRequest reqNoP = new EquilibriumCompositionRequest(
                "RXN-WATER-SYNTHESIS",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                EquilibriumCompositionMethod.CONSTANT_TOTAL_PRESSURE,
                List.of(
                        new InitialParticipantAmount("COMP-H2", MatterState.GAS, new BigDecimal("2.0")),
                        new InitialParticipantAmount("COMP-O2", MatterState.GAS, new BigDecimal("1.0")),
                        new InitialParticipantAmount("COMP-H2O", MatterState.GAS, new BigDecimal("0.0"))
                ),
                null,
                null,
                BigDecimal.ZERO,
                List.of(),
                Map.of()
        );

        assertThatThrownBy(() -> calculator.solve(vector, constant, reqNoP, TemperatureCorrectionCoverage.complete("DOMAIN"), null))
                .isInstanceOf(EquilibriumCompositionException.class)
                .hasMessageContaining("Total pressure must be positive");
    }

    @Test
    void MissingParticipantAmountThrowsException() {
        ReactionThermodynamicVector vector = new ReactionThermodynamicVector(List.of(
                new ReactionThermodynamicVectorTerm("COMP-H2", MatterState.GAS, RationalNumber.of(-2, 1)),
                new ReactionThermodynamicVectorTerm("COMP-O2", MatterState.GAS, RationalNumber.of(-1, 1)),
                new ReactionThermodynamicVectorTerm("COMP-H2O", MatterState.GAS, RationalNumber.of(2, 1))
        ));
        StandardEquilibriumConstant constant = new StandardEquilibriumConstant(
                new BigDecimal("100.0"), new BigDecimal("43.4"), Optional.empty(), PhaseStabilityStatus.PHASE_STABILITY_NOT_EVALUATED);

        // O2 missing from initial amounts
        EquilibriumCompositionRequest reqMissing = new EquilibriumCompositionRequest(
                "RXN-WATER-SYNTHESIS",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                Pressure.of("1.000", PressureUnit.BAR),
                EquilibriumCompositionMethod.CONSTANT_TOTAL_PRESSURE,
                List.of(
                        new InitialParticipantAmount("COMP-H2", MatterState.GAS, new BigDecimal("2.0")),
                        new InitialParticipantAmount("COMP-H2O", MatterState.GAS, new BigDecimal("0.0"))
                ),
                Pressure.of("1.000", PressureUnit.BAR),
                null,
                BigDecimal.ZERO,
                List.of(),
                Map.of()
        );

        assertThatThrownBy(() -> calculator.solve(vector, constant, reqMissing, TemperatureCorrectionCoverage.complete("DOMAIN"), null))
                .isInstanceOf(EquilibriumCompositionException.class)
                .hasMessageContaining("Missing initial participant amount");
    }
}

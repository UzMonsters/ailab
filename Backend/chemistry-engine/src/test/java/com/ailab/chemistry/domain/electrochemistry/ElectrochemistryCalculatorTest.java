package com.ailab.chemistry.domain.electrochemistry;

import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.Duration;
import com.ailab.chemistry.domain.measurement.DurationUnit;
import com.ailab.chemistry.domain.measurement.MassUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.infrastructure.persistence.electrochemistry.InMemoryElectrochemicalReferenceRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;

class ElectrochemistryCalculatorTest {
    private final ElectrochemistryCalculator calculator = new ElectrochemistryCalculator();
    private final ElectrochemicalReferenceRepository repository = InMemoryElectrochemicalReferenceRepository.reference();

    @Test
    void faradayConstantDeclaresValueUnitSourceAndVersion() {
        FaradayConstant constant = FaradayConstant.CODATA_2018_EXACT;

        assertThat(constant.coulombsPerMole()).isEqualByComparingTo("96485.3321233100184");
        assertThat(constant.unit()).isEqualTo("C mol^-1");
        assertThat(constant.source()).contains("2019 SI");
        assertThat(constant.version()).isEqualTo("CODATA-2018-EXACT");
    }

    @Test
    void referenceHalfReactionsAreAtomAndChargeBalancedWithExplicitElectrons() {
        List<String> expected = List.of("SRP-H2-REFERENCE", "SRP-CU2-CU", "SRP-ZN2-ZN", "SRP-AG-PLUS-AG", "SRP-FE3-FE2", "SRP-CL2-CL");

        for (String recordId : expected) {
            StandardReductionPotential record = repository.findByRecordId(recordId).orElseThrow();

            assertThat(record.electronCount().value()).isPositive();
            assertThat(record.validate().atomResidual()).isEqualTo(BigDecimal.ZERO);
            assertThat(record.validate().chargeResidual()).isEqualTo(BigDecimal.ZERO);
            assertThat(record.participants()).allSatisfy(participant -> {
                assertThat(participant.speciesCode()).startsWith("COMP-");
                assertThat(participant.formula()).isNotEmpty();
                assertThat(participant.phase()).isNotBlank();
            });
            assertThat(record.provenance().sourceCode()).isNotBlank();
            assertThat(record.provenance().citation()).isNotBlank();
            assertThat(record.conditions().temperature().in(TemperatureUnit.KELVIN)).isEqualByComparingTo("298.15");
        }
    }

    @Test
    void daniellCellBalancesElectronsAndDoesNotScaleElectrodePotentials() {
        ElectrochemicalCellResult result = calculator.calculateStandardCell(new ElectrochemicalCellRequest(
                "SRP-CU2-CU",
                "SRP-ZN2-ZN",
                ElectrochemicalCellType.GALVANIC,
                BigDecimal.ONE
        ), repository);

        assertThat(result.status()).isEqualTo(ElectrochemicalStatus.GALVANIC_AS_WRITTEN);
        assertThat(result.cathode().recordId()).isEqualTo("SRP-CU2-CU");
        assertThat(result.anode().recordId()).isEqualTo("SRP-ZN2-ZN");
        assertThat(result.electronCount().value()).isEqualByComparingTo("2");
        assertThat(result.standardCellPotential().inVolts()).isCloseTo(new BigDecimal("1.103"), offset(new BigDecimal("0.001")));
        assertThat(result.cellReaction().atomResidual()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.cellReaction().chargeResidual()).isEqualTo(BigDecimal.ZERO);
        assertThat(result.standardGibbsEnergy().value().inJoulesPerMole()).isCloseTo(new BigDecimal("-212861"), offset(new BigDecimal("20")));
        assertThat(result.cellNotation().value()).isEqualTo("Zn(s) | Zn2+(aq) || Cu2+(aq) | Cu(s)");

        ElectrochemicalCellResult scaled = calculator.calculateStandardCell(new ElectrochemicalCellRequest(
                "SRP-CU2-CU",
                "SRP-ZN2-ZN",
                ElectrochemicalCellType.GALVANIC,
                new BigDecimal("2")
        ), repository);
        assertThat(scaled.standardCellPotential().inVolts()).isEqualByComparingTo(result.standardCellPotential().inVolts());
        assertThat(scaled.electronCount().value()).isEqualByComparingTo("4");
        assertThat(scaled.standardGibbsEnergy().value().inJoulesPerMole()).isCloseTo(new BigDecimal("-425722"), offset(new BigDecimal("40")));
    }

    @Test
    void cellReversalNegatesPotentialAndGibbsEnergyAndInvertsEquilibriumConstant() {
        ElectrochemicalCellResult forward = calculator.calculateStandardCell(new ElectrochemicalCellRequest(
                "SRP-CU2-CU", "SRP-ZN2-ZN", ElectrochemicalCellType.GALVANIC, BigDecimal.ONE), repository);
        ElectrochemicalCellResult reverse = calculator.calculateStandardCell(new ElectrochemicalCellRequest(
                "SRP-ZN2-ZN", "SRP-CU2-CU", ElectrochemicalCellType.ELECTROLYTIC, BigDecimal.ONE), repository);

        assertThat(reverse.status()).isEqualTo(ElectrochemicalStatus.NONSPONTANEOUS_AS_WRITTEN);
        assertThat(reverse.standardCellPotential().inVolts()).isEqualByComparingTo(forward.standardCellPotential().inVolts().negate());
        assertThat(reverse.standardGibbsEnergy().value().inJoulesPerMole()).isEqualByComparingTo(forward.standardGibbsEnergy().value().inJoulesPerMole().negate());
        assertThat(reverse.log10EquilibriumConstant()).isEqualByComparingTo(forward.log10EquilibriumConstant().negate());
        assertThat(reverse.cellReaction().terms()).containsEntry("COMP-CU|SOLID", new BigDecimal("-1"));
    }

    @Test
    void nernstUsesExplicitActivitiesAndExcludesPureSolids() {
        NernstResult standard = calculator.calculateNonstandardCell(new NernstRequest(
                "SRP-CU2-CU",
                "SRP-ZN2-ZN",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                List.of(
                        ElectrochemicalActivity.explicit("COMP-CU2-PLUS", "AQUEOUS", BigDecimal.ONE),
                        ElectrochemicalActivity.explicit("COMP-ZN2-PLUS", "AQUEOUS", BigDecimal.ONE),
                        ElectrochemicalActivity.pureSolid("COMP-CU", "SOLID"),
                        ElectrochemicalActivity.pureSolid("COMP-ZN", "SOLID")
                )
        ), repository);

        assertThat(standard.reactionQuotient()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(standard.lnReactionQuotient()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(standard.cellPotential().inVolts()).isCloseTo(new BigDecimal("1.103"), offset(new BigDecimal("0.001")));
        assertThat(standard.activitySources()).contains("PURE_SOLID excluded from Q by explicit activity 1");

        NernstResult shifted = calculator.calculateNonstandardCell(new NernstRequest(
                "SRP-CU2-CU",
                "SRP-ZN2-ZN",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                List.of(
                        ElectrochemicalActivity.aqueousIdeal("COMP-CU2-PLUS", new BigDecimal("0.010")),
                        ElectrochemicalActivity.aqueousIdeal("COMP-ZN2-PLUS", new BigDecimal("1.0")),
                        ElectrochemicalActivity.pureSolid("COMP-CU", "SOLID"),
                        ElectrochemicalActivity.pureSolid("COMP-ZN", "SOLID")
                )
        ), repository);
        assertThat(shifted.reactionQuotient()).isEqualByComparingTo("100");
        assertThat(shifted.cellPotential().inVolts()).isLessThan(standard.cellPotential().inVolts());
        assertThat(shifted.cellPotential().inVolts()).isCloseTo(new BigDecimal("1.0438"), offset(new BigDecimal("0.001")));
    }

    @Test
    void nernstRejectsMissingInvalidAndOutOfRangeDaviesActivities() {
        assertThatThrownBy(() -> calculator.calculateNonstandardCell(new NernstRequest(
                "SRP-CU2-CU",
                "SRP-ZN2-ZN",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                List.of(ElectrochemicalActivity.explicit("COMP-CU2-PLUS", "AQUEOUS", BigDecimal.ONE))
        ), repository))
                .isInstanceOf(ElectrochemicalException.class)
                .extracting("errorCode")
                .isEqualTo(ElectrochemicalErrorCode.MISSING_ACTIVITY);

        assertThatThrownBy(() -> calculator.calculateNonstandardCell(new NernstRequest(
                "SRP-H2-REFERENCE",
                "SRP-ZN2-ZN",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                List.of(
                        ElectrochemicalActivity.idealGas("COMP-H2", BigDecimal.ZERO),
                        ElectrochemicalActivity.aqueousIdeal("COMP-H-PLUS", BigDecimal.ONE),
                        ElectrochemicalActivity.aqueousIdeal("COMP-ZN2-PLUS", BigDecimal.ONE),
                        ElectrochemicalActivity.pureSolid("COMP-ZN", "SOLID")
                )
        ), repository))
                .isInstanceOf(ElectrochemicalException.class)
                .extracting("errorCode")
                .isEqualTo(ElectrochemicalErrorCode.INVALID_ACTIVITY);

        assertThatThrownBy(() -> calculator.calculateNonstandardCell(new NernstRequest(
                "SRP-CU2-CU",
                "SRP-ZN2-ZN",
                Temperature.of("298.15", TemperatureUnit.KELVIN),
                List.of(
                        ElectrochemicalActivity.aqueousDavies("COMP-CU2-PLUS", new BigDecimal("0.10"), 2, new BigDecimal("0.8")),
                        ElectrochemicalActivity.aqueousDavies("COMP-ZN2-PLUS", new BigDecimal("0.10"), 2, new BigDecimal("0.8")),
                        ElectrochemicalActivity.pureSolid("COMP-CU", "SOLID"),
                        ElectrochemicalActivity.pureSolid("COMP-ZN", "SOLID")
                )
        ), repository))
                .isInstanceOf(ElectrochemicalException.class)
                .extracting("errorCode")
                .isEqualTo(ElectrochemicalErrorCode.ACTIVITY_MODEL_OUT_OF_RANGE);
    }

    @Test
    void electrolysisSolvesChargeCurrentDurationAmountAndMass() {
        ElectrolysisResult copper = calculator.calculateElectrolysis(ElectrolysisRequest.forCurrentAndDuration(
                "SRP-CU2-CU",
                "COMP-CU",
                "SOLID",
                ElectricCurrent.of("2", ElectricCurrentUnit.AMPERE),
                Duration.of("1800", DurationUnit.SECOND),
                CurrentEfficiency.of("1"),
                new BigDecimal("63.546")
        ), repository);

        assertThat(copper.charge().inCoulombs()).isEqualByComparingTo("3600");
        assertThat(copper.electronAmount().in(AmountOfSubstanceUnit.MOLE)).isCloseTo(new BigDecimal("0.03731"), offset(new BigDecimal("0.00002")));
        assertThat(copper.substanceAmount().in(AmountOfSubstanceUnit.MOLE)).isCloseTo(new BigDecimal("0.01865"), offset(new BigDecimal("0.00002")));
        assertThat(copper.mass().in(MassUnit.GRAM)).isCloseTo(new BigDecimal("1.185"), offset(new BigDecimal("0.002")));
        assertThat(copper.signConvention()).contains("reduction deposition");

        ElectrolysisResult efficient = calculator.calculateElectrolysis(ElectrolysisRequest.forCurrentAndDuration(
                "SRP-AG-PLUS-AG",
                "COMP-AG",
                "SOLID",
                ElectricCurrent.of("1", ElectricCurrentUnit.AMPERE),
                Duration.of("60", DurationUnit.SECOND),
                CurrentEfficiency.of("0.50"),
                new BigDecimal("107.8682")
        ), repository);
        assertThat(efficient.effectiveCharge().inCoulombs()).isEqualByComparingTo("30.00");

        ElectrolysisResult duration = calculator.calculateElectrolysis(ElectrolysisRequest.forCharge(
                "SRP-H2-REFERENCE",
                "COMP-H2",
                "GAS",
                ElectricCharge.of("193", ElectricChargeUnit.COULOMB),
                CurrentEfficiency.of("1"),
                new BigDecimal("2.01588")
        ), repository);
        assertThat(duration.substanceAmount().in(AmountOfSubstanceUnit.MOLE)).isCloseTo(new BigDecimal("0.00100"), offset(new BigDecimal("0.00001")));

        assertThatThrownBy(() -> CurrentEfficiency.of("0"))
                .isInstanceOf(ElectrochemicalException.class)
                .extracting("errorCode")
                .isEqualTo(ElectrochemicalErrorCode.INVALID_CURRENT_EFFICIENCY);
    }
}

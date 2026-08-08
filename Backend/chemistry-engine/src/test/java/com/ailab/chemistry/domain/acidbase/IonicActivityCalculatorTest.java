package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;

class IonicActivityCalculatorTest {

    private static final Temperature T25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);
    private static final ActivityParameterSet DAVIES_25C = new ActivityParameterSet(
            ActivityModel.DAVIES,
            "COMP-H2O",
            T25,
            new BigDecimal("0.509"),
            BigDecimal.ZERO,
            new BigDecimal("0.5"),
            "CRC Handbook of Chemistry and Physics, 104th Ed. (2023-2024)",
            "Davies limiting-law A parameter for water at 298.15 K",
            "CRC tabular data are copyrighted; reuse is limited to a minimal cited educational subset in this project"
    );
    private final IonicActivityCalculator calculator = new IonicActivityCalculator();

    @Test
    void calculatesIonicStrengthFromConcentrationsAndCharges() {
        IonicStrength nacl = calculator.calculateIonicStrength(List.of(
                ion("SPEC-NA-PLUS", "0.100", 1),
                ion("SPEC-CL-MINUS", "0.100", -1)
        ));

        IonicStrength sodiumCarbonateBeforeHydrolysis = calculator.calculateIonicStrength(List.of(
                ion("SPEC-NA-PLUS", "0.200", 1),
                ion("SPEC-CO3-2MINUS", "0.100", -2)
        ));

        assertThat(nacl.value()).isEqualByComparingTo("0.100");
        assertThat(sodiumCarbonateBeforeHydrolysis.value()).isCloseTo(new BigDecimal("0.300"), offset(new BigDecimal("1e-15")));
    }

    @Test
    void daviesCoefficientsRespectNeutralZeroStrengthAndChargeMagnitude() {
        ActivityCorrectionResult zero = calculator.calculateActivities(List.of(), DAVIES_25C);
        ActivityCorrectionResult mixed = calculator.calculateActivities(List.of(
                ion("SPEC-H2CO3", "0.100", 0),
                ion("SPEC-NA-PLUS", "0.100", 1),
                ion("SPEC-SO4-2MINUS", "0.100", -2)
        ), DAVIES_25C);

        assertThat(zero.ionicStrength().value()).isEqualByComparingTo("0");
        assertThat(zero.coefficientForCharge(1).value()).isEqualByComparingTo("1");
        assertThat(mixed.coefficientFor("SPEC-H2CO3").value()).isEqualByComparingTo("1");
        assertThat(mixed.coefficientFor("SPEC-SO4-2MINUS").value()).isLessThan(mixed.coefficientFor("SPEC-NA-PLUS").value());
        assertThat(mixed.activities()).allSatisfy(activity -> {
            assertThat(activity.activity()).isNotNegative();
            assertThat(activity.activity().doubleValue()).isFinite();
        });
    }

    @Test
    void rejectsNegativeConcentrationsAndDaviesRequestsOutsideValidityRange() {
        assertThatThrownBy(() -> ion("SPEC-NA-PLUS", "-0.1", 1))
                .isInstanceOf(ActivityException.class)
                .extracting("errorCode")
                .isEqualTo(ActivityErrorCode.NEGATIVE_CONCENTRATION);

        assertThatThrownBy(() -> calculator.calculateActivities(List.of(
                ion("SPEC-NA-PLUS", "0.600", 1),
                ion("SPEC-CL-MINUS", "0.600", -1)
        ), DAVIES_25C))
                .isInstanceOf(ActivityException.class)
                .extracting("errorCode")
                .isEqualTo(ActivityErrorCode.OUTSIDE_MODEL_VALIDITY_RANGE);
    }

    @Test
    void idealEquilibriumModeReproducesExistingConcentrationBasedWeakAcidResult() {
        ActivityCorrectionRequest request = ActivityCorrectionRequest.monoprotic(
                ActivityModel.IDEAL,
                ActivityEquilibriumSystemType.WEAK_ACID,
                "SPEC-CH3COOH",
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                null,
                0,
                new BigDecimal("1.75e-5"),
                null,
                new BigDecimal("1.00e-14"),
                DAVIES_25C
        );

        ActivityCorrectedEquilibriumResult result = calculator.calculateEquilibrium(request);

        assertThat(result.activityPh().getValue()).isCloseTo(new BigDecimal("2.8810"), offset(new BigDecimal("0.0010")));
        assertThat(result.idealPh().getValue()).isEqualByComparingTo(result.activityPh().getValue());
        assertThat(result.solverStatus()).isEqualTo(ActivitySolverStatus.CONVERGED);
    }

    @Test
    void daviesEquilibriumIsSelfConsistentAndDeterministicForRepresentativeSystems() {
        ActivityCorrectionRequest acetic = ActivityCorrectionRequest.monoprotic(
                ActivityModel.DAVIES,
                ActivityEquilibriumSystemType.WEAK_ACID,
                "SPEC-CH3COOH",
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                null,
                0,
                new BigDecimal("1.75e-5"),
                null,
                new BigDecimal("1.00e-14"),
                DAVIES_25C
        );
        ActivityCorrectionRequest bicarbonate = ActivityCorrectionRequest.polyprotic(
                ActivityModel.DAVIES,
                carbonicFamily(),
                PolyproticInitialForm.INTERMEDIATE_AMPHIPROTIC_SALT,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                "SPEC-NA-PLUS",
                1,
                BigDecimal.ONE,
                new BigDecimal("1.00e-14"),
                DAVIES_25C
        );
        ActivityCorrectionRequest sulfuric = ActivityCorrectionRequest.polyprotic(
                ActivityModel.DAVIES,
                sulfuricFamily(),
                PolyproticInitialForm.FULLY_PROTONATED_ACID,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                null,
                0,
                BigDecimal.ZERO,
                new BigDecimal("1.00e-14"),
                DAVIES_25C
        );

        ActivityCorrectedEquilibriumResult first = calculator.calculateEquilibrium(acetic);
        ActivityCorrectedEquilibriumResult repeated = calculator.calculateEquilibrium(acetic);
        ActivityCorrectedEquilibriumResult bicarbonateResult = calculator.calculateEquilibrium(bicarbonate);
        ActivityCorrectedEquilibriumResult sulfuricResult = calculator.calculateEquilibrium(sulfuric);

        assertThat(first.activityPh().getValue()).isNotEqualByComparingTo(first.idealPh().getValue());
        assertThat(first.activityPh().getValue()).isEqualByComparingTo(repeated.activityPh().getValue());
        assertConverged(first);
        assertConverged(bicarbonateResult);
        assertConverged(sulfuricResult);
        assertThat(bicarbonateResult.ionicStrength().value()).isLessThanOrEqualTo(new BigDecimal("0.5"));
        assertThat(sulfuricResult.coefficients()).doesNotContainKey("Ka1");
    }

    private static IonicSpeciesConcentration ion(String speciesCode, String concentration, int charge) {
        return new IonicSpeciesConcentration(speciesCode, new BigDecimal(concentration), charge);
    }

    private static void assertConverged(ActivityCorrectedEquilibriumResult result) {
        assertThat(result.solverStatus()).isEqualTo(ActivitySolverStatus.CONVERGED);
        assertThat(result.iteration().hydroniumDelta()).isLessThan(new BigDecimal("1e-10"));
        assertThat(result.iteration().ionicStrengthDelta()).isLessThan(new BigDecimal("1e-10"));
        assertThat(result.residual().massBalanceResidual()).isLessThan(new BigDecimal("1e-12"));
        assertThat(result.residual().chargeBalanceResidual()).isLessThan(new BigDecimal("1e-10"));
    }

    private static PolyproticAcidFamily carbonicFamily() {
        return new PolyproticAcidFamily(
                "FAMILY-CARBONIC",
                List.of(
                        new PolyproticSpecies("SPEC-H2CO3", "H2CO3", 2, 0),
                        new PolyproticSpecies("SPEC-HCO3-MINUS", "HCO3-", 1, -1),
                        new PolyproticSpecies("SPEC-CO3-2MINUS", "CO3^2-", 0, -2)
                ),
                List.of(
                        new PolyproticDissociationConstant(1, new BigDecimal("4.45e-7"), T25, "COMP-H2O"),
                        new PolyproticDissociationConstant(2, new BigDecimal("4.69e-11"), T25, "COMP-H2O")
                ),
                false,
                List.of("test-reference")
        );
    }

    private static PolyproticAcidFamily sulfuricFamily() {
        return new PolyproticAcidFamily(
                "FAMILY-SULFURIC",
                List.of(
                        new PolyproticSpecies("SPEC-H2SO4", "H2SO4", 2, 0),
                        new PolyproticSpecies("SPEC-HSO4-MINUS", "HSO4-", 1, -1),
                        new PolyproticSpecies("SPEC-SO4-2MINUS", "SO4^2-", 0, -2)
                ),
                List.of(new PolyproticDissociationConstant(2, new BigDecimal("1.02e-2"), T25, "COMP-H2O")),
                true,
                List.of("test-reference")
        );
    }
}

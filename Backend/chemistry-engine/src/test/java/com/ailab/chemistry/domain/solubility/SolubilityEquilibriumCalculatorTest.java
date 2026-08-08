package com.ailab.chemistry.domain.solubility;

import com.ailab.chemistry.domain.acidbase.ActivityModel;
import com.ailab.chemistry.domain.acidbase.ActivityParameterSet;
import com.ailab.chemistry.domain.acidbase.ActivityException;
import com.ailab.chemistry.domain.acidbase.IonicSpeciesConcentration;
import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;

class SolubilityEquilibriumCalculatorTest {

    private static final Temperature T25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);
    private static final ActivityParameterSet IDEAL = new ActivityParameterSet(
            ActivityModel.IDEAL, "COMP-H2O", T25, BigDecimal.ONE, BigDecimal.ZERO, new BigDecimal("999"),
            "ideal", "unit activity coefficients", "not applicable");
    private static final ActivityParameterSet DAVIES = new ActivityParameterSet(
            ActivityModel.DAVIES, "COMP-H2O", T25, new BigDecimal("0.509"), BigDecimal.ZERO, new BigDecimal("0.5"),
            "CRC Handbook of Chemistry and Physics, 104th Edition, Section 5, activity-coefficient constants",
            "Davies A parameter for water at 298.15 K",
            "CRC data are copyrighted; values are stored as a minimal cited reference subset for internal educational calculations");
    private final SolubilityEquilibriumCalculator calculator = new SolubilityEquilibriumCalculator();

    @Test
    void classifiesQspBelowAtAndAboveKspWithZeroIonConcentration() {
        SolubilityEquilibrium calcite = calcite();

        SaturationResult zero = calculator.calculateSaturation(new SaturationRequest(
                calcite, List.of(ion("SPEC-CA-2PLUS", "0", 2), ion("SPEC-CO3-2MINUS", "0.010", -2)),
                List.of(), IDEAL, new BigDecimal("1e-8")));
        SaturationResult below = calculator.calculateSaturation(new SaturationRequest(
                calcite, List.of(ion("SPEC-CA-2PLUS", "1.0e-5", 2), ion("SPEC-CO3-2MINUS", "1.0e-5", -2)),
                List.of(), IDEAL, new BigDecimal("1e-8")));
        SaturationResult at = calculator.calculateSaturation(new SaturationRequest(
                calcite, List.of(ion("SPEC-CA-2PLUS", "6.480740698e-5", 2), ion("SPEC-CO3-2MINUS", "6.480740698e-5", -2)),
                List.of(), IDEAL, new BigDecimal("1e-7")));
        SaturationResult above = calculator.calculateSaturation(new SaturationRequest(
                calcite, List.of(ion("SPEC-CA-2PLUS", "1.0e-4", 2), ion("SPEC-CO3-2MINUS", "1.0e-4", -2)),
                List.of(), IDEAL, new BigDecimal("1e-8")));

        assertThat(zero.status()).isEqualTo(SaturationStatus.UNSATURATED);
        assertThat(zero.ionicProduct().value()).isEqualByComparingTo("0");
        assertThat(below.status()).isEqualTo(SaturationStatus.UNSATURATED);
        assertThat(at.status()).isEqualTo(SaturationStatus.SATURATED);
        assertThat(above.status()).isEqualTo(SaturationStatus.SUPERSATURATED);
    }

    @Test
    void solvesPureWaterSolubilityForGeneralStoichiometryAndCommonIonReduction() {
        MolarSolubilityResult calcite = calculator.calculateMolarSolubility(new MolarSolubilityRequest(
                calcite(), List.of(), List.of(), IDEAL, new BigDecimal("1e-8")));
        MolarSolubilityResult magnesiumHydroxide = calculator.calculateMolarSolubility(new MolarSolubilityRequest(
                magnesiumHydroxide(), List.of(), List.of(), IDEAL, new BigDecimal("1e-8")));
        MolarSolubilityResult aluminumHydroxide = calculator.calculateMolarSolubility(new MolarSolubilityRequest(
                aluminumHydroxide(), List.of(), List.of(), IDEAL, new BigDecimal("1e-8")));
        MolarSolubilityResult commonIon = calculator.calculateMolarSolubility(new MolarSolubilityRequest(
                calcite(), List.of(ion("SPEC-CO3-2MINUS", "0.0100", -2)), List.of(), IDEAL, new BigDecimal("1e-8")));

        assertThat(calcite.molarSolubility().in(MolarConcentrationUnit.MOL_PER_LITER))
                .isCloseTo(new BigDecimal("6.4807e-5"), offset(new BigDecimal("1e-8")));
        assertThat(magnesiumHydroxide.molarSolubility().in(MolarConcentrationUnit.MOL_PER_LITER))
                .isCloseTo(new BigDecimal("1.12e-4"), offset(new BigDecimal("5e-7")));
        assertThat(aluminumHydroxide.molarSolubility().in(MolarConcentrationUnit.MOL_PER_LITER))
                .isCloseTo(new BigDecimal("1.826e-9"), offset(new BigDecimal("2e-12")));
        assertThat(commonIon.molarSolubility().in(MolarConcentrationUnit.MOL_PER_LITER))
                .isCloseTo(new BigDecimal("4.2e-7"), offset(new BigDecimal("2e-9")));
        assertThat(commonIon.molarSolubility().in(MolarConcentrationUnit.MOL_PER_LITER))
                .isLessThan(calcite.molarSolubility().in(MolarConcentrationUnit.MOL_PER_LITER));
    }

    @Test
    void precipitatesAfterMixingWithoutConsumingPastLimitingIonAndReportsMass() {
        PrecipitationResult result = calculator.calculatePrecipitation(new PrecipitationRequest(
                silverCarbonate(),
                List.of(
                        new SolutionIonAmount("SPEC-AG-PLUS", AmountOfSubstance.of("0.0020", AmountOfSubstanceUnit.MOLE), 1),
                        new SolutionIonAmount("SPEC-CO3-2MINUS", AmountOfSubstance.of("0.0015", AmountOfSubstanceUnit.MOLE), -2)
                ),
                Volume.of("0.1000", VolumeUnit.LITER),
                List.of(),
                IDEAL,
                new BigDecimal("275.745"),
                new BigDecimal("1e-8")
        ));

        assertThat(result.initialStatus()).isEqualTo(SaturationStatus.SUPERSATURATED);
        assertThat(result.precipitatedMoles().in(AmountOfSubstanceUnit.MOLE))
                .isCloseTo(new BigDecimal("0.00099795"), offset(new BigDecimal("2e-8")));
        assertThat(result.equilibriumConcentrations().get("SPEC-AG-PLUS")).isLessThan(new BigDecimal("0.0002"));
        assertThat(result.equilibriumConcentrations().get("SPEC-CO3-2MINUS")).isCloseTo(new BigDecimal("0.0050205"), offset(new BigDecimal("2e-7")));
        assertThat(result.precipitatedMass().orElseThrow().in(com.ailab.chemistry.domain.measurement.MassUnit.GRAM))
                .isCloseTo(new BigDecimal("0.2752"), offset(new BigDecimal("0.0002")));
        assertThat(result.residual().ionBalanceResidual()).isLessThan(new BigDecimal("1e-12"));
        assertThat(result.residual().solubilityProductResidual()).isLessThan(new BigDecimal("1e-12"));
    }

    @Test
    void daviesModeSelfConsistentlyChangesSolubilityAndRejectsRangeViolation() {
        MolarSolubilityResult ideal = calculator.calculateMolarSolubility(new MolarSolubilityRequest(
                magnesiumHydroxide(), List.of(), List.of(), IDEAL, new BigDecimal("1e-8")));
        MolarSolubilityResult davies = calculator.calculateMolarSolubility(new MolarSolubilityRequest(
                magnesiumHydroxide(), List.of(), List.of(), DAVIES, new BigDecimal("1e-8")));
        MolarSolubilityResult repeated = calculator.calculateMolarSolubility(new MolarSolubilityRequest(
                magnesiumHydroxide(), List.of(), List.of(), DAVIES, new BigDecimal("1e-8")));

        assertThat(davies.molarSolubility().in(MolarConcentrationUnit.MOL_PER_LITER))
                .isGreaterThan(ideal.molarSolubility().in(MolarConcentrationUnit.MOL_PER_LITER));
        assertThat(davies.molarSolubility().in(MolarConcentrationUnit.MOL_PER_LITER))
                .isEqualByComparingTo(repeated.molarSolubility().in(MolarConcentrationUnit.MOL_PER_LITER));
        assertThat(davies.iterations()).isGreaterThan(1);
        assertThat(davies.solverStatus()).isEqualTo(SolubilitySolverStatus.CONVERGED);

        assertThatThrownBy(() -> calculator.calculateMolarSolubility(new MolarSolubilityRequest(
                calcite(), List.of(ion("SPEC-NA-PLUS", "0.600", 1), ion("SPEC-CL-MINUS", "0.600", -1)),
                List.of(), DAVIES, new BigDecimal("1e-8"))))
                .isInstanceOf(SolubilityException.class)
                .extracting("errorCode")
                .isEqualTo(SolubilityErrorCode.OUTSIDE_ACTIVITY_MODEL_RANGE);
    }

    @Test
    void rejectsInvalidReferenceAndRequestData() {
        assertThatThrownBy(() -> new SolubilityEquilibrium(
                new SolubilityEquilibriumCode("KSP-BROKEN"),
                "COMP-BROKEN",
                List.of(term("SPEC-CA-2PLUS", "Ca^2+", 2, 1), term("SPEC-CL-MINUS", "Cl-", -1, 1)),
                ksp("1e-4"),
                conditions(),
                new SolubilityDatasetVersion("solubility-ksp-v1.0.0"),
                provenance()))
                .isInstanceOf(SolubilityException.class)
                .extracting("errorCode")
                .isEqualTo(SolubilityErrorCode.UNBALANCED_DISSOLUTION);

        assertThatThrownBy(() -> calculator.calculateSaturation(new SaturationRequest(
                calcite(), List.of(ion("SPEC-CA-2PLUS", "-0.001", 2)), List.of(), IDEAL, new BigDecimal("1e-8"))))
                .isInstanceOf(ActivityException.class)
                .extracting("errorCode")
                .isEqualTo(com.ailab.chemistry.domain.acidbase.ActivityErrorCode.NEGATIVE_CONCENTRATION);

        assertThatThrownBy(() -> new SolubilityProduct(BigDecimal.ZERO))
                .isInstanceOf(SolubilityException.class)
                .extracting("errorCode")
                .isEqualTo(SolubilityErrorCode.MISSING_KSP);
    }

    private static SolubilityEquilibrium calcite() {
        return new SolubilityEquilibrium(new SolubilityEquilibriumCode("KSP-CACO3-CALCITE"), "COMP-CACO3",
                List.of(term("SPEC-CA-2PLUS", "Ca^2+", 2, 1), term("SPEC-CO3-2MINUS", "CO3^2-", -2, 1)),
                ksp("4.20e-9"), conditions(), new SolubilityDatasetVersion("solubility-ksp-v1.0.0"), provenance());
    }

    private static SolubilityEquilibrium magnesiumHydroxide() {
        return new SolubilityEquilibrium(new SolubilityEquilibriumCode("KSP-MG-OH-2"), "COMP-MG-OH-2",
                List.of(term("SPEC-MG-2PLUS", "Mg^2+", 2, 1), term("SPEC-OH-MINUS", "OH-", -1, 2)),
                ksp("5.61e-12"), conditions(), new SolubilityDatasetVersion("solubility-ksp-v1.0.0"), provenance());
    }

    private static SolubilityEquilibrium aluminumHydroxide() {
        return new SolubilityEquilibrium(new SolubilityEquilibriumCode("KSP-AL-OH-3"), "COMP-AL-OH-3",
                List.of(term("SPEC-AL-3PLUS", "Al^3+", 3, 1), term("SPEC-OH-MINUS", "OH-", -1, 3)),
                ksp("3.0e-34"), conditions(), new SolubilityDatasetVersion("solubility-ksp-v1.0.0"), provenance());
    }

    private static SolubilityEquilibrium silverCarbonate() {
        return new SolubilityEquilibrium(new SolubilityEquilibriumCode("KSP-AG2CO3"), "COMP-AG2CO3",
                List.of(term("SPEC-AG-PLUS", "Ag+", 1, 2), term("SPEC-CO3-2MINUS", "CO3^2-", -2, 1)),
                ksp("8.46e-12"), conditions(), new SolubilityDatasetVersion("solubility-ksp-v1.0.0"), provenance());
    }

    private static DissolutionTerm term(String code, String formula, int charge, int coefficient) {
        return new DissolutionTerm(code, formula, charge, coefficient);
    }

    private static IonicSpeciesConcentration ion(String code, String concentration, int charge) {
        return new IonicSpeciesConcentration(code, new BigDecimal(concentration), charge);
    }

    private static SolubilityProduct ksp(String value) {
        return new SolubilityProduct(new BigDecimal(value));
    }

    private static SolubilityReferenceConditions conditions() {
        return new SolubilityReferenceConditions(T25, "COMP-H2O", "dimensionless activities relative to c0=1 mol/L");
    }

    private static SolubilityProvenance provenance() {
        return new SolubilityProvenance(
                "CRC-HANDBOOK-104",
                "CRC Handbook of Chemistry and Physics, 104th Edition, Section 8, solubility products at 25 C",
                "CRC values are copyrighted; reuse is limited to a minimal cited educational subset in this project");
    }
}

package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;
import com.ailab.chemistry.domain.measurement.exception.NegativeQuantityException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.data.Offset.offset;

class PolyproticTitrationCalculatorTest {

    private static final Temperature T25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);
    private static final BigDecimal KW = new BigDecimal("1.00e-14");
    private static final PolyproticTitrationCalculator CALCULATOR = new PolyproticTitrationCalculator();

    @Test
    void carbonicAcidWithSodiumHydroxideUsesTwoContinuousEquivalencePoints() {
        PolyproticTitrationRequest request = carbonicAcidWithBase();

        PolyproticTitrationCurveResult characteristic = CALCULATOR.calculateCharacteristicPoints(request);

        assertEquivalenceVolumes(characteristic, "25.00", "50.00");
        assertThat(characteristic.points()).extracting(PolyproticTitrationPointResult::region)
                .containsExactly(
                        PolyproticTitrationRegion.INITIAL,
                        PolyproticTitrationRegion.FIRST_HALF_EQUIVALENCE,
                        PolyproticTitrationRegion.FIRST_EQUIVALENCE,
                        PolyproticTitrationRegion.SECOND_HALF_EQUIVALENCE,
                        PolyproticTitrationRegion.SECOND_EQUIVALENCE,
                        PolyproticTitrationRegion.AFTER_SECOND_EQUIVALENCE
                );

        PolyproticTitrationPointResult initial = characteristic.points().get(0);
        PolyproticTitrationPointResult firstHalf = characteristic.points().get(1);
        PolyproticTitrationPointResult firstEq = characteristic.points().get(2);
        PolyproticTitrationPointResult secondHalf = characteristic.points().get(3);
        PolyproticTitrationPointResult secondEq = characteristic.points().get(4);
        PolyproticTitrationPointResult postSecond = characteristic.points().get(5);

        assertThat(initial.ph().getValue()).isCloseTo(new BigDecimal("3.6763"), offset(new BigDecimal("0.0008")));
        assertThat(firstHalf.ph().getValue()).isCloseTo(new BigDecimal("6.35"), offset(new BigDecimal("0.04")));
        assertThat(firstEq.distribution().dominantSpeciesCode()).isEqualTo("SPEC-HCO3-MINUS");
        assertThat(secondHalf.ph().getValue()).isCloseTo(new BigDecimal("10.33"), offset(new BigDecimal("0.04")));
        assertThat(secondEq.distribution().dominantSpeciesCode()).isEqualTo("SPEC-CO3-2MINUS");
        assertThat(postSecond.ph().getValue()).isGreaterThan(secondEq.ph().getValue());
        assertThat(phValues(characteristic.points())).isSorted();
        assertResidualsAreSmall(characteristic.points());
    }

    @Test
    void carbonicCurveIsContinuousAroundBothEquivalencePointsAndOrdersInputDeterministically() {
        PolyproticTitrationRequest request = carbonicAcidWithBase();
        List<Volume> unordered = List.of(ml("50.10"), ml("24.90"), ml("25.10"), ml("49.90"), ml("0.00"), ml("60.00"));

        PolyproticTitrationCurveResult curve = CALCULATOR.calculateCurve(request, unordered);

        assertThat(curve.points()).extracting(point -> point.addedTitrantVolume().in(VolumeUnit.MILLILITER))
                .containsExactly(new BigDecimal("0.00"), new BigDecimal("24.90"), new BigDecimal("25.10"), new BigDecimal("49.90"), new BigDecimal("50.10"), new BigDecimal("60.00"));
        assertThat(curve.points().get(2).ph().getValue().subtract(curve.points().get(1).ph().getValue()).abs())
                .isLessThan(new BigDecimal("0.20"));
        assertThat(curve.points().get(4).ph().getValue().subtract(curve.points().get(3).ph().getValue()).abs())
                .isLessThan(new BigDecimal("0.20"));
        assertThat(phValues(curve.points())).isSorted();
    }

    @Test
    void carbonateWithStrongAcidFollowsReverseProtonationSequence() {
        PolyproticTitrationRequest request = carbonateWithAcid();

        PolyproticTitrationCurveResult characteristic = CALCULATOR.calculateCharacteristicPoints(request);

        assertEquivalenceVolumes(characteristic, "25.00", "50.00");
        assertThat(characteristic.points().get(0).distribution().dominantSpeciesCode()).isEqualTo("SPEC-CO3-2MINUS");
        assertThat(characteristic.points().get(2).distribution().dominantSpeciesCode()).isEqualTo("SPEC-HCO3-MINUS");
        assertThat(characteristic.points().get(4).distribution().dominantSpeciesCode()).isEqualTo("SPEC-H2CO3");
        assertThat(phValues(characteristic.points())).isSortedAccordingTo((a, b) -> b.compareTo(a));
        assertResidualsAreSmall(characteristic.points());
    }

    @Test
    void sulfuricAcidWithBasePreservesCompleteFirstDissociationModel() {
        PolyproticTitrationRequest request = sulfuricAcidWithBase();

        PolyproticTitrationPointResult initial = CALCULATOR.calculatePoint(request, ml("0.00"));
        PolyproticTitrationCurveResult characteristic = CALCULATOR.calculateCharacteristicPoints(request);

        assertThat(initial.constants()).doesNotContainKey("Ka1");
        assertThat(initial.constants()).containsEntry("Ka2", new BigDecimal("1.02e-2"));
        assertThat(initial.distribution().getFractions().get(0).fraction()).isEqualByComparingTo("0");
        assertEquivalenceVolumes(characteristic, "25.00", "50.00");
        assertResidualsAreSmall(characteristic.points());
    }

    @Test
    void amphiproticBicarbonateCanBeTitratedWithAcidOrBase() {
        PolyproticTitrationPointResult acidPoint = CALCULATOR.calculatePoint(bicarbonateWithAcid(), ml("25.00"));
        PolyproticTitrationPointResult basePoint = CALCULATOR.calculatePoint(bicarbonateWithBase(), ml("25.00"));

        assertThat(acidPoint.distribution().dominantSpeciesCode()).isEqualTo("SPEC-H2CO3");
        assertThat(basePoint.distribution().dominantSpeciesCode()).isEqualTo("SPEC-CO3-2MINUS");
        assertThat(acidPoint.ph().getValue()).isLessThan(new BigDecimal("5.0"));
        assertThat(basePoint.ph().getValue()).isGreaterThan(new BigDecimal("10.5"));
        assertResidualsAreSmall(List.of(acidPoint, basePoint));
    }

    @Test
    void rejectsInvalidInputsAndUnsafeReferenceData() {
        assertThatThrownBy(() -> carbonicAcidWithBase(new BigDecimal("0")))
                .isInstanceOf(PolyproticTitrationException.class)
                .extracting("errorCode")
                .isEqualTo(PolyproticTitrationErrorCode.NON_POSITIVE_CONCENTRATION);

        assertThatThrownBy(() -> CALCULATOR.calculateCurve(carbonicAcidWithBase(), List.of(ml("1.00"), ml("1.00"))))
                .isInstanceOf(PolyproticTitrationException.class)
                .extracting("errorCode")
                .isEqualTo(PolyproticTitrationErrorCode.DUPLICATE_TITRANT_VOLUME);

        assertThatThrownBy(() -> Volume.of("-1.00", VolumeUnit.MILLILITER))
                .isInstanceOf(NegativeQuantityException.class);

        assertThatThrownBy(() -> new PolyproticTitrationRequest(
                carbonicFamily(),
                PolyproticTitrationSystemType.DIPROTIC_ACID_WITH_STRONG_MONOBASIC_BASE,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                ml("25.00"),
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                null,
                0,
                "SPEC-NA-PLUS",
                -1,
                KW,
                new BigDecimal("0.00001")))
                .isInstanceOf(PolyproticTitrationException.class)
                .extracting("errorCode")
                .isEqualTo(PolyproticTitrationErrorCode.INVALID_SPECTATOR_ION);

        PolyproticAcidFamily pathological = carbonicFamily(List.of(
                step(1, new BigDecimal("1e-80")),
                step(2, new BigDecimal("1e-80"))));
        assertThatThrownBy(() -> CALCULATOR.calculatePoint(new PolyproticTitrationRequest(
                pathological,
                PolyproticTitrationSystemType.DIPROTIC_ACID_WITH_STRONG_MONOBASIC_BASE,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                ml("25.00"),
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                null,
                0,
                "SPEC-NA-PLUS",
                1,
                new BigDecimal("1e-80"),
                new BigDecimal("0.00001")), ml("0.00")))
                .isInstanceOf(PolyproticTitrationException.class)
                .extracting("errorCode")
                .isEqualTo(PolyproticTitrationErrorCode.SOLVER_CONVERGENCE_FAILED);
    }

    private static PolyproticTitrationRequest carbonicAcidWithBase() {
        return carbonicAcidWithBase(new BigDecimal("0.100"));
    }

    private static PolyproticTitrationRequest carbonicAcidWithBase(BigDecimal concentration) {
        return new PolyproticTitrationRequest(
                carbonicFamily(),
                PolyproticTitrationSystemType.DIPROTIC_ACID_WITH_STRONG_MONOBASIC_BASE,
                MolarConcentration.of(concentration, MolarConcentrationUnit.MOL_PER_LITER),
                ml("25.00"),
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                null,
                0,
                "SPEC-NA-PLUS",
                1,
                KW,
                new BigDecimal("0.00001"));
    }

    private static PolyproticTitrationRequest carbonateWithAcid() {
        return new PolyproticTitrationRequest(
                carbonicFamily(),
                PolyproticTitrationSystemType.FULLY_DEPROTONATED_SALT_WITH_STRONG_MONOPROTIC_ACID,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                ml("25.00"),
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                "SPEC-NA-PLUS",
                1,
                "SPEC-CL-MINUS",
                -1,
                KW,
                new BigDecimal("0.00001"));
    }

    private static PolyproticTitrationRequest bicarbonateWithAcid() {
        return new PolyproticTitrationRequest(
                carbonicFamily(),
                PolyproticTitrationSystemType.AMPHIPROTIC_SALT_WITH_STRONG_MONOPROTIC_ACID,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                ml("25.00"),
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                "SPEC-NA-PLUS",
                1,
                "SPEC-CL-MINUS",
                -1,
                KW,
                new BigDecimal("0.00001"));
    }

    private static PolyproticTitrationRequest bicarbonateWithBase() {
        return new PolyproticTitrationRequest(
                carbonicFamily(),
                PolyproticTitrationSystemType.AMPHIPROTIC_SALT_WITH_STRONG_MONOBASIC_BASE,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                ml("25.00"),
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                "SPEC-NA-PLUS",
                1,
                "SPEC-NA-PLUS",
                1,
                KW,
                new BigDecimal("0.00001"));
    }

    private static PolyproticTitrationRequest sulfuricAcidWithBase() {
        return new PolyproticTitrationRequest(
                sulfuricFamily(),
                PolyproticTitrationSystemType.DIPROTIC_ACID_WITH_STRONG_MONOBASIC_BASE,
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                ml("25.00"),
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                null,
                0,
                "SPEC-NA-PLUS",
                1,
                KW,
                new BigDecimal("0.00001"));
    }

    private static PolyproticAcidFamily carbonicFamily() {
        return carbonicFamily(List.of(step(1, new BigDecimal("4.45e-7")), step(2, new BigDecimal("4.69e-11"))));
    }

    private static PolyproticAcidFamily carbonicFamily(List<PolyproticDissociationConstant> constants) {
        return new PolyproticAcidFamily(
                "FAMILY-CARBONIC",
                List.of(
                        new PolyproticSpecies("SPEC-H2CO3", "H2CO3", 2, 0),
                        new PolyproticSpecies("SPEC-HCO3-MINUS", "HCO3-", 1, -1),
                        new PolyproticSpecies("SPEC-CO3-2MINUS", "CO3^2-", 0, -2)
                ),
                constants,
                false,
                List.of("test-reference"));
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
                List.of("test-reference"));
    }

    private static PolyproticDissociationConstant step(int stepNumber, BigDecimal value) {
        return new PolyproticDissociationConstant(stepNumber, value, T25, "COMP-H2O");
    }

    private static Volume ml(String value) {
        return Volume.of(value, VolumeUnit.MILLILITER);
    }

    private static List<BigDecimal> phValues(List<PolyproticTitrationPointResult> points) {
        return points.stream().map(point -> point.ph().getValue()).toList();
    }

    private static void assertEquivalenceVolumes(PolyproticTitrationCurveResult result, String firstMl, String secondMl) {
        assertThat(result.equivalencePoints()).hasSize(2);
        assertThat(result.equivalencePoints().get(0).volume().in(VolumeUnit.MILLILITER)).isEqualByComparingTo(firstMl);
        assertThat(result.equivalencePoints().get(1).volume().in(VolumeUnit.MILLILITER)).isEqualByComparingTo(secondMl);
    }

    private static void assertResidualsAreSmall(List<PolyproticTitrationPointResult> points) {
        for (PolyproticTitrationPointResult point : points) {
            assertThat(point.solverStatus()).isEqualTo(PolyproticSolverStatus.CONVERGED);
            assertThat(point.residual().massBalanceResidual()).isLessThan(new BigDecimal("1e-14"));
            assertThat(point.residual().chargeBalanceResidual()).isLessThan(new BigDecimal("1e-12"));
        }
    }
}

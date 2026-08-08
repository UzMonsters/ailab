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

class TitrationCalculatorTest {

    private static final Temperature T25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);
    private static final TitrationCalculator CALCULATOR = new TitrationCalculator();
    private static final BigDecimal KW = new BigDecimal("1.00e-14");
    private static final BigDecimal KA_ACETIC = new BigDecimal("1.75e-5");
    private static final BigDecimal KB_AMMONIA = new BigDecimal("1.76e-5");

    @Test
    void strongAcidStrongBaseRegionsUseTotalVolumeAndWaterAutoionization() {
        TitrationRequest request = request(TitrationSystemType.STRONG_ACID_STRONG_BASE, null, null);

        TitrationPointResult initial = CALCULATOR.calculatePoint(request, ml("0.00"));
        TitrationPointResult before = CALCULATOR.calculatePoint(request, ml("12.50"));
        TitrationPointResult equivalence = CALCULATOR.calculatePoint(request, ml("25.00"));
        TitrationPointResult after = CALCULATOR.calculatePoint(request, ml("50.00"));

        assertThat(initial.getRegion()).isEqualTo(TitrationRegion.INITIAL);
        assertThat(initial.getPh().getValue()).isCloseTo(new BigDecimal("1.0000"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0005")));
        assertThat(before.getRegion()).isEqualTo(TitrationRegion.PRE_EQUIVALENCE);
        assertThat(before.getPh().getValue()).isCloseTo(new BigDecimal("1.4771"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0005")));
        assertThat(equivalence.getRegion()).isEqualTo(TitrationRegion.EQUIVALENCE);
        assertThat(equivalence.getPh().getValue()).isEqualByComparingTo("7.0000");
        assertThat(after.getRegion()).isEqualTo(TitrationRegion.POST_EQUIVALENCE);
        assertThat(after.getPh().getValue()).isCloseTo(new BigDecimal("12.5229"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0005")));
        assertThat(equivalence.getCalculationMethod()).isEqualTo(TitrationCalculationMethod.PURE_WATER_EQUIVALENCE);
        assertThat(initial.getPh().getValue()).isLessThan(before.getPh().getValue());
        assertThat(before.getPh().getValue()).isLessThan(equivalence.getPh().getValue());
        assertThat(equivalence.getPh().getValue()).isLessThan(after.getPh().getValue());
    }

    @Test
    void reverseStrongBaseStrongAcidDirectionIsMonotonicDownward() {
        TitrationRequest request = request(TitrationSystemType.STRONG_BASE_STRONG_ACID, null, null);
        TitrationCurveResult curve = CALCULATOR.calculateCurve(request, List.of(ml("0"), ml("12.5"), ml("25"), ml("50")));

        assertThat(curve.getPoints()).extracting(TitrationPointResult::getRegion)
                .containsExactly(TitrationRegion.INITIAL, TitrationRegion.PRE_EQUIVALENCE, TitrationRegion.EQUIVALENCE, TitrationRegion.POST_EQUIVALENCE);
        assertThat(curve.getPoints().get(0).getPh().getValue()).isGreaterThan(curve.getPoints().get(1).getPh().getValue());
        assertThat(curve.getPoints().get(1).getPh().getValue()).isGreaterThan(curve.getPoints().get(2).getPh().getValue());
        assertThat(curve.getPoints().get(2).getPh().getValue()).isGreaterThan(curve.getPoints().get(3).getPh().getValue());
    }

    @Test
    void weakAcidStrongBaseUsesContinuousEquilibriumAcrossEquivalence() {
        TitrationRequest request = request(TitrationSystemType.WEAK_ACID_STRONG_BASE, KA_ACETIC, null);

        TitrationPointResult initial = CALCULATOR.calculatePoint(request, ml("0"));
        TitrationPointResult half = CALCULATOR.calculatePoint(request, ml("12.5"));
        TitrationPointResult equivalence = CALCULATOR.calculatePoint(request, ml("25"));
        TitrationPointResult beforeEquivalence = CALCULATOR.calculatePoint(request, ml("24.9999"));
        TitrationPointResult afterEquivalence = CALCULATOR.calculatePoint(request, ml("25.0001"));
        TitrationPointResult post = CALCULATOR.calculatePoint(request, ml("50"));

        assertThat(initial.getPh().getValue()).isCloseTo(new BigDecimal("2.879"), org.assertj.core.data.Offset.offset(new BigDecimal("0.005")));
        assertThat(half.getRegion()).isEqualTo(TitrationRegion.HALF_EQUIVALENCE);
        assertThat(half.getPh().getValue()).isCloseTo(new BigDecimal("4.7567"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0008")));
        assertThat(equivalence.getRegion()).isEqualTo(TitrationRegion.EQUIVALENCE);
        assertThat(equivalence.getPh().getValue()).isCloseTo(new BigDecimal("8.728"), org.assertj.core.data.Offset.offset(new BigDecimal("0.01")));
        assertThat(post.getPh().getValue()).isCloseTo(new BigDecimal("12.5229"), org.assertj.core.data.Offset.offset(new BigDecimal("0.001")));
        assertThat(afterEquivalence.getPh().getValue().subtract(beforeEquivalence.getPh().getValue()).abs()).isLessThan(new BigDecimal("0.02"));
        assertThat(equivalence.getResidual().getMassBalanceResidual()).isLessThan(new BigDecimal("1e-18"));
        assertThat(equivalence.getResidual().getChargeBalanceResidual()).isLessThan(new BigDecimal("1e-12"));
        assertThat(equivalence.getSolverStatus()).isEqualTo(TitrationSolverStatus.CONVERGED);
    }

    @Test
    void weakBaseStrongAcidUsesContinuousEquilibriumAcrossCurve() {
        TitrationRequest request = request(TitrationSystemType.WEAK_BASE_STRONG_ACID, null, KB_AMMONIA);
        TitrationCurveResult curve = CALCULATOR.calculateCurve(request, List.of(ml("0"), ml("12.5"), ml("25"), ml("50")));

        assertThat(curve.getPoints().get(0).getPh().getValue()).isCloseTo(new BigDecimal("11.124"), org.assertj.core.data.Offset.offset(new BigDecimal("0.005")));
        assertThat(curve.getPoints().get(1).getPoh().getValue()).isCloseTo(new BigDecimal("4.7545"), org.assertj.core.data.Offset.offset(new BigDecimal("0.0008")));
        assertThat(curve.getPoints().get(2).getPh().getValue()).isCloseTo(new BigDecimal("5.272"), org.assertj.core.data.Offset.offset(new BigDecimal("0.01")));
        assertThat(curve.getPoints().get(3).getPh().getValue()).isCloseTo(new BigDecimal("1.4771"), org.assertj.core.data.Offset.offset(new BigDecimal("0.001")));
        assertThat(curve.getPoints()).extracting(p -> p.getSolverStatus()).containsOnly(TitrationSolverStatus.CONVERGED);
        assertThat(curve.getPoints().get(0).getPh().getValue()).isGreaterThan(curve.getPoints().get(1).getPh().getValue());
        assertThat(curve.getPoints().get(1).getPh().getValue()).isGreaterThan(curve.getPoints().get(2).getPh().getValue());
        assertThat(curve.getPoints().get(2).getPh().getValue()).isGreaterThan(curve.getPoints().get(3).getPh().getValue());
    }

    @Test
    void characteristicPointsAreDeterministicallyOrderedAndPreserveEquivalenceMetadata() {
        TitrationRequest request = request(TitrationSystemType.WEAK_ACID_STRONG_BASE, KA_ACETIC, null);

        TitrationCurveResult curve = CALCULATOR.calculateCharacteristicPoints(request);

        assertThat(curve.getEquivalencePoint().getVolume().in(VolumeUnit.MILLILITER)).isEqualByComparingTo("25.00");
        assertThat(curve.getPoints()).extracting(p -> p.getAddedTitrantVolume().in(VolumeUnit.MILLILITER))
                .containsExactly(new BigDecimal("0.00"), new BigDecimal("12.500"), new BigDecimal("25.00"), new BigDecimal("50.00"));
    }

    @Test
    void curveRejectsNegativeOrDuplicateVolumesAndUnsupportedSystems() {
        TitrationRequest request = request(TitrationSystemType.STRONG_ACID_STRONG_BASE, null, null);

        assertThatThrownBy(() -> ml("-1"))
                .isInstanceOf(NegativeQuantityException.class);

        assertThatThrownBy(() -> CALCULATOR.calculateCurve(request, List.of(ml("1"), ml("1.0"))))
                .isInstanceOf(TitrationException.class)
                .extracting("errorCode")
                .isEqualTo(TitrationErrorCode.DUPLICATE_TITRANT_VOLUME);

        assertThatThrownBy(() -> request(TitrationSystemType.UNSUPPORTED_WEAK_ACID_WEAK_BASE, KA_ACETIC, KB_AMMONIA))
                .isInstanceOf(TitrationException.class)
                .extracting("errorCode")
                .isEqualTo(TitrationErrorCode.UNSUPPORTED_TITRATION_SYSTEM);
    }

    @Test
    void weakSolverReportsStructuredFailureWhenRootFallsOutsideSupportedBracket() {
        TitrationRequest request = request(TitrationSystemType.WEAK_ACID_STRONG_BASE, new BigDecimal("1e-80"), null)
                .withResolvedSystem(TitrationSystemType.WEAK_ACID_STRONG_BASE, new BigDecimal("1e-80"), null, new BigDecimal("1e-80"), List.of("pathological-test-reference"));

        assertThatThrownBy(() -> CALCULATOR.calculatePoint(request, ml("0")))
                .isInstanceOf(TitrationException.class)
                .extracting("errorCode")
                .isEqualTo(TitrationErrorCode.SOLVER_CONVERGENCE_FAILED);
    }

    @Test
    void repeatedCurvesAreDeterministicAndOrdered() {
        TitrationRequest request = request(TitrationSystemType.STRONG_ACID_STRONG_BASE, null, null);
        List<Volume> requested = List.of(ml("50"), ml("0"), ml("25"), ml("12.5"));

        TitrationCurveResult first = CALCULATOR.calculateCurve(request, requested);
        TitrationCurveResult second = CALCULATOR.calculateCurve(request, requested);

        assertThat(first).isEqualTo(second);
        assertThat(first.getPoints()).extracting(p -> p.getAddedTitrantVolume().in(VolumeUnit.MILLILITER))
                .containsExactly(new BigDecimal("0"), new BigDecimal("12.5"), new BigDecimal("25"), new BigDecimal("50"));
    }

    private static TitrationRequest request(TitrationSystemType type, BigDecimal ka, BigDecimal kb) {
        return new TitrationRequest(
                type,
                "SPEC-ANALYTE",
                "SPEC-TITRANT",
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                ml("25.00"),
                MolarConcentration.of("0.100", MolarConcentrationUnit.MOL_PER_LITER),
                T25,
                "COMP-H2O",
                ka,
                kb,
                KW,
                List.of("test-reference")
        );
    }

    private static Volume ml(String value) {
        return Volume.of(value, VolumeUnit.MILLILITER);
    }
}

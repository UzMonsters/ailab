package com.ailab.chemistry.domain.kinetics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Duration;
import com.ailab.chemistry.domain.measurement.DurationUnit;
import com.ailab.chemistry.domain.measurement.MolarEnergy;
import com.ailab.chemistry.domain.measurement.MolarEnergyUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReactionKineticsCalculatorTest {

    private final ReactionKineticsCalculator calculator = new ReactionKineticsCalculator();

    @Test
    void rateEvaluationOrderIndependenceFromStoichiometryAndFractionalOrders() {
        // Reaction 2 A + B -> C (stoichiometry 2, 1, 1), but empirical rate r = k [A]^0.5 [B]^1.5
        KineticRateLaw rateLaw = KineticRateLaw.of(List.of(
                new KineticRateLawTerm("COMP-A", MatterState.GAS, ReactionOrder.of("0.5")),
                new KineticRateLawTerm("COMP-B", MatterState.GAS, ReactionOrder.of("1.5"))
        ));

        RateConstant k = RateConstant.of("2.0", RateConstantDimension.SECOND_ORDER);
        Map<String, BigDecimal> concs = Map.of("COMP-A", new BigDecimal("4.0"), "COMP-B", new BigDecimal("9.0"));
        Map<String, BigDecimal> nuMap = Map.of("COMP-A", new BigDecimal("-2.0"), "COMP-B", new BigDecimal("-1.0"), "COMP-C", new BigDecimal("1.0"));

        RateEvaluationResult result = calculator.calculateRate(new RateEvaluationRequest("RXN-TEST", rateLaw, k, concs), nuMap);

        // r = 2.0 * sqrt(4.0) * (9.0)^1.5 = 2.0 * 2.0 * 27.0 = 108.0 mol/(L*s)
        assertThat(result.reactionRate().value()).isCloseTo(new BigDecimal("108.0"), org.assertj.core.data.Offset.offset(new BigDecimal("0.01")));

        // Species rates: d[A]/dt = -2 * 108 = -216, d[C]/dt = +1 * 108 = +108
        BigDecimal rateA = result.speciesRates().stream().filter(s -> s.compoundCode().equals("COMP-A")).findFirst().orElseThrow().rateMolarPerSecond();
        BigDecimal rateC = result.speciesRates().stream().filter(s -> s.compoundCode().equals("COMP-C")).findFirst().orElseThrow().rateMolarPerSecond();

        assertThat(rateA).isEqualTo(new BigDecimal("-216"));
        assertThat(rateC).isEqualTo(new BigDecimal("108"));
    }

    @Test
    void zeroFirstSecondOrderIntegratedLawsAndHalfLives() {
        // Zero order: C(t) = C0 - kt, t1/2 = C0 / 2k
        IntegratedRateLawRequest zeroReq = new IntegratedRateLawRequest(
                "COMP-A", new BigDecimal("10.0"), RateConstant.of("0.5", RateConstantDimension.ZERO_ORDER),
                OverallReactionOrder.of(0), Duration.of("4.0", DurationUnit.SECOND));

        IntegratedRateLawResult zeroRes = calculator.calculateIntegratedLaw(zeroReq);
        HalfLifeResult zeroHalf = calculator.calculateHalfLife(zeroReq);

        assertThat(zeroRes.finalConcentrationMolar()).isEqualByComparingTo(new BigDecimal("8"));
        assertThat(zeroHalf.halfLife().in(DurationUnit.SECOND)).isEqualByComparingTo(new BigDecimal("10"));

        // First order: C(t) = C0 * exp(-kt), t1/2 = ln2 / k
        IntegratedRateLawRequest firstReq = new IntegratedRateLawRequest(
                "COMP-A", new BigDecimal("10.0"), RateConstant.of("0.1", RateConstantDimension.FIRST_ORDER),
                OverallReactionOrder.of(1), Duration.of("10.0", DurationUnit.SECOND));

        IntegratedRateLawResult firstRes = calculator.calculateIntegratedLaw(firstReq);
        HalfLifeResult firstHalf = calculator.calculateHalfLife(firstReq);

        // 10 * exp(-1.0) ~ 3.6787944
        assertThat(firstRes.finalConcentrationMolar()).isCloseTo(new BigDecimal("3.6787944"), org.assertj.core.data.Offset.offset(new BigDecimal("0.001")));
        assertThat(firstHalf.halfLife().in(DurationUnit.SECOND)).isCloseTo(new BigDecimal("6.93147"), org.assertj.core.data.Offset.offset(new BigDecimal("0.001")));

        // Second order: C(t) = C0 / (1 + k*C0*t), t1/2 = 1 / (k*C0)
        IntegratedRateLawRequest secondReq = new IntegratedRateLawRequest(
                "COMP-A", new BigDecimal("2.0"), RateConstant.of("0.5", RateConstantDimension.SECOND_ORDER),
                OverallReactionOrder.of(2), Duration.of("2.0", DurationUnit.SECOND));

        IntegratedRateLawResult secondRes = calculator.calculateIntegratedLaw(secondReq);
        HalfLifeResult secondHalf = calculator.calculateHalfLife(secondReq);

        // C(t) = 2.0 / (1 + 0.5 * 2.0 * 2.0) = 2.0 / 3.0 = 0.6666667
        assertThat(secondRes.finalConcentrationMolar()).isCloseTo(new BigDecimal("0.6666667"), org.assertj.core.data.Offset.offset(new BigDecimal("0.001")));
        assertThat(secondHalf.halfLife().in(DurationUnit.SECOND)).isEqualByComparingTo(new BigDecimal("1"));
    }

    @Test
    void arrheniusTemperatureDependenceAndSafetyRejections() {
        ArrheniusParameters params = new ArrheniusParameters(
                new BigDecimal("1e6"),
                MolarEnergy.of("50.0", MolarEnergyUnit.KILOJOULE_PER_MOLE),
                Temperature.of("200.0", TemperatureUnit.KELVIN),
                Temperature.of("1000.0", TemperatureUnit.KELVIN)
        );

        ArrheniusResult res298 = calculator.calculateRateConstant(new ArrheniusRequest(params, Temperature.of("298.15", TemperatureUnit.KELVIN)));
        ArrheniusResult res500 = calculator.calculateRateConstant(new ArrheniusRequest(params, Temperature.of("500.0", TemperatureUnit.KELVIN)));

        // k increases with temperature for positive Ea
        assertThat(res500.calculatedRateConstant().value()).isGreaterThan(res298.calculatedRateConstant().value());

        // Out of temperature range rejection
        assertThatThrownBy(() -> calculator.calculateRateConstant(new ArrheniusRequest(params, Temperature.of("1200.0", TemperatureUnit.KELVIN))))
                .isInstanceOf(KineticException.class)
                .extracting("errorCode")
                .isEqualTo(KineticErrorCode.OUT_OF_TEMPERATURE_RANGE);
    }
}

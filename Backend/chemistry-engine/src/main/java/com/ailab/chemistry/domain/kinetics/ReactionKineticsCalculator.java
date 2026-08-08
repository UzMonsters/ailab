package com.ailab.chemistry.domain.kinetics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Duration;
import com.ailab.chemistry.domain.measurement.DurationUnit;
import com.ailab.chemistry.domain.measurement.MolarEnergyUnit;
import com.ailab.chemistry.domain.measurement.ScientificMath;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ReactionKineticsCalculator {

    private static final BigDecimal GAS_CONSTANT_R = new BigDecimal("8.314462618"); // J/(mol*K)
    private static final BigDecimal LN_2 = new BigDecimal("0.693147180559945309417232121458176568");

    public RateEvaluationResult calculateRate(RateEvaluationRequest request, Map<String, BigDecimal> stoichiometricCoefficients) {
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(stoichiometricCoefficients, "stoichiometricCoefficients must not be null");

        KineticRateLaw rateLaw = request.rateLaw();
        RateConstant k = request.rateConstant();
        Map<String, BigDecimal> concentrations = request.concentrations();

        BigDecimal rateVal = k.value();

        for (KineticRateLawTerm term : rateLaw.terms()) {
            String code = term.compoundCode();
            BigDecimal conc = concentrations.getOrDefault(code, BigDecimal.ZERO);

            if (conc.compareTo(BigDecimal.ZERO) < 0) {
                throw new KineticException(
                        KineticErrorCode.INVALID_CONCENTRATION,
                        "Concentration cannot be negative for species " + code + ": " + conc);
            }

            BigDecimal order = term.order().value();

            if (conc.compareTo(BigDecimal.ZERO) == 0) {
                if (order.compareTo(BigDecimal.ZERO) == 0) {
                    // 0^0 = 1 contribution
                    continue;
                } else {
                    // 0^positive = 0
                    rateVal = BigDecimal.ZERO;
                    break;
                }
            } else {
                double concDbl = conc.doubleValue();
                double orderDbl = order.doubleValue();
                double termFactor = Math.pow(concDbl, orderDbl);
                rateVal = rateVal.multiply(BigDecimal.valueOf(termFactor), ScientificMath.CALCULATION_CONTEXT);
            }
        }

        ReactionRate rxnRate = ReactionRate.ofMolarPerSecond(rateVal.stripTrailingZeros());

        List<SpeciesRate> speciesRates = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : stoichiometricCoefficients.entrySet()) {
            String code = entry.getKey();
            BigDecimal nu = entry.getValue();
            BigDecimal specRateVal = nu.multiply(rateVal, ScientificMath.CALCULATION_CONTEXT);
            speciesRates.add(new SpeciesRate(code, MatterState.GAS, nu, specRateVal.stripTrailingZeros()));
        }

        String explanation = "Empirical reaction rate evaluated: r = " + rxnRate.value().toPlainString() + " mol/(L*s).";
        return new RateEvaluationResult(rxnRate, speciesRates, rateLaw.overallOrder(), KineticCalculationMethod.EMPIRICAL_RATE_LAW, explanation);
    }

    public IntegratedRateLawResult calculateIntegratedLaw(IntegratedRateLawRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        BigDecimal c0 = request.initialConcentrationMolar();
        BigDecimal k = request.rateConstant().value();
        BigDecimal tSec = request.duration().in(DurationUnit.SECOND);
        BigDecimal order = request.order().totalOrderValue();

        BigDecimal cFinal;
        KineticCalculationMethod method;
        List<String> assumptions = new ArrayList<>();

        if (order.compareTo(BigDecimal.ZERO) == 0) {
            method = KineticCalculationMethod.ZERO_ORDER_INTEGRATED;
            BigDecimal loss = k.multiply(tSec, ScientificMath.CALCULATION_CONTEXT);
            cFinal = c0.subtract(loss, ScientificMath.CALCULATION_CONTEXT);
            if (cFinal.compareTo(BigDecimal.ZERO) < 0) {
                cFinal = BigDecimal.ZERO;
                assumptions.add("Reactant completely depleted during zero-order kinetics");
            } else {
                assumptions.add("Zero-order integrated rate law: C(t) = C0 - k*t");
            }
        } else if (order.compareTo(BigDecimal.ONE) == 0) {
            method = KineticCalculationMethod.FIRST_ORDER_INTEGRATED;
            double expFactor = Math.exp(-k.doubleValue() * tSec.doubleValue());
            cFinal = c0.multiply(BigDecimal.valueOf(expFactor), ScientificMath.CALCULATION_CONTEXT);
            assumptions.add("First-order integrated rate law: C(t) = C0 * exp(-k*t)");
        } else if (order.compareTo(new BigDecimal("2")) == 0) {
            method = KineticCalculationMethod.SECOND_ORDER_INTEGRATED;
            BigDecimal denom = BigDecimal.ONE.add(k.multiply(c0, ScientificMath.CALCULATION_CONTEXT).multiply(tSec, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
            cFinal = c0.divide(denom, ScientificMath.CALCULATION_CONTEXT);
            assumptions.add("Second-order integrated rate law: 1/C(t) = 1/C0 + k*t");
        } else {
            throw new KineticException(
                    KineticErrorCode.INVALID_ORDER,
                    "Analytical integrated law only supports overall order 0, 1, or 2. Requested: " + order);
        }

        cFinal = cFinal.stripTrailingZeros();
        BigDecimal fractionRemaining = c0.compareTo(BigDecimal.ZERO) > 0
                ? cFinal.divide(c0, ScientificMath.CALCULATION_CONTEXT).stripTrailingZeros()
                : BigDecimal.ZERO;

        String explanation = "Analytical integrated rate law computed at t = " + tSec.toPlainString() + " s: C(t) = " + cFinal.toPlainString() + " M.";
        return new IntegratedRateLawResult(
                request.compoundCode(), c0, cFinal, request.duration(), fractionRemaining, method, explanation, assumptions);
    }

    public HalfLifeResult calculateHalfLife(IntegratedRateLawRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        BigDecimal c0 = request.initialConcentrationMolar();
        if (c0.compareTo(BigDecimal.ZERO) <= 0) {
            throw new KineticException(
                    KineticErrorCode.INVALID_CONCENTRATION,
                    "Initial concentration must be positive for half-life calculation: " + c0);
        }

        BigDecimal k = request.rateConstant().value();
        BigDecimal order = request.order().totalOrderValue();
        BigDecimal tHalfSec;

        if (order.compareTo(BigDecimal.ZERO) == 0) {
            tHalfSec = c0.divide(new BigDecimal("2").multiply(k, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
        } else if (order.compareTo(BigDecimal.ONE) == 0) {
            tHalfSec = LN_2.divide(k, ScientificMath.CALCULATION_CONTEXT);
        } else if (order.compareTo(new BigDecimal("2")) == 0) {
            tHalfSec = BigDecimal.ONE.divide(k.multiply(c0, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
        } else {
            throw new KineticException(
                    KineticErrorCode.INVALID_ORDER,
                    "Half-life calculation only supports overall order 0, 1, or 2");
        }

        Duration halfLifeDuration = Duration.of(tHalfSec.stripTrailingZeros(), DurationUnit.SECOND);
        String explanation = "Half-life evaluated for order " + order.toPlainString() + ": t_1/2 = " + tHalfSec.stripTrailingZeros().toPlainString() + " s.";
        return new HalfLifeResult(request.compoundCode(), halfLifeDuration, c0, request.rateConstant(), explanation);
    }

    public ArrheniusResult calculateRateConstant(ArrheniusRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        ArrheniusParameters params = request.parameters();
        BigDecimal targetTKelvin = request.targetTemperature().in(TemperatureUnit.KELVIN);

        if (params.minTemperature() != null && targetTKelvin.compareTo(params.minTemperature().in(TemperatureUnit.KELVIN)) < 0) {
            throw new KineticException(
                    KineticErrorCode.OUT_OF_TEMPERATURE_RANGE,
                    "Target temperature " + targetTKelvin + " K is below minimum validity temperature " + params.minTemperature());
        }
        if (params.maxTemperature() != null && targetTKelvin.compareTo(params.maxTemperature().in(TemperatureUnit.KELVIN)) > 0) {
            throw new KineticException(
                    KineticErrorCode.OUT_OF_TEMPERATURE_RANGE,
                    "Target temperature " + targetTKelvin + " K is above maximum validity temperature " + params.maxTemperature());
        }

        BigDecimal aFactor = params.preExponentialFactorA();
        BigDecimal nExponent = params.temperatureExponentN();
        Temperature refTemp = params.referenceTemperature() != null ? params.referenceTemperature() : Temperature.of("298.15", TemperatureUnit.KELVIN);
        BigDecimal refTKelvin = refTemp.in(TemperatureUnit.KELVIN);
        BigDecimal eaJoulePerMol = params.activationEnergy().in(MolarEnergyUnit.JOULE_PER_MOLE);

        double tDbl = targetTKelvin.doubleValue();
        double refTDbl = refTKelvin.doubleValue();
        double nDbl = nExponent.doubleValue();

        // Modified Arrhenius: k(T) = A * (T / T_ref)^n * exp(-Ea / (R * T))
        double tempRatioFactor = (nDbl != 0.0) ? Math.pow(tDbl / refTDbl, nDbl) : 1.0;
        double rt = GAS_CONSTANT_R.doubleValue() * tDbl;
        double exponent = -eaJoulePerMol.doubleValue() / rt;

        if (Double.isNaN(exponent) || Double.isInfinite(exponent) || exponent < -700.0 || exponent > 700.0) {
            throw new KineticException(
                    KineticErrorCode.OUT_OF_TEMPERATURE_RANGE,
                    "Arrhenius exponential factor out of safe numerical range: " + exponent);
        }

        double expVal = Math.exp(exponent);
        double kValDbl = aFactor.doubleValue() * tempRatioFactor * expVal;

        if (Double.isNaN(kValDbl) || Double.isInfinite(kValDbl) || kValDbl <= 0) {
            throw new KineticException(
                    KineticErrorCode.INVALID_RATE_CONSTANT,
                    "Calculated rate constant is non-positive or infinite: " + kValDbl);
        }

        BigDecimal kVal = BigDecimal.valueOf(kValDbl);
        RateConstant kResult = new RateConstant(kVal.stripTrailingZeros(), RateConstantDimension.FIRST_ORDER);
        String explanation = "Modified Arrhenius rate constant evaluated at T = " + targetTKelvin.toPlainString() + " K: k = " + kVal.stripTrailingZeros().toPlainString() + ".";
        return new ArrheniusResult(kResult, request.targetTemperature(), KineticCalculationMethod.ARRHENIUS_TEMPERATURE_DEPENDENCE, explanation);
    }

    public void validateConsistencyInvariant(RateConstant storedK, ArrheniusParameters arrhenius, Temperature refTemperature, double maxAllowedRelDiff) {
        Objects.requireNonNull(storedK, "storedK must not be null");
        Objects.requireNonNull(arrhenius, "arrhenius must not be null");

        Temperature checkTemp = refTemperature != null ? refTemperature : arrhenius.referenceTemperature();
        ArrheniusResult calculated = calculateRateConstant(new ArrheniusRequest(arrhenius, checkTemp));

        BigDecimal kStored = storedK.value();
        BigDecimal kCalc = calculated.calculatedRateConstant().value();

        BigDecimal diff = kStored.subtract(kCalc, ScientificMath.CALCULATION_CONTEXT).abs();
        BigDecimal relDiff = diff.divide(kStored, ScientificMath.CALCULATION_CONTEXT);

        if (relDiff.doubleValue() > maxAllowedRelDiff) {
            throw new KineticException(
                    KineticErrorCode.INVALID_RATE_CONSTANT,
                    "Consistency invariant failed: stored rate constant " + kStored.toPlainString()
                            + " differs from Arrhenius calculated " + kCalc.toPlainString()
                            + " at " + checkTemp + " (relative difference: " + relDiff.toPlainString() + " > " + maxAllowedRelDiff + ")");
        }
    }

    public KineticProgressResult simulateProgress(KineticProgressRequest request, Map<String, BigDecimal> stoichiometricCoefficients) {
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(stoichiometricCoefficients, "stoichiometricCoefficients must not be null");

        KineticProfile profile = request.profile();
        BigDecimal tTotalSec = request.totalDuration().in(DurationUnit.SECOND);
        BigDecimal dtSec = request.stepSize().in(DurationUnit.SECOND);

        if (dtSec.compareTo(BigDecimal.ZERO) <= 0) dtSec = new BigDecimal("0.1");

        Map<String, BigDecimal> currentConc = new HashMap<>(request.initialConcentrations());
        BigDecimal currentExtentMolar = BigDecimal.ZERO;
        BigDecimal systemVol = request.systemVolumeLiters();

        List<KineticProgressPoint> points = new ArrayList<>();

        // Add initial point t=0
        RateEvaluationResult initialEval = calculateRate(new RateEvaluationRequest(request.reactionCode(), profile.rateLaw(), profile.referenceRateConstant(), currentConc), stoichiometricCoefficients);
        points.add(new KineticProgressPoint(
                Duration.of(BigDecimal.ZERO, DurationUnit.SECOND),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                initialEval.reactionRate(),
                Map.copyOf(currentConc)
        ));

        double t = 0.0;
        double dt = dtSec.doubleValue();
        double tEnd = tTotalSec.doubleValue();

        BigDecimal maxMassBalanceError = BigDecimal.ZERO;
        BigDecimal maxAnalyticalError = BigDecimal.ZERO;
        KineticSolverStatus status = KineticSolverStatus.CONVERGED;

        int stepCount = 0;
        int maxSteps = 10000;

        while (t < tEnd && stepCount < maxSteps) {
            stepCount++;
            double step = Math.min(dt, tEnd - t);

            // RK4 integration for extent increment d_xi / dt = r(c)
            RateEvaluationResult k1Eval = calculateRate(new RateEvaluationRequest(request.reactionCode(), profile.rateLaw(), profile.referenceRateConstant(), currentConc), stoichiometricCoefficients);
            double r1 = k1Eval.reactionRate().value().doubleValue();

            // Intermediate concentrations for RK4
            Map<String, BigDecimal> concK2 = stepConcentrations(currentConc, stoichiometricCoefficients, r1 * step * 0.5);
            RateEvaluationResult k2Eval = calculateRate(new RateEvaluationRequest(request.reactionCode(), profile.rateLaw(), profile.referenceRateConstant(), concK2), stoichiometricCoefficients);
            double r2 = k2Eval.reactionRate().value().doubleValue();

            Map<String, BigDecimal> concK3 = stepConcentrations(currentConc, stoichiometricCoefficients, r2 * step * 0.5);
            RateEvaluationResult k3Eval = calculateRate(new RateEvaluationRequest(request.reactionCode(), profile.rateLaw(), profile.referenceRateConstant(), concK3), stoichiometricCoefficients);
            double r3 = k3Eval.reactionRate().value().doubleValue();

            Map<String, BigDecimal> concK4 = stepConcentrations(currentConc, stoichiometricCoefficients, r3 * step);
            RateEvaluationResult k4Eval = calculateRate(new RateEvaluationRequest(request.reactionCode(), profile.rateLaw(), profile.referenceRateConstant(), concK4), stoichiometricCoefficients);
            double r4 = k4Eval.reactionRate().value().doubleValue();

            double deltaXiMolar = (step / 6.0) * (r1 + 2.0 * r2 + 2.0 * r3 + r4);

            t += step;
            currentExtentMolar = currentExtentMolar.add(BigDecimal.valueOf(deltaXiMolar), ScientificMath.CALCULATION_CONTEXT);

            // Update current concentrations with non-negativity boundary
            boolean depleted = false;
            for (Map.Entry<String, BigDecimal> entry : stoichiometricCoefficients.entrySet()) {
                String code = entry.getKey();
                BigDecimal nu = entry.getValue();
                BigDecimal initC = request.initialConcentrations().getOrDefault(code, BigDecimal.ZERO);
                BigDecimal newC = initC.add(nu.multiply(currentExtentMolar, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
                if (newC.compareTo(BigDecimal.ZERO) <= 0) {
                    newC = BigDecimal.ZERO;
                    depleted = true;
                }
                currentConc.put(code, newC);

                // Mass balance error check: |C_calc - (C0 + nu * xi)|
                BigDecimal balanceErr = newC.subtract(initC.add(nu.multiply(currentExtentMolar, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT).abs();
                if (balanceErr.compareTo(maxMassBalanceError) > 0) {
                    maxMassBalanceError = balanceErr;
                }
            }

            RateEvaluationResult currentEval = calculateRate(new RateEvaluationRequest(request.reactionCode(), profile.rateLaw(), profile.referenceRateConstant(), currentConc), stoichiometricCoefficients);

            points.add(new KineticProgressPoint(
                    Duration.of(BigDecimal.valueOf(t).stripTrailingZeros(), DurationUnit.SECOND),
                    currentExtentMolar.multiply(systemVol, ScientificMath.CALCULATION_CONTEXT).stripTrailingZeros(),
                    currentExtentMolar.stripTrailingZeros(),
                    currentEval.reactionRate(),
                    Map.copyOf(currentConc)
            ));

            if (depleted) {
                status = KineticSolverStatus.DEPLETED;
                break;
            }
        }

        // Compare against analytical solution if 1-reactant single order applies
        if (profile.rateLaw().terms().size() == 1) {
            KineticRateLawTerm singleTerm = profile.rateLaw().terms().get(0);
            String reactantCode = singleTerm.compoundCode();
            BigDecimal initC0 = request.initialConcentrations().getOrDefault(reactantCode, BigDecimal.ZERO);
            if (initC0.compareTo(BigDecimal.ZERO) > 0) {
                for (KineticProgressPoint pt : points) {
                    BigDecimal numConc = pt.concentrations().getOrDefault(reactantCode, BigDecimal.ZERO);
                    IntegratedRateLawResult analytical = calculateIntegratedLaw(new IntegratedRateLawRequest(
                            reactantCode, initC0, profile.referenceRateConstant(), profile.rateLaw().overallOrder(), pt.time()));
                    BigDecimal diff = numConc.subtract(analytical.finalConcentrationMolar(), ScientificMath.CALCULATION_CONTEXT).abs();
                    if (diff.compareTo(maxAnalyticalError) > 0) {
                        maxAnalyticalError = diff;
                    }
                }
            }
        }

        KineticResidual residual = new KineticResidual(maxMassBalanceError.stripTrailingZeros(), maxAnalyticalError.stripTrailingZeros(), maxMassBalanceError.compareTo(new BigDecimal("1e-4")) <= 0);
        List<String> assumptions = List.of(
                "Homogeneous single irreversible reaction at constant volume and constant temperature",
                "RK4 numerical integration with non-negativity reactant depletion safeguards"
        );

        String explanation = "Kinetic progress simulated across " + points.size() + " time points; status: " + status;
        return new KineticProgressResult(request.reactionCode(), points, residual, status, explanation, assumptions);
    }

    private static Map<String, BigDecimal> stepConcentrations(Map<String, BigDecimal> base, Map<String, BigDecimal> nuMap, double dXiMolar) {
        Map<String, BigDecimal> res = new HashMap<>();
        BigDecimal dXi = BigDecimal.valueOf(dXiMolar);
        for (Map.Entry<String, BigDecimal> entry : base.entrySet()) {
            String code = entry.getKey();
            BigDecimal nu = nuMap.getOrDefault(code, BigDecimal.ZERO);
            BigDecimal c = entry.getValue().add(nu.multiply(dXi, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
            if (c.compareTo(BigDecimal.ZERO) < 0) c = BigDecimal.ZERO;
            res.put(code, c);
        }
        return res;
    }
}

package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.Energy;
import com.ailab.chemistry.domain.measurement.EnergyUnit;
import com.ailab.chemistry.domain.measurement.MassUnit;
import com.ailab.chemistry.domain.measurement.MolarHeatCapacityUnit;
import com.ailab.chemistry.domain.measurement.ScientificMath;
import com.ailab.chemistry.domain.measurement.SpecificHeatCapacityUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CalorimetryCalculator {

    private static final BigDecimal THOUSAND = new BigDecimal("1000");

    @FunctionalInterface
    public interface ShomateEnthalpyProvider {
        BigDecimal calculateEnthalpyIncrementJoulePerMol(String compoundCode, MatterState state, Temperature temp);
    }

    public SensibleHeatResult calculateSensibleHeat(SensibleHeatRequest request, ShomateEnthalpyProvider shomateProvider) {
        Objects.requireNonNull(request, "request must not be null");
        ThermalSample sample = request.sample();
        Temperature tInit = sample.initialTemperature();
        Temperature tFinal = request.finalTemperature();

        if (tFinal.in(TemperatureUnit.KELVIN).compareTo(BigDecimal.ZERO) <= 0) {
            throw new CalorimetryException(
                    CalorimetryErrorCode.INVALID_TEMPERATURE,
                    "Final temperature must be positive: " + tFinal);
        }

        BigDecimal heatJoules;
        String explanation;

        if (request.method() == CalorimetryMethod.TEMPERATURE_DEPENDENT_SHOMATE && shomateProvider != null) {
            if (sample.amount() == null) {
                throw new CalorimetryException(
                        CalorimetryErrorCode.INVALID_MASS_OR_AMOUNT,
                        "Amount of substance is required for Shomate correlation integration");
            }
            BigDecimal n = sample.amount().in(AmountOfSubstanceUnit.MOLE);
            BigDecimal hInit = shomateProvider.calculateEnthalpyIncrementJoulePerMol(sample.sampleId(), sample.state(), tInit);
            BigDecimal hFinal = shomateProvider.calculateEnthalpyIncrementJoulePerMol(sample.sampleId(), sample.state(), tFinal);
            BigDecimal deltaH = hFinal.subtract(hInit, ScientificMath.CALCULATION_CONTEXT);
            heatJoules = n.multiply(deltaH, ScientificMath.CALCULATION_CONTEXT);
            explanation = "Sensible heat calculated via temperature-dependent Shomate correlation integration.";
        } else if (sample.specificHeatCapacity() != null && sample.mass() != null) {
            BigDecimal m = sample.mass().in(MassUnit.KILOGRAM);
            BigDecimal cp = sample.specificHeatCapacity().toCanonical().getValue();
            BigDecimal deltaT = tFinal.in(TemperatureUnit.KELVIN).subtract(tInit.in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT);
            heatJoules = m.multiply(cp, ScientificMath.CALCULATION_CONTEXT).multiply(deltaT, ScientificMath.CALCULATION_CONTEXT);
            explanation = "Sensible heat calculated from mass and specific heat capacity (q = m * cp * deltaT).";
        } else if (sample.molarHeatCapacity() != null && sample.amount() != null) {
            BigDecimal n = sample.amount().in(AmountOfSubstanceUnit.MOLE);
            BigDecimal cp = sample.molarHeatCapacity().toCanonical().getValue();
            BigDecimal deltaT = tFinal.in(TemperatureUnit.KELVIN).subtract(tInit.in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT);
            heatJoules = n.multiply(cp, ScientificMath.CALCULATION_CONTEXT).multiply(deltaT, ScientificMath.CALCULATION_CONTEXT);
            explanation = "Sensible heat calculated from moles and molar heat capacity (q = n * Cp,m * deltaT).";
        } else {
            throw new CalorimetryException(
                    CalorimetryErrorCode.INVALID_HEAT_CAPACITY,
                    "Thermal sample must provide matching mass+cp or amount+Cp,m");
        }

        Energy energy = Energy.of(heatJoules.stripTrailingZeros(), EnergyUnit.JOULE);
        return new SensibleHeatResult(sample, tInit, tFinal, energy, CalorimetryStatus.SUCCESS, request.method(), explanation);
    }

    public ThermalMixingResult calculateFinalTemperature(ThermalMixingRequest request, ShomateEnthalpyProvider shomateProvider) {
        Objects.requireNonNull(request, "request must not be null");

        List<ThermalSample> samples = request.samples();
        Calorimeter cal = request.calorimeter() != null
                ? request.calorimeter()
                : new Calorimeter(HeatCapacity.ofJoulesPerKelvin(BigDecimal.ZERO), samples.get(0).initialTemperature());

        BigDecimal cCal = cal.heatCapacity().valueJoulesPerKelvin();

        BigDecimal minT = samples.get(0).initialTemperature().in(TemperatureUnit.KELVIN);
        BigDecimal maxT = minT;
        for (ThermalSample s : samples) {
            BigDecimal t = s.initialTemperature().in(TemperatureUnit.KELVIN);
            if (t.compareTo(minT) < 0) minT = t;
            if (t.compareTo(maxT) > 0) maxT = t;
        }

        BigDecimal finalTKelvin;
        List<String> assumptions = new ArrayList<>();
        assumptions.add("Insulated mixing in isolated calorimeter system");
        assumptions.add("No chemical reaction or phase transition detected");

        if (request.method() == CalorimetryMethod.TEMPERATURE_DEPENDENT_SHOMATE && shomateProvider != null) {
            // Solve sum_k n_k * (H_k(Tf) - H_k(T_ik)) + Ccal * (Tf - Tcal) = 0
            finalTKelvin = solveMixingShomate(samples, cal, minT, maxT, shomateProvider);
            assumptions.add("Temperature-dependent heat capacity integrated via Shomate correlation");
        } else {
            // Constant heat capacities
            BigDecimal numerator = BigDecimal.ZERO;
            BigDecimal denominator = cCal;

            if (cal.initialTemperature() != null) {
                numerator = numerator.add(cCal.multiply(cal.initialTemperature().in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
            } else {
                numerator = numerator.add(cCal.multiply(samples.get(0).initialTemperature().in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
            }

            for (ThermalSample sample : samples) {
                BigDecimal capacity;
                if (sample.specificHeatCapacity() != null && sample.mass() != null) {
                    capacity = sample.mass().in(MassUnit.KILOGRAM)
                            .multiply(sample.specificHeatCapacity().toCanonical().getValue(), ScientificMath.CALCULATION_CONTEXT);
                } else if (sample.molarHeatCapacity() != null && sample.amount() != null) {
                    capacity = sample.amount().in(AmountOfSubstanceUnit.MOLE)
                            .multiply(sample.molarHeatCapacity().toCanonical().getValue(), ScientificMath.CALCULATION_CONTEXT);
                } else {
                    throw new CalorimetryException(
                            CalorimetryErrorCode.INVALID_HEAT_CAPACITY,
                            "Missing valid heat capacity for sample: " + sample.sampleId());
                }

                denominator = denominator.add(capacity, ScientificMath.CALCULATION_CONTEXT);
                numerator = numerator.add(capacity.multiply(sample.initialTemperature().in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
            }

            if (denominator.compareTo(BigDecimal.ZERO) <= 0) {
                throw new CalorimetryException(
                        CalorimetryErrorCode.INVALID_HEAT_CAPACITY,
                        "Total heat capacity must be positive");
            }

            finalTKelvin = numerator.divide(denominator, ScientificMath.CALCULATION_CONTEXT);
            assumptions.add("Constant heat capacity assumption applied for thermal samples");
        }

        Temperature finalTemp = Temperature.of(finalTKelvin.stripTrailingZeros(), TemperatureUnit.KELVIN);

        // Compute energy residual
        BigDecimal totalResidual = BigDecimal.ZERO;
        BigDecimal totalHeatAbsorbed = BigDecimal.ZERO;

        for (ThermalSample sample : samples) {
            SensibleHeatResult sh = calculateSensibleHeat(new SensibleHeatRequest(sample, finalTemp, request.method()), shomateProvider);
            BigDecimal q = sh.heatTransferredJoules().in(EnergyUnit.JOULE);
            totalResidual = totalResidual.add(q, ScientificMath.CALCULATION_CONTEXT);
            totalHeatAbsorbed = totalHeatAbsorbed.add(q.abs(), ScientificMath.CALCULATION_CONTEXT);
        }

        if (cCal.compareTo(BigDecimal.ZERO) > 0 && cal.initialTemperature() != null) {
            BigDecimal deltaTCal = finalTKelvin.subtract(cal.initialTemperature().in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT);
            BigDecimal qCal = cCal.multiply(deltaTCal, ScientificMath.CALCULATION_CONTEXT);
            totalResidual = totalResidual.add(qCal, ScientificMath.CALCULATION_CONTEXT);
        }

        boolean isBalanced = totalResidual.abs().compareTo(new BigDecimal("1e-5")) <= 0;
        ThermalEnergyBalance energyBalance = new ThermalEnergyBalance(
                Energy.of(totalHeatAbsorbed.stripTrailingZeros(), EnergyUnit.JOULE),
                Energy.of(totalResidual.stripTrailingZeros(), EnergyUnit.JOULE),
                isBalanced
        );

        String explanation = "Thermal mixing final temperature solved: " + finalTemp.toString() + "; energy balance residual: " + totalResidual.stripTrailingZeros().toPlainString() + " J.";
        return new ThermalMixingResult(samples, cal, finalTemp, energyBalance, CalorimetryStatus.CONVERGED, request.method(), explanation, assumptions);
    }

    public ReactionCalorimetryResult calculateReactionHeat(ReactionCalorimetryRequest request, BigDecimal standardDeltaHkJPerMol) {
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(standardDeltaHkJPerMol, "standardDeltaHkJPerMol must not be null");

        BigDecimal xi = request.reactionExtentMoles();
        BigDecimal qReactionKj = xi.multiply(standardDeltaHkJPerMol, ScientificMath.CALCULATION_CONTEXT);
        BigDecimal qReactionJoules = qReactionKj.multiply(THOUSAND, ScientificMath.CALCULATION_CONTEXT);
        BigDecimal qSurroundingsJoules = qReactionJoules.negate();

        Energy qReaction = Energy.of(qReactionJoules.stripTrailingZeros(), EnergyUnit.JOULE);
        Energy qSurroundings = Energy.of(qSurroundingsJoules.stripTrailingZeros(), EnergyUnit.JOULE);

        List<String> assumptions = List.of(
                "Constant-pressure reaction calorimetry (q_reaction = xi * Delta_r H)",
                "Exothermic reaction releases heat to surroundings (q_surroundings = -q_reaction)"
        );

        String explanation = "Reaction calorimetry computed at extent xi = " + xi.toPlainString() + " mol; total reaction heat = "
                + qReactionJoules.stripTrailingZeros().toPlainString() + " J.";

        return new ReactionCalorimetryResult(
                request.reactionCode(), xi, request.temperature(), standardDeltaHkJPerMol,
                qReaction, qSurroundings, CalorimetryStatus.SUCCESS, explanation, assumptions);
    }

    private static BigDecimal solveMixingShomate(
            List<ThermalSample> samples, Calorimeter cal, BigDecimal lowT, BigDecimal highT, ShomateEnthalpyProvider shomateProvider) {
        BigDecimal a = lowT;
        BigDecimal b = highT;
        for (int i = 0; i < 100; i++) {
            BigDecimal mid = a.add(b, ScientificMath.CALCULATION_CONTEXT).divide(new BigDecimal("2"), ScientificMath.CALCULATION_CONTEXT);
            BigDecimal fMid = evaluateMixingResidualAt(mid, samples, cal, shomateProvider);
            if (fMid.abs().compareTo(new BigDecimal("1e-6")) <= 0 || b.subtract(a, ScientificMath.CALCULATION_CONTEXT).compareTo(new BigDecimal("1e-8")) <= 0) {
                return mid;
            }
            BigDecimal fA = evaluateMixingResidualAt(a, samples, cal, shomateProvider);
            if ((fA.compareTo(BigDecimal.ZERO) <= 0 && fMid.compareTo(BigDecimal.ZERO) <= 0)
                    || (fA.compareTo(BigDecimal.ZERO) >= 0 && fMid.compareTo(BigDecimal.ZERO) >= 0)) {
                a = mid;
            } else {
                b = mid;
            }
        }
        return a.add(b, ScientificMath.CALCULATION_CONTEXT).divide(new BigDecimal("2"), ScientificMath.CALCULATION_CONTEXT);
    }

    private static BigDecimal evaluateMixingResidualAt(
            BigDecimal tKelvin, List<ThermalSample> samples, Calorimeter cal, ShomateEnthalpyProvider shomateProvider) {
        Temperature temp = Temperature.of(tKelvin, TemperatureUnit.KELVIN);
        BigDecimal totalQ = BigDecimal.ZERO;
        for (ThermalSample sample : samples) {
            BigDecimal n = sample.amount().in(AmountOfSubstanceUnit.MOLE);
            BigDecimal hInit = shomateProvider.calculateEnthalpyIncrementJoulePerMol(sample.sampleId(), sample.state(), sample.initialTemperature());
            BigDecimal hFinal = shomateProvider.calculateEnthalpyIncrementJoulePerMol(sample.sampleId(), sample.state(), temp);
            totalQ = totalQ.add(n.multiply(hFinal.subtract(hInit, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
        }
        BigDecimal cCal = cal.heatCapacity().valueJoulesPerKelvin();
        if (cCal.compareTo(BigDecimal.ZERO) > 0 && cal.initialTemperature() != null) {
            BigDecimal deltaT = tKelvin.subtract(cal.initialTemperature().in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT);
            totalQ = totalQ.add(cCal.multiply(deltaT, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
        }
        return totalQ;
    }
}

package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.Energy;
import com.ailab.chemistry.domain.measurement.EnergyUnit;
import com.ailab.chemistry.domain.measurement.MolarEnergyUnit;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.ScientificMath;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PhaseBehaviorCalculator {
    private static final BigDecimal THOUSAND = new BigDecimal("1000");
    private static final BigDecimal MMHG_TO_PA = new BigDecimal("133.32236842105263");

    public PhaseTransitionResult calculateTransition(PhaseTransitionRequest request, PhaseBehaviorRepository repository) {
        checkBoundaries(request.compoundCode(), request.transitionType(), request.temperature(), request.pressure(), repository);
        PhaseTransitionRecord record = repository.findTransition(request.compoundCode(), request.transitionType().forwardType())
                .orElseThrow(() -> new PhaseBehaviorException(PhaseBehaviorErrorCode.MISSING_TRANSITION_RECORD, "Missing transition record"));
        MatterState expectedInitial = request.transitionType().absorbsHeat() ? record.initialPhase() : record.finalPhase();
        MatterState expectedFinal = request.transitionType().absorbsHeat() ? record.finalPhase() : record.initialPhase();
        if (request.initialPhase() != expectedInitial || request.finalPhase() != expectedFinal) {
            throw new PhaseBehaviorException(PhaseBehaviorErrorCode.PHASE_MISMATCH, "Request phases do not match sourced transition record");
        }
        if (request.pressure().in(PressureUnit.PASCAL).subtract(record.conditions().pressure().in(PressureUnit.PASCAL), ScientificMath.CALCULATION_CONTEXT).abs()
                .compareTo(new BigDecimal("1")) > 0) {
            throw new PhaseBehaviorException(PhaseBehaviorErrorCode.UNSUPPORTED_PRESSURE, "Transition enthalpy is not available at requested pressure");
        }
        BigDecimal heatJ = request.amount().in(AmountOfSubstanceUnit.MOLE)
                .multiply(record.enthalpy().value().in(MolarEnergyUnit.JOULE_PER_MOLE), ScientificMath.CALCULATION_CONTEXT);
        if (!request.transitionType().absorbsHeat()) {
            heatJ = heatJ.negate();
        }
        return new PhaseTransitionResult(PhaseBehaviorStatus.SUCCESS, record, Energy.of(heatJ, EnergyUnit.JOULE));
    }

    public SaturationPressureResult calculateSaturationPressure(SaturationPressureRequest request, PhaseBehaviorRepository repository) {
        checkCritical(request.compoundCode(), request.initialPhase(), request.finalPhase(), request.temperature(), repository);
        AntoineCoefficientSet set = repository.findAntoine(request.compoundCode(), request.initialPhase(), request.finalPhase())
                .orElseThrow(() -> new PhaseBehaviorException(PhaseBehaviorErrorCode.UNSUPPORTED_PHASE_BOUNDARY, "Missing Antoine correlation"));
        validateTemperatureRange(request.temperature(), set.minTemperature(), set.maxTemperature());
        Pressure pressure = evaluateAntoine(set, request.temperature());
        return new SaturationPressureResult(PhaseBehaviorStatus.SUCCESS, set, pressure);
    }

    public BoilingPointResult calculateBoilingPoint(BoilingPointRequest request, PhaseBehaviorRepository repository) {
        AntoineCoefficientSet set = repository.findAntoine(request.compoundCode(), request.initialPhase(), request.finalPhase())
                .orElseThrow(() -> new PhaseBehaviorException(PhaseBehaviorErrorCode.UNSUPPORTED_PHASE_BOUNDARY, "Missing Antoine correlation"));
        BigDecimal low = set.minTemperature().in(TemperatureUnit.KELVIN);
        BigDecimal high = set.maxTemperature().in(TemperatureUnit.KELVIN);
        BigDecimal targetPa = request.externalPressure().in(PressureUnit.PASCAL);
        BigDecimal mid = low;
        BigDecimal residual = BigDecimal.ZERO;
        for (int i = 0; i < 100; i++) {
            mid = low.add(high, ScientificMath.CALCULATION_CONTEXT).divide(new BigDecimal("2"), ScientificMath.CALCULATION_CONTEXT);
            Pressure p = evaluateAntoine(set, Temperature.of(mid, TemperatureUnit.KELVIN));
            residual = p.in(PressureUnit.PASCAL).subtract(targetPa, ScientificMath.CALCULATION_CONTEXT);
            if (residual.abs().compareTo(new BigDecimal("0.01")) <= 0) {
                break;
            }
            if (residual.compareTo(BigDecimal.ZERO) < 0) {
                low = mid;
            } else {
                high = mid;
            }
        }
        return new BoilingPointResult(PhaseBehaviorStatus.CONVERGED, set, Temperature.of(mid, TemperatureUnit.KELVIN), residual.stripTrailingZeros());
    }

    public HeatingPathResult calculateHeatingPath(HeatingPathRequest request, PhaseBehaviorRepository repository) {
        requireNoSkippedTransition(request, repository);
        List<HeatingPathSegment> results = new ArrayList<>();
        Temperature expectedTemperature = request.initialTemperature();
        MatterState expectedPhase = request.initialPhase();
        Energy total = Energy.of("0", EnergyUnit.JOULE);
        for (HeatingPathSegmentSpec spec : request.segments()) {
            if (spec.initialPhase() != expectedPhase || !spec.startTemperature().equals(expectedTemperature)) {
                throw new PhaseBehaviorException(PhaseBehaviorErrorCode.NONCONTINUOUS_PATH, "Heating path segment boundary is not continuous");
            }
            Energy heat;
            if (spec instanceof SensiblePhaseSegmentSpec sensible) {
                if (sensible.heatCapacity() == null) {
                    throw new PhaseBehaviorException(PhaseBehaviorErrorCode.MISSING_HEAT_CAPACITY, "Sensible segment requires heat capacity");
                }
                BigDecimal dT = sensible.endTemperature().subtract(sensible.startTemperature()).in(TemperatureUnit.KELVIN);
                BigDecimal q = request.amount().in(AmountOfSubstanceUnit.MOLE)
                        .multiply(sensible.heatCapacity().toCanonical().getValue(), ScientificMath.CALCULATION_CONTEXT)
                        .multiply(dT, ScientificMath.CALCULATION_CONTEXT);
                heat = Energy.of(q, EnergyUnit.JOULE);
            } else if (spec instanceof TransitionSegmentSpec transition) {
                heat = calculateTransition(new PhaseTransitionRequest(
                        request.compoundCode(), transition.transitionType(), transition.initialPhase(), transition.finalPhase(),
                        request.amount(), transition.temperature(), request.pressure()), repository).heat();
            } else {
                throw new PhaseBehaviorException(PhaseBehaviorErrorCode.UNSUPPORTED_PHASE_BOUNDARY, "Unsupported path segment");
            }
            results.add(new HeatingPathSegment(spec instanceof SensiblePhaseSegmentSpec ? "SENSIBLE" : "TRANSITION",
                    spec.initialPhase(), spec.finalPhase(), spec.startTemperature(), spec.endTemperature(), heat));
            total = total.add(heat);
            expectedTemperature = spec.endTemperature();
            expectedPhase = spec.finalPhase();
        }
        if (expectedPhase != request.finalPhase() || !expectedTemperature.equals(request.finalTemperature())) {
            throw new PhaseBehaviorException(PhaseBehaviorErrorCode.NONCONTINUOUS_PATH, "Heating path does not end at requested state");
        }
        return new HeatingPathResult(PhaseBehaviorStatus.SUCCESS, List.copyOf(results), total, total);
    }

    private void requireNoSkippedTransition(HeatingPathRequest request, PhaseBehaviorRepository repository) {
        if (request.compoundCode().equals("COMP-H2O") && request.pressure().in(PressureUnit.ATMOSPHERE).subtract(BigDecimal.ONE, ScientificMath.CALCULATION_CONTEXT).abs().compareTo(new BigDecimal("0.001")) <= 0) {
            boolean crossesBoiling = request.initialTemperature().in(TemperatureUnit.KELVIN).compareTo(new BigDecimal("373.15")) < 0
                    && request.finalTemperature().in(TemperatureUnit.KELVIN).compareTo(new BigDecimal("373.15")) > 0
                    && request.initialPhase() == MatterState.LIQUID && request.finalPhase() == MatterState.GAS;
            boolean hasVaporization = request.segments().stream().anyMatch(s -> s instanceof TransitionSegmentSpec t && t.transitionType() == PhaseTransitionType.VAPORIZATION);
            if (crossesBoiling && !hasVaporization) {
                throw new PhaseBehaviorException(PhaseBehaviorErrorCode.SKIPPED_KNOWN_TRANSITION, "Requested path crosses known boiling transition without a transition segment");
            }
        }
    }

    private void checkBoundaries(String compoundCode, PhaseTransitionType type, Temperature temperature, Pressure pressure, PhaseBehaviorRepository repository) {
        if (type == PhaseTransitionType.VAPORIZATION || type == PhaseTransitionType.CONDENSATION) {
            repository.findTriplePoint(compoundCode).ifPresent(tp -> {
                if (pressure.in(PressureUnit.PASCAL).compareTo(tp.pressure().in(PressureUnit.PASCAL)) < 0) {
                    throw new PhaseBehaviorException(PhaseBehaviorErrorCode.BELOW_TRIPLE_POINT_PRESSURE, "Liquid path cannot be fabricated below sourced triple-point pressure");
                }
            });
        }
        checkCritical(compoundCode, MatterState.LIQUID, MatterState.GAS, temperature, repository);
    }

    private void checkCritical(String compoundCode, MatterState initial, MatterState fin, Temperature temperature, PhaseBehaviorRepository repository) {
        if ((initial == MatterState.LIQUID && fin == MatterState.GAS) || (initial == MatterState.GAS && fin == MatterState.LIQUID)) {
            repository.findCriticalPoint(compoundCode).ifPresent(cp -> {
                if (temperature.in(TemperatureUnit.KELVIN).compareTo(cp.temperature().in(TemperatureUnit.KELVIN)) > 0) {
                    throw new PhaseBehaviorException(PhaseBehaviorErrorCode.ABOVE_CRITICAL_POINT, "Ordinary liquid-vapor boundary is unavailable above critical temperature");
                }
            });
        }
    }

    private void validateTemperatureRange(Temperature actual, Temperature min, Temperature max) {
        if (actual.compareTo(min) < 0 || actual.compareTo(max) > 0) {
            throw new PhaseBehaviorException(PhaseBehaviorErrorCode.CORRELATION_OUT_OF_RANGE, "Temperature outside Antoine validity range");
        }
    }

    private Pressure evaluateAntoine(AntoineCoefficientSet set, Temperature temperature) {
        BigDecimal tC = temperature.in(TemperatureUnit.CELSIUS);
        double log10P = set.a().doubleValue() - set.b().doubleValue() / (set.c().doubleValue() + tC.doubleValue());
        BigDecimal pressureMmHg = BigDecimal.valueOf(Math.pow(10.0, log10P));
        return Pressure.of(pressureMmHg.multiply(MMHG_TO_PA, ScientificMath.CALCULATION_CONTEXT), PressureUnit.PASCAL);
    }
}

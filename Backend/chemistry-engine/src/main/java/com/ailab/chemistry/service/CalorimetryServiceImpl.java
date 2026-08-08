package com.ailab.chemistry.service;

import com.ailab.chemistry.api.CalorimetryService;
import com.ailab.chemistry.api.ReactionCatalogService;
import com.ailab.chemistry.api.ReactionThermodynamicsService;
import com.ailab.chemistry.api.TemperatureDependentThermodynamicsService;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Energy;
import com.ailab.chemistry.domain.measurement.EnergyUnit;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.ScientificMath;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.reaction.ReactionErrorCode;
import com.ailab.chemistry.domain.reaction.ReactionException;
import com.ailab.chemistry.domain.thermodynamics.AdiabaticTemperatureRequest;
import com.ailab.chemistry.domain.thermodynamics.AdiabaticTemperatureResult;
import com.ailab.chemistry.domain.thermodynamics.CalorimetryCalculator;
import com.ailab.chemistry.domain.thermodynamics.CalorimetryErrorCode;
import com.ailab.chemistry.domain.thermodynamics.CalorimetryException;
import com.ailab.chemistry.domain.thermodynamics.CalorimetryStatus;
import com.ailab.chemistry.domain.thermodynamics.InitialParticipantAmount;
import com.ailab.chemistry.domain.thermodynamics.ReactionCalorimetryRequest;
import com.ailab.chemistry.domain.thermodynamics.ReactionCalorimetryResult;
import com.ailab.chemistry.domain.thermodynamics.ReactionThermodynamicProperty;
import com.ailab.chemistry.domain.thermodynamics.SensibleHeatRequest;
import com.ailab.chemistry.domain.thermodynamics.SensibleHeatResult;
import com.ailab.chemistry.domain.thermodynamics.StandardStateConvention;
import com.ailab.chemistry.domain.thermodynamics.TemperatureCorrectionStatus;
import com.ailab.chemistry.domain.thermodynamics.TemperatureDependentPropertyResult;
import com.ailab.chemistry.domain.thermodynamics.ThermalEnergyBalance;
import com.ailab.chemistry.domain.thermodynamics.ThermalMixingRequest;
import com.ailab.chemistry.domain.thermodynamics.ThermalMixingResult;
import com.ailab.chemistry.domain.thermodynamics.ThermodynamicReferenceConditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class CalorimetryServiceImpl implements CalorimetryService {

    private static final Temperature REFERENCE_TEMPERATURE = Temperature.of("298.15", TemperatureUnit.KELVIN);
    private static final Pressure DEFAULT_STANDARD_PRESSURE = Pressure.of("1.000", PressureUnit.BAR);
    private static final BigDecimal THOUSAND = new BigDecimal("1000");

    private final ReactionCatalogService reactionCatalogService;
    private final ReactionThermodynamicsService reactionThermodynamicsService;
    private final TemperatureDependentThermodynamicsService temperatureDependentThermodynamicsService;
    private final CalorimetryCalculator calculator = new CalorimetryCalculator();

    @Autowired
    public CalorimetryServiceImpl(
            ReactionCatalogService reactionCatalogService,
            ReactionThermodynamicsService reactionThermodynamicsService,
            TemperatureDependentThermodynamicsService temperatureDependentThermodynamicsService) {
        this.reactionCatalogService = Objects.requireNonNull(reactionCatalogService, "reactionCatalogService must not be null");
        this.reactionThermodynamicsService = Objects.requireNonNull(reactionThermodynamicsService, "reactionThermodynamicsService must not be null");
        this.temperatureDependentThermodynamicsService = Objects.requireNonNull(temperatureDependentThermodynamicsService, "temperatureDependentThermodynamicsService must not be null");
    }

    @Override
    public SensibleHeatResult calculateSensibleHeat(SensibleHeatRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        return calculator.calculateSensibleHeat(request, this::getShomateEnthalpyJoulePerMol);
    }

    @Override
    public ThermalMixingResult calculateFinalTemperature(ThermalMixingRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        return calculator.calculateFinalTemperature(request, this::getShomateEnthalpyJoulePerMol);
    }

    @Override
    public ReactionCalorimetryResult calculateReactionHeat(ReactionCalorimetryRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        try {
            reactionCatalogService.getByCode(request.reactionCode());
        } catch (ReactionException ex) {
            if (ex.getErrorCode() == ReactionErrorCode.REACTION_NOT_FOUND) {
                throw new CalorimetryException(
                        CalorimetryErrorCode.UNKNOWN_REACTION,
                        "Unknown reaction code: " + request.reactionCode());
            }
            throw ex;
        }

        Pressure stdPress = request.pressure() != null ? request.pressure() : DEFAULT_STANDARD_PRESSURE;
        BigDecimal deltaHkJPerMol;

        if (request.temperature().equals(REFERENCE_TEMPERATURE)) {
            var reactionRes = reactionThermodynamicsService.calculate(
                    request.reactionCode(),
                    new ThermodynamicReferenceConditions(request.temperature(), stdPress, MatterState.GAS, StandardStateConvention.IDEAL_GAS_STANDARD_STATE),
                    request.stateOverrides());
            deltaHkJPerMol = reactionRes.property(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY).value();
        } else {
            var reactionCorr = temperatureDependentThermodynamicsService.calculateReaction(
                    request.reactionCode(), request.temperature(), stdPress, request.stateOverrides());
            if (reactionCorr.status() != TemperatureCorrectionStatus.CALCULABLE) {
                return new ReactionCalorimetryResult(
                        request.reactionCode(),
                        request.reactionExtentMoles(),
                        request.temperature(),
                        null,
                        null,
                        null,
                        CalorimetryStatus.INCOMPLETE_COVERAGE,
                        "Missing heat-capacity correlation coverage for reaction at " + request.temperature(),
                        List.of("Incomplete correlation coverage")
                );
            }
            deltaHkJPerMol = reactionCorr.reactionProperties().get(ReactionThermodynamicProperty.STANDARD_REACTION_ENTHALPY).value();
        }

        return calculator.calculateReactionHeat(request, deltaHkJPerMol);
    }

    @Override
    public AdiabaticTemperatureResult calculateAdiabaticFinalTemperature(AdiabaticTemperatureRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        // 1. Calculate reaction heat at initial temperature
        ReactionCalorimetryResult rxnHeatRes = calculateReactionHeat(new ReactionCalorimetryRequest(
                request.reactionCode(), request.reactionExtentMoles(), request.initialTemperature(), request.pressure(), request.stateOverrides()));

        if (rxnHeatRes.status() != CalorimetryStatus.SUCCESS || rxnHeatRes.totalReactionHeatJoules() == null) {
            return new AdiabaticTemperatureResult(
                    request.reactionCode(), request.reactionExtentMoles(), request.initialTemperature(), null, null, null,
                    CalorimetryStatus.INCOMPLETE_COVERAGE, "Reaction enthalpy unavailable at initial temperature", List.of()
            );
        }

        BigDecimal qRxnJoules = rxnHeatRes.totalReactionHeatJoules().in(EnergyUnit.JOULE);

        // 2. Solve for final temperature Tf: q_rxn + sum_k n_k * (H_k(Tf) - H_k(Ti)) + Ccal*(Tf-Ti) = 0
        BigDecimal tInitKelvin = request.initialTemperature().in(TemperatureUnit.KELVIN);
        BigDecimal cCal = request.calorimeter() != null ? request.calorimeter().heatCapacity().valueJoulesPerKelvin() : BigDecimal.ZERO;

        BigDecimal lowT = qRxnJoules.compareTo(BigDecimal.ZERO) <= 0 ? tInitKelvin : new BigDecimal("100.0");
        BigDecimal highT = qRxnJoules.compareTo(BigDecimal.ZERO) <= 0 ? tInitKelvin.add(new BigDecimal("2000.0"), ScientificMath.CALCULATION_CONTEXT) : tInitKelvin;

        BigDecimal a = lowT;
        BigDecimal b = highT;
        BigDecimal finalTKelvin = null;

        for (int i = 0; i < 100; i++) {
            BigDecimal mid = a.add(b, ScientificMath.CALCULATION_CONTEXT).divide(new BigDecimal("2"), ScientificMath.CALCULATION_CONTEXT);
            BigDecimal fMid = evaluateAdiabaticResidualAt(mid, request, qRxnJoules, cCal, tInitKelvin);
            if (fMid == null) {
                // Mid temperature exceeded correlation range; reduce upper search bound
                b = mid;
                continue;
            }
            if (fMid.abs().compareTo(new BigDecimal("1e-4")) <= 0 || b.subtract(a, ScientificMath.CALCULATION_CONTEXT).compareTo(new BigDecimal("1e-6")) <= 0) {
                finalTKelvin = mid;
                break;
            }
            BigDecimal fA = evaluateAdiabaticResidualAt(a, request, qRxnJoules, cCal, tInitKelvin);
            if (fA == null) {
                return new AdiabaticTemperatureResult(
                        request.reactionCode(), request.reactionExtentMoles(), request.initialTemperature(), null, null, null,
                        CalorimetryStatus.CORRELATION_OUT_OF_RANGE,
                        "Correlation range exceeded during adiabatic root search", List.of()
                );
            }
            if ((fA.compareTo(BigDecimal.ZERO) <= 0 && fMid.compareTo(BigDecimal.ZERO) <= 0)
                    || (fA.compareTo(BigDecimal.ZERO) >= 0 && fMid.compareTo(BigDecimal.ZERO) >= 0)) {
                a = mid;
            } else {
                b = mid;
            }
        }

        if (finalTKelvin == null) {
            finalTKelvin = a.add(b, ScientificMath.CALCULATION_CONTEXT).divide(new BigDecimal("2"), ScientificMath.CALCULATION_CONTEXT);
        }

        Temperature finalTemp = Temperature.of(finalTKelvin.stripTrailingZeros(), TemperatureUnit.KELVIN);
        BigDecimal residual = evaluateAdiabaticResidualAt(finalTKelvin, request, qRxnJoules, cCal, tInitKelvin);

        if (residual == null) {
            return new AdiabaticTemperatureResult(
                    request.reactionCode(), request.reactionExtentMoles(), request.initialTemperature(), null, null, null,
                    CalorimetryStatus.CORRELATION_OUT_OF_RANGE,
                    "Adiabatic final temperature search went outside correlation validity range or required phase change",
                    List.of("No extrapolation permitted outside valid temperature correlation range")
            );
        }

        ThermalEnergyBalance energyBalance = new ThermalEnergyBalance(
                rxnHeatRes.totalReactionHeatJoules(),
                Energy.of(residual.abs().stripTrailingZeros(), EnergyUnit.JOULE),
                residual.abs().compareTo(new BigDecimal("1e-3")) <= 0
        );

        List<String> assumptions = List.of(
                "Constant-pressure adiabatic reaction calorimetry",
                "Temperature-dependent heat capacity integrated strictly within Shomate correlation range",
                "Phase stability remains unevaluated; no latent heat or phase transition occurred"
        );

        String explanation = "Adiabatic final temperature solved: " + finalTemp.toString() + "; energy residual: "
                + residual.stripTrailingZeros().toPlainString() + " J.";

        return new AdiabaticTemperatureResult(
                request.reactionCode(), request.reactionExtentMoles(), request.initialTemperature(),
                finalTemp, rxnHeatRes.totalReactionHeatJoules(), energyBalance, CalorimetryStatus.CONVERGED,
                explanation, assumptions);
    }

    private BigDecimal evaluateAdiabaticResidualAt(
            BigDecimal tKelvin, AdiabaticTemperatureRequest request, BigDecimal qRxnJoules, BigDecimal cCal, BigDecimal tInitKelvin) {
        try {
            Temperature tTarget = Temperature.of(tKelvin, TemperatureUnit.KELVIN);
            BigDecimal totalQSensible = BigDecimal.ZERO;

            for (InitialParticipantAmount participant : request.initialInventory()) {
                BigDecimal n = participant.moles();
                if (n.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal hInit = getShomateEnthalpyJoulePerMol(participant.compoundCode(), participant.state(), request.initialTemperature());
                    BigDecimal hFinal = getShomateEnthalpyJoulePerMol(participant.compoundCode(), participant.state(), tTarget);
                    totalQSensible = totalQSensible.add(n.multiply(hFinal.subtract(hInit, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
                }
            }

            if (cCal.compareTo(BigDecimal.ZERO) > 0) {
                totalQSensible = totalQSensible.add(cCal.multiply(tKelvin.subtract(tInitKelvin, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
            }

            return qRxnJoules.add(totalQSensible, ScientificMath.CALCULATION_CONTEXT);
        } catch (Exception ex) {
            return null;
        }
    }

    private BigDecimal getShomateEnthalpyJoulePerMol(String compoundCode, MatterState state, Temperature temp) {
        TemperatureDependentPropertyResult corr = temperatureDependentThermodynamicsService.calculateSpeciesProperties(compoundCode, state, temp);
        if (corr.status() != TemperatureCorrectionStatus.CALCULABLE || corr.enthalpyIncrementKjPerMol() == null) {
            throw new CalorimetryException(
                    CalorimetryErrorCode.MISSING_CORRELATION,
                    "Shomate correlation is unavailable or out of range for " + compoundCode + "|" + state + " at " + temp);
        }
        return corr.enthalpyIncrementKjPerMol().multiply(THOUSAND, ScientificMath.CALCULATION_CONTEXT);
    }
}

package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.equation.RationalNumber;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.ScientificMath;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.measurement.VolumeUnit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class EquilibriumCompositionCalculator {
    public static final BigDecimal R_BAR_L_PER_MOL_K = new BigDecimal("0.0831446261815324");
    private static final BigDecimal RESIDUAL_TOLERANCE = new BigDecimal("1e-7");
    private static final BigDecimal MIN_ACTIVITY = new BigDecimal("1e-20");
    private static final int MAX_ITERATIONS = 100;

    @FunctionalInterface
    public interface DaviesActivityProvider {
        Map<String, BigDecimal> calculateActivityCoefficients(
                List<InitialParticipantAmount> activeSpecies,
                List<InitialParticipantAmount> spectatorIons,
                BigDecimal volumeLiters,
                Temperature temperature);
    }

    public EquilibriumCompositionResult solve(
            ReactionThermodynamicVector vector,
            StandardEquilibriumConstant standardConstant,
            EquilibriumCompositionRequest request,
            TemperatureCorrectionCoverage coverage,
            DaviesActivityProvider daviesProvider) {

        Objects.requireNonNull(vector, "vector must not be null");
        Objects.requireNonNull(standardConstant, "standardConstant must not be null");
        Objects.requireNonNull(request, "request must not be null");

        validateRequest(vector, request);

        BigDecimal lnK = standardConstant.lnK();
        Temperature temperature = request.temperature();

        // 1. Build initial amounts map and validate participant coverage
        Map<String, InitialParticipantAmount> initialMap = new HashMap<>();
        for (InitialParticipantAmount amount : request.initialAmounts()) {
            initialMap.put(key(amount.compoundCode(), amount.state()), amount);
        }

        for (ReactionThermodynamicVectorTerm term : vector.terms()) {
            if (!initialMap.containsKey(term.key())) {
                throw new EquilibriumCompositionException(
                        EquilibriumCompositionErrorCode.MISSING_PARTICIPANT_AMOUNT,
                        "Missing initial participant amount for: " + term.key());
            }
        }

        // 2. Derive ExtentBounds
        ExtentBounds bounds = deriveExtentBounds(vector, initialMap);
        BigDecimal xiMin = bounds.minExtent();
        BigDecimal xiMax = bounds.maxExtent();

        // 3. Solve for equilibrium extent xi
        BigDecimal xi;
        EquilibriumCompositionStatus status;
        boolean atForwardBound = false;
        boolean atReverseBound = false;

        BigDecimal delta = xiMax.subtract(xiMin, ScientificMath.CALCULATION_CONTEXT);
        if (delta.compareTo(BigDecimal.ZERO) == 0) {
            xi = xiMin;
            status = EquilibriumCompositionStatus.INITIAL_STATE_AT_EQUILIBRIUM;
        } else {
            // Check initial state (xi = 0)
            BigDecimal fZero = evaluateResidualAt(BigDecimal.ZERO, vector, initialMap, request, lnK, daviesProvider);
            if (fZero != null && fZero.abs().compareTo(RESIDUAL_TOLERANCE) <= 0) {
                xi = BigDecimal.ZERO;
                status = EquilibriumCompositionStatus.INITIAL_STATE_AT_EQUILIBRIUM;
            } else {
                BigDecimal eps = delta.multiply(new BigDecimal("1e-12"), ScientificMath.CALCULATION_CONTEXT);
                if (eps.compareTo(new BigDecimal("1e-14")) < 0) {
                    eps = new BigDecimal("1e-14");
                }
                BigDecimal a = xiMin.add(eps, ScientificMath.CALCULATION_CONTEXT);
                BigDecimal b = xiMax.subtract(eps, ScientificMath.CALCULATION_CONTEXT);
                if (a.compareTo(b) > 0) {
                    a = xiMin.add(xiMax, ScientificMath.CALCULATION_CONTEXT).divide(new BigDecimal("2"), ScientificMath.CALCULATION_CONTEXT);
                    b = a;
                }

                BigDecimal fA = evaluateResidualAt(a, vector, initialMap, request, lnK, daviesProvider);
                BigDecimal fB = evaluateResidualAt(b, vector, initialMap, request, lnK, daviesProvider);

                if (fB != null && fB.compareTo(BigDecimal.ZERO) <= 0) {
                    xi = xiMax;
                    status = EquilibriumCompositionStatus.BOUNDED_AT_FORWARD_LIMIT;
                    atForwardBound = true;
                } else if (fA != null && fA.compareTo(BigDecimal.ZERO) >= 0) {
                    xi = xiMin;
                    status = EquilibriumCompositionStatus.BOUNDED_AT_REVERSE_LIMIT;
                    atReverseBound = true;
                } else {
                    // Root search between a and b
                    xi = solveRoot(a, b, fA, fB, vector, initialMap, request, lnK, daviesProvider);
                    status = EquilibriumCompositionStatus.CONVERGED;
                }
            }
        }

        EquilibriumExtent extent = new EquilibriumExtent(xi, bounds, atForwardBound, atReverseBound);

        // 4. Build final composition and activities
        List<EquilibriumParticipantState> finalComposition = new ArrayList<>();
        List<ParticipantActivity> activities = new ArrayList<>();
        BigDecimal maxMassBalanceError = BigDecimal.ZERO;

        BigDecimal volumeLiters = request.volume() != null ? request.volume().in(VolumeUnit.LITER) : null;
        BigDecimal totalPressureBar = request.totalPressure() != null ? request.totalPressure().in(PressureUnit.BAR) : null;
        BigDecimal inertMoles = request.inertGasMoles() != null ? request.inertGasMoles() : BigDecimal.ZERO;

        BigDecimal totalGasMoles = calculateTotalGasMoles(vector, initialMap, xi, inertMoles);

        Map<String, BigDecimal> daviesGammaMap = Map.of();
        if (request.method() == EquilibriumCompositionMethod.AQUEOUS_DAVIES && daviesProvider != null && volumeLiters != null) {
            List<InitialParticipantAmount> currentAmounts = new ArrayList<>();
            for (ReactionThermodynamicVectorTerm term : vector.terms()) {
                InitialParticipantAmount init = initialMap.get(term.key());
                BigDecimal nu = toBigDecimal(term.coefficient());
                BigDecimal finalMoles = init.moles().add(nu.multiply(xi, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
                if (finalMoles.compareTo(BigDecimal.ZERO) < 0) {
                    finalMoles = BigDecimal.ZERO;
                }
                currentAmounts.add(new InitialParticipantAmount(init.compoundCode(), init.state(), finalMoles, init.speciesCode(), init.ionicCharge()));
            }
            daviesGammaMap = daviesProvider.calculateActivityCoefficients(currentAmounts, request.spectatorIons(), volumeLiters, temperature);
        }

        ActivityBasis basis = switch (request.method()) {
            case CONSTANT_TOTAL_PRESSURE -> ActivityBasis.IDEAL_GAS_PARTIAL_PRESSURE;
            case CONSTANT_VOLUME_IDEAL_GAS -> ActivityBasis.IDEAL_GAS_PARTIAL_PRESSURE;
            case AQUEOUS_IDEAL -> ActivityBasis.AQUEOUS_IDEAL;
            case AQUEOUS_DAVIES -> ActivityBasis.AQUEOUS_DAVIES;
        };

        for (ReactionThermodynamicVectorTerm term : vector.terms()) {
            InitialParticipantAmount init = initialMap.get(term.key());
            BigDecimal nu = toBigDecimal(term.coefficient());
            BigDecimal finalMoles = init.moles().add(nu.multiply(xi, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
            if (finalMoles.compareTo(BigDecimal.ZERO) < 0) {
                finalMoles = BigDecimal.ZERO;
            }

            BigDecimal expectedMoles = init.moles().add(nu.multiply(xi, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
            BigDecimal massErr = finalMoles.subtract(expectedMoles, ScientificMath.CALCULATION_CONTEXT).abs();
            if (massErr.compareTo(maxMassBalanceError) > 0) {
                maxMassBalanceError = massErr;
            }

            BigDecimal activity;
            BigDecimal partialPressureBar = null;
            BigDecimal concentrationMolPerLiter = null;

            if (term.state() == MatterState.GAS) {
                if (request.method() == EquilibriumCompositionMethod.CONSTANT_TOTAL_PRESSURE) {
                    BigDecimal y = finalMoles.divide(totalGasMoles, ScientificMath.CALCULATION_CONTEXT);
                    partialPressureBar = y.multiply(totalPressureBar, ScientificMath.CALCULATION_CONTEXT);
                } else if (request.method() == EquilibriumCompositionMethod.CONSTANT_VOLUME_IDEAL_GAS) {
                    partialPressureBar = finalMoles.multiply(R_BAR_L_PER_MOL_K, ScientificMath.CALCULATION_CONTEXT)
                            .multiply(temperature.in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT)
                            .divide(volumeLiters, ScientificMath.CALCULATION_CONTEXT);
                } else {
                    throw new EquilibriumCompositionException(
                            EquilibriumCompositionErrorCode.UNSUPPORTED_MODEL,
                            "Gas state participant in non-gas method: " + request.method());
                }
                activity = partialPressureBar;
                BigDecimal actForRecord = activity.compareTo(BigDecimal.ZERO) <= 0 ? MIN_ACTIVITY : activity;
                activities.add(new ParticipantActivity(init.compoundCode(), MatterState.GAS, basis, actForRecord,
                        init.speciesCode(), null, null, init.ionicCharge()));
            } else if (request.method() == EquilibriumCompositionMethod.AQUEOUS_IDEAL || request.method() == EquilibriumCompositionMethod.AQUEOUS_DAVIES) {
                concentrationMolPerLiter = finalMoles.divide(volumeLiters, ScientificMath.CALCULATION_CONTEXT);
                BigDecimal gamma = BigDecimal.ONE;
                if (request.method() == EquilibriumCompositionMethod.AQUEOUS_DAVIES) {
                    String specKey = init.speciesCode() != null ? init.speciesCode() : init.compoundCode();
                    gamma = daviesGammaMap.getOrDefault(specKey, BigDecimal.ONE);
                }
                activity = gamma.multiply(concentrationMolPerLiter, ScientificMath.CALCULATION_CONTEXT);
                BigDecimal actForRecord = concentrationMolPerLiter.compareTo(BigDecimal.ZERO) <= 0 ? MIN_ACTIVITY : concentrationMolPerLiter;
                activities.add(ParticipantActivity.aqueous(init.compoundCode(), init.speciesCode(), actForRecord, gamma, basis, init.ionicCharge() == null ? 0 : init.ionicCharge()));
            } else if (term.state() == MatterState.SOLID) {
                activity = BigDecimal.ONE;
                activities.add(ParticipantActivity.pureSolid(init.compoundCode()));
            } else if (term.state() == MatterState.LIQUID) {
                activity = BigDecimal.ONE;
                activities.add(ParticipantActivity.pureLiquid(init.compoundCode()));
            } else {
                activity = BigDecimal.ONE;
                activities.add(ParticipantActivity.explicitDimensionless(init.compoundCode(), term.state(), activity));
            }

            finalComposition.add(new EquilibriumParticipantState(
                    init.compoundCode(), term.state(), init.speciesCode(), init.moles(), finalMoles, nu,
                    activity, partialPressureBar, concentrationMolPerLiter));
        }

        ReactionActivityInput actInput = new ReactionActivityInput(activities);
        ThermodynamicEquilibriumCalculator thermoCalc = new ThermodynamicEquilibriumCalculator();
        ReactionQuotient Q = thermoCalc.reactionQuotient(vector, actInput);

        BigDecimal finalLnQ = Q.lnQ();
        BigDecimal absResidual = finalLnQ.subtract(lnK, ScientificMath.CALCULATION_CONTEXT).abs();
        BigDecimal relResidual = lnK.abs().compareTo(BigDecimal.ZERO) > 0
                ? absResidual.divide(lnK.abs(), ScientificMath.CALCULATION_CONTEXT)
                : absResidual;

        BigDecimal rt = R_BAR_L_PER_MOL_K.multiply(temperature.in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT);
        BigDecimal deltaG = rt.multiply(finalLnQ.subtract(lnK, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);

        EquilibriumCompositionResidual residual = new EquilibriumCompositionResidual(
                finalLnQ, lnK, absResidual, relResidual, deltaG, maxMassBalanceError);

        List<String> assumptions = List.of(
                "Stateless equilibrium composition calculation for single reaction",
                "System model: " + request.method(),
                "Dimensionless activities used with standard state 1 bar / 1 mol/L"
        );

        String explanation = "Equilibrium composition solved via deterministic bounded root solver; extent xi = "
                + xi.stripTrailingZeros().toPlainString() + " mol, status = " + status;

        return new EquilibriumCompositionResult(
                request.reactionCode(), temperature, status, extent, finalComposition, Q, standardConstant,
                residual, coverage, standardConstant.phaseStabilityStatus(), request.method(), explanation, assumptions);
    }

    private static void validateRequest(ReactionThermodynamicVector vector, EquilibriumCompositionRequest request) {
        if (request.temperature().in(TemperatureUnit.KELVIN).compareTo(BigDecimal.ZERO) <= 0) {
            throw new EquilibriumCompositionException(
                    EquilibriumCompositionErrorCode.INVALID_TEMPERATURE,
                    "Target temperature must be positive");
        }
        if (request.method() == EquilibriumCompositionMethod.CONSTANT_TOTAL_PRESSURE) {
            if (request.totalPressure() == null || request.totalPressure().in(PressureUnit.BAR).compareTo(BigDecimal.ZERO) <= 0) {
                throw new EquilibriumCompositionException(
                        EquilibriumCompositionErrorCode.INVALID_PRESSURE,
                        "Total pressure must be positive for CONSTANT_TOTAL_PRESSURE system model");
            }
        }
        if (request.method() == EquilibriumCompositionMethod.CONSTANT_VOLUME_IDEAL_GAS
                || request.method() == EquilibriumCompositionMethod.AQUEOUS_IDEAL
                || request.method() == EquilibriumCompositionMethod.AQUEOUS_DAVIES) {
            if (request.volume() == null || request.volume().in(VolumeUnit.LITER).compareTo(BigDecimal.ZERO) <= 0) {
                throw new EquilibriumCompositionException(
                        EquilibriumCompositionErrorCode.INVALID_VOLUME,
                        "Volume must be positive for " + request.method() + " system model");
            }
        }

        // Validate state overrides / phase consistency
        for (ReactionThermodynamicVectorTerm term : vector.terms()) {
            MatterState expectedState = term.state();
            if (request.stateOverrides().containsKey(term.compoundCode())) {
                MatterState override = request.stateOverrides().get(term.compoundCode());
                if (override != expectedState) {
                    throw new EquilibriumCompositionException(
                            EquilibriumCompositionErrorCode.PHASE_MISMATCH,
                            "State override mismatch for " + term.compoundCode() + ": expected " + expectedState + " but got " + override);
                }
            }
        }

        boolean anyPositive = false;
        for (InitialParticipantAmount amount : request.initialAmounts()) {
            if (amount.moles().compareTo(BigDecimal.ZERO) > 0) {
                anyPositive = true;
            }
        }
        if (!anyPositive) {
            throw new EquilibriumCompositionException(
                    EquilibriumCompositionErrorCode.INVALID_INITIAL_AMOUNTS,
                    "At least one participant initial amount must be positive");
        }
    }

    private static ExtentBounds deriveExtentBounds(ReactionThermodynamicVector vector, Map<String, InitialParticipantAmount> initialMap) {
        BigDecimal minExtent = new BigDecimal("-1e18");
        BigDecimal maxExtent = new BigDecimal("1e18");
        String limitingReactant = null;
        String limitingProduct = null;

        for (ReactionThermodynamicVectorTerm term : vector.terms()) {
            InitialParticipantAmount init = initialMap.get(term.key());
            BigDecimal nu = toBigDecimal(term.coefficient());
            BigDecimal n0 = init.moles();

            if (nu.compareTo(BigDecimal.ZERO) < 0) {
                // Reactant: n0 + nu * xi >= 0 => xi <= n0 / |nu|
                BigDecimal bound = n0.divide(nu.abs(), ScientificMath.CALCULATION_CONTEXT);
                if (bound.compareTo(maxExtent) < 0) {
                    maxExtent = bound;
                    limitingReactant = term.compoundCode();
                }
            } else if (nu.compareTo(BigDecimal.ZERO) > 0) {
                // Product: n0 + nu * xi >= 0 => xi >= -n0 / nu
                BigDecimal bound = n0.negate().divide(nu, ScientificMath.CALCULATION_CONTEXT);
                if (bound.compareTo(minExtent) > 0) {
                    minExtent = bound;
                    limitingProduct = term.compoundCode();
                }
            }
        }

        if (minExtent.compareTo(maxExtent) > 0) {
            throw new EquilibriumCompositionException(
                    EquilibriumCompositionErrorCode.NO_VALID_ROOT,
                    "No valid extent bounds: minExtent (" + minExtent + ") > maxExtent (" + maxExtent + ")");
        }

        return new ExtentBounds(minExtent, maxExtent, limitingReactant, limitingProduct);
    }

    private static BigDecimal solveRoot(
            BigDecimal low, BigDecimal high, BigDecimal fLow, BigDecimal fHigh,
            ReactionThermodynamicVector vector,
            Map<String, InitialParticipantAmount> initialMap,
            EquilibriumCompositionRequest request,
            BigDecimal lnK,
            DaviesActivityProvider daviesProvider) {

        BigDecimal a = low;
        BigDecimal b = high;
        BigDecimal fa = fLow;
        BigDecimal fb = fHigh;

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            BigDecimal mid = a.add(b, ScientificMath.CALCULATION_CONTEXT).divide(new BigDecimal("2"), ScientificMath.CALCULATION_CONTEXT);
            BigDecimal secant = null;
            if (fa != null && fb != null && fb.subtract(fa, ScientificMath.CALCULATION_CONTEXT).abs().compareTo(new BigDecimal("1e-15")) > 0) {
                secant = a.subtract(fa.multiply(b.subtract(a, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT)
                        .divide(fb.subtract(fa, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
            }

            BigDecimal candidate = mid;
            if (secant != null && secant.compareTo(a) > 0 && secant.compareTo(b) < 0) {
                candidate = secant;
            }

            BigDecimal fCand = evaluateResidualAt(candidate, vector, initialMap, request, lnK, daviesProvider);
            if (fCand == null) {
                candidate = mid;
                fCand = evaluateResidualAt(candidate, vector, initialMap, request, lnK, daviesProvider);
            }

            if (fCand == null) {
                break;
            }

            if (fCand.abs().compareTo(RESIDUAL_TOLERANCE) <= 0 || b.subtract(a, ScientificMath.CALCULATION_CONTEXT).abs().compareTo(new BigDecimal("1e-12")) <= 0) {
                return candidate;
            }

            if (fCand.compareTo(BigDecimal.ZERO) < 0) {
                a = candidate;
                fa = fCand;
            } else {
                b = candidate;
                fb = fCand;
            }
        }
        return a.add(b, ScientificMath.CALCULATION_CONTEXT).divide(new BigDecimal("2"), ScientificMath.CALCULATION_CONTEXT);
    }

    private static BigDecimal evaluateResidualAt(
            BigDecimal xi,
            ReactionThermodynamicVector vector,
            Map<String, InitialParticipantAmount> initialMap,
            EquilibriumCompositionRequest request,
            BigDecimal lnK,
            DaviesActivityProvider daviesProvider) {

        try {
            BigDecimal volumeLiters = request.volume() != null ? request.volume().in(VolumeUnit.LITER) : null;
            BigDecimal totalPressureBar = request.totalPressure() != null ? request.totalPressure().in(PressureUnit.BAR) : null;
            BigDecimal inertMoles = request.inertGasMoles() != null ? request.inertGasMoles() : BigDecimal.ZERO;
            Temperature temperature = request.temperature();

            BigDecimal totalGasMoles = calculateTotalGasMoles(vector, initialMap, xi, inertMoles);

            Map<String, BigDecimal> daviesGammaMap = Map.of();
            if (request.method() == EquilibriumCompositionMethod.AQUEOUS_DAVIES && daviesProvider != null && volumeLiters != null) {
                List<InitialParticipantAmount> currentAmounts = new ArrayList<>();
                for (ReactionThermodynamicVectorTerm term : vector.terms()) {
                    InitialParticipantAmount init = initialMap.get(term.key());
                    BigDecimal nu = toBigDecimal(term.coefficient());
                    BigDecimal finalMoles = init.moles().add(nu.multiply(xi, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
                    if (finalMoles.compareTo(BigDecimal.ZERO) <= 0) {
                        return null; // out of domain
                    }
                    currentAmounts.add(new InitialParticipantAmount(init.compoundCode(), init.state(), finalMoles, init.speciesCode(), init.ionicCharge()));
                }
                daviesGammaMap = daviesProvider.calculateActivityCoefficients(currentAmounts, request.spectatorIons(), volumeLiters, temperature);
            }

            BigDecimal lnQ = BigDecimal.ZERO;
            for (ReactionThermodynamicVectorTerm term : vector.terms()) {
                InitialParticipantAmount init = initialMap.get(term.key());
                BigDecimal nu = toBigDecimal(term.coefficient());
                BigDecimal moles = init.moles().add(nu.multiply(xi, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);

                if (moles.compareTo(BigDecimal.ZERO) <= 0) {
                    return null; // out of domain
                }

                BigDecimal act;
                if (term.state() == MatterState.GAS) {
                    if (request.method() == EquilibriumCompositionMethod.CONSTANT_TOTAL_PRESSURE) {
                        BigDecimal y = moles.divide(totalGasMoles, ScientificMath.CALCULATION_CONTEXT);
                        act = y.multiply(totalPressureBar, ScientificMath.CALCULATION_CONTEXT);
                    } else {
                        act = moles.multiply(R_BAR_L_PER_MOL_K, ScientificMath.CALCULATION_CONTEXT)
                                .multiply(temperature.in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT)
                                .divide(volumeLiters, ScientificMath.CALCULATION_CONTEXT);
                    }
                } else if (request.method() == EquilibriumCompositionMethod.AQUEOUS_IDEAL || request.method() == EquilibriumCompositionMethod.AQUEOUS_DAVIES) {
                    BigDecimal conc = moles.divide(volumeLiters, ScientificMath.CALCULATION_CONTEXT);
                    if (request.method() == EquilibriumCompositionMethod.AQUEOUS_DAVIES) {
                        String specKey = init.speciesCode() != null ? init.speciesCode() : init.compoundCode();
                        BigDecimal gamma = daviesGammaMap.getOrDefault(specKey, BigDecimal.ONE);
                        act = gamma.multiply(conc, ScientificMath.CALCULATION_CONTEXT);
                    } else {
                        act = conc;
                    }
                } else {
                    act = BigDecimal.ONE;
                }

                BigDecimal lnAct = ThermodynamicDecimalMath.ln(act);
                lnQ = lnQ.add(lnAct.multiply(nu, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
            }

            return lnQ.subtract(lnK, ScientificMath.CALCULATION_CONTEXT);
        } catch (Exception ex) {
            return null;
        }
    }

    private static BigDecimal calculateTotalGasMoles(
            ReactionThermodynamicVector vector,
            Map<String, InitialParticipantAmount> initialMap,
            BigDecimal xi,
            BigDecimal inertMoles) {
        BigDecimal total = inertMoles;
        for (ReactionThermodynamicVectorTerm term : vector.terms()) {
            if (term.state() == MatterState.GAS) {
                InitialParticipantAmount init = initialMap.get(term.key());
                BigDecimal nu = toBigDecimal(term.coefficient());
                total = total.add(init.moles().add(nu.multiply(xi, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
            }
        }
        return total;
    }

    private static BigDecimal toBigDecimal(RationalNumber rational) {
        return new BigDecimal(rational.getNumerator())
                .divide(new BigDecimal(rational.getDenominator()), ScientificMath.CALCULATION_CONTEXT);
    }

    private static String key(String compoundCode, MatterState state) {
        return compoundCode + "|" + state.name();
    }
}

package com.ailab.chemistry.domain.solubility;

import com.ailab.chemistry.domain.acidbase.ActivityModel;
import com.ailab.chemistry.domain.acidbase.ActivityParameterSet;
import com.ailab.chemistry.domain.acidbase.AcidBaseDecimalMath;
import com.ailab.chemistry.domain.acidbase.IonicSpeciesConcentration;
import com.ailab.chemistry.domain.acidbase.IonicStrength;
import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.Mass;
import com.ailab.chemistry.domain.measurement.MassUnit;
import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.VolumeUnit;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class SolubilityEquilibriumCalculator {
    private static final MathContext MC = MathContext.DECIMAL128;
    private static final BigDecimal TWO = new BigDecimal("2");
    private static final int MAX_ITERATIONS = 240;
    private static final BigDecimal SOLVER_TOLERANCE = new BigDecimal("1e-28");

    public SaturationResult calculateSaturation(SaturationRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        SolubilityEquilibrium equilibrium = Objects.requireNonNull(request.equilibrium(), "equilibrium must be resolved");
        ActivityParameterSet parameterSet = Objects.requireNonNull(request.activityParameterSet(), "activityParameterSet must not be null");
        Map<String, BigDecimal> concentrations = concentrationMap(request.dissolvedIons());
        List<IonicSpeciesConcentration> ions = ionsFor(equilibrium, concentrations, request.spectatorIons(), false);
        ActivityPoint point = activityPoint(ions, parameterSet);
        BigDecimal q = ionProduct(equilibrium, point.activities());
        BigDecimal ratio = q.divide(equilibrium.solubilityProduct().value(), MC);
        SaturationStatus status = classify(ratio, request.comparisonTolerance());
        return new SaturationResult(equilibrium, parameterSet.model(), new IonProduct(q), new SaturationRatio(ratio),
                status, point.ionicStrength(), point.activities(), 1,
                new SolubilityResidual(q.subtract(equilibrium.solubilityProduct().value(), MC).abs(), BigDecimal.ZERO));
    }

    public MolarSolubilityResult calculateMolarSolubility(MolarSolubilityRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        SolubilityEquilibrium equilibrium = Objects.requireNonNull(request.equilibrium(), "equilibrium must not be null");
        ActivityParameterSet parameterSet = Objects.requireNonNull(request.activityParameterSet(), "activityParameterSet must not be null");
        Map<String, BigDecimal> initial = concentrationMap(request.initialIons());
        BigDecimal upper = initialUpper(equilibrium, initial);
        while (qAtDissolution(equilibrium, initial, request.spectatorIons(), parameterSet, upper).compareTo(equilibrium.solubilityProduct().value()) < 0) {
            upper = upper.multiply(TWO, MC);
            if (upper.compareTo(new BigDecimal("10")) > 0) {
                throw new SolubilityException(SolubilityErrorCode.SOLVER_CONVERGENCE_FAILED, "Unable to bracket solubility extent");
            }
        }
        SolvePoint point = solveExtent(equilibrium, initial, request.spectatorIons(), parameterSet, BigDecimal.ZERO, upper);
        Map<String, BigDecimal> concentrations = concentrationsAfterDissolution(equilibrium, initial, point.extent());
        ActivityPoint activities = activityPoint(ionsFor(equilibrium, concentrations, request.spectatorIons(), true), parameterSet);
        BigDecimal q = ionProduct(equilibrium, activities.activities());
        return new MolarSolubilityResult(equilibrium, parameterSet.model(),
                MolarConcentration.of(point.extent(), MolarConcentrationUnit.MOL_PER_LITER),
                concentrations, classify(q.divide(equilibrium.solubilityProduct().value(), MC), request.comparisonTolerance()),
                activities.ionicStrength(), point.iterations(),
                new SolubilityResidual(q.subtract(equilibrium.solubilityProduct().value(), MC).abs(), BigDecimal.ZERO),
                SolubilitySolverStatus.CONVERGED);
    }

    public PrecipitationResult calculatePrecipitation(PrecipitationRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        SolubilityEquilibrium equilibrium = Objects.requireNonNull(request.equilibrium(), "equilibrium must not be null");
        ActivityParameterSet parameterSet = Objects.requireNonNull(request.activityParameterSet(), "activityParameterSet must not be null");
        BigDecimal liters = request.finalVolume().in(VolumeUnit.LITER);
        Map<String, BigDecimal> initial = new LinkedHashMap<>();
        for (SolutionIonAmount amount : request.ionAmounts()) {
            if (amount.amount().in(AmountOfSubstanceUnit.MOLE).compareTo(BigDecimal.ZERO) < 0) {
                throw new SolubilityException(SolubilityErrorCode.NEGATIVE_CONCENTRATION, "Ion amount must not be negative");
            }
            initial.merge(amount.speciesCode(), amount.amount().in(AmountOfSubstanceUnit.MOLE).divide(liters, MC), (a, b) -> a.add(b, MC));
        }
        SaturationResult initialSaturation = calculateSaturation(new SaturationRequest(equilibrium, ionsFromMap(initial), request.spectatorIons(), parameterSet, request.comparisonTolerance()));
        if (initialSaturation.status() != SaturationStatus.SUPERSATURATED) {
            return precipitationResult(request, initial, BigDecimal.ZERO, initialSaturation, initialSaturation, BigDecimal.ZERO, 1, SolubilitySolverStatus.NOT_REQUIRED);
        }
        BigDecimal upper = maximumPrecipitationExtent(equilibrium, initial);
        SolvePoint point = solvePrecipitation(equilibrium, initial, request.spectatorIons(), parameterSet, BigDecimal.ZERO, upper);
        Map<String, BigDecimal> finalConcentrations = concentrationsAfterPrecipitation(equilibrium, initial, point.extent());
        SaturationResult finalSaturation = calculateSaturation(new SaturationRequest(equilibrium, ionsFromMap(finalConcentrations), request.spectatorIons(), parameterSet, request.comparisonTolerance()));
        return precipitationResult(request, finalConcentrations, point.extent(), initialSaturation, finalSaturation, point.extent().multiply(liters, MC), point.iterations(), SolubilitySolverStatus.CONVERGED);
    }

    private PrecipitationResult precipitationResult(PrecipitationRequest request, Map<String, BigDecimal> concentrations, BigDecimal extentPerLiter,
                                                    SaturationResult initialSaturation, SaturationResult finalSaturation, BigDecimal moles,
                                                    int iterations, SolubilitySolverStatus status) {
        Optional<Mass> mass = Optional.empty();
        if (request.precipitateMolarMassGramsPerMole() != null) {
            mass = Optional.of(Mass.of(moles.multiply(request.precipitateMolarMassGramsPerMole(), MC), MassUnit.GRAM));
        }
        BigDecimal residual = finalSaturation.ionicProduct().value().subtract(request.equilibrium().solubilityProduct().value(), MC).abs();
        if (moles.compareTo(BigDecimal.ZERO) == 0) {
            residual = BigDecimal.ZERO;
        }
        return new PrecipitationResult(request.equilibrium(), request.activityParameterSet().model(), initialSaturation.status(),
                initialSaturation.ionicProduct(), AmountOfSubstance.of(moles, AmountOfSubstanceUnit.MOLE), mass,
                Map.copyOf(concentrations), finalSaturation.ionicProduct(), finalSaturation.ionicStrength(), iterations,
                new SolubilityResidual(residual, BigDecimal.ZERO), status);
    }

    private SolvePoint solveExtent(SolubilityEquilibrium equilibrium, Map<String, BigDecimal> initial, List<IonicSpeciesConcentration> spectators,
                                   ActivityParameterSet parameterSet, BigDecimal lower, BigDecimal upper) {
        BigDecimal fLower = qAtDissolution(equilibrium, initial, spectators, parameterSet, lower).subtract(equilibrium.solubilityProduct().value(), MC);
        for (int i = 1; i <= MAX_ITERATIONS; i++) {
            BigDecimal mid = lower.add(upper, MC).divide(TWO, MC);
            BigDecimal fMid = qAtDissolution(equilibrium, initial, spectators, parameterSet, mid).subtract(equilibrium.solubilityProduct().value(), MC);
            if (isSolved(fMid, equilibrium.solubilityProduct().value()) || upper.subtract(lower, MC).abs().compareTo(SOLVER_TOLERANCE) <= 0) {
                return new SolvePoint(mid, i);
            }
            if (fLower.signum() == fMid.signum()) {
                lower = mid;
                fLower = fMid;
            } else {
                upper = mid;
            }
        }
        throw new SolubilityException(SolubilityErrorCode.SOLVER_CONVERGENCE_FAILED, "Solubility solver reached maximum iterations");
    }

    private SolvePoint solvePrecipitation(SolubilityEquilibrium equilibrium, Map<String, BigDecimal> initial, List<IonicSpeciesConcentration> spectators,
                                          ActivityParameterSet parameterSet, BigDecimal lower, BigDecimal upper) {
        BigDecimal fLower = qAtPrecipitation(equilibrium, initial, spectators, parameterSet, lower).subtract(equilibrium.solubilityProduct().value(), MC);
        for (int i = 1; i <= MAX_ITERATIONS; i++) {
            BigDecimal mid = lower.add(upper, MC).divide(TWO, MC);
            BigDecimal fMid = qAtPrecipitation(equilibrium, initial, spectators, parameterSet, mid).subtract(equilibrium.solubilityProduct().value(), MC);
            if (isSolved(fMid, equilibrium.solubilityProduct().value()) || upper.subtract(lower, MC).abs().compareTo(SOLVER_TOLERANCE) <= 0) {
                return new SolvePoint(mid, i);
            }
            if (fLower.signum() == fMid.signum()) {
                lower = mid;
                fLower = fMid;
            } else {
                upper = mid;
            }
        }
        throw new SolubilityException(SolubilityErrorCode.SOLVER_CONVERGENCE_FAILED, "Precipitation solver reached maximum iterations");
    }

    private BigDecimal qAtDissolution(SolubilityEquilibrium equilibrium, Map<String, BigDecimal> initial, List<IonicSpeciesConcentration> spectators,
                                      ActivityParameterSet parameterSet, BigDecimal extent) {
        Map<String, BigDecimal> concentrations = concentrationsAfterDissolution(equilibrium, initial, extent);
        return ionProduct(equilibrium, activityPoint(ionsFor(equilibrium, concentrations, spectators, true), parameterSet).activities());
    }

    private BigDecimal qAtPrecipitation(SolubilityEquilibrium equilibrium, Map<String, BigDecimal> initial, List<IonicSpeciesConcentration> spectators,
                                        ActivityParameterSet parameterSet, BigDecimal extent) {
        Map<String, BigDecimal> concentrations = concentrationsAfterPrecipitation(equilibrium, initial, extent);
        return ionProduct(equilibrium, activityPoint(ionsFor(equilibrium, concentrations, spectators, true), parameterSet).activities());
    }

    private Map<String, BigDecimal> concentrationsAfterDissolution(SolubilityEquilibrium equilibrium, Map<String, BigDecimal> initial, BigDecimal extent) {
        Map<String, BigDecimal> concentrations = new LinkedHashMap<>(initial);
        for (DissolutionTerm term : equilibrium.terms()) {
            concentrations.merge(term.speciesCode(), extent.multiply(BigDecimal.valueOf(term.coefficient()), MC), (a, b) -> a.add(b, MC));
        }
        return concentrations;
    }

    private Map<String, BigDecimal> concentrationsAfterPrecipitation(SolubilityEquilibrium equilibrium, Map<String, BigDecimal> initial, BigDecimal extent) {
        Map<String, BigDecimal> concentrations = new LinkedHashMap<>(initial);
        for (DissolutionTerm term : equilibrium.terms()) {
            BigDecimal current = concentrations.getOrDefault(term.speciesCode(), BigDecimal.ZERO);
            BigDecimal next = current.subtract(extent.multiply(BigDecimal.valueOf(term.coefficient()), MC), MC);
            if (next.compareTo(BigDecimal.ZERO) < 0) {
                throw new SolubilityException(SolubilityErrorCode.NEGATIVE_CONCENTRATION, "Precipitation extent consumes more ion than available");
            }
            concentrations.put(term.speciesCode(), next.max(BigDecimal.ZERO));
        }
        return concentrations;
    }

    private BigDecimal maximumPrecipitationExtent(SolubilityEquilibrium equilibrium, Map<String, BigDecimal> initial) {
        BigDecimal upper = null;
        for (DissolutionTerm term : equilibrium.terms()) {
            BigDecimal available = initial.getOrDefault(term.speciesCode(), BigDecimal.ZERO);
            BigDecimal possible = available.divide(BigDecimal.valueOf(term.coefficient()), MC);
            upper = upper == null ? possible : upper.min(possible);
        }
        return upper == null ? BigDecimal.ZERO : upper;
    }

    private BigDecimal initialUpper(SolubilityEquilibrium equilibrium, Map<String, BigDecimal> initial) {
        BigDecimal upper = new BigDecimal("1e-12");
        for (DissolutionTerm term : equilibrium.terms()) {
            upper = upper.max(initial.getOrDefault(term.speciesCode(), BigDecimal.ZERO).add(new BigDecimal("1e-12"), MC));
        }
        return upper.max(new BigDecimal("1e-9"));
    }

    private Map<String, BigDecimal> concentrationMap(List<IonicSpeciesConcentration> ions) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (IonicSpeciesConcentration ion : ions) {
            if (ion.concentration().compareTo(BigDecimal.ZERO) < 0) {
                throw new SolubilityException(SolubilityErrorCode.NEGATIVE_CONCENTRATION, "Concentration must not be negative");
            }
            map.merge(ion.speciesCode().toUpperCase(), ion.concentration(), (a, b) -> a.add(b, MC));
        }
        return map;
    }

    private List<IonicSpeciesConcentration> ionsFromMap(Map<String, BigDecimal> concentrations) {
        List<IonicSpeciesConcentration> ions = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : concentrations.entrySet()) {
            ions.add(new IonicSpeciesConcentration(entry.getKey(), entry.getValue(), chargeFromCode(entry.getKey())));
        }
        return ions;
    }

    private List<IonicSpeciesConcentration> ionsFor(SolubilityEquilibrium equilibrium, Map<String, BigDecimal> concentrations,
                                                     List<IonicSpeciesConcentration> spectators, boolean includeSpectators) {
        List<IonicSpeciesConcentration> ions = new ArrayList<>();
        for (DissolutionTerm term : equilibrium.terms()) {
            ions.add(new IonicSpeciesConcentration(term.speciesCode(), concentrations.getOrDefault(term.speciesCode(), BigDecimal.ZERO), term.charge()));
        }
        for (Map.Entry<String, BigDecimal> entry : concentrations.entrySet()) {
            boolean alreadyIncluded = equilibrium.terms().stream().anyMatch(term -> term.speciesCode().equalsIgnoreCase(entry.getKey()));
            if (!alreadyIncluded) {
                ions.add(new IonicSpeciesConcentration(entry.getKey(), entry.getValue(), chargeFromCode(entry.getKey())));
            }
        }
        if (includeSpectators) {
            ions.addAll(spectators);
        }
        return ions;
    }

    private ActivityPoint activityPoint(List<IonicSpeciesConcentration> ions, ActivityParameterSet parameterSet) {
        BigDecimal strength = BigDecimal.ZERO;
        for (IonicSpeciesConcentration ion : ions) {
            strength = strength.add(ion.concentration().multiply(BigDecimal.valueOf((long) ion.charge() * ion.charge()), MC), MC);
        }
        strength = strength.divide(TWO, MC);
        if (parameterSet.model() == ActivityModel.DAVIES
                && (strength.compareTo(parameterSet.minimumIonicStrength()) < 0 || strength.compareTo(parameterSet.maximumIonicStrength()) > 0)) {
            throw new SolubilityException(SolubilityErrorCode.OUTSIDE_ACTIVITY_MODEL_RANGE, "Davies model is valid only through ionic strength " + parameterSet.maximumIonicStrength());
        }
        Map<String, BigDecimal> activities = new LinkedHashMap<>();
        for (IonicSpeciesConcentration ion : ions) {
            BigDecimal gamma = coefficient(ion.charge(), strength, parameterSet);
            activities.put(ion.speciesCode(), gamma.multiply(ion.concentration(), MC));
        }
        return new ActivityPoint(new IonicStrength(strength), activities);
    }

    private BigDecimal coefficient(int charge, BigDecimal ionicStrength, ActivityParameterSet parameterSet) {
        if (parameterSet.model() == ActivityModel.IDEAL || charge == 0 || ionicStrength.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ONE;
        }
        BigDecimal sqrtI = ionicStrength.sqrt(MC);
        BigDecimal term = sqrtI.divide(BigDecimal.ONE.add(sqrtI, MC), MC).subtract(new BigDecimal("0.3").multiply(ionicStrength, MC), MC);
        BigDecimal exponent = parameterSet.daviesA().negate(MC)
                .multiply(BigDecimal.valueOf((long) charge * charge), MC)
                .multiply(term, MC);
        return AcidBaseDecimalMath.tenPower(exponent);
    }

    private BigDecimal ionProduct(SolubilityEquilibrium equilibrium, Map<String, BigDecimal> activities) {
        BigDecimal product = BigDecimal.ONE;
        for (DissolutionTerm term : equilibrium.terms()) {
            BigDecimal activity = activities.getOrDefault(term.speciesCode(), BigDecimal.ZERO);
            if (activity.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            }
            product = product.multiply(activity.pow(term.coefficient(), MC), MC);
        }
        return product;
    }

    private SaturationStatus classify(BigDecimal ratio, BigDecimal tolerance) {
        if (ratio.compareTo(BigDecimal.ONE.subtract(tolerance, MC)) < 0) {
            return SaturationStatus.UNSATURATED;
        }
        if (ratio.compareTo(BigDecimal.ONE.add(tolerance, MC)) > 0) {
            return SaturationStatus.SUPERSATURATED;
        }
        return SaturationStatus.SATURATED;
    }

    private boolean isSolved(BigDecimal residual, BigDecimal target) {
        BigDecimal absoluteTolerance = target.abs().multiply(new BigDecimal("1e-12"), MC).max(new BigDecimal("1e-40"));
        return residual.abs().compareTo(absoluteTolerance) <= 0;
    }

    private int chargeFromCode(String speciesCode) {
        if (speciesCode.contains("3PLUS")) return 3;
        if (speciesCode.contains("2PLUS")) return 2;
        if (speciesCode.contains("PLUS")) return 1;
        if (speciesCode.contains("3MINUS")) return -3;
        if (speciesCode.contains("2MINUS")) return -2;
        if (speciesCode.contains("MINUS")) return -1;
        return 0;
    }

    private record ActivityPoint(IonicStrength ionicStrength, Map<String, BigDecimal> activities) {}
    private record SolvePoint(BigDecimal extent, int iterations) {}
}

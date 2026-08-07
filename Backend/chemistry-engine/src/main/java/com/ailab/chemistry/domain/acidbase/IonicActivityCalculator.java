package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.PhValue;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class IonicActivityCalculator {
    private static final MathContext MC = AcidBaseDecimalMath.MATH_CONTEXT;
    private static final BigDecimal HALF = new BigDecimal("0.5");
    private static final BigDecimal TWO = new BigDecimal("2");
    private static final BigDecimal LOWER_H = new BigDecimal("1e-30");
    private static final BigDecimal SOLVER_TOLERANCE = new BigDecimal("1e-28");
    private static final BigDecimal ITERATION_TOLERANCE = new BigDecimal("1e-12");
    private static final int MAX_BISECTION_ITERATIONS = 240;
    private static final int MAX_ACTIVITY_ITERATIONS = 80;

    public IonicStrength calculateIonicStrength(List<IonicSpeciesConcentration> species) {
        Objects.requireNonNull(species, "species must not be null");
        BigDecimal sum = BigDecimal.ZERO;
        for (IonicSpeciesConcentration ion : species) {
            BigDecimal zSquared = BigDecimal.valueOf((long) ion.charge() * ion.charge());
            sum = sum.add(ion.concentration().multiply(zSquared, MC), MC);
        }
        return new IonicStrength(sum.multiply(HALF, MC));
    }

    public ActivityCorrectionResult calculateActivities(List<IonicSpeciesConcentration> species, ActivityParameterSet parameterSet) {
        Objects.requireNonNull(parameterSet, "parameterSet must not be null");
        IonicStrength strength = calculateIonicStrength(species);
        if (parameterSet.model() == ActivityModel.DAVIES) {
            validateDaviesRange(strength, parameterSet);
        }
        List<ActivityCoefficient> coefficients = new ArrayList<>();
        List<ChemicalActivity> activities = new ArrayList<>();
        for (IonicSpeciesConcentration ion : species) {
            BigDecimal gamma = coefficient(ion.charge(), strength.value(), parameterSet);
            coefficients.add(new ActivityCoefficient(ion.speciesCode(), ion.charge(), gamma));
            activities.add(new ChemicalActivity(ion.speciesCode(), gamma.multiply(ion.concentration(), MC)));
        }
        for (int charge : List.of(-2, -1, 0, 1, 2)) {
            coefficients.add(new ActivityCoefficient("CHARGE-" + charge, charge, coefficient(charge, strength.value(), parameterSet)));
        }
        return new ActivityCorrectionResult(parameterSet.model(), strength, coefficients, activities);
    }

    public ActivityCorrectedEquilibriumResult calculateEquilibrium(ActivityCorrectionRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        ActivityParameterSet parameterSet = request.parameterSet() == null
                ? idealParameterSet(request)
                : request.parameterSet();
        if (!parameterSet.solventCode().equalsIgnoreCase(request.solventCode())) {
            throw new ActivityException(ActivityErrorCode.UNSUPPORTED_SOLVENT, "Activity parameter set solvent does not match request solvent");
        }
        if (!parameterSet.temperature().equals(request.temperature())) {
            throw new ActivityException(ActivityErrorCode.UNSUPPORTED_TEMPERATURE, "Activity parameter set temperature does not match request temperature");
        }
        if (request.model() == ActivityModel.IDEAL) {
            return solveWithFixedIonicStrength(request, idealParameterSet(request), BigDecimal.ZERO, BigDecimal.ZERO, 1);
        }

        PhValue idealBaselinePh = solveWithFixedIonicStrength(request, idealParameterSet(request), BigDecimal.ZERO, BigDecimal.ZERO, 1).activityPh();
        BigDecimal previousI = BigDecimal.ZERO;
        BigDecimal previousH = BigDecimal.ZERO;
        Map<Integer, BigDecimal> previousGammas = gammaMap(previousI, parameterSet);
        ActivityCorrectedEquilibriumResult latest = null;
        for (int i = 1; i <= MAX_ACTIVITY_ITERATIONS; i++) {
            latest = solveWithFixedIonicStrength(request, parameterSet, previousI, previousH, i);
            BigDecimal newI = latest.ionicStrength().value();
            validateDaviesRange(latest.ionicStrength(), parameterSet);
            BigDecimal hDelta = latest.hydroniumConcentration().subtract(previousH, MC).abs();
            BigDecimal iDelta = newI.subtract(previousI, MC).abs();
            BigDecimal gammaDelta = maxGammaDelta(previousGammas, gammaMap(newI, parameterSet));
            latest = withIteration(latest, new ActivityIterationResult(i, hDelta, iDelta, gammaDelta));
            latest = withIdealPh(latest, idealBaselinePh);
            if (i > 1 && hDelta.compareTo(ITERATION_TOLERANCE) < 0 && iDelta.compareTo(ITERATION_TOLERANCE) < 0 && gammaDelta.compareTo(new BigDecimal("1e-10")) < 0) {
                return latest;
            }
            previousH = latest.hydroniumConcentration();
            previousI = previousI.add(newI, MC).divide(TWO, MC);
            previousGammas = gammaMap(previousI, parameterSet);
        }
        throw new ActivityException(ActivityErrorCode.SOLVER_CONVERGENCE_FAILED, "Activity-corrected equilibrium did not converge");
    }

    private ActivityCorrectedEquilibriumResult solveWithFixedIonicStrength(
            ActivityCorrectionRequest request,
            ActivityParameterSet parameterSet,
            BigDecimal ionicStrength,
            BigDecimal previousH,
            int iteration) {
        Map<Integer, BigDecimal> gammas = gammaMap(ionicStrength, parameterSet);
        SystemSpec spec = spec(request);
        BigDecimal h = solveHydronium(hydronium -> chargeBalance(spec, hydronium, request.kw(), gammas));
        PointState state = state(spec, h, request.kw(), gammas);
        IonicStrength newStrength = calculateIonicStrength(state.ions());
        if (parameterSet.model() == ActivityModel.DAVIES) {
            validateDaviesRange(newStrength, parameterSet);
        }
        Map<Integer, BigDecimal> finalGammas = gammaMap(newStrength.value(), parameterSet);
        BigDecimal gammaH = finalGammas.get(1);
        BigDecimal gammaOh = finalGammas.get(-1);
        BigDecimal activityH = gammaH.multiply(h, MC);
        BigDecimal activityOh = gammaOh.multiply(state.oh(), MC);
        PhValue activityPh = new PhValue(AcidBaseDecimalMath.log10(activityH).negate(MC).setScale(4, RoundingMode.HALF_UP));
        PhValue activityPoh = new PhValue(AcidBaseDecimalMath.log10(activityOh).negate(MC).setScale(4, RoundingMode.HALF_UP));
        PhValue idealPh = new PhValue(AcidBaseDecimalMath.log10(h).negate(MC).setScale(4, RoundingMode.HALF_UP));
        ActivityCorrectionResult corrections = calculateActivities(state.ions(), parameterSet.model() == ActivityModel.IDEAL ? idealParameterSet(request) : parameterSet);
        BigDecimal chargeResidual = chargeBalance(spec, h, request.kw(), finalGammas).abs();
        BigDecimal massResidual = spec.total().subtract(state.familyConcentrations().stream().reduce(BigDecimal.ZERO, (a, b) -> a.add(b, MC)), MC).abs();
        if (spec.kind() == SpecKind.STRONG || spec.kind() == SpecKind.WATER) {
            massResidual = BigDecimal.ZERO;
        }
        return new ActivityCorrectedEquilibriumResult(
                parameterSet.model(),
                request.systemType(),
                idealPh,
                activityPh,
                activityPoh,
                h,
                state.oh(),
                newStrength,
                corrections.coefficientMap(),
                corrections.activities(),
                state.distribution(),
                spec.constants(),
                new ActivityIterationResult(iteration, h.subtract(previousH, MC).abs(), newStrength.value().subtract(ionicStrength, MC).abs(), BigDecimal.ZERO),
                new PolyproticResidual(massResidual.abs(), chargeResidual),
                ActivitySolverStatus.CONVERGED
        );
    }

    private SystemSpec spec(ActivityCorrectionRequest request) {
        BigDecimal total = request.concentration() == null ? BigDecimal.ZERO : request.concentration().in(MolarConcentrationUnit.MOL_PER_LITER);
        BigDecimal fixedCharge = total.multiply(request.spectatorStoichiometry(), MC).multiply(BigDecimal.valueOf(request.spectatorIonCharge()), MC);
        List<IonicSpeciesConcentration> spectatorIons = new ArrayList<>();
        if (request.spectatorIonCode() != null && request.spectatorStoichiometry().compareTo(BigDecimal.ZERO) > 0) {
            spectatorIons.add(new IonicSpeciesConcentration(request.spectatorIonCode(), total.multiply(request.spectatorStoichiometry(), MC), request.spectatorIonCharge()));
        }
        Map<String, BigDecimal> constants = new LinkedHashMap<>();
        constants.put("Kw", request.kw());
        if (request.systemType() == ActivityEquilibriumSystemType.PURE_WATER) {
            return new SystemSpec(SpecKind.WATER, total, fixedCharge, List.of(), List.of(), false, spectatorIons, constants);
        }
        if (request.systemType() == ActivityEquilibriumSystemType.STRONG_ACID || request.systemType() == ActivityEquilibriumSystemType.STRONG_BASE) {
            return new SystemSpec(SpecKind.STRONG, total, request.systemType() == ActivityEquilibriumSystemType.STRONG_ACID ? total.negate(MC) : total, List.of(), List.of(), false,
                    List.of(new IonicSpeciesConcentration(request.systemType() == ActivityEquilibriumSystemType.STRONG_ACID ? "SPEC-CL-MINUS" : "SPEC-NA-PLUS", total, request.systemType() == ActivityEquilibriumSystemType.STRONG_ACID ? -1 : 1)), constants);
        }
        if (request.systemType() == ActivityEquilibriumSystemType.WEAK_ACID || request.systemType() == ActivityEquilibriumSystemType.CONJUGATE_BASE_SALT) {
            constants.put("Ka", request.ka());
            fixedCharge = request.systemType() == ActivityEquilibriumSystemType.CONJUGATE_BASE_SALT ? total : BigDecimal.ZERO;
            spectatorIons = request.systemType() == ActivityEquilibriumSystemType.CONJUGATE_BASE_SALT
                    ? List.of(new IonicSpeciesConcentration("SPEC-NA-PLUS", total, 1))
                    : List.of();
            return new SystemSpec(SpecKind.FAMILY, total, fixedCharge,
                    List.of(species("HA", request.speciesCode(), 0), species("A", conjugateCode(request), -1)),
                    List.of(request.ka()), false, spectatorIons, constants);
        }
        if (request.systemType() == ActivityEquilibriumSystemType.WEAK_BASE || request.systemType() == ActivityEquilibriumSystemType.CONJUGATE_ACID_SALT) {
            BigDecimal ka = request.ka() != null ? request.ka() : request.kw().divide(request.kb(), MC);
            constants.put("Ka", ka);
            fixedCharge = request.systemType() == ActivityEquilibriumSystemType.CONJUGATE_ACID_SALT ? total.negate(MC) : BigDecimal.ZERO;
            spectatorIons = request.systemType() == ActivityEquilibriumSystemType.CONJUGATE_ACID_SALT
                    ? List.of(new IonicSpeciesConcentration("SPEC-CL-MINUS", total, -1))
                    : List.of();
            return new SystemSpec(SpecKind.FAMILY, total, fixedCharge,
                    List.of(species("BH", conjugateCode(request), 1), species("B", request.speciesCode(), 0)),
                    List.of(ka), false, spectatorIons, constants);
        }
        if (request.systemType() == ActivityEquilibriumSystemType.POLYPROTIC && request.polyproticFamily() != null) {
            PolyproticAcidFamily family = request.polyproticFamily();
            List<AcidSpecies> species = family.species().stream()
                    .map(s -> species(s.speciesCode(), s.speciesCode(), s.charge()))
                    .toList();
            List<BigDecimal> constantsList = family.constants().stream().map(PolyproticDissociationConstant::value).toList();
            for (PolyproticDissociationConstant constant : family.constants()) {
                constants.put("Ka" + constant.stepNumber(), constant.value());
            }
            return new SystemSpec(SpecKind.FAMILY, total, fixedCharge, species, constantsList, family.firstDissociationComplete(), spectatorIons, constants);
        }
        throw new ActivityException(ActivityErrorCode.UNSUPPORTED_EQUILIBRIUM_SYSTEM, "Unsupported activity-corrected equilibrium system");
    }

    private String conjugateCode(ActivityCorrectionRequest request) {
        return switch (request.speciesCode().toUpperCase()) {
            case "SPEC-CH3COOH" -> "SPEC-CH3COO-MINUS";
            case "SPEC-CH3COO-MINUS" -> "SPEC-CH3COOH";
            case "SPEC-NH3" -> "SPEC-NH4-PLUS";
            case "SPEC-NH4-PLUS" -> "SPEC-NH3";
            default -> request.speciesCode() + "-CONJUGATE";
        };
    }

    private AcidSpecies species(String label, String code, int charge) {
        return new AcidSpecies(label, code, charge);
    }

    private PointState state(SystemSpec spec, BigDecimal h, BigDecimal kw, Map<Integer, BigDecimal> gammas) {
        BigDecimal oh = kw.divide(gammas.get(1).multiply(gammas.get(-1), MC).multiply(h, MC), MC);
        List<IonicSpeciesConcentration> ions = new ArrayList<>(spec.spectatorIons());
        ions.add(new IonicSpeciesConcentration("SPEC-H3O-PLUS", h, 1));
        ions.add(new IonicSpeciesConcentration("SPEC-OH-MINUS", oh, -1));
        SpeciesDistribution distribution = null;
        List<BigDecimal> concentrations = List.of();
        if (spec.kind() == SpecKind.FAMILY) {
            concentrations = familyConcentrations(spec, h, gammas);
            List<DistributionFraction> fractions = new ArrayList<>();
            for (int i = 0; i < spec.species().size(); i++) {
                AcidSpecies acidSpecies = spec.species().get(i);
                BigDecimal concentration = concentrations.get(i);
                if (acidSpecies.charge() != 0) {
                    ions.add(new IonicSpeciesConcentration(acidSpecies.code(), concentration, acidSpecies.charge()));
                }
                BigDecimal fraction = spec.total().compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : concentration.divide(spec.total(), MC);
                fractions.add(new DistributionFraction(acidSpecies.code(), spec.species().size() - 1 - i, acidSpecies.charge(), fraction, concentration));
            }
            distribution = new SpeciesDistribution(fractions);
        }
        return new PointState(oh, ions, distribution, concentrations);
    }

    private BigDecimal chargeBalance(SystemSpec spec, BigDecimal h, BigDecimal kw, Map<Integer, BigDecimal> gammas) {
        PointState state = state(spec, h, kw, gammas);
        BigDecimal balance = h.subtract(state.oh(), MC).add(spec.fixedCharge(), MC);
        if (spec.kind() == SpecKind.FAMILY) {
            for (int i = 0; i < spec.species().size(); i++) {
                balance = balance.add(BigDecimal.valueOf(spec.species().get(i).charge()).multiply(state.familyConcentrations().get(i), MC), MC);
            }
        }
        return balance;
    }

    private List<BigDecimal> familyConcentrations(SystemSpec spec, BigDecimal h, Map<Integer, BigDecimal> gammas) {
        List<BigDecimal> ratios = new ArrayList<>();
        ratios.add(spec.firstComplete() ? BigDecimal.ZERO : BigDecimal.ONE);
        BigDecimal cumulative = BigDecimal.ONE;
        int constantIndex = 0;
        int start = spec.firstComplete() ? 1 : 0;
        for (int i = 1; i < spec.species().size(); i++) {
            if (spec.firstComplete() && i == 1) {
                ratios.add(BigDecimal.ONE);
                continue;
            }
            AcidSpecies previous = spec.species().get(i - 1);
            AcidSpecies current = spec.species().get(i);
            BigDecimal k = spec.constantsList().get(constantIndex++);
            BigDecimal numerator = k.multiply(gammas.get(previous.charge()), MC);
            BigDecimal denominator = gammas.get(1).multiply(gammas.get(current.charge()), MC).multiply(h, MC);
            cumulative = (i == start + 1 || !spec.firstComplete()) ? cumulative.multiply(numerator.divide(denominator, MC), MC) : cumulative;
            ratios.add(cumulative);
        }
        BigDecimal denom = ratios.stream().reduce(BigDecimal.ZERO, (a, b) -> a.add(b, MC));
        List<BigDecimal> concentrations = new ArrayList<>();
        for (BigDecimal ratio : ratios) {
            concentrations.add(spec.total().multiply(ratio, MC).divide(denom, MC));
        }
        return concentrations;
    }

    private BigDecimal solveHydronium(HFunction function) {
        BigDecimal lower = LOWER_H;
        BigDecimal upper = BigDecimal.ONE;
        BigDecimal fLower = function.apply(lower);
        BigDecimal fUpper = function.apply(upper);
        while (fLower.signum() == fUpper.signum() && upper.compareTo(new BigDecimal("100")) < 0) {
            upper = upper.multiply(TWO, MC);
            fUpper = function.apply(upper);
        }
        if (fLower.signum() == fUpper.signum()) {
            throw new ActivityException(ActivityErrorCode.SOLVER_CONVERGENCE_FAILED, "Activity charge-balance solver failed to bracket hydronium");
        }
        for (int i = 0; i < MAX_BISECTION_ITERATIONS; i++) {
            BigDecimal mid = lower.add(upper, MC).divide(TWO, MC);
            BigDecimal fMid = function.apply(mid);
            if (fMid.abs().compareTo(SOLVER_TOLERANCE) <= 0) {
                return mid;
            }
            if (fLower.signum() == fMid.signum()) {
                lower = mid;
                fLower = fMid;
            } else {
                upper = mid;
            }
        }
        throw new ActivityException(ActivityErrorCode.SOLVER_CONVERGENCE_FAILED, "Activity charge-balance solver reached maximum iterations");
    }

    private Map<Integer, BigDecimal> gammaMap(BigDecimal ionicStrength, ActivityParameterSet parameterSet) {
        Map<Integer, BigDecimal> values = new LinkedHashMap<>();
        for (int charge : List.of(-2, -1, 0, 1, 2)) {
            values.put(charge, coefficient(charge, ionicStrength, parameterSet));
        }
        return values;
    }

    private BigDecimal coefficient(int charge, BigDecimal ionicStrength, ActivityParameterSet parameterSet) {
        if (parameterSet.model() == ActivityModel.IDEAL || charge == 0 || ionicStrength.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ONE;
        }
        validateDaviesRange(new IonicStrength(ionicStrength), parameterSet);
        BigDecimal sqrtI = ionicStrength.sqrt(MC);
        BigDecimal term = sqrtI.divide(BigDecimal.ONE.add(sqrtI, MC), MC).subtract(new BigDecimal("0.3").multiply(ionicStrength, MC), MC);
        BigDecimal exponent = parameterSet.daviesA().negate(MC)
                .multiply(BigDecimal.valueOf((long) charge * charge), MC)
                .multiply(term, MC);
        return AcidBaseDecimalMath.tenPower(exponent);
    }

    private void validateDaviesRange(IonicStrength strength, ActivityParameterSet parameterSet) {
        if (parameterSet.model() != ActivityModel.DAVIES) {
            return;
        }
        if (strength.value().compareTo(parameterSet.minimumIonicStrength()) < 0 || strength.value().compareTo(parameterSet.maximumIonicStrength()) > 0) {
            throw new ActivityException(ActivityErrorCode.OUTSIDE_MODEL_VALIDITY_RANGE, "Davies model is supported only through ionic strength " + parameterSet.maximumIonicStrength());
        }
    }

    private ActivityParameterSet idealParameterSet(ActivityCorrectionRequest request) {
        return new ActivityParameterSet(ActivityModel.IDEAL, request.solventCode(), request.temperature(), BigDecimal.ONE, BigDecimal.ZERO, new BigDecimal("999"), "ideal", "no activity correction", "n/a");
    }

    private ActivityCorrectedEquilibriumResult withIteration(ActivityCorrectedEquilibriumResult result, ActivityIterationResult iteration) {
        return new ActivityCorrectedEquilibriumResult(result.model(), result.systemType(), result.idealPh(), result.activityPh(), result.activityPoh(),
                result.hydroniumConcentration(), result.hydroxideConcentration(), result.ionicStrength(), result.coefficients(), result.activities(),
                result.distribution(), result.constants(), iteration, result.residual(), result.solverStatus());
    }

    private ActivityCorrectedEquilibriumResult withIdealPh(ActivityCorrectedEquilibriumResult result, PhValue idealPh) {
        return new ActivityCorrectedEquilibriumResult(result.model(), result.systemType(), idealPh, result.activityPh(), result.activityPoh(),
                result.hydroniumConcentration(), result.hydroxideConcentration(), result.ionicStrength(), result.coefficients(), result.activities(),
                result.distribution(), result.constants(), result.iteration(), result.residual(), result.solverStatus());
    }

    private BigDecimal maxGammaDelta(Map<Integer, BigDecimal> previous, Map<Integer, BigDecimal> current) {
        BigDecimal max = BigDecimal.ZERO;
        for (int charge : current.keySet()) {
            max = max.max(current.get(charge).subtract(previous.getOrDefault(charge, BigDecimal.ONE), MC).abs());
        }
        return max;
    }

    private enum SpecKind { WATER, STRONG, FAMILY }

    private record AcidSpecies(String label, String code, int charge) {}

    private record SystemSpec(
            SpecKind kind,
            BigDecimal total,
            BigDecimal fixedCharge,
            List<AcidSpecies> species,
            List<BigDecimal> constantsList,
            boolean firstComplete,
            List<IonicSpeciesConcentration> spectatorIons,
            Map<String, BigDecimal> constants) {}

    private record PointState(
            BigDecimal oh,
            List<IonicSpeciesConcentration> ions,
            SpeciesDistribution distribution,
            List<BigDecimal> familyConcentrations) {}

    @FunctionalInterface
    private interface HFunction {
        BigDecimal apply(BigDecimal h);
    }
}

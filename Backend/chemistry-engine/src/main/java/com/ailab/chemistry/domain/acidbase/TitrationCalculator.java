package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.PhValue;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class TitrationCalculator {

    private static final MathContext MC = AcidBaseDecimalMath.MATH_CONTEXT;
    private static final BigDecimal TWO = new BigDecimal("2");
    private static final BigDecimal FOUR = new BigDecimal("4");
    private static final BigDecimal LOWER_H = new BigDecimal("1e-30");
    private static final BigDecimal UPPER_H_FLOOR = new BigDecimal("1");
    private static final BigDecimal SOLVER_TOLERANCE = new BigDecimal("1e-28");
    private static final int MAX_ITERATIONS = 240;

    public TitrationPointResult calculatePoint(TitrationRequest request, Volume addedTitrantVolume) {
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(addedTitrantVolume, "addedTitrantVolume must not be null");
        requireResolved(request);
        if (addedTitrantVolume.in(VolumeUnit.LITER).compareTo(BigDecimal.ZERO) < 0) {
            throw new TitrationException(TitrationErrorCode.NEGATIVE_TITRANT_VOLUME, "Added titrant volume must not be negative");
        }

        BigDecimal analyteMoles = analyteMoles(request);
        BigDecimal titrantMoles = titrantMoles(request, addedTitrantVolume);
        Volume totalVolume = request.analyteVolume().add(addedTitrantVolume);
        BigDecimal totalLiters = totalVolume.in(VolumeUnit.LITER);
        BigDecimal analyticalConcentration = analyteMoles.divide(totalLiters, MC);
        BigDecimal equivalenceMoles = analyteMoles;
        TitrationRegion region = classifyRegion(request.systemType(), titrantMoles, equivalenceMoles);

        return switch (request.systemType()) {
            case STRONG_ACID_STRONG_BASE -> strongPoint(request, addedTitrantVolume, totalVolume, region,
                    analyteMoles.subtract(titrantMoles, MC), true, analyticalConcentration);
            case STRONG_BASE_STRONG_ACID -> strongPoint(request, addedTitrantVolume, totalVolume, region,
                    analyteMoles.subtract(titrantMoles, MC), false, analyticalConcentration);
            case WEAK_ACID_STRONG_BASE -> weakAcidPoint(request, addedTitrantVolume, totalVolume, region,
                    analyteMoles, titrantMoles, analyticalConcentration);
            case WEAK_BASE_STRONG_ACID -> weakBasePoint(request, addedTitrantVolume, totalVolume, region,
                    analyteMoles, titrantMoles, analyticalConcentration);
            case UNSUPPORTED_WEAK_ACID_WEAK_BASE -> throw new TitrationException(
                    TitrationErrorCode.UNSUPPORTED_TITRATION_SYSTEM, "Weak acid plus weak base titration is not supported");
        };
    }

    public TitrationPointResult calculatePoint(TitrationPointRequest request) {
        return calculatePoint(request.request(), request.addedTitrantVolume());
    }

    public TitrationCurveResult calculateCurve(TitrationRequest request, List<Volume> titrantVolumes) {
        Objects.requireNonNull(titrantVolumes, "titrantVolumes must not be null");
        List<Volume> ordered = new ArrayList<>(titrantVolumes);
        ordered.sort(Comparator.naturalOrder());
        for (int i = 0; i < ordered.size(); i++) {
            BigDecimal liters = ordered.get(i).in(VolumeUnit.LITER);
            if (liters.compareTo(BigDecimal.ZERO) < 0) {
                throw new TitrationException(TitrationErrorCode.NEGATIVE_TITRANT_VOLUME, "Added titrant volume must not be negative");
            }
            if (i > 0 && ordered.get(i).compareTo(ordered.get(i - 1)) == 0) {
                throw new TitrationException(TitrationErrorCode.DUPLICATE_TITRANT_VOLUME, "Duplicate titrant volumes are not allowed");
            }
        }
        List<TitrationPointResult> points = ordered.stream()
                .map(volume -> calculatePoint(request, volume))
                .toList();
        return new TitrationCurveResult(request, points, equivalencePoint(request));
    }

    public TitrationCurveResult calculateCharacteristicPoints(TitrationRequest request) {
        EquivalencePoint equivalence = equivalencePoint(request);
        Volume zero = Volume.of("0.00", VolumeUnit.MILLILITER);
        Volume half = Volume.of(equivalence.volume().in(VolumeUnit.MILLILITER).divide(TWO, MC).setScale(3, RoundingMode.UNNECESSARY), VolumeUnit.MILLILITER);
        Volume doubleEquivalence = equivalence.volume().multiply(TWO);
        return calculateCurve(request, List.of(zero, half, equivalence.volume(), doubleEquivalence));
    }

    public EquivalencePoint equivalencePoint(TitrationRequest request) {
        requireResolved(request);
        BigDecimal liters = analyteMoles(request)
                .divide(request.titrantConcentration().in(MolarConcentrationUnit.MOL_PER_LITER), MC);
        return new EquivalencePoint(Volume.of(liters, VolumeUnit.LITER), request.systemType());
    }

    private TitrationPointResult strongPoint(
            TitrationRequest request,
            Volume addedTitrantVolume,
            Volume totalVolume,
            TitrationRegion region,
            BigDecimal analyteMinusTitrantMoles,
            boolean acidAnalyte,
            BigDecimal analyticalConcentration) {
        BigDecimal kw = request.kw();
        BigDecimal totalLiters = totalVolume.in(VolumeUnit.LITER);
        BigDecimal hydronium;
        TitrationCalculationMethod method;
        if (analyteMinusTitrantMoles.signum() == 0) {
            hydronium = sqrt(kw);
            method = TitrationCalculationMethod.PURE_WATER_EQUIVALENCE;
        } else {
            BigDecimal excess = analyteMinusTitrantMoles.abs().divide(totalLiters, MC);
            boolean acidExcess = acidAnalyte == (analyteMinusTitrantMoles.signum() > 0);
            if (acidExcess) {
                hydronium = acidExcessHydronium(excess, kw);
            } else {
                BigDecimal hydroxide = baseExcessHydroxide(excess, kw);
                hydronium = kw.divide(hydroxide, MC);
            }
            method = TitrationCalculationMethod.STOICHIOMETRIC_STRONG_EXCESS_WITH_WATER_AUTOIONIZATION;
        }
        return result(addedTitrantVolume, totalVolume, hydronium, region, method,
                commonAssumptions(), constants(request), TitrationResidual.zero(), TitrationSolverStatus.CONVERGED, analyticalConcentration);
    }

    private TitrationPointResult weakAcidPoint(
            TitrationRequest request,
            Volume addedTitrantVolume,
            Volume totalVolume,
            TitrationRegion region,
            BigDecimal analyteMoles,
            BigDecimal titrantMoles,
            BigDecimal analyticalConcentration) {
        BigDecimal totalLiters = totalVolume.in(VolumeUnit.LITER);
        BigDecimal formalAcid = analyteMoles.divide(totalLiters, MC);
        BigDecimal strongCation = titrantMoles.divide(totalLiters, MC);
        BigDecimal h = solveHydronium(hydronium -> weakAcidChargeBalance(hydronium, formalAcid, strongCation, request.ka(), request.kw()),
                formalAcid, strongCation);
        BigDecimal aMinus = formalAcid.multiply(request.ka(), MC).divide(h.add(request.ka(), MC), MC);
        BigDecimal ha = formalAcid.subtract(aMinus, MC);
        BigDecimal hydroxide = request.kw().divide(h, MC);
        BigDecimal chargeResidual = h.add(strongCation, MC).subtract(hydroxide, MC).subtract(aMinus, MC).abs();
        BigDecimal massResidual = formalAcid.subtract(ha, MC).subtract(aMinus, MC).abs();
        return result(addedTitrantVolume, totalVolume, h, region,
                TitrationCalculationMethod.CONTINUOUS_WEAK_ACID_CHARGE_BALANCE,
                commonAssumptions(), constants(request), new TitrationResidual(massResidual, chargeResidual),
                TitrationSolverStatus.CONVERGED, analyticalConcentration);
    }

    private TitrationPointResult weakBasePoint(
            TitrationRequest request,
            Volume addedTitrantVolume,
            Volume totalVolume,
            TitrationRegion region,
            BigDecimal analyteMoles,
            BigDecimal titrantMoles,
            BigDecimal analyticalConcentration) {
        BigDecimal totalLiters = totalVolume.in(VolumeUnit.LITER);
        BigDecimal formalBase = analyteMoles.divide(totalLiters, MC);
        BigDecimal strongAnion = titrantMoles.divide(totalLiters, MC);
        BigDecimal conjugateKa = request.kw().divide(request.kb(), MC);
        BigDecimal h = solveHydronium(hydronium -> weakBaseChargeBalance(hydronium, formalBase, strongAnion, conjugateKa, request.kw()),
                formalBase, strongAnion);
        BigDecimal bhPlus = formalBase.multiply(h, MC).divide(h.add(conjugateKa, MC), MC);
        BigDecimal freeBase = formalBase.subtract(bhPlus, MC);
        BigDecimal hydroxide = request.kw().divide(h, MC);
        BigDecimal chargeResidual = h.add(bhPlus, MC).subtract(hydroxide, MC).subtract(strongAnion, MC).abs();
        BigDecimal massResidual = formalBase.subtract(bhPlus, MC).subtract(freeBase, MC).abs();
        return result(addedTitrantVolume, totalVolume, h, region,
                TitrationCalculationMethod.CONTINUOUS_WEAK_BASE_CHARGE_BALANCE,
                commonAssumptions(), constants(request), new TitrationResidual(massResidual, chargeResidual),
                TitrationSolverStatus.CONVERGED, analyticalConcentration);
    }

    private BigDecimal solveHydronium(HydroniumFunction function, BigDecimal formal, BigDecimal strongIon) {
        BigDecimal lower = LOWER_H;
        BigDecimal upper = UPPER_H_FLOOR.max(formal.add(strongIon, MC).add(BigDecimal.ONE, MC));
        BigDecimal fLower = function.apply(lower);
        BigDecimal fUpper = function.apply(upper);
        if (fLower.signum() == 0) {
            return lower;
        }
        if (fUpper.signum() == 0) {
            return upper;
        }
        if (fLower.signum() == fUpper.signum()) {
            throw new TitrationException(TitrationErrorCode.SOLVER_CONVERGENCE_FAILED, "Titration charge-balance solver failed to bracket a hydronium root");
        }
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            BigDecimal mid = lower.add(upper, MC).divide(TWO, MC);
            BigDecimal fMid = function.apply(mid);
            if (fMid.abs().compareTo(SOLVER_TOLERANCE) <= 0 || upper.subtract(lower, MC).abs().compareTo(SOLVER_TOLERANCE) <= 0) {
                return mid;
            }
            if (fLower.signum() == fMid.signum()) {
                lower = mid;
                fLower = fMid;
            } else {
                upper = mid;
            }
        }
        throw new TitrationException(TitrationErrorCode.SOLVER_CONVERGENCE_FAILED, "Titration charge-balance solver reached maximum iterations");
    }

    private BigDecimal weakAcidChargeBalance(BigDecimal h, BigDecimal formalAcid, BigDecimal strongCation, BigDecimal ka, BigDecimal kw) {
        BigDecimal aMinus = formalAcid.multiply(ka, MC).divide(h.add(ka, MC), MC);
        return h.add(strongCation, MC).subtract(kw.divide(h, MC), MC).subtract(aMinus, MC);
    }

    private BigDecimal weakBaseChargeBalance(BigDecimal h, BigDecimal formalBase, BigDecimal strongAnion, BigDecimal conjugateKa, BigDecimal kw) {
        BigDecimal bhPlus = formalBase.multiply(h, MC).divide(h.add(conjugateKa, MC), MC);
        return h.add(bhPlus, MC).subtract(kw.divide(h, MC), MC).subtract(strongAnion, MC);
    }

    private TitrationPointResult result(
            Volume addedTitrantVolume,
            Volume totalVolume,
            BigDecimal hydronium,
            TitrationRegion region,
            TitrationCalculationMethod method,
            List<TitrationAssumption> assumptions,
            Map<String, BigDecimal> constants,
            TitrationResidual residual,
            TitrationSolverStatus solverStatus,
            BigDecimal analyticalConcentration) {
        BigDecimal ph = AcidBaseDecimalMath.log10(hydronium).negate(MC).setScale(4, RoundingMode.HALF_UP);
        BigDecimal pkw = AcidBaseDecimalMath.log10(constants.get("Kw")).negate(MC);
        BigDecimal poh = pkw.subtract(ph, MC).setScale(4, RoundingMode.HALF_UP);
        return new TitrationPointResult(
                addedTitrantVolume,
                totalVolume,
                new PhValue(ph),
                new PhValue(poh),
                region,
                method,
                assumptions,
                constants,
                residual,
                solverStatus,
                MolarConcentration.of(analyticalConcentration, MolarConcentrationUnit.MOL_PER_LITER)
        );
    }

    private static BigDecimal acidExcessHydronium(BigDecimal concentration, BigDecimal kw) {
        return concentration.add(sqrt(concentration.multiply(concentration, MC).add(kw.multiply(FOUR, MC), MC)), MC)
                .divide(TWO, MC);
    }

    private static BigDecimal baseExcessHydroxide(BigDecimal concentration, BigDecimal kw) {
        return concentration.add(sqrt(concentration.multiply(concentration, MC).add(kw.multiply(FOUR, MC), MC)), MC)
                .divide(TWO, MC);
    }

    private static BigDecimal sqrt(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new TitrationException(TitrationErrorCode.NUMERICALLY_UNSAFE_REQUEST, "Square-root input must not be negative");
        }
        return value.sqrt(MC);
    }

    private static BigDecimal analyteMoles(TitrationRequest request) {
        return request.analyteConcentration().in(MolarConcentrationUnit.MOL_PER_LITER)
                .multiply(request.analyteVolume().in(VolumeUnit.LITER), MC);
    }

    private static BigDecimal titrantMoles(TitrationRequest request, Volume addedTitrantVolume) {
        return request.titrantConcentration().in(MolarConcentrationUnit.MOL_PER_LITER)
                .multiply(addedTitrantVolume.in(VolumeUnit.LITER), MC);
    }

    private static TitrationRegion classifyRegion(TitrationSystemType systemType, BigDecimal titrantMoles, BigDecimal equivalenceMoles) {
        int comparison = titrantMoles.compareTo(equivalenceMoles);
        if (titrantMoles.compareTo(BigDecimal.ZERO) == 0) {
            return TitrationRegion.INITIAL;
        }
        if (comparison == 0) {
            return TitrationRegion.EQUIVALENCE;
        }
        if (isWeakSystem(systemType) && titrantMoles.multiply(TWO, MC).compareTo(equivalenceMoles) == 0) {
            return TitrationRegion.HALF_EQUIVALENCE;
        }
        return comparison < 0 ? TitrationRegion.PRE_EQUIVALENCE : TitrationRegion.POST_EQUIVALENCE;
    }

    private static boolean isWeakSystem(TitrationSystemType systemType) {
        return systemType == TitrationSystemType.WEAK_ACID_STRONG_BASE
                || systemType == TitrationSystemType.WEAK_BASE_STRONG_ACID;
    }

    private static Map<String, BigDecimal> constants(TitrationRequest request) {
        Map<String, BigDecimal> constants = new LinkedHashMap<>();
        constants.put("Kw", request.kw());
        if (request.ka() != null) {
            constants.put("Ka", request.ka());
        }
        if (request.kb() != null) {
            constants.put("Kb", request.kb());
        }
        return constants;
    }

    private static List<TitrationAssumption> commonAssumptions() {
        return List.of(
                TitrationAssumption.AQUEOUS_SOLVENT,
                TitrationAssumption.IDEAL_SOLUTION,
                TitrationAssumption.MONOPROTIC_ANALYTE,
                TitrationAssumption.MONOBASIC_TITRANT,
                TitrationAssumption.ADDITIVE_VOLUMES,
                TitrationAssumption.WATER_AUTOIONIZATION_INCLUDED,
                TitrationAssumption.TEMPERATURE_SPECIFIC_CONSTANTS,
                TitrationAssumption.NO_ACTIVITY_COEFFICIENT_CORRECTION
        );
    }

    private static void requireResolved(TitrationRequest request) {
        if (request.systemType() == null || request.kw() == null) {
            throw new TitrationException(TitrationErrorCode.INSUFFICIENT_REFERENCE_DATA, "Titration request must be resolved before calculation");
        }
    }

    @FunctionalInterface
    private interface HydroniumFunction {
        BigDecimal apply(BigDecimal hydronium);
    }
}

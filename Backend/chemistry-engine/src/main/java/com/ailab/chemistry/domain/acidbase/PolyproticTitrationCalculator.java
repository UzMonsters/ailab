package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public final class PolyproticTitrationCalculator {
    private static final MathContext MC = AcidBaseDecimalMath.MATH_CONTEXT;
    private static final BigDecimal TWO = new BigDecimal("2");

    private final PolyproticEquilibriumCalculator equilibriumCalculator;

    public PolyproticTitrationCalculator() {
        this(new PolyproticEquilibriumCalculator());
    }

    public PolyproticTitrationCalculator(PolyproticEquilibriumCalculator equilibriumCalculator) {
        this.equilibriumCalculator = Objects.requireNonNull(equilibriumCalculator, "equilibriumCalculator must not be null");
    }

    public PolyproticTitrationPointResult calculatePoint(PolyproticTitrationRequest request, Volume addedTitrantVolume) {
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(addedTitrantVolume, "addedTitrantVolume must not be null");
        requireResolved(request);
        BigDecimal addedLiters = addedTitrantVolume.in(VolumeUnit.LITER);
        if (addedLiters.compareTo(BigDecimal.ZERO) < 0) {
            throw new PolyproticTitrationException(PolyproticTitrationErrorCode.NEGATIVE_TITRANT_VOLUME, "Added titrant volume must not be negative");
        }
        BigDecimal analyteMoles = analyteMoles(request);
        BigDecimal titrantMoles = titrantMoles(request, addedTitrantVolume);
        Volume totalVolume = request.analyteVolume().add(addedTitrantVolume);
        BigDecimal totalLiters = totalVolume.in(VolumeUnit.LITER);
        BigDecimal familyConcentration = analyteMoles.divide(totalLiters, MC);
        BigDecimal fixedCharge = fixedChargeConcentration(request, analyteMoles, titrantMoles, totalLiters);

        try {
            PolyproticEquilibriumResult equilibrium = equilibriumCalculator.calculateForFixedCharge(
                    request.family(),
                    initialForm(request.systemType()),
                    MolarConcentration.of(familyConcentration, MolarConcentrationUnit.MOL_PER_LITER),
                    request.temperature(),
                    request.solventCode(),
                    fixedCharge,
                    request.kw()
            );
            return new PolyproticTitrationPointResult(
                    addedTitrantVolume,
                    totalVolume,
                    equilibrium.getPh(),
                    equilibrium.getPoh(),
                    equilibrium.getHydroniumConcentration(),
                    equilibrium.getHydroxideConcentration(),
                    MolarConcentration.of(familyConcentration, MolarConcentrationUnit.MOL_PER_LITER),
                    fixedCharge,
                    equilibrium.getDistribution(),
                    classifyRegion(request, titrantMoles),
                    method(request.family()),
                    equilibrium.getConstants(),
                    equilibrium.getAssumptions(),
                    new PolyproticTitrationResidual(equilibrium.getResidual().massBalanceResidual(), equilibrium.getResidual().chargeBalanceResidual()),
                    equilibrium.getSolverStatus()
            );
        } catch (PolyproticException ex) {
            throw translate(ex);
        }
    }

    public PolyproticTitrationCurveResult calculateCurve(PolyproticTitrationRequest request, List<Volume> addedVolumes) {
        Objects.requireNonNull(addedVolumes, "addedVolumes must not be null");
        List<Volume> ordered = new ArrayList<>(addedVolumes);
        ordered.sort(Comparator.naturalOrder());
        for (int i = 0; i < ordered.size(); i++) {
            if (i > 0 && ordered.get(i).compareTo(ordered.get(i - 1)) == 0) {
                throw new PolyproticTitrationException(PolyproticTitrationErrorCode.DUPLICATE_TITRANT_VOLUME, "Duplicate titrant volumes are not allowed");
            }
        }
        List<PolyproticTitrationPointResult> points = ordered.stream()
                .map(volume -> calculatePoint(request, volume))
                .toList();
        return new PolyproticTitrationCurveResult(request, points, equivalencePoints(request));
    }

    public PolyproticTitrationCurveResult calculateCharacteristicPoints(PolyproticTitrationRequest request) {
        Volume first = equivalenceVolume(request, BigDecimal.ONE);
        Volume second = equivalenceVolume(request, TWO);
        List<Volume> volumes = List.of(
                Volume.of("0.00", VolumeUnit.MILLILITER),
                first.divide(TWO),
                first,
                first.add(second).divide(TWO),
                second,
                second.add(first.divide(TWO))
        );
        return calculateCurve(request, volumes);
    }

    public List<PolyproticEquivalencePoint> equivalencePoints(PolyproticTitrationRequest request) {
        return List.of(
                new PolyproticEquivalencePoint(1, equivalenceVolume(request, BigDecimal.ONE), PolyproticTitrationRegion.FIRST_EQUIVALENCE),
                new PolyproticEquivalencePoint(2, equivalenceVolume(request, TWO), PolyproticTitrationRegion.SECOND_EQUIVALENCE)
        );
    }

    private static PolyproticTitrationMethod method(PolyproticAcidFamily family) {
        return family.firstDissociationComplete()
                ? PolyproticTitrationMethod.SULFURIC_FIRST_DISSOCIATION_COMPLETE_CHARGE_BALANCE
                : PolyproticTitrationMethod.CONTINUOUS_POLYPROTIC_CHARGE_BALANCE;
    }

    private static PolyproticInitialForm initialForm(PolyproticTitrationSystemType systemType) {
        return switch (systemType) {
            case DIPROTIC_ACID_WITH_STRONG_MONOBASIC_BASE -> PolyproticInitialForm.FULLY_PROTONATED_ACID;
            case FULLY_DEPROTONATED_SALT_WITH_STRONG_MONOPROTIC_ACID -> PolyproticInitialForm.FULLY_DEPROTONATED_SALT;
            case AMPHIPROTIC_SALT_WITH_STRONG_MONOPROTIC_ACID, AMPHIPROTIC_SALT_WITH_STRONG_MONOBASIC_BASE -> PolyproticInitialForm.INTERMEDIATE_AMPHIPROTIC_SALT;
        };
    }

    private static BigDecimal fixedChargeConcentration(
            PolyproticTitrationRequest request,
            BigDecimal analyteMoles,
            BigDecimal titrantMoles,
            BigDecimal totalLiters) {
        BigDecimal analyteSpectatorMoles = analyteMoles.multiply(analyteSpectatorStoichiometry(request), MC)
                .multiply(BigDecimal.valueOf(request.analyteSpectatorIonCharge()), MC);
        BigDecimal titrantSpectatorMoles = titrantMoles.multiply(BigDecimal.valueOf(request.titrantSpectatorIonCharge()), MC);
        return analyteSpectatorMoles.add(titrantSpectatorMoles, MC).divide(totalLiters, MC);
    }

    private static BigDecimal analyteSpectatorStoichiometry(PolyproticTitrationRequest request) {
        return switch (request.systemType()) {
            case DIPROTIC_ACID_WITH_STRONG_MONOBASIC_BASE -> BigDecimal.ZERO;
            case AMPHIPROTIC_SALT_WITH_STRONG_MONOPROTIC_ACID, AMPHIPROTIC_SALT_WITH_STRONG_MONOBASIC_BASE -> BigDecimal.ONE;
            case FULLY_DEPROTONATED_SALT_WITH_STRONG_MONOPROTIC_ACID -> TWO;
        };
    }

    private static PolyproticTitrationRegion classifyRegion(PolyproticTitrationRequest request, BigDecimal titrantMoles) {
        BigDecimal first = analyteMoles(request);
        BigDecimal second = first.multiply(TWO, MC);
        BigDecimal halfFirst = first.divide(TWO, MC);
        BigDecimal halfSecond = first.add(second, MC).divide(TWO, MC);
        BigDecimal toleranceMoles = request.titrantConcentration().in(MolarConcentrationUnit.MOL_PER_LITER)
                .multiply(request.volumeToleranceLiters(), MC);

        if (within(titrantMoles, BigDecimal.ZERO, toleranceMoles)) {
            return PolyproticTitrationRegion.INITIAL;
        }
        if (within(titrantMoles, halfFirst, toleranceMoles)) {
            return PolyproticTitrationRegion.FIRST_HALF_EQUIVALENCE;
        }
        if (within(titrantMoles, first, toleranceMoles)) {
            return PolyproticTitrationRegion.FIRST_EQUIVALENCE;
        }
        if (within(titrantMoles, halfSecond, toleranceMoles)) {
            return PolyproticTitrationRegion.SECOND_HALF_EQUIVALENCE;
        }
        if (within(titrantMoles, second, toleranceMoles)) {
            return PolyproticTitrationRegion.SECOND_EQUIVALENCE;
        }
        if (titrantMoles.compareTo(first) < 0) {
            return PolyproticTitrationRegion.BEFORE_FIRST_EQUIVALENCE;
        }
        if (titrantMoles.compareTo(second) < 0) {
            return PolyproticTitrationRegion.BETWEEN_EQUIVALENCE_POINTS;
        }
        return PolyproticTitrationRegion.AFTER_SECOND_EQUIVALENCE;
    }

    private static boolean within(BigDecimal value, BigDecimal target, BigDecimal tolerance) {
        return value.subtract(target, MC).abs().compareTo(tolerance) <= 0;
    }

    private static Volume equivalenceVolume(PolyproticTitrationRequest request, BigDecimal equivalents) {
        BigDecimal liters = analyteMoles(request).multiply(equivalents, MC)
                .divide(request.titrantConcentration().in(MolarConcentrationUnit.MOL_PER_LITER), MC)
                .setScale(12, RoundingMode.HALF_UP)
                .stripTrailingZeros();
        return Volume.of(liters, VolumeUnit.LITER);
    }

    private static BigDecimal analyteMoles(PolyproticTitrationRequest request) {
        return request.analyteConcentration().in(MolarConcentrationUnit.MOL_PER_LITER)
                .multiply(request.analyteVolume().in(VolumeUnit.LITER), MC);
    }

    private static BigDecimal titrantMoles(PolyproticTitrationRequest request, Volume addedTitrantVolume) {
        return request.titrantConcentration().in(MolarConcentrationUnit.MOL_PER_LITER)
                .multiply(addedTitrantVolume.in(VolumeUnit.LITER), MC);
    }

    private static void requireResolved(PolyproticTitrationRequest request) {
        if (request.family() == null || request.kw() == null) {
            throw new PolyproticTitrationException(PolyproticTitrationErrorCode.INSUFFICIENT_REFERENCE_DATA, "Titration request must be resolved before pure calculation");
        }
    }

    private static PolyproticTitrationException translate(PolyproticException ex) {
        PolyproticTitrationErrorCode code = switch (ex.getErrorCode()) {
            case SOLVER_CONVERGENCE_FAILED -> PolyproticTitrationErrorCode.SOLVER_CONVERGENCE_FAILED;
            case UNSUPPORTED_SOLVENT -> PolyproticTitrationErrorCode.UNSUPPORTED_SOLVENT;
            case NON_POSITIVE_CONCENTRATION -> PolyproticTitrationErrorCode.NON_POSITIVE_CONCENTRATION;
            default -> PolyproticTitrationErrorCode.INSUFFICIENT_REFERENCE_DATA;
        };
        return new PolyproticTitrationException(code, ex.getMessage());
    }
}

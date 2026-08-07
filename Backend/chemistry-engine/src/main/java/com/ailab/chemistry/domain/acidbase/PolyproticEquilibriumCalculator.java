package com.ailab.chemistry.domain.acidbase;

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

public final class PolyproticEquilibriumCalculator {
    private static final MathContext MC = AcidBaseDecimalMath.MATH_CONTEXT;
    private static final BigDecimal TWO = new BigDecimal("2");
    private static final BigDecimal LOWER_H = new BigDecimal("1e-30");
    private static final BigDecimal SOLVER_TOLERANCE = new BigDecimal("1e-28");
    private static final int MAX_ITERATIONS = 240;
    private static final String WATER = "COMP-H2O";

    public PolyproticEquilibriumResult calculate(PolyproticEquilibriumRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        PolyproticAcidFamily family = request.family();
        if (family == null) {
            throw new PolyproticException(PolyproticErrorCode.INSUFFICIENT_REFERENCE_DATA, "Polyprotic family must be resolved before pure calculation");
        }
        if (!WATER.equalsIgnoreCase(request.solventCode())) {
            throw new PolyproticException(PolyproticErrorCode.UNSUPPORTED_SOLVENT, "Only water solvent COMP-H2O is supported");
        }
        validateSpectator(request);
        BigDecimal total = request.totalAnalyticalConcentration().in(MolarConcentrationUnit.MOL_PER_LITER);
        BigDecimal fixedCharge = total.multiply(request.spectatorStoichiometry(), MC).multiply(BigDecimal.valueOf(request.spectatorIonCharge()), MC);
        return calculateForFixedCharge(family, request.initialForm(), request.totalAnalyticalConcentration(), request.temperature(), request.solventCode(), fixedCharge, request.kw());
    }

    public PolyproticEquilibriumResult calculateForFixedCharge(
            PolyproticAcidFamily family,
            PolyproticInitialForm initialForm,
            com.ailab.chemistry.domain.measurement.MolarConcentration totalAnalyticalConcentration,
            com.ailab.chemistry.domain.measurement.Temperature temperature,
            String solventCode,
            BigDecimal fixedCharge,
            BigDecimal kw) {
        Objects.requireNonNull(family, "family must not be null");
        Objects.requireNonNull(initialForm, "initialForm must not be null");
        Objects.requireNonNull(totalAnalyticalConcentration, "totalAnalyticalConcentration must not be null");
        Objects.requireNonNull(temperature, "temperature must not be null");
        Objects.requireNonNull(fixedCharge, "fixedCharge must not be null");
        Objects.requireNonNull(kw, "kw must not be null");
        if (!WATER.equalsIgnoreCase(solventCode)) {
            throw new PolyproticException(PolyproticErrorCode.UNSUPPORTED_SOLVENT, "Only water solvent COMP-H2O is supported");
        }
        if (kw.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PolyproticException(PolyproticErrorCode.INSUFFICIENT_REFERENCE_DATA, "Kw must be positive");
        }
        BigDecimal total = totalAnalyticalConcentration.in(MolarConcentrationUnit.MOL_PER_LITER);
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PolyproticException(PolyproticErrorCode.NON_POSITIVE_CONCENTRATION, "Total analytical concentration must be positive");
        }
        BigDecimal h = solveHydronium(hydronium -> chargeBalance(family, total, kw, fixedCharge, hydronium));
        SpeciesDistribution distribution = distributionAt(family, h, total);
        BigDecimal oh = kw.divide(h, MC);
        BigDecimal chargeResidual = chargeBalance(family, total, kw, fixedCharge, h).abs();
        BigDecimal massResidual = total.subtract(sumConcentrations(distribution), MC).abs();
        BigDecimal phValue = AcidBaseDecimalMath.log10(h).negate(MC).setScale(4, RoundingMode.HALF_UP);
        BigDecimal pKw = AcidBaseDecimalMath.log10(kw).negate(MC);
        BigDecimal pohValue = pKw.subtract(phValue, MC).setScale(4, RoundingMode.HALF_UP);
        return new PolyproticEquilibriumResult(
                family,
                initialForm,
                new PhValue(phValue),
                new PhValue(pohValue),
                h,
                oh,
                distribution,
                constants(family, kw),
                assumptions(family),
                new PolyproticResidual(massResidual, chargeResidual),
                PolyproticSolverStatus.CONVERGED,
                family.firstDissociationComplete()
                        ? PolyproticCalculationMethod.SULFURIC_FIRST_DISSOCIATION_COMPLETE
                        : PolyproticCalculationMethod.STANDARD_POLYPROTIC_CHARGE_BALANCE
        );
    }

    public List<DistributionFraction> calculateDistribution(PolyproticAcidFamily family, PhValue ph) {
        Objects.requireNonNull(ph, "ph must not be null");
        BigDecimal h = AcidBaseDecimalMath.tenPower(ph.getValue().negate(MC));
        return distributionAt(family, h, BigDecimal.ZERO).fractions();
    }

    private BigDecimal solveHydronium(HydroniumFunction function) {
        BigDecimal lower = LOWER_H;
        BigDecimal upper = BigDecimal.ONE;
        BigDecimal fLower = function.apply(lower);
        BigDecimal fUpper = function.apply(upper);
        while (fLower.signum() == fUpper.signum() && upper.compareTo(new BigDecimal("100")) < 0) {
            upper = upper.multiply(TWO, MC);
            fUpper = function.apply(upper);
        }
        if (fLower.signum() == fUpper.signum()) {
            throw new PolyproticException(PolyproticErrorCode.SOLVER_CONVERGENCE_FAILED, "Polyprotic charge-balance solver failed to bracket hydronium");
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
        throw new PolyproticException(PolyproticErrorCode.SOLVER_CONVERGENCE_FAILED, "Polyprotic charge-balance solver reached maximum iterations");
    }

    private BigDecimal chargeBalance(PolyproticAcidFamily family, BigDecimal total, BigDecimal kw, BigDecimal fixedCharge, BigDecimal h) {
        SpeciesDistribution distribution = distributionAt(family, h, total);
        BigDecimal speciesCharge = distribution.fractions().stream()
                .map(fraction -> BigDecimal.valueOf(fraction.charge()).multiply(fraction.concentration(), MC))
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b, MC));
        return h.subtract(kw.divide(h, MC), MC).add(fixedCharge, MC).add(speciesCharge, MC);
    }

    private SpeciesDistribution distributionAt(PolyproticAcidFamily family, BigDecimal h, BigDecimal total) {
        Objects.requireNonNull(family, "family must not be null");
        List<BigDecimal> numerators = family.firstDissociationComplete()
                ? completeFirstDissociationNumerators(family, h)
                : standardNumerators(family, h);
        BigDecimal denominator = numerators.stream().reduce(BigDecimal.ZERO, (a, b) -> a.add(b, MC));
        if (denominator.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PolyproticException(PolyproticErrorCode.NUMERICALLY_UNSAFE_REQUEST, "Distribution denominator must be positive");
        }
        List<DistributionFraction> fractions = new ArrayList<>();
        for (int i = 0; i < family.species().size(); i++) {
            BigDecimal fraction = numerators.get(i).divide(denominator, MC);
            BigDecimal concentration = total.multiply(fraction, MC);
            PolyproticSpecies species = family.species().get(i);
            fractions.add(new DistributionFraction(species.speciesCode(), species.protonsRemaining(), species.charge(), fraction, concentration));
        }
        return new SpeciesDistribution(fractions);
    }

    private List<BigDecimal> standardNumerators(PolyproticAcidFamily family, BigDecimal h) {
        int n = family.totalProtons();
        List<BigDecimal> values = new ArrayList<>();
        BigDecimal product = BigDecimal.ONE;
        for (int i = 0; i <= n; i++) {
            if (i > 0) {
                product = product.multiply(family.constantForStep(i).value(), MC);
            }
            values.add(product.multiply(h.pow(n - i, MC), MC));
        }
        return values;
    }

    private List<BigDecimal> completeFirstDissociationNumerators(PolyproticAcidFamily family, BigDecimal h) {
        int n = family.totalProtons();
        List<BigDecimal> values = new ArrayList<>();
        values.add(BigDecimal.ZERO);
        BigDecimal product = BigDecimal.ONE;
        for (int i = 1; i <= n; i++) {
            if (i > 1) {
                product = product.multiply(family.constantForStep(i).value(), MC);
            }
            values.add(product.multiply(h.pow(n - i, MC), MC));
        }
        return values;
    }

    private void validateSpectator(PolyproticEquilibriumRequest request) {
        PolyproticSpecies initialSpecies = request.family().speciesForInitialForm(request.initialForm());
        if (initialSpecies.charge() == 0) {
            if (request.spectatorStoichiometry().compareTo(BigDecimal.ZERO) != 0) {
                throw new PolyproticException(PolyproticErrorCode.INVALID_SPECTATOR_STOICHIOMETRY, "Neutral acid initial form must not include spectator ions");
            }
            return;
        }
        if (request.spectatorIonCode() == null || request.spectatorIonCharge() == 0) {
            throw new PolyproticException(PolyproticErrorCode.MISSING_SPECTATOR_ION, "Charged initial forms require spectator ion charge");
        }
        BigDecimal required = BigDecimal.valueOf(-initialSpecies.charge())
                .divide(BigDecimal.valueOf(request.spectatorIonCharge()), MC);
        if (required.compareTo(request.spectatorStoichiometry()) != 0) {
            throw new PolyproticException(PolyproticErrorCode.INVALID_SPECTATOR_STOICHIOMETRY, "Spectator stoichiometry must neutralize the initial species charge");
        }
    }

    private BigDecimal sumConcentrations(SpeciesDistribution distribution) {
        return distribution.fractions().stream()
                .map(DistributionFraction::concentration)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b, MC));
    }

    private Map<String, BigDecimal> constants(PolyproticAcidFamily family, BigDecimal kw) {
        Map<String, BigDecimal> constants = new LinkedHashMap<>();
        constants.put("Kw", kw);
        for (PolyproticDissociationConstant constant : family.constants()) {
            constants.put("Ka" + constant.stepNumber(), constant.value());
        }
        return constants;
    }

    private List<PolyproticAssumption> assumptions(PolyproticAcidFamily family) {
        List<PolyproticAssumption> assumptions = new ArrayList<>(List.of(
                PolyproticAssumption.AQUEOUS_SOLVENT,
                PolyproticAssumption.IDEAL_SOLUTION,
                PolyproticAssumption.TEMPERATURE_SPECIFIC_CONSTANTS,
                PolyproticAssumption.WATER_AUTOIONIZATION_INCLUDED,
                PolyproticAssumption.MASS_BALANCE_ENFORCED,
                PolyproticAssumption.CHARGE_BALANCE_ENFORCED,
                PolyproticAssumption.NO_ACTIVITY_COEFFICIENT_CORRECTION
        ));
        if (family.firstDissociationComplete()) {
            assumptions.add(PolyproticAssumption.FIRST_DISSOCIATION_COMPLETE);
        }
        return assumptions;
    }

    @FunctionalInterface
    private interface HydroniumFunction {
        BigDecimal apply(BigDecimal hydronium);
    }
}

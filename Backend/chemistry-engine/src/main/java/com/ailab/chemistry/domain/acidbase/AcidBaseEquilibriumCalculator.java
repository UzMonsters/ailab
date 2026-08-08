package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.PhValue;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

public final class AcidBaseEquilibriumCalculator {

    private static final MathContext MC = MathContext.DECIMAL128;
    private static final double CONVERGENCE_TOLERANCE = 1.0e-12;
    private static final int MAX_ITERATIONS = 100;

    public AcidBaseEquilibriumResult calculatePureWater(BigDecimal kw) {
        Objects.requireNonNull(kw, "Kw must not be null");
        if (kw.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AcidBaseCalculationException(AcidBaseCalculationErrorCode.MISSING_EQUILIBRIUM_CONSTANT, "Kw must be strictly positive (> 0)");
        }

        double kwDouble = kw.doubleValue();
        BigDecimal pKwExact = p(kw);
        double pKwDouble = pKwExact.doubleValue();
        BigDecimal pKw = pKwExact.setScale(4, RoundingMode.HALF_UP);

        double hDouble = Math.sqrt(kwDouble);
        double ohDouble = hDouble;
        double phDouble = p(BigDecimal.valueOf(hDouble)).doubleValue();
        double pohDouble = pKwDouble - phDouble;

        PhValue ph = new PhValue(BigDecimal.valueOf(phDouble).setScale(4, RoundingMode.HALF_UP));
        PhValue poh = new PhValue(BigDecimal.valueOf(pohDouble).setScale(4, RoundingMode.HALF_UP));

        HydroniumConcentration h3o = HydroniumConcentration.of(BigDecimal.valueOf(hDouble));
        HydroxideConcentration oh = HydroxideConcentration.of(BigDecimal.valueOf(ohDouble));

        return new AcidBaseEquilibriumResult(
                AcidBaseSystemType.PURE_WATER,
                ph,
                poh,
                h3o,
                oh,
                kw,
                pKw,
                null,
                AcidBaseCalculationMethod.PURE_WATER_AUTOIONIZATION,
                List.of(EquilibriumAssumption.IDEAL_SOLUTION, EquilibriumAssumption.WATER_AUTOIONIZATION_INCLUDED),
                EquilibriumResidual.zero(),
                SolverStatus.CONVERGED
        );
    }

    public AcidBaseEquilibriumResult calculateStrongAcid(MolarConcentration concentration, BigDecimal kw) {
        validateInputs(concentration, kw);

        double ca = concentration.in(MolarConcentrationUnit.MOL_PER_LITER).doubleValue();
        double kwDouble = kw.doubleValue();
        BigDecimal pKwExact = p(kw);
        double pKwDouble = pKwExact.doubleValue();
        BigDecimal pKw = pKwExact.setScale(4, RoundingMode.HALF_UP);

        // Quadratic solution: h^2 - Ca*h - Kw = 0 -> h = (Ca + sqrt(Ca^2 + 4Kw)) / 2
        double hDouble = (ca + Math.sqrt(ca * ca + 4.0 * kwDouble)) / 2.0;
        double ohDouble = kwDouble / hDouble;
        double phDouble = p(BigDecimal.valueOf(hDouble)).doubleValue();
        double pohDouble = pKwDouble - phDouble;

        double residualVal = Math.abs(hDouble * hDouble - ca * hDouble - kwDouble);

        PhValue ph = new PhValue(BigDecimal.valueOf(phDouble).setScale(4, RoundingMode.HALF_UP));
        PhValue poh = new PhValue(BigDecimal.valueOf(pohDouble).setScale(4, RoundingMode.HALF_UP));

        HydroniumConcentration h3o = HydroniumConcentration.of(BigDecimal.valueOf(hDouble));
        HydroxideConcentration oh = HydroxideConcentration.of(BigDecimal.valueOf(ohDouble));

        return new AcidBaseEquilibriumResult(
                AcidBaseSystemType.STRONG_ACID,
                ph,
                poh,
                h3o,
                oh,
                kw,
                pKw,
                null,
                AcidBaseCalculationMethod.EXACT_QUADRATIC_STRONG_ELECTROLYTE,
                List.of(EquilibriumAssumption.IDEAL_SOLUTION, EquilibriumAssumption.COMPLETE_DISSOCIATION, EquilibriumAssumption.WATER_AUTOIONIZATION_INCLUDED),
                EquilibriumResidual.of(BigDecimal.valueOf(residualVal)),
                SolverStatus.CONVERGED
        );
    }

    public AcidBaseEquilibriumResult calculateStrongBase(MolarConcentration concentration, BigDecimal kw) {
        validateInputs(concentration, kw);

        double cb = concentration.in(MolarConcentrationUnit.MOL_PER_LITER).doubleValue();
        double kwDouble = kw.doubleValue();
        BigDecimal pKwExact = p(kw);
        double pKwDouble = pKwExact.doubleValue();
        BigDecimal pKw = pKwExact.setScale(4, RoundingMode.HALF_UP);

        // Quadratic solution: oh^2 - Cb*oh - Kw = 0 -> oh = (Cb + sqrt(Cb^2 + 4Kw)) / 2
        double ohDouble = (cb + Math.sqrt(cb * cb + 4.0 * kwDouble)) / 2.0;
        double hDouble = kwDouble / ohDouble;
        double phDouble = p(BigDecimal.valueOf(hDouble)).doubleValue();
        double pohDouble = pKwDouble - phDouble;

        double residualVal = Math.abs(ohDouble * ohDouble - cb * ohDouble - kwDouble);

        PhValue ph = new PhValue(BigDecimal.valueOf(phDouble).setScale(4, RoundingMode.HALF_UP));
        PhValue poh = new PhValue(BigDecimal.valueOf(pohDouble).setScale(4, RoundingMode.HALF_UP));

        HydroniumConcentration h3o = HydroniumConcentration.of(BigDecimal.valueOf(hDouble));
        HydroxideConcentration oh = HydroxideConcentration.of(BigDecimal.valueOf(ohDouble));

        return new AcidBaseEquilibriumResult(
                AcidBaseSystemType.STRONG_BASE,
                ph,
                poh,
                h3o,
                oh,
                kw,
                pKw,
                null,
                AcidBaseCalculationMethod.EXACT_QUADRATIC_STRONG_ELECTROLYTE,
                List.of(EquilibriumAssumption.IDEAL_SOLUTION, EquilibriumAssumption.COMPLETE_DISSOCIATION, EquilibriumAssumption.WATER_AUTOIONIZATION_INCLUDED),
                EquilibriumResidual.of(BigDecimal.valueOf(residualVal)),
                SolverStatus.CONVERGED
        );
    }

    public AcidBaseEquilibriumResult calculateWeakAcid(AcidBaseSystemType systemType, MolarConcentration concentration, BigDecimal ka, BigDecimal kw) {
        validateWeakInputs(concentration, ka, kw);

        double ca = concentration.in(MolarConcentrationUnit.MOL_PER_LITER).doubleValue();
        double kaDouble = ka.doubleValue();
        double kwDouble = kw.doubleValue();
        BigDecimal pKwExact = p(kw);
        double pKwDouble = pKwExact.doubleValue();
        BigDecimal pKw = pKwExact.setScale(4, RoundingMode.HALF_UP);

        // f(h) = h^3 + Ka*h^2 - (Ca*Ka + Kw)*h - Ka*Kw = 0
        double lower = 1.0e-15;
        double upper = Math.sqrt(ca * kaDouble) * 3.0 + Math.sqrt(kwDouble) * 3.0 + 1.0e-7;

        double hDouble = solveCubicRoot(h -> h * h * h + kaDouble * h * h - (ca * kaDouble + kwDouble) * h - kaDouble * kwDouble,
                h -> 3.0 * h * h + 2.0 * kaDouble * h - (ca * kaDouble + kwDouble), lower, upper);

        double ohDouble = kwDouble / hDouble;
        double phDouble = p(BigDecimal.valueOf(hDouble)).doubleValue();
        double pohDouble = pKwDouble - phDouble;

        double residualVal = Math.abs(hDouble * hDouble * hDouble + kaDouble * hDouble * hDouble - (ca * kaDouble + kwDouble) * hDouble - kaDouble * kwDouble);

        PhValue ph = new PhValue(BigDecimal.valueOf(phDouble).setScale(4, RoundingMode.HALF_UP));
        PhValue poh = new PhValue(BigDecimal.valueOf(pohDouble).setScale(4, RoundingMode.HALF_UP));

        HydroniumConcentration h3o = HydroniumConcentration.of(BigDecimal.valueOf(hDouble));
        HydroxideConcentration oh = HydroxideConcentration.of(BigDecimal.valueOf(ohDouble));

        return new AcidBaseEquilibriumResult(
                systemType,
                ph,
                poh,
                h3o,
                oh,
                kw,
                pKw,
                ka,
                AcidBaseCalculationMethod.EXACT_CUBIC_ROOT_SOLVER,
                List.of(EquilibriumAssumption.IDEAL_SOLUTION, EquilibriumAssumption.WATER_AUTOIONIZATION_INCLUDED),
                EquilibriumResidual.of(BigDecimal.valueOf(residualVal)),
                SolverStatus.CONVERGED
        );
    }

    public AcidBaseEquilibriumResult calculateWeakBase(AcidBaseSystemType systemType, MolarConcentration concentration, BigDecimal kb, BigDecimal kw) {
        validateWeakInputs(concentration, kb, kw);

        double cb = concentration.in(MolarConcentrationUnit.MOL_PER_LITER).doubleValue();
        double kbDouble = kb.doubleValue();
        double kwDouble = kw.doubleValue();
        BigDecimal pKwExact = p(kw);
        double pKwDouble = pKwExact.doubleValue();
        BigDecimal pKw = pKwExact.setScale(4, RoundingMode.HALF_UP);

        // g(oh) = oh^3 + Kb*oh^2 - (Cb*Kb + Kw)*oh - Kb*Kw = 0
        double lower = 1.0e-15;
        double upper = Math.sqrt(cb * kbDouble) * 3.0 + Math.sqrt(kwDouble) * 3.0 + 1.0e-7;

        double ohDouble = solveCubicRoot(oh -> oh * oh * oh + kbDouble * oh * oh - (cb * kbDouble + kwDouble) * oh - kbDouble * kwDouble,
                oh -> 3.0 * oh * oh + 2.0 * kbDouble * oh - (cb * kbDouble + kwDouble), lower, upper);

        double hDouble = kwDouble / ohDouble;
        double phDouble = p(BigDecimal.valueOf(hDouble)).doubleValue();
        double pohDouble = pKwDouble - phDouble;

        double residualVal = Math.abs(ohDouble * ohDouble * ohDouble + kbDouble * ohDouble * ohDouble - (cb * kbDouble + kwDouble) * ohDouble - kbDouble * kwDouble);

        PhValue ph = new PhValue(BigDecimal.valueOf(phDouble).setScale(4, RoundingMode.HALF_UP));
        PhValue poh = new PhValue(BigDecimal.valueOf(pohDouble).setScale(4, RoundingMode.HALF_UP));

        HydroniumConcentration h3o = HydroniumConcentration.of(BigDecimal.valueOf(hDouble));
        HydroxideConcentration oh = HydroxideConcentration.of(BigDecimal.valueOf(ohDouble));

        return new AcidBaseEquilibriumResult(
                systemType,
                ph,
                poh,
                h3o,
                oh,
                kw,
                pKw,
                kb,
                AcidBaseCalculationMethod.EXACT_CUBIC_ROOT_SOLVER,
                List.of(EquilibriumAssumption.IDEAL_SOLUTION, EquilibriumAssumption.WATER_AUTOIONIZATION_INCLUDED),
                EquilibriumResidual.of(BigDecimal.valueOf(residualVal)),
                SolverStatus.CONVERGED
        );
    }

    private interface Function {
        double eval(double x);
    }

    private double solveCubicRoot(Function f, Function df, double lower, double upper) {
        double x = (lower + upper) / 2.0;

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            double fx = f.eval(x);
            double scale = Math.abs(x * x * x) + 1.0e-20;
            if (Math.abs(fx) / scale < 1.0e-10 || Math.abs(fx) < 1.0e-20) {
                return x;
            }
            double dfx = df.eval(x);
            double nextX = x - fx / dfx;

            if (nextX <= 0 || Double.isNaN(nextX) || Double.isInfinite(nextX) || nextX > upper * 2) {
                if (fx > 0) {
                    upper = x;
                } else {
                    lower = x;
                }
                nextX = (lower + upper) / 2.0;
            }
            if (Math.abs(nextX - x) / x < 1.0e-12) {
                return nextX;
            }
            x = nextX;
        }

        return x;
    }

    private void validateInputs(MolarConcentration conc, BigDecimal kw) {
        if (conc == null || conc.in(MolarConcentrationUnit.MOL_PER_LITER).compareTo(BigDecimal.ZERO) <= 0) {
            throw new AcidBaseCalculationException(AcidBaseCalculationErrorCode.NON_POSITIVE_CONCENTRATION, "Concentration must be strictly positive (> 0)");
        }
        if (kw == null || kw.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AcidBaseCalculationException(AcidBaseCalculationErrorCode.MISSING_EQUILIBRIUM_CONSTANT, "Kw constant must be strictly positive (> 0)");
        }
    }

    private void validateWeakInputs(MolarConcentration conc, BigDecimal k, BigDecimal kw) {
        validateInputs(conc, kw);
        if (k == null || k.compareTo(BigDecimal.ZERO) <= 0) {
            throw new AcidBaseCalculationException(AcidBaseCalculationErrorCode.MISSING_EQUILIBRIUM_CONSTANT, "Equilibrium constant K (Ka/Kb) must be strictly positive (> 0)");
        }
    }

    private BigDecimal p(BigDecimal k) {
        return AcidBaseDecimalMath.log10(k).negate(MC);
    }
}

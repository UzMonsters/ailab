package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.equation.RationalNumber;
import com.ailab.chemistry.domain.measurement.ScientificMath;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public final class ThermodynamicEquilibriumCalculator {
    public static final BigDecimal GAS_CONSTANT_J_PER_MOL_K = new BigDecimal("8.31446261815324");
    private static final BigDecimal THOUSAND = new BigDecimal("1000");
    private static final BigDecimal DIRECTION_TOLERANCE_KJ_PER_MOL = new BigDecimal("0.001");

    public StandardEquilibriumConstant standardConstant(BigDecimal deltaGibbsStandardKjPerMol, Temperature temperature) {
        return standardConstant(deltaGibbsStandardKjPerMol, temperature, PhaseStabilityStatus.PHASE_STABILITY_NOT_EVALUATED);
    }

    public StandardEquilibriumConstant standardConstant(BigDecimal deltaGibbsStandardKjPerMol, Temperature temperature,
                                                        PhaseStabilityStatus phaseStabilityStatus) {
        Objects.requireNonNull(deltaGibbsStandardKjPerMol, "deltaGibbsStandardKjPerMol must not be null");
        validateTemperature(temperature);
        BigDecimal rt = rt(temperature);
        BigDecimal lnK = deltaGibbsStandardKjPerMol.multiply(THOUSAND, ScientificMath.CALCULATION_CONTEXT)
                .negate()
                .divide(rt, ScientificMath.CALCULATION_CONTEXT)
                .stripTrailingZeros();
        BigDecimal log10K = lnK.divide(ThermodynamicDecimalMath.LN_10, ScientificMath.CALCULATION_CONTEXT).stripTrailingZeros();
        Optional<BigDecimal> direct = ThermodynamicDecimalMath.safelyExponentiable(lnK)
                ? Optional.of(ThermodynamicDecimalMath.exp(lnK))
                : Optional.empty();
        return new StandardEquilibriumConstant(lnK, log10K, direct, phaseStabilityStatus);
    }

    public ReactionQuotient reactionQuotient(ReactionThermodynamicVector vector, ReactionActivityInput input) {
        Objects.requireNonNull(vector, "vector must not be null");
        Objects.requireNonNull(input, "input must not be null");
        BigDecimal lnQ = BigDecimal.ZERO;
        for (ReactionThermodynamicVectorTerm term : vector.terms()) {
            ParticipantActivity activity = input.find(term.compoundCode(), term.state())
                    .orElseThrow(() -> new EquilibriumException(EquilibriumErrorCode.MISSING_PARTICIPANT_ACTIVITY,
                            "Missing activity for " + term.key()));
            BigDecimal lnActivity = ThermodynamicDecimalMath.ln(activity.activity());
            lnQ = lnQ.add(scale(lnActivity, term.coefficient()), ScientificMath.CALCULATION_CONTEXT);
        }
        lnQ = lnQ.stripTrailingZeros();
        BigDecimal log10Q = lnQ.divide(ThermodynamicDecimalMath.LN_10, ScientificMath.CALCULATION_CONTEXT).stripTrailingZeros();
        Optional<BigDecimal> direct = ThermodynamicDecimalMath.safelyExponentiable(lnQ)
                ? Optional.of(ThermodynamicDecimalMath.exp(lnQ))
                : Optional.empty();
        return new ReactionQuotient(lnQ, log10Q, direct);
    }

    public NonstandardGibbsResult nonstandardGibbs(StandardEquilibriumConstant constant, ReactionQuotient quotient,
                                                   Temperature temperature) {
        Objects.requireNonNull(constant, "constant must not be null");
        Objects.requireNonNull(quotient, "quotient must not be null");
        validateTemperature(temperature);
        BigDecimal rt = rt(temperature);
        BigDecimal standard = rt.multiply(constant.lnK(), ScientificMath.CALCULATION_CONTEXT)
                .divide(THOUSAND, ScientificMath.CALCULATION_CONTEXT)
                .negate()
                .stripTrailingZeros();
        BigDecimal nonstandard = standard.add(rt.multiply(quotient.lnQ(), ScientificMath.CALCULATION_CONTEXT)
                        .divide(THOUSAND, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT)
                .stripTrailingZeros();
        return new NonstandardGibbsResult(null, EquilibriumCalculationStatus.CALCULABLE, null, quotient,
                standard, nonstandard, direction(nonstandard), TemperatureCorrectionCoverage.complete("DOMAIN"),
                constant.phaseStabilityStatus(), EquilibriumCalculationMethod.NONSTANDARD_GIBBS,
                "Thermodynamic driving force only; no kinetic prediction is implied.");
    }

    private static ThermodynamicDirection direction(BigDecimal deltaGibbsKjPerMol) {
        if (deltaGibbsKjPerMol.abs(ScientificMath.CALCULATION_CONTEXT).compareTo(DIRECTION_TOLERANCE_KJ_PER_MOL) <= 0) {
            return ThermodynamicDirection.EQUILIBRIUM_WITHIN_TOLERANCE;
        }
        return deltaGibbsKjPerMol.compareTo(BigDecimal.ZERO) < 0
                ? ThermodynamicDirection.FORWARD_THERMODYNAMIC_DRIVING_FORCE
                : ThermodynamicDirection.REVERSE_THERMODYNAMIC_DRIVING_FORCE;
    }

    private static BigDecimal rt(Temperature temperature) {
        return GAS_CONSTANT_J_PER_MOL_K.multiply(temperature.in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT);
    }

    private static void validateTemperature(Temperature temperature) {
        Objects.requireNonNull(temperature, "temperature must not be null");
        if (temperature.in(TemperatureUnit.KELVIN).compareTo(BigDecimal.ZERO) <= 0) {
            throw new EquilibriumException(EquilibriumErrorCode.INVALID_TEMPERATURE,
                    "Target temperature must be greater than 0 K");
        }
    }

    private static BigDecimal scale(BigDecimal value, RationalNumber multiplier) {
        return value.multiply(new BigDecimal(multiplier.getNumerator()), ScientificMath.CALCULATION_CONTEXT)
                .divide(new BigDecimal(multiplier.getDenominator()), ScientificMath.CALCULATION_CONTEXT);
    }
}

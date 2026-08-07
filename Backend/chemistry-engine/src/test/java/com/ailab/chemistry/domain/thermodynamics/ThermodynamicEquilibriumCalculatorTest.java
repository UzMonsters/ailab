package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.equation.RationalNumber;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ThermodynamicEquilibriumCalculatorTest {
    private static final Temperature T298 = Temperature.of("298.15", TemperatureUnit.KELVIN);
    private static final Pressure P1BAR = Pressure.of("1.000", PressureUnit.BAR);

    private final ThermodynamicEquilibriumCalculator calculator = new ThermodynamicEquilibriumCalculator();

    @Test
    void waterVapourFormationStandardConstantUsesDimensionlessLogarithms() {
        var constant = calculator.standardConstant(new BigDecimal("-457.144"), T298);

        assertThat(constant.lnK()).isCloseTo(new BigDecimal("184.409812"), offset("0.000001"));
        assertThat(constant.log10K()).isCloseTo(new BigDecimal("80.088164"), offset("0.000001"));
        assertThat(constant.directK()).isPresent();
        assertThat(constant.phaseStabilityStatus()).isEqualTo(PhaseStabilityStatus.PHASE_STABILITY_NOT_EVALUATED);
    }

    @Test
    void reactionQuotientUsesSignedStoichiometricCoefficientsAndGasStandardPressure() {
        var quotient = calculator.reactionQuotient(waterVapourVector(), ReactionActivityInput.of(List.of(
                ParticipantActivity.idealGasPartialPressure("COMP-H2O", Pressure.of("0.200", PressureUnit.BAR), P1BAR),
                ParticipantActivity.idealGasPartialPressure("COMP-H2", Pressure.of("0.100", PressureUnit.BAR), P1BAR),
                ParticipantActivity.idealGasPartialPressure("COMP-O2", Pressure.of("0.210", PressureUnit.BAR), P1BAR)
        )));

        assertThat(quotient.lnQ()).isCloseTo(new BigDecimal("2.946942"), offset("0.000001"));
        assertThat(quotient.log10Q()).isCloseTo(new BigDecimal("1.279841"), offset("0.000001"));
    }

    @Test
    void nonstandardGibbsUsesStandardGibbsPlusRtLnQ() {
        var constant = calculator.standardConstant(new BigDecimal("-457.144"), T298);
        var quotient = calculator.reactionQuotient(waterVapourVector(), ReactionActivityInput.of(List.of(
                ParticipantActivity.explicitDimensionless("COMP-H2O", MatterState.GAS, new BigDecimal("0.2")),
                ParticipantActivity.explicitDimensionless("COMP-H2", MatterState.GAS, new BigDecimal("0.1")),
                ParticipantActivity.explicitDimensionless("COMP-O2", MatterState.GAS, new BigDecimal("0.21"))
        )));

        var result = calculator.nonstandardGibbs(constant, quotient, T298);

        assertThat(result.deltaGibbsKjPerMol()).isCloseTo(new BigDecimal("-449.838657"), offset("0.000001"));
        assertThat(result.direction()).isEqualTo(ThermodynamicDirection.FORWARD_THERMODYNAMIC_DRIVING_FORCE);
    }

    @Test
    void equilibriumConditionHasZeroDrivingForceWhenLnQEqualsLnK() {
        var constant = calculator.standardConstant(new BigDecimal("-1.718282"), T298);
        var quotient = calculator.reactionQuotient(ReactionThermodynamicVector.of(List.of(
                new ReactionThermodynamicVectorTerm("COMP-X", MatterState.GAS, RationalNumber.ONE)
        )), ReactionActivityInput.of(List.of(
                ParticipantActivity.explicitDimensionless("COMP-X", MatterState.GAS, new BigDecimal("1.99966968"))
        )));

        var result = calculator.nonstandardGibbs(constant, quotient, T298);

        assertThat(result.deltaGibbsKjPerMol()).isCloseTo(BigDecimal.ZERO, offset("0.001"));
        assertThat(result.direction()).isEqualTo(ThermodynamicDirection.EQUILIBRIUM_WITHIN_TOLERANCE);
    }

    @Test
    void directionTracksWhetherQIsBelowOrAboveK() {
        var constant = calculator.standardConstant(new BigDecimal("-1.718282"), T298);
        var vector = ReactionThermodynamicVector.of(List.of(
                new ReactionThermodynamicVectorTerm("COMP-X", MatterState.GAS, RationalNumber.ONE)
        ));

        var forward = calculator.nonstandardGibbs(constant, calculator.reactionQuotient(vector,
                ReactionActivityInput.of(List.of(ParticipantActivity.explicitDimensionless("COMP-X", MatterState.GAS, BigDecimal.ONE)))), T298);
        var reverse = calculator.nonstandardGibbs(constant, calculator.reactionQuotient(vector,
                ReactionActivityInput.of(List.of(ParticipantActivity.explicitDimensionless("COMP-X", MatterState.GAS, new BigDecimal("4"))))), T298);

        assertThat(forward.direction()).isEqualTo(ThermodynamicDirection.FORWARD_THERMODYNAMIC_DRIVING_FORCE);
        assertThat(reverse.direction()).isEqualTo(ThermodynamicDirection.REVERSE_THERMODYNAMIC_DRIVING_FORCE);
    }

    @Test
    void reversalNegatesLnKAndScalingMultipliesLnK() {
        var forward = calculator.standardConstant(new BigDecimal("-514.382"), T298);
        var reverse = calculator.standardConstant(new BigDecimal("514.382"), T298);
        var doubled = calculator.standardConstant(new BigDecimal("-1028.764"), T298);

        assertThat(reverse.lnK()).isCloseTo(forward.lnK().negate(), offset("0.000001"));
        assertThat(doubled.lnK()).isCloseTo(forward.lnK().multiply(new BigDecimal("2")), offset("0.000001"));
    }

    @Test
    void pureCondensedPhaseActivitiesAreOneOnlyWhenExplicitlyPresent() {
        var water = ParticipantActivity.pureLiquid("COMP-H2O");
        var calcite = ParticipantActivity.pureSolid("COMP-CACO3");

        assertThat(water.activity()).isEqualByComparingTo("1");
        assertThat(water.basis()).isEqualTo(ActivityBasis.PURE_LIQUID);
        assertThat(calcite.activity()).isEqualByComparingTo("1");
        assertThat(calcite.basis()).isEqualTo(ActivityBasis.PURE_SOLID);
    }

    @Test
    void invalidActivityInputsAreRejected() {
        assertThatThrownBy(() -> ParticipantActivity.explicitDimensionless("COMP-H2", MatterState.GAS, BigDecimal.ZERO))
                .isInstanceOf(EquilibriumException.class)
                .extracting("errorCode")
                .isEqualTo(EquilibriumErrorCode.INVALID_ACTIVITY);

        assertThatThrownBy(() -> new ParticipantActivity("COMP-H2O", MatterState.GAS, ActivityBasis.PURE_LIQUID,
                BigDecimal.ONE, null, null, null, null))
                .isInstanceOf(EquilibriumException.class)
                .extracting("errorCode")
                .isEqualTo(EquilibriumErrorCode.CONFLICTING_ACTIVITY_BASIS);
    }

    @Test
    void missingParticipantActivityIsRejected() {
        assertThatThrownBy(() -> calculator.reactionQuotient(waterVapourVector(), ReactionActivityInput.of(List.of(
                ParticipantActivity.explicitDimensionless("COMP-H2O", MatterState.GAS, new BigDecimal("0.2"))
        )))).isInstanceOf(EquilibriumException.class)
                .extracting("errorCode")
                .isEqualTo(EquilibriumErrorCode.MISSING_PARTICIPANT_ACTIVITY);
    }

    @Test
    void directKOverflowIsRepresentedByLogarithmsOnly() {
        var constant = calculator.standardConstant(new BigDecimal("-10000"), T298);

        assertThat(constant.lnK()).isGreaterThan(new BigDecimal("4000"));
        assertThat(constant.directK()).isEmpty();
    }

    @Test
    void thermodynamicDecimalMathRoundTripsDeterministically() {
        var values = List.of(new BigDecimal("1E-12"), new BigDecimal("0.1"), BigDecimal.ONE,
                BigDecimal.TEN, new BigDecimal("1E12"));
        for (BigDecimal value : values) {
            var roundTrip = ThermodynamicDecimalMath.exp(ThermodynamicDecimalMath.ln(value));
            assertThat(roundTrip).isCloseTo(value, offset(value.abs().multiply(new BigDecimal("1E-12"))));
            assertThat(ThermodynamicDecimalMath.pow10(ThermodynamicDecimalMath.log10(value)))
                    .isCloseTo(value, offset(value.abs().multiply(new BigDecimal("1E-12"))));
        }
    }

    private static ReactionThermodynamicVector waterVapourVector() {
        return ReactionThermodynamicVector.of(List.of(
                new ReactionThermodynamicVectorTerm("COMP-H2O", MatterState.GAS, RationalNumber.of(2, 1)),
                new ReactionThermodynamicVectorTerm("COMP-H2", MatterState.GAS, RationalNumber.of(-2, 1)),
                new ReactionThermodynamicVectorTerm("COMP-O2", MatterState.GAS, RationalNumber.of(-1, 1))
        ));
    }

    private static org.assertj.core.data.Offset<BigDecimal> offset(String value) {
        return org.assertj.core.data.Offset.offset(new BigDecimal(value));
    }

    private static org.assertj.core.data.Offset<BigDecimal> offset(BigDecimal value) {
        return org.assertj.core.data.Offset.offset(value);
    }
}

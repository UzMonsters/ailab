package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.ScientificMath;

import java.math.BigDecimal;
import java.util.Objects;

public record ParticipantActivity(
        String compoundCode,
        MatterState state,
        ActivityBasis basis,
        BigDecimal activity,
        String speciesCode,
        BigDecimal concentrationMolPerLiter,
        BigDecimal activityCoefficient,
        Integer ionicCharge) {

    public ParticipantActivity {
        if (compoundCode == null || compoundCode.isBlank()) {
            throw new EquilibriumException(EquilibriumErrorCode.INVALID_ACTIVITY, "Compound code must be present");
        }
        Objects.requireNonNull(state, "state must not be null");
        Objects.requireNonNull(basis, "basis must not be null");
        Objects.requireNonNull(activity, "activity must not be null");
        if (activity.compareTo(BigDecimal.ZERO) <= 0 || !Double.isFinite(activity.doubleValue())) {
            throw new EquilibriumException(EquilibriumErrorCode.INVALID_ACTIVITY,
                    "Thermodynamic activity must be positive and finite");
        }
        if (basis == ActivityBasis.PURE_LIQUID && state != MatterState.LIQUID) {
            throw new EquilibriumException(EquilibriumErrorCode.CONFLICTING_ACTIVITY_BASIS,
                    "PURE_LIQUID activity requires a liquid participant");
        }
        if (basis == ActivityBasis.PURE_SOLID && state != MatterState.SOLID) {
            throw new EquilibriumException(EquilibriumErrorCode.CONFLICTING_ACTIVITY_BASIS,
                    "PURE_SOLID activity requires a solid participant");
        }
        if (basis == ActivityBasis.IDEAL_GAS_PARTIAL_PRESSURE && state != MatterState.GAS) {
            throw new EquilibriumException(EquilibriumErrorCode.CONFLICTING_ACTIVITY_BASIS,
                    "Ideal-gas activity requires a gas participant");
        }
    }

    public static ParticipantActivity idealGasPartialPressure(String compoundCode, Pressure partialPressure, Pressure standardPressure) {
        Objects.requireNonNull(partialPressure, "partialPressure must not be null");
        Objects.requireNonNull(standardPressure, "standardPressure must not be null");
        BigDecimal activity = partialPressure.in(PressureUnit.PASCAL)
                .divide(standardPressure.in(PressureUnit.PASCAL), ScientificMath.CALCULATION_CONTEXT);
        return new ParticipantActivity(compoundCode, MatterState.GAS, ActivityBasis.IDEAL_GAS_PARTIAL_PRESSURE,
                activity, null, null, null, null);
    }

    public static ParticipantActivity explicitDimensionless(String compoundCode, MatterState state, BigDecimal activity) {
        return new ParticipantActivity(compoundCode, state, ActivityBasis.EXPLICIT_DIMENSIONLESS_ACTIVITY,
                activity, null, null, null, null);
    }

    public static ParticipantActivity pureLiquid(String compoundCode) {
        return new ParticipantActivity(compoundCode, MatterState.LIQUID, ActivityBasis.PURE_LIQUID,
                BigDecimal.ONE, null, null, null, null);
    }

    public static ParticipantActivity pureSolid(String compoundCode) {
        return new ParticipantActivity(compoundCode, MatterState.SOLID, ActivityBasis.PURE_SOLID,
                BigDecimal.ONE, null, null, null, null);
    }

    public static ParticipantActivity aqueous(String compoundCode, String speciesCode, BigDecimal concentrationMolPerLiter,
                                              BigDecimal activityCoefficient, ActivityBasis basis) {
        return aqueous(compoundCode, speciesCode, concentrationMolPerLiter, activityCoefficient, basis, 0);
    }

    public static ParticipantActivity aqueous(String compoundCode, String speciesCode, BigDecimal concentrationMolPerLiter,
                                              BigDecimal activityCoefficient, ActivityBasis basis, int ionicCharge) {
        if (basis != ActivityBasis.AQUEOUS_IDEAL && basis != ActivityBasis.AQUEOUS_DAVIES) {
            throw new EquilibriumException(EquilibriumErrorCode.CONFLICTING_ACTIVITY_BASIS,
                    "Aqueous activity requires an aqueous basis");
        }
        BigDecimal gamma = activityCoefficient == null ? BigDecimal.ONE : activityCoefficient;
        BigDecimal activity = concentrationMolPerLiter.multiply(gamma, ScientificMath.CALCULATION_CONTEXT);
        return new ParticipantActivity(compoundCode, MatterState.UNKNOWN, basis, activity,
                speciesCode, concentrationMolPerLiter, gamma, ionicCharge);
    }

    public String key() {
        return compoundCode + "|" + state.name();
    }
}

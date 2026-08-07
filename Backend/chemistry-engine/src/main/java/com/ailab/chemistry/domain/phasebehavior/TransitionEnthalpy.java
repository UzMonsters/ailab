package com.ailab.chemistry.domain.phasebehavior;

import com.ailab.chemistry.domain.measurement.MolarEnergy;
import com.ailab.chemistry.domain.measurement.MolarEnergyUnit;

import java.math.BigDecimal;
import java.util.Objects;

public record TransitionEnthalpy(MolarEnergy value, String originalValue, String originalUnit, String uncertainty) {
    public TransitionEnthalpy {
        Objects.requireNonNull(value, "value must not be null");
        if (value.in(MolarEnergyUnit.JOULE_PER_MOLE).compareTo(BigDecimal.ZERO) <= 0) {
            throw new PhaseBehaviorException(PhaseBehaviorErrorCode.MISSING_TRANSITION_RECORD, "Stored forward transition enthalpy must be positive");
        }
    }
}

package com.ailab.chemistry.domain.gas;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;

import java.util.Objects;

public record GasMixtureComponent(String compoundCode, AmountOfSubstance amount) {
    public GasMixtureComponent {
        Objects.requireNonNull(compoundCode, "compoundCode must not be null");
        Objects.requireNonNull(amount, "amount must not be null");
    }
}

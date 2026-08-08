package com.ailab.chemistry.domain.phasebehavior;

public enum PhaseTransitionType {
    FUSION,
    FREEZING,
    VAPORIZATION,
    CONDENSATION,
    SUBLIMATION,
    DEPOSITION;

    public boolean absorbsHeat() {
        return this == FUSION || this == VAPORIZATION || this == SUBLIMATION;
    }

    public PhaseTransitionType forwardType() {
        return switch (this) {
            case FREEZING -> FUSION;
            case CONDENSATION -> VAPORIZATION;
            case DEPOSITION -> SUBLIMATION;
            default -> this;
        };
    }
}

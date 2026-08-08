package com.ailab.chemistry.domain.thermodynamics;

public enum EquilibriumCompositionStatus {
    CONVERGED,
    INITIAL_STATE_AT_EQUILIBRIUM,
    BOUNDED_AT_FORWARD_LIMIT,
    BOUNDED_AT_REVERSE_LIMIT,
    NO_VALID_ROOT,
    INCOMPLETE_COVERAGE,
    NON_CONVERGENT,
    INVALID_INPUT
}

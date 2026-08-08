package com.ailab.chemistry.domain.thermodynamics;

public enum CalorimetryStatus {
    SUCCESS,
    CONVERGED,
    INCOMPLETE_COVERAGE,
    CORRELATION_OUT_OF_RANGE,
    PHASE_CHANGE_REQUIRED,
    NON_CONVERGENT,
    INVALID_INPUT
}

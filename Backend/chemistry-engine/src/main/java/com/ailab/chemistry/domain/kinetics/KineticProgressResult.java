package com.ailab.chemistry.domain.kinetics;

import java.util.List;
import java.util.Objects;

public record KineticProgressResult(
        String reactionCode,
        List<KineticProgressPoint> points,
        KineticResidual residual,
        KineticSolverStatus status,
        String explanation,
        List<String> assumptions) {
    public KineticProgressResult {
        Objects.requireNonNull(reactionCode, "reactionCode must not be null");
        points = points == null ? List.of() : List.copyOf(points);
        assumptions = assumptions == null ? List.of() : List.copyOf(assumptions);
    }
}

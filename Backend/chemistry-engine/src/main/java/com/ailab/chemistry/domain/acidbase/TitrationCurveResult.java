package com.ailab.chemistry.domain.acidbase;

import java.util.List;
import java.util.Objects;

public record TitrationCurveResult(
        TitrationRequest request,
        List<TitrationPointResult> points,
        EquivalencePoint equivalencePoint
) {
    public TitrationCurveResult {
        Objects.requireNonNull(request, "request must not be null");
        points = List.copyOf(Objects.requireNonNull(points));
        Objects.requireNonNull(equivalencePoint, "equivalencePoint must not be null");
    }

    public List<TitrationPointResult> getPoints() {
        return points;
    }

    public EquivalencePoint getEquivalencePoint() {
        return equivalencePoint;
    }
}

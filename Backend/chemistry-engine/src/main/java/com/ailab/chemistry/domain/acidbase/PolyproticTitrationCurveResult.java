package com.ailab.chemistry.domain.acidbase;

import java.util.List;
import java.util.Objects;

public record PolyproticTitrationCurveResult(
        PolyproticTitrationRequest request,
        List<PolyproticTitrationPointResult> points,
        List<PolyproticEquivalencePoint> equivalencePoints
) {
    public PolyproticTitrationCurveResult {
        Objects.requireNonNull(request, "request must not be null");
        points = List.copyOf(Objects.requireNonNull(points, "points must not be null"));
        equivalencePoints = List.copyOf(Objects.requireNonNull(equivalencePoints, "equivalencePoints must not be null"));
    }
}

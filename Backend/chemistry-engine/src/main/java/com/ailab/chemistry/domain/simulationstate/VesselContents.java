package com.ailab.chemistry.domain.simulationstate;

import java.math.BigDecimal;
import java.util.List;

public record VesselContents(List<MaterialPortion> portions) {
    public VesselContents {
        portions = List.copyOf(portions == null ? List.of() : portions);
    }

    public BigDecimal quantity(String compoundCode, String unit) {
        return portions.stream()
                .filter(portion -> portion.compoundCode().equals(compoundCode) && portion.unit().equals(unit))
                .map(MaterialPortion::quantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

package com.ailab.chemistry.domain.element.property;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ElementPhysicalProperties {
    private final List<DensityDatum> densities;
    private final List<PhaseTransitionDatum> phaseTransitions;

    public ElementPhysicalProperties(List<DensityDatum> densities, List<PhaseTransitionDatum> phaseTransitions) {
        this.densities = densities != null ? List.copyOf(densities) : Collections.emptyList();
        this.phaseTransitions = phaseTransitions != null ? List.copyOf(phaseTransitions) : Collections.emptyList();
    }

    public List<DensityDatum> getDensities() { return densities; }
    public List<PhaseTransitionDatum> getPhaseTransitions() { return phaseTransitions; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementPhysicalProperties that = (ElementPhysicalProperties) o;
        return Objects.equals(densities, that.densities) &&
                Objects.equals(phaseTransitions, that.phaseTransitions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(densities, phaseTransitions);
    }
}

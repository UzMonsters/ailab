package com.ailab.chemistry.domain.element.property;

import java.util.Objects;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;

public final class PhaseTransitionDatum {
    private final PhaseTransitionKind kind;
    private final Temperature temperature;
    private final Pressure referencePressure;
    private final TransitionBehavior behavior;
    private final ScientificEvidenceStatus evidenceStatus;
    private final PropertyProvenance provenance;

    public PhaseTransitionDatum(
            PhaseTransitionKind kind,
            Temperature temperature,
            Pressure referencePressure,
            TransitionBehavior behavior,
            ScientificEvidenceStatus evidenceStatus,
            PropertyProvenance provenance) {
        this.kind = Objects.requireNonNull(kind, "Transition kind must not be null");
        this.temperature = temperature;
        this.referencePressure = referencePressure;
        this.behavior = behavior != null ? behavior : TransitionBehavior.NORMAL_TRANSITION;
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = Objects.requireNonNull(provenance, "Provenance must not be null");
    }

    public PhaseTransitionKind getKind() { return kind; }
    public Temperature getTemperature() { return temperature; }
    public Pressure getReferencePressure() { return referencePressure; }
    public TransitionBehavior getBehavior() { return behavior; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public PropertyProvenance getProvenance() { return provenance; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhaseTransitionDatum that = (PhaseTransitionDatum) o;
        return kind == that.kind &&
                Objects.equals(temperature, that.temperature) &&
                Objects.equals(referencePressure, that.referencePressure) &&
                behavior == that.behavior;
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, temperature, referencePressure, behavior);
    }
}

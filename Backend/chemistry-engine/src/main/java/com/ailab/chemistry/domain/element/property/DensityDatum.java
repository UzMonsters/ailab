package com.ailab.chemistry.domain.element.property;

import java.util.Objects;
import com.ailab.chemistry.domain.element.StandardState;
import com.ailab.chemistry.domain.measurement.Density;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;

public final class DensityDatum {
    private final Density density;
    private final Temperature referenceTemperature;
    private final Pressure referencePressure;
    private final StandardState referenceState;
    private final ScientificEvidenceStatus evidenceStatus;
    private final PropertyProvenance provenance;

    public DensityDatum(
            Density density,
            Temperature referenceTemperature,
            Pressure referencePressure,
            StandardState referenceState,
            ScientificEvidenceStatus evidenceStatus,
            PropertyProvenance provenance) {
        this.density = Objects.requireNonNull(density, "Density must not be null");
        this.referenceTemperature = referenceTemperature;
        this.referencePressure = referencePressure;
        this.referenceState = referenceState;
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = Objects.requireNonNull(provenance, "Provenance must not be null");
    }

    public Density getDensity() { return density; }
    public Temperature getReferenceTemperature() { return referenceTemperature; }
    public Pressure getReferencePressure() { return referencePressure; }
    public StandardState getReferenceState() { return referenceState; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public PropertyProvenance getProvenance() { return provenance; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DensityDatum that = (DensityDatum) o;
        return Objects.equals(density, that.density) &&
                Objects.equals(referenceTemperature, that.referenceTemperature) &&
                Objects.equals(referencePressure, that.referencePressure) &&
                referenceState == that.referenceState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(density, referenceTemperature, referencePressure, referenceState);
    }
}

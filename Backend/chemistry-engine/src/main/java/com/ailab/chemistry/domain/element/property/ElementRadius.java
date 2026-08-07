package com.ailab.chemistry.domain.element.property;

import java.math.BigDecimal;
import java.util.Objects;
import com.ailab.chemistry.domain.measurement.Length;

public final class ElementRadius {
    private final RadiusKind kind;
    private final Length radius;
    private final IonicRadiusContext ionicContext;
    private final ScientificEvidenceStatus evidenceStatus;
    private final PropertyProvenance provenance;

    public ElementRadius(
            RadiusKind kind,
            Length radius,
            IonicRadiusContext ionicContext,
            ScientificEvidenceStatus evidenceStatus,
            PropertyProvenance provenance) {
        this.kind = Objects.requireNonNull(kind, "Radius kind must not be null");
        this.radius = Objects.requireNonNull(radius, "Radius length must not be null");
        if (radius.getValueInMeters().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ElementPropertyException(
                    ElementPropertyErrorCode.INVALID_RADIUS,
                    "Radius must be strictly positive (> 0): " + radius
            );
        }
        if (kind == RadiusKind.IONIC && ionicContext == null) {
            throw new ElementPropertyException(
                    ElementPropertyErrorCode.INVALID_IONIC_RADIUS_CONTEXT,
                    "Ionic radius requires an ionic radius context"
            );
        }
        if (kind != RadiusKind.IONIC && ionicContext != null) {
            throw new ElementPropertyException(
                    ElementPropertyErrorCode.INVALID_IONIC_RADIUS_CONTEXT,
                    "Non-ionic radius must not contain ionic context"
            );
        }
        this.ionicContext = ionicContext;
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = Objects.requireNonNull(provenance, "Provenance must not be null");
    }

    public RadiusKind getKind() { return kind; }
    public Length getRadius() { return radius; }
    public IonicRadiusContext getIonicContext() { return ionicContext; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public PropertyProvenance getProvenance() { return provenance; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementRadius that = (ElementRadius) o;
        return kind == that.kind &&
                Objects.equals(radius, that.radius) &&
                Objects.equals(ionicContext, that.ionicContext);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, radius, ionicContext);
    }
}

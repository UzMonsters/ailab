package com.ailab.chemistry.domain.physicalproperty;

import com.ailab.chemistry.domain.measurement.Temperature;

public final class CompoundPhaseTransitionDatum {

    public enum TransitionType {
        MELTING, BOILING, SUBLIMATION, DECOMPOSITION, GLASS_TRANSITION, OTHER
    }

    public enum TransitionBehavior {
        NORMAL_TRANSITION, DECOMPOSES, SUBLIMES, ESTIMATED, PREDICTED, NOT_APPLICABLE, UNKNOWN
    }

    private final TransitionType transitionType;
    private final Temperature temperature;
    private final PropertyReferenceConditions conditions;
    private final TransitionBehavior behavior;
    private final ScientificEvidenceStatus evidenceStatus;
    private final ScientificProvenance provenance;

    public CompoundPhaseTransitionDatum(TransitionType transitionType, Temperature temperature, PropertyReferenceConditions conditions, TransitionBehavior behavior, ScientificEvidenceStatus evidenceStatus, ScientificProvenance provenance) {
        if (transitionType == null) {
            throw new CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode.INVALID_PHASE_TRANSITION_DATUM, "Transition type cannot be null");
        }
        this.transitionType = transitionType;
        this.temperature = temperature;
        this.conditions = conditions != null ? conditions : PropertyReferenceConditions.stp(null);
        this.behavior = behavior != null ? behavior : TransitionBehavior.NORMAL_TRANSITION;
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = provenance != null ? provenance : ScientificProvenance.crcHandbook104th("Phase transition datum");
    }

    public TransitionType getTransitionType() { return transitionType; }
    public Temperature getTemperature() { return temperature; }
    public PropertyReferenceConditions getConditions() { return conditions; }
    public TransitionBehavior getBehavior() { return behavior; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public ScientificProvenance getProvenance() { return provenance; }
}

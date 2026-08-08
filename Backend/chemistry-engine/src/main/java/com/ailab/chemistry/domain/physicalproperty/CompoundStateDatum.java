package com.ailab.chemistry.domain.physicalproperty;

import com.ailab.chemistry.domain.element.MatterState;

public final class CompoundStateDatum {
    private final MatterState state;
    private final PropertyReferenceConditions conditions;
    private final ScientificEvidenceStatus evidenceStatus;
    private final ScientificProvenance provenance;

    public CompoundStateDatum(MatterState state, PropertyReferenceConditions conditions, ScientificEvidenceStatus evidenceStatus, ScientificProvenance provenance) {
        this.state = state != null ? state : MatterState.UNKNOWN;
        this.conditions = conditions != null ? conditions : PropertyReferenceConditions.stp(state);
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ScientificEvidenceStatus.EVALUATED;
        this.provenance = provenance != null ? provenance : ScientificProvenance.crcHandbook104th("Standard state");
    }

    public MatterState getState() { return state; }
    public PropertyReferenceConditions getConditions() { return conditions; }
    public ScientificEvidenceStatus getEvidenceStatus() { return evidenceStatus; }
    public ScientificProvenance getProvenance() { return provenance; }
}

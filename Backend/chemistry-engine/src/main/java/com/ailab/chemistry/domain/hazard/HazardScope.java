package com.ailab.chemistry.domain.hazard;

import com.ailab.chemistry.domain.element.MatterState;

import java.math.BigDecimal;
import java.util.Objects;

public final class HazardScope {
    private final String compoundCode;
    private final MatterState matterState;
    private final PhysicalForm physicalForm;
    private final String purityOrFormulation;
    private final BigDecimal minConcentration;
    private final BigDecimal maxConcentration;
    private final String concentrationBasis;
    private final String temperatureRange;
    private final String pressureRange;
    private final String particleSizeNotes;
    private final String scopeDescription;

    public HazardScope(String compoundCode, MatterState matterState, PhysicalForm physicalForm, String purityOrFormulation, BigDecimal minConcentration, BigDecimal maxConcentration, String concentrationBasis, String temperatureRange, String pressureRange, String particleSizeNotes, String scopeDescription) {
        if (minConcentration != null && maxConcentration != null && minConcentration.compareTo(maxConcentration) > 0) {
            throw new HazardException(HazardErrorCode.INVALID_CONCENTRATION_SCOPE, "minConcentration cannot be greater than maxConcentration");
        }
        this.compoundCode = compoundCode;
        this.matterState = matterState != null ? matterState : MatterState.UNKNOWN;
        this.physicalForm = physicalForm != null ? physicalForm : PhysicalForm.UNSPECIFIED;
        this.purityOrFormulation = purityOrFormulation;
        this.minConcentration = minConcentration;
        this.maxConcentration = maxConcentration;
        this.concentrationBasis = concentrationBasis;
        this.temperatureRange = temperatureRange;
        this.pressureRange = pressureRange;
        this.particleSizeNotes = particleSizeNotes;
        this.scopeDescription = scopeDescription != null ? scopeDescription : "Pure substance or specified concentration scope";
    }

    public static HazardScope pureSubstance(String compoundCode, MatterState matterState) {
        return new HazardScope(compoundCode, matterState, PhysicalForm.UNSPECIFIED, "Pure substance", new BigDecimal("100.0"), new BigDecimal("100.0"), "% w/w", null, null, null, "Pure substance reference scope");
    }

    public String getCompoundCode() { return compoundCode; }
    public MatterState getMatterState() { return matterState; }
    public PhysicalForm getPhysicalForm() { return physicalForm; }
    public String getPurityOrFormulation() { return purityOrFormulation; }
    public BigDecimal getMinConcentration() { return minConcentration; }
    public BigDecimal getMaxConcentration() { return maxConcentration; }
    public String getConcentrationBasis() { return concentrationBasis; }
    public String getTemperatureRange() { return temperatureRange; }
    public String getPressureRange() { return pressureRange; }
    public String getParticleSizeNotes() { return particleSizeNotes; }
    public String getScopeDescription() { return scopeDescription; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HazardScope that = (HazardScope) o;
        return Objects.equals(compoundCode, that.compoundCode) &&
               matterState == that.matterState &&
               physicalForm == that.physicalForm &&
               Objects.equals(minConcentration, that.minConcentration) &&
               Objects.equals(maxConcentration, that.maxConcentration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compoundCode, matterState, physicalForm, minConcentration, maxConcentration);
    }
}

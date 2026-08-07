package com.ailab.chemistry.domain.reaction;

import com.ailab.chemistry.domain.measurement.PhRange;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.Temperature;

import java.util.Objects;
import java.util.UUID;

public final class ReactionConditionSet {
    private final UUID id;
    private final Temperature temperature;
    private final Pressure pressure;
    private final String medium;
    private final ReactionAtmosphere atmosphere;
    private final PhRange phRange;
    private final EnergyInput energyInput;
    private final String concentrationNotes;
    private final String description;
    private final ReactionEvidenceStatus evidenceStatus;
    private final ReactionProvenance provenance;

    public ReactionConditionSet(UUID id, Temperature temperature, Pressure pressure, String medium,
                                ReactionAtmosphere atmosphere, PhRange phRange, EnergyInput energyInput,
                                String concentrationNotes, String description,
                                ReactionEvidenceStatus evidenceStatus, ReactionProvenance provenance) {
        this.id = id != null ? id : UUID.randomUUID();
        this.temperature = temperature;
        this.pressure = pressure;
        this.medium = medium != null ? medium.trim() : "";
        this.atmosphere = atmosphere != null ? atmosphere : ReactionAtmosphere.UNSPECIFIED;
        this.phRange = phRange;
        this.energyInput = energyInput != null ? energyInput : EnergyInput.NONE;
        this.concentrationNotes = concentrationNotes != null ? concentrationNotes.trim() : "";
        this.description = description != null ? description.trim() : "";
        this.evidenceStatus = evidenceStatus != null ? evidenceStatus : ReactionEvidenceStatus.CURATED_AUTHORITATIVE;
        this.provenance = provenance;
    }

    public UUID getId() {
        return id;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public Pressure getPressure() {
        return pressure;
    }

    public String getMedium() {
        return medium;
    }

    public ReactionAtmosphere getAtmosphere() {
        return atmosphere;
    }

    public PhRange getPhRange() {
        return phRange;
    }

    public EnergyInput getEnergyInput() {
        return energyInput;
    }

    public String getConcentrationNotes() {
        return concentrationNotes;
    }

    public String getDescription() {
        return description;
    }

    public ReactionEvidenceStatus getEvidenceStatus() {
        return evidenceStatus;
    }

    public ReactionProvenance getProvenance() {
        return provenance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionConditionSet that = (ReactionConditionSet) o;
        return Objects.equals(medium, that.medium) &&
                atmosphere == that.atmosphere &&
                energyInput == that.energyInput &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(medium, atmosphere, energyInput, description);
    }
}

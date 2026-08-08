package com.ailab.chemistry.infrastructure.persistence.reaction;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "reaction_condition_sets", schema = "chemistry")
public class ReactionConditionSetEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reaction_id", nullable = false)
    private ReactionEntity reaction;

    @Column(name = "temperature_value")
    private String temperatureValue;

    @Column(name = "pressure_value")
    private String pressureValue;

    @Column(name = "medium")
    private String medium;

    @Column(name = "atmosphere")
    private String atmosphere;

    @Column(name = "energy_input")
    private String energyInput;

    @Column(name = "concentration_notes")
    private String concentrationNotes;

    @Column(name = "description")
    private String description;

    @Column(name = "evidence_status")
    private String evidenceStatus;

    @Column(name = "source_document_id")
    private String sourceDocumentId;

    public ReactionConditionSetEntity() {}

    public ReactionConditionSetEntity(UUID id, ReactionEntity reaction, String temperatureValue, String pressureValue,
                                    String medium, String atmosphere, String energyInput, String concentrationNotes,
                                    String description, String evidenceStatus, String sourceDocumentId) {
        this.id = id;
        this.reaction = reaction;
        this.temperatureValue = temperatureValue;
        this.pressureValue = pressureValue;
        this.medium = medium;
        this.atmosphere = atmosphere;
        this.energyInput = energyInput;
        this.concentrationNotes = concentrationNotes;
        this.description = description;
        this.evidenceStatus = evidenceStatus;
        this.sourceDocumentId = sourceDocumentId;
    }

    public UUID getId() {
        return id;
    }

    public ReactionEntity getReaction() {
        return reaction;
    }

    public String getTemperatureValue() {
        return temperatureValue;
    }

    public String getPressureValue() {
        return pressureValue;
    }

    public String getMedium() {
        return medium;
    }

    public String getAtmosphere() {
        return atmosphere;
    }

    public String getEnergyInput() {
        return energyInput;
    }

    public String getConcentrationNotes() {
        return concentrationNotes;
    }

    public String getDescription() {
        return description;
    }

    public String getEvidenceStatus() {
        return evidenceStatus;
    }

    public String getSourceDocumentId() {
        return sourceDocumentId;
    }
}

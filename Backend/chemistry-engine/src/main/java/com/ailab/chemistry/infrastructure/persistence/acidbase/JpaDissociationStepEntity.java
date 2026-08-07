package com.ailab.chemistry.infrastructure.persistence.acidbase;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "dissociation_steps", schema = "chemistry")
public class JpaDissociationStepEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "acid_species_code", nullable = false, length = 64)
    private String acidSpeciesCode;

    @Column(name = "deprotonated_species_code", nullable = false, length = 64)
    private String deprotonatedSpeciesCode;

    @Column(name = "step_number", nullable = false)
    private int stepNumber;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public JpaDissociationStepEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getAcidSpeciesCode() { return acidSpeciesCode; }
    public void setAcidSpeciesCode(String acidSpeciesCode) { this.acidSpeciesCode = acidSpeciesCode; }
    public String getDeprotonatedSpeciesCode() { return deprotonatedSpeciesCode; }
    public void setDeprotonatedSpeciesCode(String deprotonatedSpeciesCode) { this.deprotonatedSpeciesCode = deprotonatedSpeciesCode; }
    public int getStepNumber() { return stepNumber; }
    public void setStepNumber(int stepNumber) { this.stepNumber = stepNumber; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

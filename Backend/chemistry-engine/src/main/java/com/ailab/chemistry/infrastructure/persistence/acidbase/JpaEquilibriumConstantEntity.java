package com.ailab.chemistry.infrastructure.persistence.acidbase;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "equilibrium_constants", schema = "chemistry")
public class JpaEquilibriumConstantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "species_code", nullable = false, length = 64)
    private String speciesCode;

    @Column(name = "type", nullable = false, length = 16)
    private String type;

    @Column(name = "step_number", nullable = false)
    private int stepNumber;

    @Column(name = "k_value", nullable = false, precision = 38, scale = 16)
    private BigDecimal kValue;

    @Column(name = "p_value", nullable = false, precision = 10, scale = 4)
    private BigDecimal pValue;

    @Column(name = "temperature_celsius", nullable = false, precision = 6, scale = 2)
    private BigDecimal temperatureCelsius;

    @Column(name = "solvent_code", nullable = false, length = 64)
    private String solventCode;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public JpaEquilibriumConstantEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getSpeciesCode() { return speciesCode; }
    public void setSpeciesCode(String speciesCode) { this.speciesCode = speciesCode; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getStepNumber() { return stepNumber; }
    public void setStepNumber(int stepNumber) { this.stepNumber = stepNumber; }
    public BigDecimal getkValue() { return kValue; }
    public void setkValue(BigDecimal kValue) { this.kValue = kValue; }
    public BigDecimal getpValue() { return pValue; }
    public void setpValue(BigDecimal pValue) { this.pValue = pValue; }
    public BigDecimal getTemperatureCelsius() { return temperatureCelsius; }
    public void setTemperatureCelsius(BigDecimal temperatureCelsius) { this.temperatureCelsius = temperatureCelsius; }
    public String getSolventCode() { return solventCode; }
    public void setSolventCode(String solventCode) { this.solventCode = solventCode; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

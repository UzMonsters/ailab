package com.ailab.chemistry.infrastructure.persistence.acidbase;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "ionic_activity_parameter_sets", schema = "chemistry")
public class JpaActivityParameterSetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "model", nullable = false, length = 32)
    private String model;

    @Column(name = "solvent_code", nullable = false, length = 64)
    private String solventCode;

    @Column(name = "temperature_celsius", nullable = false, precision = 6, scale = 2)
    private BigDecimal temperatureCelsius;

    @Column(name = "davies_a", nullable = false, precision = 12, scale = 8)
    private BigDecimal daviesA;

    @Column(name = "min_ionic_strength", nullable = false, precision = 12, scale = 8)
    private BigDecimal minIonicStrength;

    @Column(name = "max_ionic_strength", nullable = false, precision = 12, scale = 8)
    private BigDecimal maxIonicStrength;

    @Column(name = "source_document", nullable = false)
    private String sourceDocument;

    @Column(name = "evidence", nullable = false)
    private String evidence;

    @Column(name = "license", nullable = false)
    private String license;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public UUID getId() { return id; }
    public String getModel() { return model; }
    public String getSolventCode() { return solventCode; }
    public BigDecimal getTemperatureCelsius() { return temperatureCelsius; }
    public BigDecimal getDaviesA() { return daviesA; }
    public BigDecimal getMinIonicStrength() { return minIonicStrength; }
    public BigDecimal getMaxIonicStrength() { return maxIonicStrength; }
    public String getSourceDocument() { return sourceDocument; }
    public String getEvidence() { return evidence; }
    public String getLicense() { return license; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}

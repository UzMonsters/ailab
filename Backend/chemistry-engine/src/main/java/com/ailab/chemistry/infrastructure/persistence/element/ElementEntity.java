package com.ailab.chemistry.infrastructure.persistence.element;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "elements", schema = "chemistry")
public class ElementEntity {
    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(name = "atomic_number", nullable = false, unique = true)
    private int atomicNumber;

    @Column(nullable = false, unique = true, length = 5)
    private String symbol;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "latin_name", length = 100)
    private String latinName;

    @Column(name = "atomic_mass_value", nullable = false, precision = 20, scale = 10)
    private BigDecimal atomicMassValue;

    @Column(name = "atomic_mass_kind", nullable = false, length = 50)
    private String atomicMassKind;

    @Column(name = "atomic_mass_lower_bound", precision = 20, scale = 10)
    private BigDecimal atomicMassLowerBound;

    @Column(name = "atomic_mass_upper_bound", precision = 20, scale = 10)
    private BigDecimal atomicMassUpperBound;

    @Column(name = "period_number", nullable = false)
    private int periodNumber;

    @Column(name = "group_number")
    private Integer groupNumber;

    @Column(nullable = false, length = 5)
    private String block;

    @Column(name = "electron_configuration", nullable = false, length = 100)
    private String electronConfiguration;

    @Column(name = "electron_configuration_status", nullable = false, length = 50)
    private String electronConfigurationStatus;

    @Column(name = "standard_state", nullable = false, length = 20)
    private String standardState;

    @Column(name = "radioactivity_status", nullable = false, length = 50)
    private String radioactivityStatus;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false, length = 50)
    private String series;

    @Column(name = "catalog_version_id", nullable = false, length = 50)
    private String catalogVersionId;

    @Column(name = "source_reference", length = 500)
    private String sourceReference;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public int getAtomicNumber() { return atomicNumber; }
    public void setAtomicNumber(int atomicNumber) { this.atomicNumber = atomicNumber; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLatinName() { return latinName; }
    public void setLatinName(String latinName) { this.latinName = latinName; }
    public BigDecimal getAtomicMassValue() { return atomicMassValue; }
    public void setAtomicMassValue(BigDecimal atomicMassValue) { this.atomicMassValue = atomicMassValue; }
    public String getAtomicMassKind() { return atomicMassKind; }
    public void setAtomicMassKind(String atomicMassKind) { this.atomicMassKind = atomicMassKind; }
    public BigDecimal getAtomicMassLowerBound() { return atomicMassLowerBound; }
    public void setAtomicMassLowerBound(BigDecimal atomicMassLowerBound) { this.atomicMassLowerBound = atomicMassLowerBound; }
    public BigDecimal getAtomicMassUpperBound() { return atomicMassUpperBound; }
    public void setAtomicMassUpperBound(BigDecimal atomicMassUpperBound) { this.atomicMassUpperBound = atomicMassUpperBound; }
    public int getPeriodNumber() { return periodNumber; }
    public void setPeriodNumber(int periodNumber) { this.periodNumber = periodNumber; }
    public Integer getGroupNumber() { return groupNumber; }
    public void setGroupNumber(Integer groupNumber) { this.groupNumber = groupNumber; }
    public String getBlock() { return block; }
    public void setBlock(String block) { this.block = block; }
    public String getElectronConfiguration() { return electronConfiguration; }
    public void setElectronConfiguration(String electronConfiguration) { this.electronConfiguration = electronConfiguration; }
    public String getElectronConfigurationStatus() { return electronConfigurationStatus; }
    public void setElectronConfigurationStatus(String electronConfigurationStatus) { this.electronConfigurationStatus = electronConfigurationStatus; }
    public String getStandardState() { return standardState; }
    public void setStandardState(String standardState) { this.standardState = standardState; }
    public String getRadioactivityStatus() { return radioactivityStatus; }
    public void setRadioactivityStatus(String radioactivityStatus) { this.radioactivityStatus = radioactivityStatus; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSeries() { return series; }
    public void setSeries(String series) { this.series = series; }
    public String getCatalogVersionId() { return catalogVersionId; }
    public void setCatalogVersionId(String catalogVersionId) { this.catalogVersionId = catalogVersionId; }
    public String getSourceReference() { return sourceReference; }
    public void setSourceReference(String sourceReference) { this.sourceReference = sourceReference; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

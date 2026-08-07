package com.ailab.chemistry.infrastructure.persistence.compound;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "compounds", schema = "chemistry")
public class CompoundEntity {

    @Id
    private UUID id;

    @Column(name = "compound_code", nullable = false)
    private String compoundCode;

    @Column(name = "primary_name", nullable = false)
    private String primaryName;

    @Column(name = "original_formula", nullable = false)
    private String originalFormula;

    @Column(name = "normalized_formula", nullable = false)
    private String normalizedFormula;

    @Column(name = "composition_formula", nullable = false)
    private String compositionFormula;

    @Column(name = "net_charge", nullable = false)
    private int netCharge;

    @Column(name = "hydrate_info")
    private String hydrateInfo;

    @Column(name = "molar_mass_value", nullable = false)
    private BigDecimal molarMassValue;

    @Column(name = "molar_mass_lower_bound")
    private BigDecimal molarMassLowerBound;

    @Column(name = "molar_mass_upper_bound")
    private BigDecimal molarMassUpperBound;

    @Column(name = "molar_mass_kind", nullable = false)
    private String molarMassKind;

    @Column(name = "element_catalog_version", nullable = false)
    private String elementCatalogVersion;

    @Column(name = "compound_catalog_version_id", nullable = false)
    private String compoundCatalogVersionId;

    @Column(name = "source_identifier", nullable = false)
    private String sourceIdentifier;

    @Column(name = "source_title", nullable = false)
    private String sourceTitle;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "compound", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CompoundAliasEntity> aliases = new ArrayList<>();

    @OneToMany(mappedBy = "compound", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CompoundComponentEntity> components = new ArrayList<>();

    @OneToMany(mappedBy = "compound", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CompoundExternalIdentifierEntity> externalIdentifiers = new ArrayList<>();

    public CompoundEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCompoundCode() { return compoundCode; }
    public void setCompoundCode(String compoundCode) { this.compoundCode = compoundCode; }

    public String getPrimaryName() { return primaryName; }
    public void setPrimaryName(String primaryName) { this.primaryName = primaryName; }

    public String getOriginalFormula() { return originalFormula; }
    public void setOriginalFormula(String originalFormula) { this.originalFormula = originalFormula; }

    public String getNormalizedFormula() { return normalizedFormula; }
    public void setNormalizedFormula(String normalizedFormula) { this.normalizedFormula = normalizedFormula; }

    public String getCompositionFormula() { return compositionFormula; }
    public void setCompositionFormula(String compositionFormula) { this.compositionFormula = compositionFormula; }

    public int getNetCharge() { return netCharge; }
    public void setNetCharge(int netCharge) { this.netCharge = netCharge; }

    public String getHydrateInfo() { return hydrateInfo; }
    public void setHydrateInfo(String hydrateInfo) { this.hydrateInfo = hydrateInfo; }

    public BigDecimal getMolarMassValue() { return molarMassValue; }
    public void setMolarMassValue(BigDecimal molarMassValue) { this.molarMassValue = molarMassValue; }

    public BigDecimal getMolarMassLowerBound() { return molarMassLowerBound; }
    public void setMolarMassLowerBound(BigDecimal molarMassLowerBound) { this.molarMassLowerBound = molarMassLowerBound; }

    public BigDecimal getMolarMassUpperBound() { return molarMassUpperBound; }
    public void setMolarMassUpperBound(BigDecimal molarMassUpperBound) { this.molarMassUpperBound = molarMassUpperBound; }

    public String getMolarMassKind() { return molarMassKind; }
    public void setMolarMassKind(String molarMassKind) { this.molarMassKind = molarMassKind; }

    public String getElementCatalogVersion() { return elementCatalogVersion; }
    public void setElementCatalogVersion(String elementCatalogVersion) { this.elementCatalogVersion = elementCatalogVersion; }

    public String getCompoundCatalogVersionId() { return compoundCatalogVersionId; }
    public void setCompoundCatalogVersionId(String compoundCatalogVersionId) { this.compoundCatalogVersionId = compoundCatalogVersionId; }

    public String getSourceIdentifier() { return sourceIdentifier; }
    public void setSourceIdentifier(String sourceIdentifier) { this.sourceIdentifier = sourceIdentifier; }

    public String getSourceTitle() { return sourceTitle; }
    public void setSourceTitle(String sourceTitle) { this.sourceTitle = sourceTitle; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public List<CompoundAliasEntity> getAliases() { return aliases; }
    public void setAliases(List<CompoundAliasEntity> aliases) { this.aliases = aliases; }

    public List<CompoundComponentEntity> getComponents() { return components; }
    public void setComponents(List<CompoundComponentEntity> components) { this.components = components; }

    public List<CompoundExternalIdentifierEntity> getExternalIdentifiers() { return externalIdentifiers; }
    public void setExternalIdentifiers(List<CompoundExternalIdentifierEntity> externalIdentifiers) { this.externalIdentifiers = externalIdentifiers; }
}

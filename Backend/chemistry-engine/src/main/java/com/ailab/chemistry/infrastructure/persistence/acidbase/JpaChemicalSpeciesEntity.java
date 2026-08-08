package com.ailab.chemistry.infrastructure.persistence.acidbase;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "chemical_species", schema = "chemistry")
public class JpaChemicalSpeciesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "species_code", nullable = false, unique = true, length = 64)
    private String speciesCode;

    @Column(name = "name", nullable = false, length = 128)
    private String name;

    @Column(name = "formula", nullable = false, length = 128)
    private String formula;

    @Column(name = "kind", nullable = false, length = 32)
    private String kind;

    @Column(name = "charge", nullable = false)
    private int charge;

    @Column(name = "primary_role", nullable = false, length = 32)
    private String primaryRole;

    @Column(name = "dissociation_behavior", nullable = false, length = 32)
    private String dissociationBehavior = "WEAK_ELECTROLYTE";

    @Column(name = "associated_compound_code", length = 64)
    private String associatedCompoundCode;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public JpaChemicalSpeciesEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getSpeciesCode() { return speciesCode; }
    public void setSpeciesCode(String speciesCode) { this.speciesCode = speciesCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFormula() { return formula; }
    public void setFormula(String formula) { this.formula = formula; }
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    public int getCharge() { return charge; }
    public void setCharge(int charge) { this.charge = charge; }
    public String getPrimaryRole() { return primaryRole; }
    public void setPrimaryRole(String primaryRole) { this.primaryRole = primaryRole; }
    public String getDissociationBehavior() { return dissociationBehavior; }
    public void setDissociationBehavior(String dissociationBehavior) { this.dissociationBehavior = dissociationBehavior; }
    public String getAssociatedCompoundCode() { return associatedCompoundCode; }
    public void setAssociatedCompoundCode(String associatedCompoundCode) { this.associatedCompoundCode = associatedCompoundCode; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

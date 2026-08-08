package com.ailab.chemistry.infrastructure.persistence.acidbase;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "conjugate_pairs", schema = "chemistry")
public class JpaConjugatePairEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "pair_code", nullable = false, unique = true, length = 64)
    private String pairCode;

    @Column(name = "acid_species_code", nullable = false, length = 64)
    private String acidSpeciesCode;

    @Column(name = "base_species_code", nullable = false, length = 64)
    private String baseSpeciesCode;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public JpaConjugatePairEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getPairCode() { return pairCode; }
    public void setPairCode(String pairCode) { this.pairCode = pairCode; }
    public String getAcidSpeciesCode() { return acidSpeciesCode; }
    public void setAcidSpeciesCode(String acidSpeciesCode) { this.acidSpeciesCode = acidSpeciesCode; }
    public String getBaseSpeciesCode() { return baseSpeciesCode; }
    public void setBaseSpeciesCode(String baseSpeciesCode) { this.baseSpeciesCode = baseSpeciesCode; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}

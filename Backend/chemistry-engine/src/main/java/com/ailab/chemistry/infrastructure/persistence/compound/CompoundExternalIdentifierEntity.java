package com.ailab.chemistry.infrastructure.persistence.compound;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "compound_external_identifiers", schema = "chemistry")
public class CompoundExternalIdentifierEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compound_id", nullable = false)
    private CompoundEntity compound;

    @Column(name = "scheme", nullable = false)
    private String scheme;

    @Column(name = "identifier_value", nullable = false)
    private String value;

    public CompoundExternalIdentifierEntity() {}

    public CompoundExternalIdentifierEntity(UUID id, CompoundEntity compound, String scheme, String value) {
        this.id = id;
        this.compound = compound;
        this.scheme = scheme;
        this.value = value;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public CompoundEntity getCompound() { return compound; }
    public void setCompound(CompoundEntity compound) { this.compound = compound; }

    public String getScheme() { return scheme; }
    public void setScheme(String scheme) { this.scheme = scheme; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}

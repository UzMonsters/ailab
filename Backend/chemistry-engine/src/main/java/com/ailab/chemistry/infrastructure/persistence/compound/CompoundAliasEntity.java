package com.ailab.chemistry.infrastructure.persistence.compound;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "compound_aliases", schema = "chemistry")
public class CompoundAliasEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compound_id", nullable = false)
    private CompoundEntity compound;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "role", nullable = false)
    private String role;

    public CompoundAliasEntity() {}

    public CompoundAliasEntity(UUID id, CompoundEntity compound, String name, String role) {
        this.id = id;
        this.compound = compound;
        this.name = name;
        this.role = role;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public CompoundEntity getCompound() { return compound; }
    public void setCompound(CompoundEntity compound) { this.compound = compound; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

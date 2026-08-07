package com.ailab.chemistry.infrastructure.persistence.reaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reaction_type_definitions", schema = "chemistry")
public class ReactionTypeDefinitionEntity {

    @Id
    @Column(name = "type_code")
    private String typeCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    public ReactionTypeDefinitionEntity() {}

    public String getTypeCode() {
        return typeCode;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }
}

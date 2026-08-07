package com.ailab.chemistry.infrastructure.persistence.reaction;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "reaction_aliases", schema = "chemistry")
public class ReactionAliasEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reaction_id", nullable = false)
    private ReactionEntity reaction;

    @Column(name = "alias_name", nullable = false)
    private String aliasName;

    @Column(name = "alias_type")
    private String aliasType;

    public ReactionAliasEntity() {}

    public ReactionAliasEntity(ReactionEntity reaction, String aliasName, String aliasType) {
        this.reaction = reaction;
        this.aliasName = aliasName;
        this.aliasType = aliasType;
    }

    public UUID getId() {
        return id;
    }

    public ReactionEntity getReaction() {
        return reaction;
    }

    public String getAliasName() {
        return aliasName;
    }

    public String getAliasType() {
        return aliasType;
    }
}

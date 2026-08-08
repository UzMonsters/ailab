package com.ailab.chemistry.domain.reaction;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public final class Reaction {
    private final ReactionId id;
    private final ReactionCode reactionCode;
    private final ReactionName primaryName;
    private final List<ReactionAlias> aliases;
    private final ReactionEquation equation;
    private final List<ReactionTerm> terms;
    private final ReactionDirectionality directionality;
    private final List<Catalyst> catalysts;
    private final List<ReactionConditionSet> conditionSets;
    private final List<ReactionTypeAssignment> typeAssignments;
    private final String catalogVersion;
    private final ReactionProvenance provenance;

    public Reaction(ReactionId id, ReactionCode reactionCode, ReactionName primaryName,
                    List<ReactionAlias> aliases, ReactionEquation equation, List<ReactionTerm> terms,
                    ReactionDirectionality directionality, List<Catalyst> catalysts,
                    List<ReactionConditionSet> conditionSets, List<ReactionTypeAssignment> typeAssignments,
                    String catalogVersion, ReactionProvenance provenance) {
        this.id = Objects.requireNonNull(id, "ReactionId must not be null");
        this.reactionCode = Objects.requireNonNull(reactionCode, "ReactionCode must not be null");
        this.primaryName = Objects.requireNonNull(primaryName, "PrimaryName must not be null");
        this.equation = Objects.requireNonNull(equation, "ReactionEquation must not be null");
        this.directionality = directionality != null ? directionality : ReactionDirectionality.UNKNOWN;
        this.catalogVersion = catalogVersion != null ? catalogVersion.trim() : "reaction-core-v1.0.0";
        this.provenance = Objects.requireNonNull(provenance, "ReactionProvenance must not be null");

        // Validate terms
        Objects.requireNonNull(terms, "Terms list must not be null");
        if (terms.isEmpty()) {
            throw new ReactionException(ReactionErrorCode.INVALID_REACTION_TERM, "Reaction must have at least one reactant and one product");
        }

        long reactantCount = terms.stream().filter(t -> t.getSide() == ReactionSide.REACTANT).count();
        long productCount = terms.stream().filter(t -> t.getSide() == ReactionSide.PRODUCT).count();

        if (reactantCount == 0 || productCount == 0) {
            throw new ReactionException(ReactionErrorCode.INVALID_REACTION_TERM, "Reaction must have at least one reactant and one product");
        }

        // Validate duplicate terms on same side
        Set<String> reactantCompounds = new HashSet<>();
        Set<String> productCompounds = new HashSet<>();
        for (ReactionTerm term : terms) {
            if (term.getSide() == ReactionSide.REACTANT) {
                if (!reactantCompounds.add(term.getCompoundCode().toUpperCase())) {
                    throw new ReactionException(ReactionErrorCode.REACTION_DUPLICATE_TERM, "Duplicate reactant compound: " + term.getCompoundCode());
                }
            } else {
                if (!productCompounds.add(term.getCompoundCode().toUpperCase())) {
                    throw new ReactionException(ReactionErrorCode.REACTION_DUPLICATE_TERM, "Duplicate product compound: " + term.getCompoundCode());
                }
            }
        }

        // Deterministic term ordering (by termOrder)
        List<ReactionTerm> sortedTerms = new ArrayList<>(terms);
        sortedTerms.sort(Comparator.comparingInt(ReactionTerm::getTermOrder));
        this.terms = Collections.unmodifiableList(sortedTerms);

        // Validate aliases for duplicates
        if (aliases != null) {
            Set<String> aliasNames = new HashSet<>();
            for (ReactionAlias alias : aliases) {
                if (!aliasNames.add(alias.getAliasName().toLowerCase())) {
                    throw new ReactionException(ReactionErrorCode.INVALID_REACTION_NAME, "Duplicate alias: " + alias.getAliasName());
                }
            }
            this.aliases = Collections.unmodifiableList(new ArrayList<>(aliases));
        } else {
            this.aliases = Collections.emptyList();
        }

        // Validate type assignments for duplicates
        if (typeAssignments != null) {
            Set<ReactionTypeCode> typeCodes = new HashSet<>();
            for (ReactionTypeAssignment assignment : typeAssignments) {
                if (!typeCodes.add(assignment.getTypeCode())) {
                    throw new ReactionException(ReactionErrorCode.DUPLICATE_REACTION_TYPE, "Duplicate type assignment: " + assignment.getTypeCode());
                }
            }
            this.typeAssignments = Collections.unmodifiableList(new ArrayList<>(typeAssignments));
        } else {
            this.typeAssignments = Collections.emptyList();
        }

        this.catalysts = catalysts != null ? Collections.unmodifiableList(new ArrayList<>(catalysts)) : Collections.emptyList();
        this.conditionSets = conditionSets != null ? Collections.unmodifiableList(new ArrayList<>(conditionSets)) : Collections.emptyList();
    }

    public ReactionId getId() {
        return id;
    }

    public ReactionCode getReactionCode() {
        return reactionCode;
    }

    public ReactionName getPrimaryName() {
        return primaryName;
    }

    public List<ReactionAlias> getAliases() {
        return aliases;
    }

    public ReactionEquation getEquation() {
        return equation;
    }

    public List<ReactionTerm> getTerms() {
        return terms;
    }

    public List<ReactionTerm> getReactants() {
        return terms.stream().filter(t -> t.getSide() == ReactionSide.REACTANT).collect(Collectors.toList());
    }

    public List<ReactionTerm> getProducts() {
        return terms.stream().filter(t -> t.getSide() == ReactionSide.PRODUCT).collect(Collectors.toList());
    }

    public ReactionDirectionality getDirectionality() {
        return directionality;
    }

    public List<Catalyst> getCatalysts() {
        return catalysts;
    }

    public List<ReactionConditionSet> getConditionSets() {
        return conditionSets;
    }

    public List<ReactionTypeAssignment> getTypeAssignments() {
        return typeAssignments;
    }

    public String getCatalogVersion() {
        return catalogVersion;
    }

    public ReactionProvenance getProvenance() {
        return provenance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reaction reaction = (Reaction) o;
        return Objects.equals(id, reaction.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

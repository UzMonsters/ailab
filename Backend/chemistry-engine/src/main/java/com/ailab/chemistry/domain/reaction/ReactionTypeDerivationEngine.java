package com.ailab.chemistry.domain.reaction;

import java.util.ArrayList;
import java.util.List;

public final class ReactionTypeDerivationEngine {

    public static List<ReactionTypeAssignment> deriveSafeTypes(ReactionDirectionality directionality,
                                                               List<ReactionTerm> terms) {
        List<ReactionTypeAssignment> derived = new ArrayList<>();

        // RULE-DIRECTIONALITY
        if (directionality == ReactionDirectionality.REVERSIBLE) {
            derived.add(new ReactionTypeAssignment(
                    ReactionTypeCode.REVERSIBLE_REACTION,
                    DerivationBasis.SAFE_RULE_DERIVED,
                    "RULE-DIRECTIONALITY: Reaction marked as reversible in reference catalogue"
            ));
        }

        if (terms != null) {
            boolean hasGasProduct = terms.stream()
                    .anyMatch(t -> t.getSide() == ReactionSide.PRODUCT && t.getSpeciesState() == ReactionSpeciesState.GAS);
            boolean hasGasReactant = terms.stream()
                    .anyMatch(t -> t.getSide() == ReactionSide.REACTANT && t.getSpeciesState() == ReactionSpeciesState.GAS);

            // RULE-EXPLICIT-GAS-PRODUCT
            if (hasGasProduct && !hasGasReactant) {
                derived.add(new ReactionTypeAssignment(
                        ReactionTypeCode.GAS_EVOLUTION,
                        DerivationBasis.SAFE_RULE_DERIVED,
                        "RULE-EXPLICIT-GAS-PRODUCT: Gas state product formed from non-gaseous reactants"
                ));
            }

            boolean hasSolidProduct = terms.stream()
                    .anyMatch(t -> t.getSide() == ReactionSide.PRODUCT && t.getSpeciesState() == ReactionSpeciesState.SOLID);
            boolean hasAqReactant = terms.stream()
                    .anyMatch(t -> t.getSide() == ReactionSide.REACTANT && (t.getSpeciesState() == ReactionSpeciesState.AQUEOUS || t.getSpeciesState() == ReactionSpeciesState.DISSOLVED));

            // RULE-EXPLICIT-PRECIPITATE-STATE
            if (hasSolidProduct && hasAqReactant) {
                derived.add(new ReactionTypeAssignment(
                        ReactionTypeCode.PRECIPITATION,
                        DerivationBasis.SAFE_RULE_DERIVED,
                        "RULE-EXPLICIT-PRECIPITATE-STATE: Solid state precipitate formed from aqueous solution reactants"
                ));
            }
        }

        return List.copyOf(derived);
    }
}

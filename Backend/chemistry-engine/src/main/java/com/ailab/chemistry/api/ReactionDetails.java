package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.reaction.*;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

public record ReactionDetails(
        UUID reactionId,
        String reactionCode,
        String primaryName,
        List<String> aliases,
        String originalEquation,
        String normalizedEquation,
        String canonicalEquation,
        String reactionSignature,
        List<TermDetails> terms,
        ReactionDirectionality directionality,
        List<CatalystDetails> catalysts,
        List<ConditionSetDetails> conditionSets,
        List<TypeAssignmentDetails> typeAssignments,
        String catalogVersion,
        ProvenanceDetails provenance
) {
    public record TermDetails(
            UUID compoundId,
            String compoundCode,
            String formula,
            ReactionSide side,
            BigInteger coefficient,
            ReactionSpeciesState speciesState,
            int termOrder
    ) {}

    public record CatalystDetails(
            UUID id,
            CatalystReferenceType referenceType,
            String referenceCode,
            CatalystRole role,
            String physicalForm,
            String loadingDescription
    ) {}

    public record ConditionSetDetails(
            UUID id,
            String temperatureDescription,
            String pressureDescription,
            String medium,
            ReactionAtmosphere atmosphere,
            EnergyInput energyInput,
            String description
    ) {}

    public record TypeAssignmentDetails(
            ReactionTypeCode typeCode,
            DerivationBasis derivationBasis,
            String explanation
    ) {}

    public record ProvenanceDetails(
            String sourceDocumentId,
            List<String> fieldsSupplied,
            String notes
    ) {}
}

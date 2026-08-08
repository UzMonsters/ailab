package com.ailab.chemistry.infrastructure.persistence.reaction;

import com.ailab.chemistry.domain.reaction.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReactionEntityMapper {

    public Reaction toDomain(ReactionEntity entity) {
        if (entity == null) return null;

        ReactionId id = new ReactionId(entity.getId());
        ReactionCode code = new ReactionCode(entity.getReactionCode());
        ReactionName name = new ReactionName(entity.getPrimaryName());

        List<ReactionAlias> aliases = entity.getAliases().stream()
                .map(a -> new ReactionAlias(a.getAliasName(), a.getAliasType()))
                .collect(Collectors.toList());

        ReactionEquation equation = new ReactionEquation(
                entity.getOriginalEquation(),
                entity.getNormalizedEquation(),
                entity.getCanonicalBalancedEquation(),
                entity.getReactionSignature()
        );

        List<ReactionTerm> terms = entity.getTerms().stream()
                .map(t -> new ReactionTerm(
                        t.getCompoundId(),
                        t.getCompoundCode(),
                        t.getFormula(),
                        ReactionSide.valueOf(t.getSide()),
                        t.getCoefficient(),
                        t.getSpeciesState() != null ? ReactionSpeciesState.valueOf(t.getSpeciesState()) : ReactionSpeciesState.UNKNOWN,
                        t.getTermOrder()
                )).collect(Collectors.toList());

        ReactionDirectionality directionality = ReactionDirectionality.valueOf(entity.getDirectionality());

        List<Catalyst> catalysts = entity.getCatalysts().stream()
                .map(c -> new Catalyst(
                        c.getId(),
                        CatalystReferenceType.valueOf(c.getReferenceType()),
                        c.getReferenceCode(),
                        CatalystRole.valueOf(c.getCatalystRole()),
                        c.getPhysicalForm(),
                        c.getLoadingDescription(),
                        c.getEvidenceStatus() != null ? ReactionEvidenceStatus.valueOf(c.getEvidenceStatus()) : ReactionEvidenceStatus.CURATED_AUTHORITATIVE,
                        new ReactionProvenance(c.getSourceDocumentId() != null ? c.getSourceDocumentId() : entity.getSourceDocumentId(), List.of("catalyst"), "")
                )).collect(Collectors.toList());

        List<ReactionConditionSet> conditionSets = entity.getConditionSets().stream()
                .map(c -> new ReactionConditionSet(
                        c.getId(),
                        null, null,
                        c.getMedium(),
                        c.getAtmosphere() != null ? ReactionAtmosphere.valueOf(c.getAtmosphere()) : ReactionAtmosphere.UNSPECIFIED,
                        null,
                        c.getEnergyInput() != null ? EnergyInput.valueOf(c.getEnergyInput()) : EnergyInput.NONE,
                        c.getConcentrationNotes(),
                        c.getDescription(),
                        c.getEvidenceStatus() != null ? ReactionEvidenceStatus.valueOf(c.getEvidenceStatus()) : ReactionEvidenceStatus.CURATED_AUTHORITATIVE,
                        new ReactionProvenance(c.getSourceDocumentId() != null ? c.getSourceDocumentId() : entity.getSourceDocumentId(), List.of("conditions"), "")
                )).collect(Collectors.toList());

        List<ReactionTypeAssignment> typeAssignments = entity.getTypeAssignments().stream()
                .map(t -> new ReactionTypeAssignment(
                        ReactionTypeCode.valueOf(t.getTypeCode()),
                        DerivationBasis.valueOf(t.getDerivationBasis()),
                        t.getExplanation()
                )).collect(Collectors.toList());

        ReactionProvenance provenance = new ReactionProvenance(
                entity.getSourceDocumentId(),
                List.of("equation", "reactionName", "directionality"),
                entity.getProvenanceNotes()
        );

        return new Reaction(id, code, name, aliases, equation, terms, directionality, catalysts, conditionSets, typeAssignments, entity.getCatalogVersionId(), provenance);
    }

    public ReactionEntity toEntity(Reaction reaction) {
        if (reaction == null) return null;

        ReactionEntity entity = new ReactionEntity(
                reaction.getId().getValue(),
                reaction.getReactionCode().getValue(),
                reaction.getPrimaryName().getValue(),
                reaction.getEquation().getOriginalEquation(),
                reaction.getEquation().getNormalizedEquation(),
                reaction.getEquation().getCanonicalBalancedEquation(),
                reaction.getEquation().getReactionSignature(),
                reaction.getDirectionality().name(),
                reaction.getCatalogVersion(),
                reaction.getProvenance().getSourceDocumentId(),
                reaction.getProvenance().getNotes()
        );

        for (ReactionAlias alias : reaction.getAliases()) {
            entity.getAliases().add(new ReactionAliasEntity(entity, alias.getAliasName(), alias.getAliasType()));
        }

        for (ReactionTerm term : reaction.getTerms()) {
            entity.getTerms().add(new ReactionTermEntity(
                    entity,
                    term.getCompoundId(),
                    term.getCompoundCode(),
                    term.getFormula(),
                    term.getSide().name(),
                    term.getCoefficient(),
                    term.getSpeciesState().name(),
                    term.getTermOrder()
            ));
        }

        for (Catalyst cat : reaction.getCatalysts()) {
            UUID compId = cat.getReferenceType() == CatalystReferenceType.COMPOUND ? UUID.nameUUIDFromBytes(("compound-" + cat.getReferenceCode()).getBytes()) : null;
            Integer atomicNum = cat.getReferenceType() == CatalystReferenceType.ELEMENT ? Integer.parseInt(cat.getReferenceCode()) : null;
            entity.getCatalysts().add(new ReactionCatalystEntity(
                    cat.getId(),
                    entity,
                    cat.getReferenceType().name(),
                    cat.getReferenceCode(),
                    compId,
                    atomicNum,
                    cat.getRole().name(),
                    cat.getPhysicalForm(),
                    cat.getLoadingDescription(),
                    cat.getEvidenceStatus().name(),
                    cat.getProvenance() != null ? cat.getProvenance().getSourceDocumentId() : reaction.getProvenance().getSourceDocumentId()
            ));
        }

        for (ReactionConditionSet cond : reaction.getConditionSets()) {
            entity.getConditionSets().add(new ReactionConditionSetEntity(
                    cond.getId(),
                    entity,
                    cond.getTemperature() != null ? cond.getTemperature().toString() : null,
                    cond.getPressure() != null ? cond.getPressure().toString() : null,
                    cond.getMedium(),
                    cond.getAtmosphere().name(),
                    cond.getEnergyInput().name(),
                    cond.getConcentrationNotes(),
                    cond.getDescription(),
                    cond.getEvidenceStatus().name(),
                    cond.getProvenance() != null ? cond.getProvenance().getSourceDocumentId() : reaction.getProvenance().getSourceDocumentId()
            ));
        }

        for (ReactionTypeAssignment type : reaction.getTypeAssignments()) {
            entity.getTypeAssignments().add(new ReactionTypeAssignmentEntity(
                    entity,
                    type.getTypeCode().name(),
                    type.getDerivationBasis().name(),
                    type.getExplanation()
            ));
        }

        return entity;
    }
}

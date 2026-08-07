package com.ailab.chemistry.service;

import com.ailab.chemistry.api.*;
import com.ailab.chemistry.domain.classification.*;
import com.ailab.chemistry.domain.compound.Compound;
import com.ailab.chemistry.domain.compound.CompoundCode;
import com.ailab.chemistry.domain.compound.CompoundId;
import com.ailab.chemistry.domain.compound.CompoundRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChemicalClassificationServiceImpl implements ChemicalClassificationService {

    private final ClassificationProfileRepository profileRepository;
    private final CompoundRepository compoundRepository;

    public ChemicalClassificationServiceImpl(ClassificationProfileRepository profileRepository, CompoundRepository compoundRepository) {
        this.profileRepository = profileRepository;
        this.compoundRepository = compoundRepository;
    }

    @Override
    public CompoundClassificationDetails getByCompoundId(UUID compoundId) {
        ClassificationProfile profile = profileRepository.findByCompoundId(new CompoundId(compoundId))
                .orElseThrow(() -> new ClassificationException(
                        ClassificationErrorCode.CLASSIFICATION_PROFILE_NOT_FOUND,
                        "Classification profile not found for compound ID: " + compoundId));

        Compound compound = compoundRepository.findById(new CompoundId(compoundId)).orElse(null);
        String code = compound != null ? compound.getCode().getValue() : compoundId.toString();
        String name = compound != null ? compound.getPrimaryName() : "Unknown Compound";

        return toProfileDetails(profile, code, name);
    }

    @Override
    public CompoundClassificationDetails getByCompoundCode(String compoundCode) {
        Compound compound = compoundRepository.findByCode(new CompoundCode(compoundCode))
                .orElseThrow(() -> new ClassificationException(
                        ClassificationErrorCode.CLASSIFICATION_PROFILE_NOT_FOUND,
                        "Compound not found for code: " + compoundCode));

        ClassificationProfile profile = profileRepository.findByCompoundId(compound.getId())
                .orElseThrow(() -> new ClassificationException(
                        ClassificationErrorCode.CLASSIFICATION_PROFILE_NOT_FOUND,
                        "Classification profile not found for compound code: " + compoundCode));

        return toProfileDetails(profile, compound.getCode().getValue(), compound.getPrimaryName());
    }

    @Override
    public List<CompoundSummary> findCompoundsByClassification(String classificationCode) {
        ClassificationCode code = new ClassificationCode(classificationCode);
        List<ClassificationProfile> profiles = profileRepository.findByClassificationCode(code);

        List<CompoundSummary> summaries = new ArrayList<>();
        for (ClassificationProfile p : profiles) {
            compoundRepository.findById(p.getCompoundId()).ifPresent(c -> summaries.add(toSummary(c)));
        }
        return summaries;
    }

    @Override
    public ClassificationTaxonomyDetails getActiveTaxonomy() {
        ClassificationTaxonomy taxonomy = profileRepository.getActiveTaxonomy();
        List<ClassificationTaxonomyDetails.DefinitionDetail> defs = taxonomy.getDefinitions().stream()
                .map(d -> new ClassificationTaxonomyDetails.DefinitionDetail(
                        d.getCode().getValue(),
                        d.getDimension().name(),
                        d.getName(),
                        d.getDescription(),
                        d.getSortOrder(),
                        d.getParentCode() != null ? d.getParentCode().getValue() : null
                ))
                .collect(Collectors.toList());

        return new ClassificationTaxonomyDetails(
                taxonomy.getVersion().getVersion(),
                taxonomy.getVersion().getName(),
                taxonomy.getVersion().getPublicationDate(),
                defs
        );
    }

    @Override
    public ClassificationExplanation explain(String compoundCode, String classificationCode) {
        Compound compound = compoundRepository.findByCode(new CompoundCode(compoundCode))
                .orElseThrow(() -> new ClassificationException(
                        ClassificationErrorCode.CLASSIFICATION_PROFILE_NOT_FOUND,
                        "Compound not found for code: " + compoundCode));

        ClassificationProfile profile = profileRepository.findByCompoundId(compound.getId())
                .orElseThrow(() -> new ClassificationException(
                        ClassificationErrorCode.CLASSIFICATION_PROFILE_NOT_FOUND,
                        "Classification profile not found for compound code: " + compoundCode));

        ClassificationCode code = new ClassificationCode(classificationCode);
        ClassificationAssignment assignment = profile.getAssignment(code)
                .orElseThrow(() -> new ClassificationException(
                        ClassificationErrorCode.CLASSIFICATION_DEFINITION_NOT_FOUND,
                        "Classification assignment " + classificationCode + " not found for compound " + compoundCode));

        ClassificationTaxonomy taxonomy = profileRepository.getActiveTaxonomy();
        ClassificationDefinition def = taxonomy.findDefinition(code).orElse(null);
        String name = def != null ? def.getName() : code.getValue();

        return new ClassificationExplanation(
                compound.getCode().getValue(),
                compound.getPrimaryName(),
                code,
                name,
                assignment.getDimension(),
                assignment.getBasis(),
                assignment.getEvidenceStatus(),
                assignment.getRuleCode() != null ? assignment.getRuleCode().getCode() : null,
                assignment.getProvenance().getSourceIdentifier(),
                assignment.getProvenance().getSourceTitle(),
                assignment.getExplanatoryNote()
        );
    }

    private CompoundClassificationDetails toProfileDetails(ClassificationProfile profile, String compoundCode, String primaryName) {
        List<CompoundClassificationDetails.AssignmentDetail> assignmentDetails = profile.getAssignments().stream()
                .map(a -> new CompoundClassificationDetails.AssignmentDetail(
                        a.getCode().getValue(),
                        a.getDimension().name(),
                        a.getBasis().name(),
                        a.getEvidenceStatus().name(),
                        a.getRuleCode() != null ? a.getRuleCode().getCode() : null,
                        a.getProvenance().getSourceIdentifier(),
                        a.getProvenance().getSourceTitle(),
                        a.getExplanatoryNote()
                ))
                .collect(Collectors.toList());

        return new CompoundClassificationDetails(
                profile.getCompoundId().getValue(),
                compoundCode,
                primaryName,
                profile.getTaxonomyVersion().getVersion(),
                assignmentDetails
        );
    }

    private CompoundSummary toSummary(Compound c) {
        return new CompoundSummary(
                c.getId().getValue(),
                c.getCode().getValue(),
                c.getPrimaryName(),
                c.getFormula().getOriginalFormula(),
                c.getFormula().getNormalizedFormula(),
                c.getFormula().getCompositionFormula(),
                c.getNetCharge().getValue(),
                c.getMolarMass().getRepresentativeValue(),
                c.getMolarMass().getUnit()
        );
    }
}

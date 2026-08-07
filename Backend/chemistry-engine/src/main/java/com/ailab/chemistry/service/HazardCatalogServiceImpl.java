package com.ailab.chemistry.service;

import com.ailab.chemistry.api.*;
import com.ailab.chemistry.domain.compound.Compound;
import com.ailab.chemistry.domain.compound.CompoundCode;
import com.ailab.chemistry.domain.compound.CompoundId;
import com.ailab.chemistry.domain.compound.CompoundRepository;
import com.ailab.chemistry.domain.hazard.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HazardCatalogServiceImpl implements HazardCatalogService {

    private final HazardProfileRepository hazardRepository;
    private final CompoundRepository compoundRepository;

    public HazardCatalogServiceImpl(HazardProfileRepository hazardRepository, CompoundRepository compoundRepository) {
        this.hazardRepository = hazardRepository;
        this.compoundRepository = compoundRepository;
    }

    @Override
    public HazardProfileDetails getByCompoundId(UUID compoundId) {
        HazardProfile profile = hazardRepository.findByCompoundId(new CompoundId(compoundId))
                .orElseThrow(() -> new HazardException(
                        HazardErrorCode.HAZARD_PROFILE_NOT_FOUND,
                        "Hazard profile not found for compound ID: " + compoundId));

        Compound compound = compoundRepository.findById(new CompoundId(compoundId)).orElse(null);
        String code = compound != null ? compound.getCode().getValue() : compoundId.toString();
        String name = compound != null ? compound.getPrimaryName() : "Unknown Compound";

        return toDetails(profile, code, name);
    }

    @Override
    public HazardProfileDetails getByCompoundCode(String compoundCode) {
        Compound compound = compoundRepository.findByCode(new CompoundCode(compoundCode))
                .orElseThrow(() -> new HazardException(
                        HazardErrorCode.HAZARD_PROFILE_NOT_FOUND,
                        "Compound not found for code: " + compoundCode));

        HazardProfile profile = hazardRepository.findByCompoundId(compound.getId())
                .orElseThrow(() -> new HazardException(
                        HazardErrorCode.HAZARD_PROFILE_NOT_FOUND,
                        "Hazard profile not found for compound code: " + compoundCode));

        return toDetails(profile, compound.getCode().getValue(), compound.getPrimaryName());
    }

    @Override
    public List<HazardProfileSummary> findBySummaryFlag(HazardSummaryFlag flag) {
        List<HazardProfile> profiles = hazardRepository.findBySummaryFlag(flag);
        List<HazardProfileSummary> summaries = new ArrayList<>();
        for (HazardProfile p : profiles) {
            compoundRepository.findById(p.getCompoundId()).ifPresent(c ->
                    summaries.add(new HazardProfileSummary(c.getId().getValue(), c.getCode().getValue(), c.getPrimaryName(), p.getDatasetVersion(), p.getSummaryFlags())));
        }
        return summaries;
    }

    @Override
    public List<HazardProfileSummary> findByPictogram(String pictogramCode) {
        List<HazardProfile> profiles = hazardRepository.findByPictogramCode(pictogramCode);
        List<HazardProfileSummary> summaries = new ArrayList<>();
        for (HazardProfile p : profiles) {
            compoundRepository.findById(p.getCompoundId()).ifPresent(c ->
                    summaries.add(new HazardProfileSummary(c.getId().getValue(), c.getCode().getValue(), c.getPrimaryName(), p.getDatasetVersion(), p.getSummaryFlags())));
        }
        return summaries;
    }

    @Override
    public List<HazardProfileSummary> findByHazardClass(String classificationSystem, String hazardClassCode) {
        List<HazardProfile> profiles = hazardRepository.findByHazardClass(classificationSystem, hazardClassCode);
        List<HazardProfileSummary> summaries = new ArrayList<>();
        for (HazardProfile p : profiles) {
            compoundRepository.findById(p.getCompoundId()).ifPresent(c ->
                    summaries.add(new HazardProfileSummary(c.getId().getValue(), c.getCode().getValue(), c.getPrimaryName(), p.getDatasetVersion(), p.getSummaryFlags())));
        }
        return summaries;
    }

    @Override
    public HazardLabelDetails getLabel(String compoundCode, String sourceDocumentId) {
        Compound compound = compoundRepository.findByCode(new CompoundCode(compoundCode))
                .orElseThrow(() -> new HazardException(
                        HazardErrorCode.HAZARD_PROFILE_NOT_FOUND,
                        "Compound not found for code: " + compoundCode));

        HazardProfile profile = hazardRepository.findByCompoundId(compound.getId())
                .orElseThrow(() -> new HazardException(
                        HazardErrorCode.HAZARD_PROFILE_NOT_FOUND,
                        "Hazard profile not found for compound code: " + compoundCode));

        HazardLabelSummary labelSummary = profile.getLabelSummaries().stream()
                .filter(l -> l.getSourceDocumentId().equalsIgnoreCase(sourceDocumentId))
                .findFirst()
                .orElseThrow(() -> new HazardException(
                        HazardErrorCode.HAZARD_SOURCE_DOCUMENT_NOT_FOUND,
                        "Label summary not found for source document ID: " + sourceDocumentId));

        return new HazardLabelDetails(
                compoundCode,
                labelSummary.getClassificationSystem().name(),
                labelSummary.getRevision(),
                labelSummary.getJurisdiction().name(),
                labelSummary.getSignalWord(),
                labelSummary.getPictograms().stream().map(GhsPictogram::getCode).collect(Collectors.toList()),
                labelSummary.getHazardStatements().stream().map(HazardStatement::getStatementCode).collect(Collectors.toList()),
                labelSummary.getPrecautionaryStatements().stream().map(PrecautionaryStatement::getStatementCode).collect(Collectors.toList()),
                sourceDocumentId
        );
    }

    @Override
    public HazardExplanation explainSummaryFlag(String compoundCode, HazardSummaryFlag flag) {
        Compound compound = compoundRepository.findByCode(new CompoundCode(compoundCode))
                .orElseThrow(() -> new HazardException(
                        HazardErrorCode.HAZARD_PROFILE_NOT_FOUND,
                        "Compound not found for code: " + compoundCode));

        HazardProfile profile = hazardRepository.findByCompoundId(compound.getId())
                .orElseThrow(() -> new HazardException(
                        HazardErrorCode.HAZARD_PROFILE_NOT_FOUND,
                        "Hazard profile not found for compound code: " + compoundCode));

        return HazardSummaryDerivationEngine.explain(compoundCode, flag, profile.getClassifications(), profile.getSupplementalHazards());
    }

    private HazardProfileDetails toDetails(HazardProfile profile, String code, String name) {
        Map<String, String> availMap = profile.getAvailabilityMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().name()));

        return new HazardProfileDetails(
                profile.getCompoundId().getValue(),
                code,
                name,
                profile.getDatasetVersion(),
                availMap,
                profile.getSummaryFlags()
        );
    }
}

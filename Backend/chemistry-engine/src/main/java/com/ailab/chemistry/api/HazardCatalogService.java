package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.hazard.HazardExplanation;
import com.ailab.chemistry.domain.hazard.HazardSummaryFlag;

import java.util.List;
import java.util.UUID;

public interface HazardCatalogService {

    HazardProfileDetails getByCompoundId(UUID compoundId);

    HazardProfileDetails getByCompoundCode(String compoundCode);

    List<HazardProfileSummary> findBySummaryFlag(HazardSummaryFlag flag);

    List<HazardProfileSummary> findByPictogram(String pictogramCode);

    List<HazardProfileSummary> findByHazardClass(String classificationSystem, String hazardClassCode);

    HazardLabelDetails getLabel(String compoundCode, String sourceDocumentId);

    HazardExplanation explainSummaryFlag(String compoundCode, HazardSummaryFlag flag);
}

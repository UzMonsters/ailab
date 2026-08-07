package com.ailab.chemistry.domain.hazard;

import com.ailab.chemistry.domain.compound.CompoundId;

import java.util.List;
import java.util.Optional;

public interface HazardProfileRepository {

    Optional<HazardProfile> findByCompoundId(CompoundId compoundId);

    Optional<HazardProfile> findByCompoundCode(String compoundCode);

    List<HazardProfile> findBySummaryFlag(HazardSummaryFlag flag);

    List<HazardProfile> findByPictogramCode(String pictogramCode);

    List<HazardProfile> findByHazardClass(String classificationSystem, String hazardClassCode);

    List<HazardProfile> findAll();

    long count();
}

package com.ailab.chemistry.infrastructure.persistence.hazard;

import com.ailab.chemistry.domain.compound.CompoundId;
import com.ailab.chemistry.domain.hazard.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("!(test | standalone-engine)")
public class HazardProfileRepositoryImpl implements HazardProfileRepository {

    private final JpaHazardProfileRepository jpaRepository;

    public HazardProfileRepositoryImpl(JpaHazardProfileRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<HazardProfile> findByCompoundId(CompoundId compoundId) {
        return Optional.empty();
    }

    @Override
    public Optional<HazardProfile> findByCompoundCode(String compoundCode) {
        return Optional.empty();
    }

    @Override
    public List<HazardProfile> findBySummaryFlag(HazardSummaryFlag flag) {
        return List.of();
    }

    @Override
    public List<HazardProfile> findByPictogramCode(String pictogramCode) {
        return List.of();
    }

    @Override
    public List<HazardProfile> findByHazardClass(String classificationSystem, String hazardClassCode) {
        return List.of();
    }

    @Override
    public List<HazardProfile> findAll() {
        return List.of();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}

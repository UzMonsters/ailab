package com.ailab.chemistry.infrastructure.persistence.classification;

import com.ailab.chemistry.domain.classification.*;
import com.ailab.chemistry.domain.compound.CompoundId;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Profile("!(test | standalone-engine)")
public class ClassificationProfileRepositoryImpl implements ClassificationProfileRepository {

    private final JpaClassificationProfileRepository profileJpaRepository;

    public ClassificationProfileRepositoryImpl(JpaClassificationProfileRepository profileJpaRepository) {
        this.profileJpaRepository = profileJpaRepository;
    }

    @Override
    public Optional<ClassificationProfile> findByCompoundId(CompoundId compoundId) {
        return profileJpaRepository.findByCompoundId(compoundId.getValue()).map(ClassificationMapper::toDomain);
    }

    @Override
    public Optional<ClassificationProfile> findByCompoundCode(String compoundCode) {
        return profileJpaRepository.findByCompoundCode(compoundCode).map(ClassificationMapper::toDomain);
    }

    @Override
    public List<ClassificationProfile> findByClassificationCode(ClassificationCode classificationCode) {
        return profileJpaRepository.findByAssignmentCode(classificationCode.getValue()).stream()
                .map(ClassificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ClassificationProfile> findAll() {
        return profileJpaRepository.findAll().stream()
                .map(ClassificationMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return profileJpaRepository.count();
    }

    @Override
    public ClassificationTaxonomy getActiveTaxonomy() {
        return KnownClassificationRegistry.TAXONOMY;
    }
}

package com.ailab.chemistry.infrastructure.persistence.compound;

import com.ailab.chemistry.domain.compound.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Profile("!(test | standalone-engine)")
public class CompoundRepositoryImpl implements CompoundRepository {

    private final JpaCompoundRepository jpaRepository;

    public CompoundRepositoryImpl(JpaCompoundRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Compound> findById(CompoundId id) {
        return jpaRepository.findById(id.getValue()).map(CompoundMapper::toDomain);
    }

    @Override
    public Optional<Compound> findByCode(CompoundCode code) {
        return jpaRepository.findByCompoundCode(code.getValue()).map(CompoundMapper::toDomain);
    }

    @Override
    public List<Compound> findByNormalizedFormula(String normalizedFormula) {
        return jpaRepository.findByNormalizedFormula(normalizedFormula).stream()
                .map(CompoundMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Compound> findByCompositionFormula(String compositionFormula) {
        return jpaRepository.findByCompositionFormula(compositionFormula).stream()
                .map(CompoundMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Compound> searchByName(String query) {
        return jpaRepository.searchByNameOrAlias(query).stream()
                .map(CompoundMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Compound> findAll() {
        return jpaRepository.findAll().stream()
                .map(CompoundMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public Compound save(Compound compound) {
        throw new UnsupportedOperationException("Runtime save is managed via Flyway migrations for Compound Core Catalogue");
    }
}

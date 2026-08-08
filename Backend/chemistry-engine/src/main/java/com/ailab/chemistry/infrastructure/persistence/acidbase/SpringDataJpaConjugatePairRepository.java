package com.ailab.chemistry.infrastructure.persistence.acidbase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataJpaConjugatePairRepository extends JpaRepository<JpaConjugatePairEntity, UUID> {
    Optional<JpaConjugatePairEntity> findByAcidSpeciesCodeIgnoreCaseOrBaseSpeciesCodeIgnoreCase(String acidCode, String baseCode);
}

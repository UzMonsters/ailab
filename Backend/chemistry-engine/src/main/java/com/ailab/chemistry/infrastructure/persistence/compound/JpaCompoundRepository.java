package com.ailab.chemistry.infrastructure.persistence.compound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaCompoundRepository extends JpaRepository<CompoundEntity, UUID> {
    Optional<CompoundEntity> findByCompoundCode(String compoundCode);
    List<CompoundEntity> findByNormalizedFormula(String normalizedFormula);
    List<CompoundEntity> findByCompositionFormula(String compositionFormula);

    @Query("SELECT DISTINCT c FROM CompoundEntity c LEFT JOIN c.aliases a " +
           "WHERE LOWER(c.primaryName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.compoundCode) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(a.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<CompoundEntity> searchByNameOrAlias(@Param("query") String query);
}

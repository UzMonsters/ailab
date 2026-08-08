package com.ailab.chemistry.infrastructure.persistence.element;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaElementRepository extends JpaRepository<ElementEntity, UUID> {
    Optional<ElementEntity> findByAtomicNumber(int atomicNumber);
    Optional<ElementEntity> findBySymbol(String symbol);
}

package com.ailab.chemistry.infrastructure.persistence.element.property;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaElementPropertyProfileRepository extends JpaRepository<ElementPropertyProfileEntity, UUID> {
    Optional<ElementPropertyProfileEntity> findByAtomicNumber(int atomicNumber);
    Optional<ElementPropertyProfileEntity> findBySymbol(String symbol);
}

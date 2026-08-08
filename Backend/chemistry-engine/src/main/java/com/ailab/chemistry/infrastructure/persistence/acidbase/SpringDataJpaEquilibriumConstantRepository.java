package com.ailab.chemistry.infrastructure.persistence.acidbase;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;

@Repository
public interface SpringDataJpaEquilibriumConstantRepository extends JpaRepository<JpaEquilibriumConstantEntity, UUID> {
    Optional<JpaEquilibriumConstantEntity> findBySpeciesCodeIgnoreCaseAndTypeIgnoreCase(String speciesCode, String type);

    Optional<JpaEquilibriumConstantEntity> findBySpeciesCodeIgnoreCaseAndTypeIgnoreCaseAndTemperatureCelsiusAndSolventCodeIgnoreCase(
            String speciesCode,
            String type,
            BigDecimal temperatureCelsius,
            String solventCode
    );
}

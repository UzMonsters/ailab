package com.ailab.chemistry.infrastructure.persistence.solubility;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataJpaSolubilityEquilibriumRepository extends JpaRepository<JpaSolubilityEquilibriumEntity, UUID> {
    Optional<JpaSolubilityEquilibriumEntity> findByEquilibriumCodeIgnoreCaseAndTemperatureCelsiusAndSolventCodeIgnoreCase(
            String equilibriumCode,
            BigDecimal temperatureCelsius,
            String solventCode
    );

    List<JpaSolubilityEquilibriumEntity> findAll();
}

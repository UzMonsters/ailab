package com.ailab.chemistry.infrastructure.persistence.acidbase;

import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataJpaActivityParameterSetRepository extends JpaRepository<JpaActivityParameterSetEntity, UUID> {
    Optional<JpaActivityParameterSetEntity> findByModelIgnoreCaseAndTemperatureCelsiusAndSolventCodeIgnoreCase(
            String model,
            BigDecimal temperatureCelsius,
            String solventCode
    );
}

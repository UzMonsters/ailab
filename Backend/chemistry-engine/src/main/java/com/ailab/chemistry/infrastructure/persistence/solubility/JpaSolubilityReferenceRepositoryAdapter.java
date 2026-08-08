package com.ailab.chemistry.infrastructure.persistence.solubility;

import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.solubility.*;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Component
@Primary
@Profile("!(test | standalone-engine)")
@Transactional(readOnly = true)
public class JpaSolubilityReferenceRepositoryAdapter implements SolubilityReferenceRepository {
    private final SpringDataJpaSolubilityEquilibriumRepository repository;

    public JpaSolubilityReferenceRepositoryAdapter(SpringDataJpaSolubilityEquilibriumRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<SolubilityEquilibrium> findByCode(SolubilityEquilibriumCode code, Temperature temperature, String solventCode) {
        return repository.findByEquilibriumCodeIgnoreCaseAndTemperatureCelsiusAndSolventCodeIgnoreCase(
                code.value(),
                temperature.in(TemperatureUnit.CELSIUS).setScale(2, RoundingMode.HALF_UP),
                solventCode
        ).map(this::toDomain);
    }

    @Override
    public List<SolubilityEquilibrium> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    private SolubilityEquilibrium toDomain(JpaSolubilityEquilibriumEntity entity) {
        return new SolubilityEquilibrium(
                new SolubilityEquilibriumCode(entity.getEquilibriumCode()),
                entity.getSolidCompoundCode(),
                entity.getTerms().stream()
                        .map(term -> new DissolutionTerm(term.getSpeciesCode(), term.getFormula(), term.getCharge(), term.getCoefficient()))
                        .toList(),
                new SolubilityProduct(entity.getKspValue()),
                new SolubilityReferenceConditions(Temperature.of(entity.getTemperatureCelsius(), TemperatureUnit.CELSIUS),
                        entity.getSolventCode(), entity.getActivityConvention()),
                new SolubilityDatasetVersion(entity.getDatasetVersion()),
                new SolubilityProvenance(entity.getSourceIdentifier(), entity.getCitation(), entity.getReuseLimitations())
        );
    }
}

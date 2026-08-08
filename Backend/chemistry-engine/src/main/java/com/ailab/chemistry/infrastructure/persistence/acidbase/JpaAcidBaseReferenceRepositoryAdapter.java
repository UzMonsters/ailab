package com.ailab.chemistry.infrastructure.persistence.acidbase;

import com.ailab.chemistry.domain.acidbase.*;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Primary
@Profile("!(test | standalone-engine)")
@Transactional(readOnly = true)
public class JpaAcidBaseReferenceRepositoryAdapter implements AcidBaseReferenceRepository {

    private final SpringDataJpaChemicalSpeciesRepository speciesRepository;
    private final SpringDataJpaConjugatePairRepository conjugatePairRepository;
    private final SpringDataJpaDissociationStepRepository dissociationStepRepository;
    private final SpringDataJpaEquilibriumConstantRepository equilibriumConstantRepository;

    public JpaAcidBaseReferenceRepositoryAdapter(
            SpringDataJpaChemicalSpeciesRepository speciesRepository,
            SpringDataJpaConjugatePairRepository conjugatePairRepository,
            SpringDataJpaDissociationStepRepository dissociationStepRepository,
            SpringDataJpaEquilibriumConstantRepository equilibriumConstantRepository) {
        this.speciesRepository = Objects.requireNonNull(speciesRepository);
        this.conjugatePairRepository = Objects.requireNonNull(conjugatePairRepository);
        this.dissociationStepRepository = Objects.requireNonNull(dissociationStepRepository);
        this.equilibriumConstantRepository = Objects.requireNonNull(equilibriumConstantRepository);
    }

    @Override
    public Optional<ChemicalSpecies> findSpeciesByCode(ChemicalSpeciesCode code) {
        if (code == null) return Optional.empty();
        return speciesRepository.findBySpeciesCodeIgnoreCase(code.getValue()).map(this::toDomainSpecies);
    }

    @Override
    public List<ChemicalSpecies> findAllSpecies() {
        return speciesRepository.findAll().stream().map(this::toDomainSpecies).collect(Collectors.toList());
    }

    @Override
    public List<DissociationStep> findDissociationStepsForSpecies(ChemicalSpeciesCode code) {
        if (code == null) return List.of();
        return dissociationStepRepository.findByAcidSpeciesCodeIgnoreCaseOrderByStepNumberAsc(code.getValue())
                .stream().map(this::toDomainStep).collect(Collectors.toList());
    }

    @Override
    public Optional<EquilibriumConstant> findKa(ChemicalSpeciesCode code, Temperature temperature, String solventCode) {
        if (code == null) return Optional.empty();
        String type = "SPEC-H2O".equalsIgnoreCase(code.getValue()) ? "KW" : "KA";
        return equilibriumConstantRepository.findBySpeciesCodeIgnoreCaseAndTypeIgnoreCaseAndTemperatureCelsiusAndSolventCodeIgnoreCase(
                        code.getValue(),
                        type,
                        temperature.in(TemperatureUnit.CELSIUS).setScale(2, java.math.RoundingMode.HALF_UP),
                        solventCode
                )
                .map(this::toDomainConstant);
    }

    @Override
    public Optional<EquilibriumConstant> findKb(ChemicalSpeciesCode code, Temperature temperature, String solventCode) {
        if (code == null) return Optional.empty();
        return equilibriumConstantRepository.findBySpeciesCodeIgnoreCaseAndTypeIgnoreCaseAndTemperatureCelsiusAndSolventCodeIgnoreCase(
                        code.getValue(),
                        "KB",
                        temperature.in(TemperatureUnit.CELSIUS).setScale(2, java.math.RoundingMode.HALF_UP),
                        solventCode
                )
                .map(this::toDomainConstant);
    }

    @Override
    public Optional<ConjugatePair> findConjugatePair(ChemicalSpeciesCode code) {
        if (code == null) return Optional.empty();
        return conjugatePairRepository.findByAcidSpeciesCodeIgnoreCaseOrBaseSpeciesCodeIgnoreCase(code.getValue(), code.getValue())
                .map(e -> new ConjugatePair(e.getPairCode(), e.getAcidSpeciesCode(), e.getBaseSpeciesCode()));
    }

    private ChemicalSpecies toDomainSpecies(JpaChemicalSpeciesEntity entity) {
        return new ChemicalSpecies(
                new ChemicalSpeciesCode(entity.getSpeciesCode()),
                entity.getName(),
                entity.getFormula(),
                SpeciesKind.valueOf(entity.getKind()),
                SpeciesCharge.of(entity.getCharge()),
                AcidBaseRole.fromString(entity.getPrimaryRole()),
                DissociationBehavior.fromString(entity.getDissociationBehavior()),
                entity.getAssociatedCompoundCode()
        );
    }

    private DissociationStep toDomainStep(JpaDissociationStepEntity entity) {
        Optional<EquilibriumConstant> ka = equilibriumConstantRepository
                .findBySpeciesCodeIgnoreCaseAndTypeIgnoreCase(entity.getAcidSpeciesCode(), "KA")
                .map(this::toDomainConstant);
        return new DissociationStep(entity.getAcidSpeciesCode(), entity.getDeprotonatedSpeciesCode(), entity.getStepNumber(), ka.orElse(null));
    }

    private EquilibriumConstant toDomainConstant(JpaEquilibriumConstantEntity entity) {
        EquilibriumReferenceConditions cond = new EquilibriumReferenceConditions(
                Temperature.of(entity.getTemperatureCelsius(), TemperatureUnit.CELSIUS),
                entity.getSolventCode()
        );
        return new EquilibriumConstant(
                entity.getId(),
                entity.getSpeciesCode(),
                EquilibriumConstantType.valueOf(entity.getType()),
                entity.getStepNumber(),
                entity.getkValue(),
                cond,
                AcidBaseProvenance.defaultExperimental()
        );
    }
}

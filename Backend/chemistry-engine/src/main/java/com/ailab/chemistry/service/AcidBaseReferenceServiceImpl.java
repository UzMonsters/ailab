package com.ailab.chemistry.service;

import com.ailab.chemistry.api.*;
import com.ailab.chemistry.domain.acidbase.*;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AcidBaseReferenceServiceImpl implements AcidBaseReferenceService {

    private final AcidBaseReferenceRepository repository;

    @Autowired
    public AcidBaseReferenceServiceImpl(AcidBaseReferenceRepository repository) {
        this.repository = Objects.requireNonNull(repository, "AcidBaseReferenceRepository must not be null");
    }

    @Override
    public ChemicalSpeciesDetails getSpecies(String speciesCode) {
        ChemicalSpecies s = repository.findSpeciesByCode(new ChemicalSpeciesCode(speciesCode))
                .orElseThrow(() -> new AcidBaseException(AcidBaseErrorCode.SPECIES_NOT_FOUND, "Chemical species not found: " + speciesCode));
        return toSpeciesDetails(s);
    }

    @Override
    public List<DissociationStepDetails> getDissociationSteps(String speciesCode) {
        return repository.findDissociationStepsForSpecies(new ChemicalSpeciesCode(speciesCode)).stream()
                .map(this::toDissociationStepDetails)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<EquilibriumConstantDetails> findKa(String speciesCode, Temperature temperature, String solventCode) {
        return repository.findKa(new ChemicalSpeciesCode(speciesCode), temperature, solventCode)
                .map(this::toEquilibriumConstantDetails);
    }

    @Override
    public Optional<EquilibriumConstantDetails> findKb(String speciesCode, Temperature temperature, String solventCode) {
        return repository.findKb(new ChemicalSpeciesCode(speciesCode), temperature, solventCode)
                .map(this::toEquilibriumConstantDetails);
    }

    @Override
    public ConjugatePairDetails getConjugatePair(String speciesCode) {
        ConjugatePair pair = repository.findConjugatePair(new ChemicalSpeciesCode(speciesCode))
                .orElseThrow(() -> new AcidBaseException(AcidBaseErrorCode.CONJUGATE_PAIR_NOT_FOUND, "Conjugate pair not found for species: " + speciesCode));
        return new ConjugatePairDetails(pair.getPairCode(), pair.getAcidSpeciesCode(), pair.getBaseSpeciesCode());
    }

    private ChemicalSpeciesDetails toSpeciesDetails(ChemicalSpecies s) {
        return new ChemicalSpeciesDetails(
                s.getCode().getValue(),
                s.getName(),
                s.getFormulaStr(),
                s.getKind().name(),
                s.getCharge().getValue(),
                s.getPrimaryRole().name(),
                s.getDissociationBehavior().name(),
                s.getAssociatedCompoundCode().orElse(null)
        );
    }

    private DissociationStepDetails toDissociationStepDetails(DissociationStep step) {
        EquilibriumConstantDetails kaDetails = step.getKaConstant().map(this::toEquilibriumConstantDetails).orElse(null);
        return new DissociationStepDetails(
                step.getAcidSpeciesCode(),
                step.getDeprotonatedSpeciesCode(),
                step.getStepNumber(),
                kaDetails
        );
    }

    private EquilibriumConstantDetails toEquilibriumConstantDetails(EquilibriumConstant c) {
        return new EquilibriumConstantDetails(
                c.getSpeciesCode(),
                c.getType().name(),
                c.getStepNumber(),
                c.getValue(),
                c.getPValue(),
                c.getConditions().getTemperature().in(TemperatureUnit.CELSIUS),
                c.getConditions().getSolventCompoundCode()
        );
    }
}

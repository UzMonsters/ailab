package com.ailab.chemistry.service;

import com.ailab.chemistry.api.*;
import com.ailab.chemistry.domain.acidbase.*;
import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.Temperature;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class AcidBaseEquilibriumServiceImpl implements AcidBaseEquilibriumService {

    private final AcidBaseReferenceService referenceService;
    private final AcidBaseEquilibriumCalculator calculator;

    @Autowired
    public AcidBaseEquilibriumServiceImpl(AcidBaseReferenceService referenceService) {
        this.referenceService = Objects.requireNonNull(referenceService, "AcidBaseReferenceService must not be null");
        this.calculator = new AcidBaseEquilibriumCalculator();
    }

    @Override
    public AcidBaseEquilibriumResult calculatePureWater(Temperature temperature) {
        BigDecimal kw = getKw(temperature);
        return calculator.calculatePureWater(kw);
    }

    @Override
    public AcidBaseEquilibriumResult calculateStrongAcid(String speciesCode, MolarConcentration concentration, Temperature temperature) {
        ChemicalSpeciesDetails species = referenceService.getSpecies(speciesCode);
        if (!"STRONG_ELECTROLYTE".equalsIgnoreCase(species.getDissociationBehavior()) || !"ACID".equalsIgnoreCase(species.getPrimaryRole())) {
            throw new AcidBaseCalculationException(AcidBaseCalculationErrorCode.SPECIES_ROLE_MISMATCH, "Species " + speciesCode + " is not a strong acid (role: " + species.getPrimaryRole() + ", behavior: " + species.getDissociationBehavior() + ")");
        }
        BigDecimal kw = getKw(temperature);
        return calculator.calculateStrongAcid(concentration, kw);
    }

    @Override
    public AcidBaseEquilibriumResult calculateStrongBase(String speciesCode, MolarConcentration concentration, Temperature temperature) {
        ChemicalSpeciesDetails species = referenceService.getSpecies(speciesCode);
        if (!"STRONG_ELECTROLYTE".equalsIgnoreCase(species.getDissociationBehavior()) || !"BASE".equalsIgnoreCase(species.getPrimaryRole())) {
            throw new AcidBaseCalculationException(AcidBaseCalculationErrorCode.SPECIES_ROLE_MISMATCH, "Species " + speciesCode + " is not a strong base (role: " + species.getPrimaryRole() + ", behavior: " + species.getDissociationBehavior() + ")");
        }
        BigDecimal kw = getKw(temperature);
        return calculator.calculateStrongBase(concentration, kw);
    }

    @Override
    public AcidBaseEquilibriumResult calculateWeakAcid(String speciesCode, MolarConcentration concentration, Temperature temperature) {
        ChemicalSpeciesDetails species = referenceService.getSpecies(speciesCode);
        EquilibriumConstantDetails kaDetails = referenceService.findKa(speciesCode, temperature, "COMP-H2O")
                .orElseThrow(() -> new AcidBaseCalculationException(AcidBaseCalculationErrorCode.MISSING_EQUILIBRIUM_CONSTANT, "Missing Ka constant for species " + speciesCode + " at requested temperature"));
        BigDecimal kw = getKw(temperature);
        return calculator.calculateWeakAcid(AcidBaseSystemType.WEAK_ACID, concentration, kaDetails.getValue(), kw);
    }

    @Override
    public AcidBaseEquilibriumResult calculateWeakBase(String speciesCode, MolarConcentration concentration, Temperature temperature) {
        ChemicalSpeciesDetails species = referenceService.getSpecies(speciesCode);
        EquilibriumConstantDetails kbDetails = referenceService.findKb(speciesCode, temperature, "COMP-H2O")
                .orElseThrow(() -> new AcidBaseCalculationException(AcidBaseCalculationErrorCode.MISSING_EQUILIBRIUM_CONSTANT, "Missing Kb constant for species " + speciesCode + " at requested temperature"));
        BigDecimal kw = getKw(temperature);
        return calculator.calculateWeakBase(AcidBaseSystemType.WEAK_BASE, concentration, kbDetails.getValue(), kw);
    }

    @Override
    public AcidBaseEquilibriumResult calculateSaltHydrolysis(String speciesCode, MolarConcentration concentration, Temperature temperature) {
        ChemicalSpeciesDetails species = referenceService.getSpecies(speciesCode);
        BigDecimal kw = getKw(temperature);

        // 1. Check if it acts as conjugate acid (CATION or ACID role)
        if ("ACID".equalsIgnoreCase(species.getPrimaryRole()) || "CATION".equalsIgnoreCase(species.getKind())) {
            BigDecimal ka = referenceService.findKa(speciesCode, temperature, "COMP-H2O")
                    .map(EquilibriumConstantDetails::getValue)
                    .orElseGet(() -> {
                        ConjugatePairDetails pair = referenceService.getConjugatePair(speciesCode);
                        EquilibriumConstantDetails kbBase = referenceService.findKb(pair.getBaseSpeciesCode(), temperature, "COMP-H2O")
                                .orElseThrow(() -> new AcidBaseCalculationException(AcidBaseCalculationErrorCode.MISSING_EQUILIBRIUM_CONSTANT, "Missing Kb constant for conjugate base " + pair.getBaseSpeciesCode()));
                        return kw.divide(kbBase.getValue(), MathContext.DECIMAL128);
                    });
            return calculator.calculateWeakAcid(AcidBaseSystemType.CONJUGATE_ACID_SALT, concentration, ka, kw);
        }

        // 2. Check if it acts as conjugate base (ANION or BASE role)
        if ("BASE".equalsIgnoreCase(species.getPrimaryRole()) || "ANION".equalsIgnoreCase(species.getKind())) {
            BigDecimal kb = referenceService.findKb(speciesCode, temperature, "COMP-H2O")
                    .map(EquilibriumConstantDetails::getValue)
                    .orElseGet(() -> {
                        ConjugatePairDetails pair = referenceService.getConjugatePair(speciesCode);
                        EquilibriumConstantDetails kaAcid = referenceService.findKa(pair.getAcidSpeciesCode(), temperature, "COMP-H2O")
                                .orElseThrow(() -> new AcidBaseCalculationException(AcidBaseCalculationErrorCode.MISSING_EQUILIBRIUM_CONSTANT, "Missing Ka constant for conjugate acid " + pair.getAcidSpeciesCode()));
                        return kw.divide(kaAcid.getValue(), MathContext.DECIMAL128);
                    });
            return calculator.calculateWeakBase(AcidBaseSystemType.CONJUGATE_BASE_SALT, concentration, kb, kw);
        }

        throw new AcidBaseCalculationException(AcidBaseCalculationErrorCode.SPECIES_ROLE_MISMATCH, "Species " + speciesCode + " does not undergo salt hydrolysis");
    }

    private BigDecimal getKw(Temperature temperature) {
        return referenceService.findKa("SPEC-H2O", temperature, "COMP-H2O")
                .map(EquilibriumConstantDetails::getValue)
                .orElseThrow(() -> new AcidBaseCalculationException(AcidBaseCalculationErrorCode.MISSING_EQUILIBRIUM_CONSTANT, "Missing Kw equilibrium constant for temperature " + temperature));
    }
}

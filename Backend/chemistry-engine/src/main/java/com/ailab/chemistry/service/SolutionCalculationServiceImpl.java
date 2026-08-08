package com.ailab.chemistry.service;

import com.ailab.chemistry.api.CompoundCatalogService;
import com.ailab.chemistry.api.CompoundDetails;
import com.ailab.chemistry.api.SolutionCalculationService;
import com.ailab.chemistry.domain.compound.MolarMass;
import com.ailab.chemistry.domain.compound.MolarMassCalculationBasis;
import com.ailab.chemistry.domain.compound.MolarMassKind;
import com.ailab.chemistry.domain.measurement.*;
import com.ailab.chemistry.domain.solution.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class SolutionCalculationServiceImpl implements SolutionCalculationService {

    private final CompoundCatalogService compoundCatalogService;
    private final SolutionCalculator calculator;

    @Autowired
    public SolutionCalculationServiceImpl(CompoundCatalogService compoundCatalogService) {
        this.compoundCatalogService = Objects.requireNonNull(compoundCatalogService, "CompoundCatalogService must not be null");
        this.calculator = new SolutionCalculator();
    }

    @Override
    public MolarConcentration calculateMolarity(String soluteCompoundCode, AmountOfSubstance soluteAmount, Volume finalSolutionVolume) {
        verifyCompoundExists(soluteCompoundCode);
        return calculator.calculateMolarity(soluteAmount, finalSolutionVolume);
    }

    @Override
    public Molality calculateMolality(String soluteCompoundCode, AmountOfSubstance soluteAmount, Mass solventMass) {
        verifyCompoundExists(soluteCompoundCode);
        return calculator.calculateMolality(soluteAmount, solventMass);
    }

    @Override
    public MassConcentration calculateMassConcentration(Mass soluteMass, Volume finalSolutionVolume) {
        return calculator.calculateMassConcentration(soluteMass, finalSolutionVolume);
    }

    @Override
    public ConcentrationConversionResult convertMassConcentrationToMolarity(String soluteCompoundCode, MassConcentration concentration) {
        MolarMass molarMass = getMolarMassForCompound(soluteCompoundCode);
        return calculator.convertMassConcentrationToMolarity(soluteCompoundCode, concentration, molarMass);
    }

    @Override
    public DilutionResult calculateDilution(DilutionRequest request) {
        verifyCompoundExists(request.getSoluteCompoundCode());
        return calculator.calculateDilution(request);
    }

    @Override
    public SolutionPreparationResult calculatePreparation(String soluteCompoundCode, MolarConcentration targetConcentration, Volume finalVolume) {
        MolarMass molarMass = getMolarMassForCompound(soluteCompoundCode);
        return calculator.calculatePreparation(soluteCompoundCode, targetConcentration, finalVolume, molarMass);
    }

    @Override
    public SolutionMixingResult mixSameSoluteSolutions(List<SolutionComposition> solutions, SolutionVolumeAssumption volumeAssumption) {
        if (solutions == null || solutions.isEmpty()) {
            throw new SolutionException(SolutionErrorCode.INVALID_QUANTITY, "Solutions list for mixing cannot be empty");
        }
        for (SolutionComposition sol : solutions) {
            verifyCompoundExists(sol.getSoluteCode());
            verifyCompoundExists(sol.getSolventCode());
        }
        return calculator.mixSameSoluteSolutions(solutions, volumeAssumption);
    }

    private void verifyCompoundExists(String compoundCode) {
        if (compoundCode == null || compoundCode.isBlank()) {
            throw new SolutionException(SolutionErrorCode.COMPOUND_NOT_FOUND, "Compound code must not be null or blank");
        }
        compoundCatalogService.getByCode(compoundCode);
    }

    private MolarMass getMolarMassForCompound(String compoundCode) {
        CompoundDetails details = compoundCatalogService.getByCode(compoundCode);
        BigDecimal repVal = details.getMolarMassValue();
        BigDecimal lower = details.getMolarMassLowerBound();
        BigDecimal upper = details.getMolarMassUpperBound();

        MolarMassKind kind;
        try {
            kind = MolarMassKind.valueOf(details.getMolarMassKind());
        } catch (Exception e) {
            kind = MolarMassKind.EXACT_FROM_FIXED_VALUES;
        }

        MolarMassCalculationBasis basis = new MolarMassCalculationBasis("IUPAC-2021", "1.0");

        if (lower != null && upper != null) {
            return MolarMass.interval(repVal, lower, upper, basis);
        } else {
            return MolarMass.exact(repVal, basis);
        }
    }
}

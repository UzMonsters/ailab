package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.measurement.*;
import com.ailab.chemistry.domain.solution.*;

import java.util.List;

public interface SolutionCalculationService {

    MolarConcentration calculateMolarity(
            String soluteCompoundCode,
            AmountOfSubstance soluteAmount,
            Volume finalSolutionVolume
    );

    Molality calculateMolality(
            String soluteCompoundCode,
            AmountOfSubstance soluteAmount,
            Mass solventMass
    );

    MassConcentration calculateMassConcentration(
            Mass soluteMass,
            Volume finalSolutionVolume
    );

    ConcentrationConversionResult convertMassConcentrationToMolarity(
            String soluteCompoundCode,
            MassConcentration concentration
    );

    DilutionResult calculateDilution(
            DilutionRequest request
    );

    SolutionPreparationResult calculatePreparation(
            String soluteCompoundCode,
            MolarConcentration targetConcentration,
            Volume finalVolume
    );

    SolutionMixingResult mixSameSoluteSolutions(
            List<SolutionComposition> solutions,
            SolutionVolumeAssumption volumeAssumption
    );
}

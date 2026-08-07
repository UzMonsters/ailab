package com.ailab.chemistry.domain.solution;

import com.ailab.chemistry.domain.compound.MolarMass;
import com.ailab.chemistry.domain.measurement.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public final class SolutionCalculator {

    public MolarConcentration calculateMolarity(AmountOfSubstance soluteAmount, Volume finalSolutionVolume) {
        Objects.requireNonNull(soluteAmount, "Solute amount must not be null");
        Objects.requireNonNull(finalSolutionVolume, "Final solution volume must not be null");

        BigDecimal moles = soluteAmount.in(AmountOfSubstanceUnit.MOLE);
        BigDecimal liters = finalSolutionVolume.in(VolumeUnit.LITER);
        if (liters.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SolutionException(SolutionErrorCode.INVALID_QUANTITY, "Final solution volume must be positive: " + liters);
        }

        BigDecimal molarityVal = moles.divide(liters, ScientificMath.CALCULATION_CONTEXT);
        return MolarConcentration.of(molarityVal, MolarConcentrationUnit.MOL_PER_LITER);
    }

    public Molality calculateMolality(AmountOfSubstance soluteAmount, Mass solventMass) {
        Objects.requireNonNull(soluteAmount, "Solute amount must not be null");
        Objects.requireNonNull(solventMass, "Solvent mass must not be null");

        BigDecimal moles = soluteAmount.in(AmountOfSubstanceUnit.MOLE);
        BigDecimal kg = solventMass.in(MassUnit.KILOGRAM);
        if (kg.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SolutionException(SolutionErrorCode.INVALID_QUANTITY, "Solvent mass must be positive: " + kg);
        }

        BigDecimal molalityVal = moles.divide(kg, ScientificMath.CALCULATION_CONTEXT);
        return Molality.of(molalityVal);
    }

    public MassConcentration calculateMassConcentration(Mass soluteMass, Volume finalSolutionVolume) {
        Objects.requireNonNull(soluteMass, "Solute mass must not be null");
        Objects.requireNonNull(finalSolutionVolume, "Final solution volume must not be null");

        BigDecimal grams = soluteMass.in(MassUnit.GRAM);
        BigDecimal liters = finalSolutionVolume.in(VolumeUnit.LITER);
        if (liters.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SolutionException(SolutionErrorCode.INVALID_QUANTITY, "Final solution volume must be positive: " + liters);
        }

        BigDecimal massConcVal = grams.divide(liters, ScientificMath.CALCULATION_CONTEXT);
        return MassConcentration.of(massConcVal, MassConcentrationUnit.GRAM_PER_LITER);
    }

    public MassFraction calculateMassFraction(Mass soluteMass, Mass totalSolutionMass) {
        Objects.requireNonNull(soluteMass, "Solute mass must not be null");
        Objects.requireNonNull(totalSolutionMass, "Total solution mass must not be null");

        BigDecimal soluteGrams = soluteMass.in(MassUnit.GRAM);
        BigDecimal totalGrams = totalSolutionMass.in(MassUnit.GRAM);
        if (totalGrams.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SolutionException(SolutionErrorCode.INVALID_QUANTITY, "Total solution mass must be positive: " + totalGrams);
        }

        BigDecimal fractionVal = soluteGrams.divide(totalGrams, ScientificMath.CALCULATION_CONTEXT);
        return MassFraction.of(fractionVal);
    }

    public MoleFraction calculateMoleFraction(AmountOfSubstance componentAmount, AmountOfSubstance totalAmount) {
        Objects.requireNonNull(componentAmount, "Component amount must not be null");
        Objects.requireNonNull(totalAmount, "Total amount must not be null");

        BigDecimal compMoles = componentAmount.in(AmountOfSubstanceUnit.MOLE);
        BigDecimal totMoles = totalAmount.in(AmountOfSubstanceUnit.MOLE);
        if (totMoles.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SolutionException(SolutionErrorCode.INVALID_QUANTITY, "Total amount of substance must be positive: " + totMoles);
        }

        BigDecimal fractionVal = compMoles.divide(totMoles, ScientificMath.CALCULATION_CONTEXT);
        return MoleFraction.of(fractionVal);
    }

    public ConcentrationConversionResult convertMassConcentrationToMolarity(
            String soluteCompoundCode,
            MassConcentration concentration,
            MolarMass molarMass) {
        Objects.requireNonNull(soluteCompoundCode, "Solute compound code must not be null");
        Objects.requireNonNull(concentration, "Mass concentration must not be null");
        Objects.requireNonNull(molarMass, "Molar mass must not be null");

        BigDecimal gPerL = concentration.in(MassConcentrationUnit.GRAM_PER_LITER);
        BigDecimal repMolarMass = molarMass.getRepresentativeValue();
        if (repMolarMass.compareTo(BigDecimal.ZERO) <= 0) {
            throw new SolutionException(SolutionErrorCode.CALCULATION_ERROR, "Molar mass must be positive: " + repMolarMass);
        }

        BigDecimal repMolarityVal = gPerL.divide(repMolarMass, ScientificMath.CALCULATION_CONTEXT);
        MolarConcentration repMolarity = MolarConcentration.of(repMolarityVal, MolarConcentrationUnit.MOL_PER_LITER);

        MolarConcentration lowMolarity = null;
        MolarConcentration uppMolarity = null;

        if (molarMass.getUpperBound() != null) {
            BigDecimal lowVal = gPerL.divide(molarMass.getUpperBound(), ScientificMath.CALCULATION_CONTEXT);
            lowMolarity = MolarConcentration.of(lowVal, MolarConcentrationUnit.MOL_PER_LITER);
        }

        if (molarMass.getLowerBound() != null) {
            BigDecimal uppVal = gPerL.divide(molarMass.getLowerBound(), ScientificMath.CALCULATION_CONTEXT);
            uppMolarity = MolarConcentration.of(uppVal, MolarConcentrationUnit.MOL_PER_LITER);
        }

        return new ConcentrationConversionResult(
                soluteCompoundCode,
                repMolarity,
                lowMolarity,
                uppMolarity,
                null,
                concentration,
                null,
                null,
                null,
                molarMass
        );
    }

    public DilutionResult calculateDilution(DilutionRequest request) {
        Objects.requireNonNull(request, "Dilution request must not be null");

        MolarConcentration c1 = request.getInitialConcentration().orElse(null);
        Volume v1 = request.getInitialVolume().orElse(null);
        MolarConcentration c2 = request.getTargetConcentration().orElse(null);
        Volume v2 = request.getTargetVolume().orElse(null);

        if (c1 == null || c2 == null) {
            throw new SolutionException(SolutionErrorCode.DILUTION_PARAM_MISSING, "Initial concentration C1 and target concentration C2 are required for dilution");
        }

        BigDecimal c1Val = c1.in(MolarConcentrationUnit.MOL_PER_LITER);
        BigDecimal c2Val = c2.in(MolarConcentrationUnit.MOL_PER_LITER);

        if (c2Val.compareTo(c1Val) > 0) {
            throw new SolutionException(SolutionErrorCode.INVALID_DILUTION_TARGET, "Target concentration C2 (" + c2 + ") cannot exceed initial concentration C1 (" + c1 + ")");
        }

        BigDecimal v1Liters;
        BigDecimal v2Liters;

        if (v1 != null && v2 == null) {
            v1Liters = v1.in(VolumeUnit.LITER);
            v2Liters = c1Val.multiply(v1Liters, ScientificMath.CALCULATION_CONTEXT).divide(c2Val, ScientificMath.CALCULATION_CONTEXT);
            v2 = Volume.of(v2Liters, VolumeUnit.LITER);
        } else if (v2 != null && v1 == null) {
            v2Liters = v2.in(VolumeUnit.LITER);
            v1Liters = c2Val.multiply(v2Liters, ScientificMath.CALCULATION_CONTEXT).divide(c1Val, ScientificMath.CALCULATION_CONTEXT);
            v1 = Volume.of(v1Liters, VolumeUnit.LITER);
        } else if (v1 != null && v2 != null) {
            v1Liters = v1.in(VolumeUnit.LITER);
            v2Liters = v2.in(VolumeUnit.LITER);
        } else {
            throw new SolutionException(SolutionErrorCode.DILUTION_PARAM_MISSING, "Either initial volume V1 or target volume V2 must be provided");
        }

        BigDecimal addedVolLiters = v2Liters.subtract(v1Liters, ScientificMath.CALCULATION_CONTEXT);
        if (addedVolLiters.compareTo(BigDecimal.ZERO) < 0) {
            addedVolLiters = BigDecimal.ZERO;
        }
        Volume addedVolume = Volume.of(addedVolLiters, VolumeUnit.LITER);

        BigDecimal soluteMolesVal = c1Val.multiply(v1Liters, ScientificMath.CALCULATION_CONTEXT);
        AmountOfSubstance soluteMoles = AmountOfSubstance.of(soluteMolesVal, AmountOfSubstanceUnit.MOLE);

        return new DilutionResult(
                request.getSoluteCompoundCode(),
                c1,
                v1,
                c2,
                v2,
                addedVolume,
                soluteMoles
        );
    }

    public SolutionPreparationResult calculatePreparation(
            String soluteCompoundCode,
            MolarConcentration targetConcentration,
            Volume targetVolume,
            MolarMass soluteMolarMass) {
        Objects.requireNonNull(soluteCompoundCode, "Solute compound code must not be null");
        Objects.requireNonNull(targetConcentration, "Target concentration must not be null");
        Objects.requireNonNull(targetVolume, "Target volume must not be null");
        Objects.requireNonNull(soluteMolarMass, "Solute MolarMass must not be null");

        BigDecimal c = targetConcentration.in(MolarConcentrationUnit.MOL_PER_LITER);
        BigDecimal v = targetVolume.in(VolumeUnit.LITER);

        BigDecimal reqMolesVal = c.multiply(v, ScientificMath.CALCULATION_CONTEXT);
        AmountOfSubstance reqMoles = AmountOfSubstance.of(reqMolesVal, AmountOfSubstanceUnit.MOLE);

        BigDecimal repMolarMass = soluteMolarMass.getRepresentativeValue();
        BigDecimal reqMassGrams = reqMolesVal.multiply(repMolarMass, ScientificMath.CALCULATION_CONTEXT);
        Mass reqMass = Mass.of(reqMassGrams, MassUnit.GRAM);

        Mass reqMassLow = null;
        Mass reqMassUpp = null;

        if (soluteMolarMass.getLowerBound() != null) {
            BigDecimal lowGrams = reqMolesVal.multiply(soluteMolarMass.getLowerBound(), ScientificMath.CALCULATION_CONTEXT);
            reqMassLow = Mass.of(lowGrams, MassUnit.GRAM);
        }

        if (soluteMolarMass.getUpperBound() != null) {
            BigDecimal uppGrams = reqMolesVal.multiply(soluteMolarMass.getUpperBound(), ScientificMath.CALCULATION_CONTEXT);
            reqMassUpp = Mass.of(uppGrams, MassUnit.GRAM);
        }

        return new SolutionPreparationResult(
                soluteCompoundCode,
                targetConcentration,
                targetVolume,
                reqMass,
                reqMassLow,
                reqMassUpp,
                reqMoles,
                null,
                null
        );
    }

    public SolutionMixingResult mixSameSoluteSolutions(
            List<SolutionComposition> solutions,
            SolutionVolumeAssumption volumeAssumption) {
        Objects.requireNonNull(solutions, "Solutions list must not be null");
        if (solutions.isEmpty()) {
            throw new SolutionException(SolutionErrorCode.INVALID_QUANTITY, "Solutions list for mixing cannot be empty");
        }
        Objects.requireNonNull(volumeAssumption, "Volume assumption must not be null");

        String primarySolute = solutions.get(0).getSoluteCode();
        String primarySolvent = solutions.get(0).getSolventCode();

        for (SolutionComposition sol : solutions) {
            if (!sol.getSoluteCode().equalsIgnoreCase(primarySolute)) {
                throw new SolutionException(SolutionErrorCode.INCOMPATIBLE_SOLUTES, "All mixed solutions must contain the same solute: expected " + primarySolute + " but found " + sol.getSoluteCode());
            }
            if (!sol.getSolventCode().equalsIgnoreCase(primarySolvent)) {
                throw new SolutionException(SolutionErrorCode.INCOMPATIBLE_SOLVENTS, "All mixed solutions must contain the same solvent: expected " + primarySolvent + " but found " + sol.getSolventCode());
            }
        }

        BigDecimal totalSoluteMolesVal = BigDecimal.ZERO;
        BigDecimal totalSoluteMassGrams = BigDecimal.ZERO;
        BigDecimal totalSolventMassKg = BigDecimal.ZERO;
        BigDecimal totalVolumeLiters = BigDecimal.ZERO;

        for (SolutionComposition sol : solutions) {
            totalSoluteMolesVal = totalSoluteMolesVal.add(sol.getSoluteAmount().in(AmountOfSubstanceUnit.MOLE), ScientificMath.CALCULATION_CONTEXT);
            totalSoluteMassGrams = totalSoluteMassGrams.add(sol.getSoluteMass().in(MassUnit.GRAM), ScientificMath.CALCULATION_CONTEXT);
            totalSolventMassKg = totalSolventMassKg.add(sol.getSolventMass().in(MassUnit.KILOGRAM), ScientificMath.CALCULATION_CONTEXT);

            if (volumeAssumption == SolutionVolumeAssumption.ADDITIVE_VOLUMES) {
                if (sol.getSolutionVolume().isPresent()) {
                    totalVolumeLiters = totalVolumeLiters.add(sol.getSolutionVolume().get().in(VolumeUnit.LITER), ScientificMath.CALCULATION_CONTEXT);
                } else if (sol.getSolventVolume().isPresent()) {
                    totalVolumeLiters = totalVolumeLiters.add(sol.getSolventVolume().get().in(VolumeUnit.LITER), ScientificMath.CALCULATION_CONTEXT);
                } else {
                    throw new SolutionException(SolutionErrorCode.INVALID_QUANTITY, "Volume missing for solution component during additive volume mixing");
                }
            } else if (volumeAssumption == SolutionVolumeAssumption.NON_ADDITIVE_DENSITY_REQUIRED) {
                if (sol.getDensity().isEmpty()) {
                    throw new SolutionException(SolutionErrorCode.NON_ADDITIVE_VOLUME_DENSITY_REQUIRED, "Mixing under non-additive volume assumption requires mixture density");
                }
                // If density is provided for mixture
                BigDecimal solutionMassKg = sol.getTotalSolutionMass().in(MassUnit.KILOGRAM);
                BigDecimal densityKgPerM3 = sol.getDensity().get().getValueInKgPerM3();
                BigDecimal m3 = solutionMassKg.divide(densityKgPerM3, ScientificMath.CALCULATION_CONTEXT);
                BigDecimal l = m3.multiply(new BigDecimal("1000"), ScientificMath.CALCULATION_CONTEXT);
                totalVolumeLiters = totalVolumeLiters.add(l, ScientificMath.CALCULATION_CONTEXT);
            } else {
                if (sol.getSolutionVolume().isPresent()) {
                    totalVolumeLiters = totalVolumeLiters.add(sol.getSolutionVolume().get().in(VolumeUnit.LITER), ScientificMath.CALCULATION_CONTEXT);
                }
            }
        }

        AmountOfSubstance totalSoluteMoles = AmountOfSubstance.of(totalSoluteMolesVal, AmountOfSubstanceUnit.MOLE);
        Mass totalSoluteMass = Mass.of(totalSoluteMassGrams, MassUnit.GRAM);
        Mass totalSolventMass = Mass.of(totalSolventMassKg, MassUnit.KILOGRAM);
        Volume finalVolume = Volume.of(totalVolumeLiters, VolumeUnit.LITER);

        MolarConcentration finalMolarity = calculateMolarity(totalSoluteMoles, finalVolume);
        Molality finalMolality = calculateMolality(totalSoluteMoles, totalSolventMass);
        MassConcentration finalMassConc = calculateMassConcentration(totalSoluteMass, finalVolume);
        MassFraction finalMassFrac = calculateMassFraction(totalSoluteMass, totalSoluteMass.add(totalSolventMass));

        return new SolutionMixingResult(
                primarySolute,
                primarySolvent,
                totalSoluteMoles,
                totalSoluteMass,
                totalSolventMass,
                finalVolume,
                finalMolarity,
                finalMolality,
                finalMassConc,
                finalMassFrac,
                volumeAssumption
        );
    }
}

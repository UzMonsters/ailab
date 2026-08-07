package com.ailab.chemistry.domain.solubility;

import com.ailab.chemistry.domain.acidbase.ActivityModel;
import com.ailab.chemistry.domain.acidbase.ActivityParameterSet;
import com.ailab.chemistry.domain.acidbase.IonicSpeciesConcentration;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;

import java.math.BigDecimal;
import java.util.List;

public record PrecipitationRequest(
        SolubilityEquilibrium equilibrium,
        String equilibriumCode,
        List<SolutionIonAmount> ionAmounts,
        Volume finalVolume,
        List<IonicSpeciesConcentration> spectatorIons,
        ActivityParameterSet activityParameterSet,
        Temperature temperature,
        String solventCode,
        ActivityModel activityModel,
        BigDecimal precipitateMolarMassGramsPerMole,
        BigDecimal comparisonTolerance
) {
    public PrecipitationRequest(SolubilityEquilibrium equilibrium, List<SolutionIonAmount> ionAmounts, Volume finalVolume,
                                List<IonicSpeciesConcentration> spectatorIons, ActivityParameterSet activityParameterSet,
                                BigDecimal precipitateMolarMassGramsPerMole, BigDecimal comparisonTolerance) {
        this(equilibrium, null, ionAmounts, finalVolume, spectatorIons, activityParameterSet,
                equilibrium.conditions().temperature(), equilibrium.conditions().solventCode(), activityParameterSet.model(),
                precipitateMolarMassGramsPerMole, comparisonTolerance);
    }

    public PrecipitationRequest {
        ionAmounts = List.copyOf(ionAmounts == null ? List.of() : ionAmounts);
        spectatorIons = List.copyOf(spectatorIons == null ? List.of() : spectatorIons);
        if (finalVolume != null && finalVolume.in(VolumeUnit.LITER).compareTo(BigDecimal.ZERO) <= 0) {
            throw new SolubilityException(SolubilityErrorCode.NON_POSITIVE_VOLUME, "Final precipitation volume must be positive");
        }
        comparisonTolerance = comparisonTolerance == null ? new BigDecimal("1e-8") : comparisonTolerance;
    }

    public static PrecipitationRequest forEquilibriumCode(String equilibriumCode, List<SolutionIonAmount> ionAmounts, Volume finalVolume,
                                                          List<IonicSpeciesConcentration> spectatorIons, Temperature temperature,
                                                          String solventCode, ActivityModel activityModel) {
        return new PrecipitationRequest(null, equilibriumCode, ionAmounts, finalVolume, spectatorIons, null,
                temperature, solventCode, activityModel, null, new BigDecimal("1e-8"));
    }
}

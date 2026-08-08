package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;

import java.math.BigDecimal;
import java.util.Objects;

public record BufferPerturbationRequest(
        BufferCalculationRequest initialBuffer,
        StrongReagentType reagentType,
        AmountOfSubstance reagentAmount,
        BufferVolumePolicy volumePolicy,
        Volume finalVolume
) {
    public BufferPerturbationRequest {
        Objects.requireNonNull(initialBuffer, "initialBuffer must not be null");
        Objects.requireNonNull(reagentType, "reagentType must not be null");
        Objects.requireNonNull(reagentAmount, "reagentAmount must not be null");
        Objects.requireNonNull(volumePolicy, "volumePolicy must not be null");
        if (reagentAmount.in(AmountOfSubstanceUnit.MOLE).compareTo(BigDecimal.ZERO) <= 0) {
            throw new BufferException(BufferErrorCode.NON_POSITIVE_COMPONENT_AMOUNT, "Strong reagent amount must be positive");
        }
        if (volumePolicy == BufferVolumePolicy.NEGLIGIBLE_ADDED_VOLUME) {
            finalVolume = initialBuffer.finalVolume();
        } else {
            Objects.requireNonNull(finalVolume, "finalVolume must not be null for explicit final-volume perturbations");
        }
        BigDecimal initialLiters = initialBuffer.finalVolume().in(VolumeUnit.LITER);
        BigDecimal finalLiters = finalVolume.in(VolumeUnit.LITER);
        if (finalLiters.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BufferException(BufferErrorCode.INVALID_FINAL_VOLUME, "Perturbation final volume must be positive");
        }
        if (finalLiters.compareTo(initialLiters) < 0) {
            throw new BufferException(BufferErrorCode.INVALID_FINAL_VOLUME, "Perturbation final volume cannot be smaller than initial buffer volume");
        }
    }

    public static BufferPerturbationRequest strongAcidNegligibleVolume(BufferCalculationRequest initialBuffer, AmountOfSubstance reagentAmount) {
        return new BufferPerturbationRequest(initialBuffer, StrongReagentType.STRONG_ACID, reagentAmount, BufferVolumePolicy.NEGLIGIBLE_ADDED_VOLUME, initialBuffer.finalVolume());
    }

    public static BufferPerturbationRequest strongBaseNegligibleVolume(BufferCalculationRequest initialBuffer, AmountOfSubstance reagentAmount) {
        return new BufferPerturbationRequest(initialBuffer, StrongReagentType.STRONG_BASE, reagentAmount, BufferVolumePolicy.NEGLIGIBLE_ADDED_VOLUME, initialBuffer.finalVolume());
    }

    public static BufferPerturbationRequest strongAcidWithFinalVolume(BufferCalculationRequest initialBuffer, AmountOfSubstance reagentAmount, Volume finalVolume) {
        return new BufferPerturbationRequest(initialBuffer, StrongReagentType.STRONG_ACID, reagentAmount, BufferVolumePolicy.EXPLICIT_FINAL_VOLUME, finalVolume);
    }

    public static BufferPerturbationRequest strongBaseWithFinalVolume(BufferCalculationRequest initialBuffer, AmountOfSubstance reagentAmount, Volume finalVolume) {
        return new BufferPerturbationRequest(initialBuffer, StrongReagentType.STRONG_BASE, reagentAmount, BufferVolumePolicy.EXPLICIT_FINAL_VOLUME, finalVolume);
    }
}

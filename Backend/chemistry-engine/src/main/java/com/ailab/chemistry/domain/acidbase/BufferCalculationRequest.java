package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

public record BufferCalculationRequest(
        BufferSystem system,
        String acidSpeciesCode,
        String baseSpeciesCode,
        BufferComponent acidComponent,
        BufferComponent baseComponent,
        Volume finalVolume,
        Temperature temperature,
        String solventCode
) {
    public BufferCalculationRequest {
        if (system != null) {
            acidSpeciesCode = system.acidSpeciesCode();
            baseSpeciesCode = system.baseSpeciesCode();
            temperature = system.temperature();
            solventCode = system.solventCode();
        }
        acidSpeciesCode = requireText(acidSpeciesCode, "acidSpeciesCode");
        baseSpeciesCode = requireText(baseSpeciesCode, "baseSpeciesCode");
        Objects.requireNonNull(acidComponent, "acidComponent must not be null");
        Objects.requireNonNull(baseComponent, "baseComponent must not be null");
        Objects.requireNonNull(finalVolume, "finalVolume must not be null");
        Objects.requireNonNull(temperature, "temperature must not be null");
        solventCode = requireText(solventCode, "solventCode");
        validatePositive(acidComponent.moles(), BufferErrorCode.NON_POSITIVE_COMPONENT_AMOUNT, "Acid component amount must be positive");
        validatePositive(baseComponent.moles(), BufferErrorCode.NON_POSITIVE_COMPONENT_AMOUNT, "Base component amount must be positive");
        validatePositive(acidComponent.concentration().in(MolarConcentrationUnit.MOL_PER_LITER), BufferErrorCode.NON_POSITIVE_COMPONENT_AMOUNT, "Acid component concentration must be positive");
        validatePositive(baseComponent.concentration().in(MolarConcentrationUnit.MOL_PER_LITER), BufferErrorCode.NON_POSITIVE_COMPONENT_AMOUNT, "Base component concentration must be positive");
        validatePositive(finalVolume.in(VolumeUnit.LITER), BufferErrorCode.NON_POSITIVE_VOLUME, "Final volume must be positive");
    }

    public static BufferCalculationRequest fromAmounts(BufferSystem system, AmountOfSubstance acidAmount, AmountOfSubstance baseAmount, Volume finalVolume) {
        return new BufferCalculationRequest(
                system,
                system.acidSpeciesCode(),
                system.baseSpeciesCode(),
                BufferComponent.fromAmount(system.acidSpeciesCode(), acidAmount, finalVolume),
                BufferComponent.fromAmount(system.baseSpeciesCode(), baseAmount, finalVolume),
                finalVolume,
                system.temperature(),
                system.solventCode()
        );
    }

    public static BufferCalculationRequest fromSpeciesAmounts(
            String acidSpeciesCode,
            String baseSpeciesCode,
            AmountOfSubstance acidAmount,
            AmountOfSubstance baseAmount,
            Volume finalVolume,
            Temperature temperature,
            String solventCode) {
        return new BufferCalculationRequest(
                null,
                acidSpeciesCode,
                baseSpeciesCode,
                BufferComponent.fromAmount(acidSpeciesCode, acidAmount, finalVolume),
                BufferComponent.fromAmount(baseSpeciesCode, baseAmount, finalVolume),
                finalVolume,
                temperature,
                solventCode
        );
    }

    public static BufferCalculationRequest fromSpeciesConcentrations(
            String acidSpeciesCode,
            String baseSpeciesCode,
            MolarConcentration acidConcentration,
            MolarConcentration baseConcentration,
            Volume finalVolume,
            Temperature temperature,
            String solventCode) {
        return new BufferCalculationRequest(
                null,
                acidSpeciesCode,
                baseSpeciesCode,
                BufferComponent.fromConcentration(acidSpeciesCode, acidConcentration, finalVolume),
                BufferComponent.fromConcentration(baseSpeciesCode, baseConcentration, finalVolume),
                finalVolume,
                temperature,
                solventCode
        );
    }

    public Optional<BufferSystem> maybeSystem() {
        return Optional.ofNullable(system);
    }

    public BufferCalculationRequest withSystem(BufferSystem resolvedSystem) {
        return new BufferCalculationRequest(resolvedSystem, acidSpeciesCode, baseSpeciesCode, acidComponent, baseComponent, finalVolume, temperature, solventCode);
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new BufferException(BufferErrorCode.INVALID_CONJUGATE_PAIR, name + " must not be blank");
        }
        return value.trim();
    }

    private static void validatePositive(BigDecimal value, BufferErrorCode code, String message) {
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BufferException(code, message);
        }
    }
}

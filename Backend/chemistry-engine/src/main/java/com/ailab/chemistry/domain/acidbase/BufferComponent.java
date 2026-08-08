package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.MolarConcentrationUnit;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;

import java.math.BigDecimal;
import java.util.Objects;

public record BufferComponent(
        String speciesCode,
        AmountOfSubstance amount,
        MolarConcentration concentration
) {
    public BufferComponent {
        if (speciesCode == null || speciesCode.isBlank()) {
            throw new BufferException(BufferErrorCode.INVALID_CONJUGATE_PAIR, "Species code must not be blank");
        }
        speciesCode = speciesCode.trim();
        Objects.requireNonNull(amount, "amount must not be null");
        Objects.requireNonNull(concentration, "concentration must not be null");
    }

    public static BufferComponent fromAmount(String speciesCode, AmountOfSubstance amount, Volume finalVolume) {
        BigDecimal liters = liters(finalVolume);
        BigDecimal moles = amount.in(AmountOfSubstanceUnit.MOLE);
        MolarConcentration concentration = MolarConcentration.of(moles.divide(liters, java.math.MathContext.DECIMAL128), MolarConcentrationUnit.MOL_PER_LITER);
        return new BufferComponent(speciesCode, amount, concentration);
    }

    public static BufferComponent fromConcentration(String speciesCode, MolarConcentration concentration, Volume finalVolume) {
        BigDecimal liters = liters(finalVolume);
        BigDecimal molar = concentration.in(MolarConcentrationUnit.MOL_PER_LITER);
        AmountOfSubstance amount = AmountOfSubstance.of(molar.multiply(liters, java.math.MathContext.DECIMAL128), AmountOfSubstanceUnit.MOLE);
        return new BufferComponent(speciesCode, amount, concentration);
    }

    private static BigDecimal liters(Volume finalVolume) {
        Objects.requireNonNull(finalVolume, "finalVolume must not be null");
        BigDecimal liters = finalVolume.in(VolumeUnit.LITER);
        if (liters.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BufferException(BufferErrorCode.NON_POSITIVE_VOLUME, "Final volume must be positive");
        }
        return liters;
    }

    public BigDecimal moles() {
        return amount.in(AmountOfSubstanceUnit.MOLE);
    }

    public AmountOfSubstance getAmount() {
        return amount;
    }

    public MolarConcentration getConcentration() {
        return concentration;
    }
}

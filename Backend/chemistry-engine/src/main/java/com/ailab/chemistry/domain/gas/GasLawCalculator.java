package com.ailab.chemistry.domain.gas;

import com.ailab.chemistry.domain.measurement.AmountOfSubstance;
import com.ailab.chemistry.domain.measurement.AmountOfSubstanceUnit;
import com.ailab.chemistry.domain.measurement.Density;
import com.ailab.chemistry.domain.measurement.DensityUnit;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.ScientificConstants;
import com.ailab.chemistry.domain.measurement.ScientificMath;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import com.ailab.chemistry.domain.measurement.Volume;
import com.ailab.chemistry.domain.measurement.VolumeUnit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GasLawCalculator {
    private static final BigDecimal THOUSAND = new BigDecimal("1000");
    private static final BigDecimal RESIDUAL_TOLERANCE = new BigDecimal("0.001");

    public GasStateResult calculateState(GasStateRequest request) {
        GasEquationModel model = request.model() == null ? GasEquationModel.IDEAL_GAS : request.model();
        CompressibilityFactor z = zFor(model, request.compressibilityFactor());
        int known = countKnown(request.pressure(), request.volume(), request.amount(), request.temperature());
        if (known != 3 && known != 4) {
            throw new GasLawException(GasLawErrorCode.INVALID_UNKNOWN_COUNT, "Exactly one state variable must be unknown, or all four must be supplied for residual validation");
        }
        validatePositive(request.pressure(), request.volume(), request.amount(), request.temperature());

        Pressure pressure = request.pressure();
        Volume volume = request.volume();
        AmountOfSubstance amount = request.amount();
        Temperature temperature = request.temperature();

        BigDecimal r = ScientificConstants.IDEAL_GAS_CONSTANT_SI;
        if (pressure == null) {
            BigDecimal p = z.value().multiply(amount.in(AmountOfSubstanceUnit.MOLE), ScientificMath.CALCULATION_CONTEXT)
                    .multiply(r, ScientificMath.CALCULATION_CONTEXT)
                    .multiply(temperature.in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT)
                    .divide(litersToCubicMeters(volume), ScientificMath.CALCULATION_CONTEXT);
            pressure = Pressure.of(p, PressureUnit.PASCAL);
        } else if (volume == null) {
            BigDecimal vM3 = z.value().multiply(amount.in(AmountOfSubstanceUnit.MOLE), ScientificMath.CALCULATION_CONTEXT)
                    .multiply(r, ScientificMath.CALCULATION_CONTEXT)
                    .multiply(temperature.in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT)
                    .divide(pressure.in(PressureUnit.PASCAL), ScientificMath.CALCULATION_CONTEXT);
            volume = Volume.of(vM3.multiply(THOUSAND, ScientificMath.CALCULATION_CONTEXT), VolumeUnit.LITER);
        } else if (amount == null) {
            BigDecimal n = pressure.in(PressureUnit.PASCAL).multiply(litersToCubicMeters(volume), ScientificMath.CALCULATION_CONTEXT)
                    .divide(z.value().multiply(r, ScientificMath.CALCULATION_CONTEXT).multiply(temperature.in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
            amount = AmountOfSubstance.of(n, AmountOfSubstanceUnit.MOLE);
        } else if (temperature == null) {
            BigDecimal t = pressure.in(PressureUnit.PASCAL).multiply(litersToCubicMeters(volume), ScientificMath.CALCULATION_CONTEXT)
                    .divide(z.value().multiply(amount.in(AmountOfSubstanceUnit.MOLE), ScientificMath.CALCULATION_CONTEXT).multiply(r, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
            temperature = Temperature.of(t, TemperatureUnit.KELVIN);
        }

        BigDecimal residual = residual(pressure, volume, amount, temperature, z).abs();
        GasCalculationStatus status = residual.compareTo(RESIDUAL_TOLERANCE) <= 0
                ? GasCalculationStatus.SUCCESS
                : GasCalculationStatus.RESIDUAL_EXCEEDS_TOLERANCE;
        GasCalculationMethod method = model == GasEquationModel.IDEAL_GAS
                ? GasCalculationMethod.IDEAL_GAS_LAW
                : GasCalculationMethod.EXPLICIT_COMPRESSIBILITY_FACTOR;
        return new GasStateResult(new GasState(pressure, volume, amount, temperature, z), model, method, status, residual);
    }

    public GasMixtureResult calculateMixture(GasMixture mixture) {
        validateStrictPositive(mixture.totalPressure().in(PressureUnit.PASCAL), GasLawErrorCode.INVALID_PRESSURE, "Total pressure must be positive");
        BigDecimal totalMoles = BigDecimal.ZERO;
        for (GasMixtureComponent component : mixture.components()) {
            BigDecimal moles = component.amount().in(AmountOfSubstanceUnit.MOLE);
            if (moles.compareTo(BigDecimal.ZERO) < 0) {
                throw new GasLawException(GasLawErrorCode.INVALID_AMOUNT, "Mixture component amount cannot be negative");
            }
            totalMoles = totalMoles.add(moles, ScientificMath.CALCULATION_CONTEXT);
        }
        validateStrictPositive(totalMoles, GasLawErrorCode.INVALID_MIXTURE, "Mixture total amount must be positive");

        List<MoleFraction> fractions = new ArrayList<>();
        List<PartialPressure> partials = new ArrayList<>();
        BigDecimal fractionSum = BigDecimal.ZERO;
        Pressure partialSum = Pressure.of("0", PressureUnit.PASCAL);
        for (GasMixtureComponent component : mixture.components()) {
            BigDecimal y = component.amount().in(AmountOfSubstanceUnit.MOLE).divide(totalMoles, ScientificMath.CALCULATION_CONTEXT);
            Pressure p = mixture.totalPressure().multiply(y);
            fractions.add(new MoleFraction(component.compoundCode(), y.stripTrailingZeros()));
            partials.add(new PartialPressure(component.compoundCode(), p));
            fractionSum = fractionSum.add(y, ScientificMath.CALCULATION_CONTEXT);
            partialSum = partialSum.add(p);
        }
        return new GasMixtureResult(GasCalculationStatus.SUCCESS, List.copyOf(fractions), List.copyOf(partials), partialSum, fractionSum.stripTrailingZeros());
    }

    public GasStateResult calculateTransformation(GasStateTransformation request) {
        validatePositive(request.initialPressure(), request.initialVolume(), null, request.initialTemperature());
        validatePositive(request.finalPressure(), request.finalVolume(), null, request.finalTemperature());
        BigDecimal p1 = request.initialPressure().in(PressureUnit.PASCAL);
        BigDecimal v1 = litersToCubicMeters(request.initialVolume());
        BigDecimal t1 = request.initialTemperature().in(TemperatureUnit.KELVIN);
        BigDecimal p2 = request.finalPressure() == null ? null : request.finalPressure().in(PressureUnit.PASCAL);
        BigDecimal v2 = request.finalVolume() == null ? null : litersToCubicMeters(request.finalVolume());
        BigDecimal t2 = request.finalTemperature() == null ? null : request.finalTemperature().in(TemperatureUnit.KELVIN);

        if (request.constraint() == GasTransformationConstraint.CONSTANT_TEMPERATURE && t2 != null && t1.compareTo(t2) != 0) {
            throw new GasLawException(GasLawErrorCode.INCONSISTENT_PROCESS_CONSTRAINT, "Constant-temperature transformation requires equal temperatures");
        }
        if (request.constraint() == GasTransformationConstraint.CONSTANT_PRESSURE && p2 != null && p1.compareTo(p2) != 0) {
            throw new GasLawException(GasLawErrorCode.INCONSISTENT_PROCESS_CONSTRAINT, "Constant-pressure transformation requires equal pressures");
        }
        if (request.constraint() == GasTransformationConstraint.CONSTANT_VOLUME && v2 != null && v1.compareTo(v2) != 0) {
            throw new GasLawException(GasLawErrorCode.INCONSISTENT_PROCESS_CONSTRAINT, "Constant-volume transformation requires equal volumes");
        }

        if (p2 == null) {
            p2 = p1.multiply(v1, ScientificMath.CALCULATION_CONTEXT).multiply(t2, ScientificMath.CALCULATION_CONTEXT)
                    .divide(t1.multiply(v2, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
        } else if (v2 == null) {
            v2 = p1.multiply(v1, ScientificMath.CALCULATION_CONTEXT).multiply(t2, ScientificMath.CALCULATION_CONTEXT)
                    .divide(t1.multiply(p2, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
        } else if (t2 == null) {
            t2 = p2.multiply(v2, ScientificMath.CALCULATION_CONTEXT).multiply(t1, ScientificMath.CALCULATION_CONTEXT)
                    .divide(p1.multiply(v1, ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
        } else {
            throw new GasLawException(GasLawErrorCode.INVALID_UNKNOWN_COUNT, "Exactly one final variable must be unknown");
        }

        GasState state = new GasState(
                Pressure.of(p2, PressureUnit.PASCAL),
                Volume.of(v2.multiply(THOUSAND, ScientificMath.CALCULATION_CONTEXT), VolumeUnit.LITER),
                AmountOfSubstance.of(BigDecimal.ONE, AmountOfSubstanceUnit.MOLE),
                Temperature.of(t2, TemperatureUnit.KELVIN),
                CompressibilityFactor.ideal());
        return new GasStateResult(state, GasEquationModel.IDEAL_GAS, GasCalculationMethod.COMBINED_GAS_LAW, GasCalculationStatus.SUCCESS, BigDecimal.ZERO);
    }

    public Density calculateDensity(Pressure pressure, BigDecimal molarMassKgPerMol, Temperature temperature, CompressibilityFactor z) {
        validateStrictPositive(molarMassKgPerMol, GasLawErrorCode.INVALID_AMOUNT, "Molar mass must be positive");
        CompressibilityFactor actualZ = z == null ? CompressibilityFactor.ideal() : z;
        validatePositive(pressure, null, null, temperature);
        BigDecimal density = pressure.in(PressureUnit.PASCAL).multiply(molarMassKgPerMol, ScientificMath.CALCULATION_CONTEXT)
                .divide(actualZ.value().multiply(ScientificConstants.IDEAL_GAS_CONSTANT_SI, ScientificMath.CALCULATION_CONTEXT)
                        .multiply(temperature.in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT), ScientificMath.CALCULATION_CONTEXT);
        return Density.of(density, DensityUnit.KILOGRAM_PER_CUBIC_METER);
    }

    public BigDecimal calculateMolarMass(Density density, Pressure pressure, Temperature temperature, CompressibilityFactor z) {
        CompressibilityFactor actualZ = z == null ? CompressibilityFactor.ideal() : z;
        validatePositive(pressure, null, null, temperature);
        return density.in(DensityUnit.KILOGRAM_PER_CUBIC_METER).multiply(actualZ.value(), ScientificMath.CALCULATION_CONTEXT)
                .multiply(ScientificConstants.IDEAL_GAS_CONSTANT_SI, ScientificMath.CALCULATION_CONTEXT)
                .multiply(temperature.in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT)
                .divide(pressure.in(PressureUnit.PASCAL), ScientificMath.CALCULATION_CONTEXT)
                .stripTrailingZeros();
    }

    private CompressibilityFactor zFor(GasEquationModel model, CompressibilityFactor z) {
        if (model == GasEquationModel.IDEAL_GAS) {
            return CompressibilityFactor.ideal();
        }
        if (z == null) {
            throw new GasLawException(GasLawErrorCode.MISSING_COMPRESSIBILITY_FACTOR, "Explicit-Z model requires a positive compressibility factor");
        }
        return z;
    }

    private int countKnown(Object... values) {
        int count = 0;
        for (Object value : values) {
            if (value != null) {
                count++;
            }
        }
        return count;
    }

    private void validatePositive(Pressure pressure, Volume volume, AmountOfSubstance amount, Temperature temperature) {
        if (pressure != null) {
            validateStrictPositive(pressure.in(PressureUnit.PASCAL), GasLawErrorCode.INVALID_PRESSURE, "Pressure must be positive");
        }
        if (volume != null) {
            validateStrictPositive(volume.in(VolumeUnit.LITER), GasLawErrorCode.INVALID_VOLUME, "Volume must be positive");
        }
        if (amount != null) {
            validateStrictPositive(amount.in(AmountOfSubstanceUnit.MOLE), GasLawErrorCode.INVALID_AMOUNT, "Amount must be positive");
        }
        if (temperature != null) {
            validateStrictPositive(temperature.in(TemperatureUnit.KELVIN), GasLawErrorCode.INVALID_TEMPERATURE, "Temperature must be positive");
        }
    }

    private void validateStrictPositive(BigDecimal value, GasLawErrorCode errorCode, String message) {
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new GasLawException(errorCode, message);
        }
    }

    private BigDecimal litersToCubicMeters(Volume volume) {
        return volume.in(VolumeUnit.LITER).divide(THOUSAND, ScientificMath.CALCULATION_CONTEXT);
    }

    private BigDecimal residual(Pressure pressure, Volume volume, AmountOfSubstance amount, Temperature temperature, CompressibilityFactor z) {
        BigDecimal left = pressure.in(PressureUnit.PASCAL).multiply(litersToCubicMeters(volume), ScientificMath.CALCULATION_CONTEXT);
        BigDecimal right = z.value().multiply(amount.in(AmountOfSubstanceUnit.MOLE), ScientificMath.CALCULATION_CONTEXT)
                .multiply(ScientificConstants.IDEAL_GAS_CONSTANT_SI, ScientificMath.CALCULATION_CONTEXT)
                .multiply(temperature.in(TemperatureUnit.KELVIN), ScientificMath.CALCULATION_CONTEXT);
        return left.subtract(right, ScientificMath.CALCULATION_CONTEXT);
    }
}

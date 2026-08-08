package com.ailab.chemistry.domain.physicalproperty;

import com.ailab.chemistry.domain.compound.CompoundId;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.Length;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;

import java.math.BigDecimal;
import java.util.Objects;

public final class PropertyReferenceConditions {
    private final Temperature temperature;
    private final Pressure pressure;
    private final MatterState matterState;
    private final CompoundId solventId;
    private final String concentration;
    private final Length wavelength;
    private final String note;

    public PropertyReferenceConditions(Temperature temperature, Pressure pressure, MatterState matterState, CompoundId solventId, String concentration, Length wavelength, String note) {
        this.temperature = temperature != null ? temperature : Temperature.of(new BigDecimal("298.15"), TemperatureUnit.KELVIN);
        this.pressure = pressure != null ? pressure : Pressure.of(new BigDecimal("1.0"), PressureUnit.ATMOSPHERE);
        this.matterState = matterState != null ? matterState : MatterState.UNKNOWN;
        this.solventId = solventId;
        this.concentration = concentration;
        this.wavelength = wavelength;
        this.note = note;
    }

    public static PropertyReferenceConditions stp(MatterState state) {
        return new PropertyReferenceConditions(
                Temperature.of(new BigDecimal("298.15"), TemperatureUnit.KELVIN),
                Pressure.of(new BigDecimal("1.0"), PressureUnit.ATMOSPHERE),
                state, null, null, null, "Standard reference conditions (25 °C, 1 atm)"
        );
    }

    public Temperature getTemperature() { return temperature; }
    public Pressure getPressure() { return pressure; }
    public MatterState getMatterState() { return matterState; }
    public CompoundId getSolventId() { return solventId; }
    public String getConcentration() { return concentration; }
    public Length getWavelength() { return wavelength; }
    public String getNote() { return note; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyReferenceConditions that = (PropertyReferenceConditions) o;
        return Objects.equals(temperature, that.temperature) && Objects.equals(pressure, that.pressure) && matterState == that.matterState && Objects.equals(solventId, that.solventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(temperature, pressure, matterState, solventId);
    }
}

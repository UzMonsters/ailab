package com.ailab.chemistry.physicalproperty;

import com.ailab.chemistry.compound.TestElementMassProvider;
import com.ailab.chemistry.domain.measurement.*;
import com.ailab.chemistry.domain.physicalproperty.*;
import com.ailab.chemistry.infrastructure.persistence.physicalproperty.InMemoryCompoundPhysicalPropertyProfileRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PhysicalPropertyDomainTests {

    private final TestElementMassProvider massProvider = new TestElementMassProvider();
    private final CompoundPhysicalPropertyProfileRepository repository = new InMemoryCompoundPhysicalPropertyProfileRepository(massProvider);

    @Test
    void testMeasurementExtensionsConversionsAndValidation() {
        MolarHeatCapacity mhc = MolarHeatCapacity.of(75.38, MolarHeatCapacityUnit.JOULE_PER_MOLE_KELVIN);
        assertThat(mhc.getValue()).isEqualTo(new BigDecimal("75.38"));

        assertThatThrownBy(() -> MolarHeatCapacity.of(-10.0, MolarHeatCapacityUnit.JOULE_PER_MOLE_KELVIN))
                .isInstanceOf(IllegalArgumentException.class);

        DynamicViscosity viscosity = DynamicViscosity.of(1.074, DynamicViscosityUnit.MILLIPASCAL_SECOND);
        assertThat(viscosity.toCanonical().getValue()).isEqualTo(new BigDecimal("0.001074"));

        PhValue phVal = PhValue.of("7.0");
        assertThat(phVal.getValue()).isEqualTo(new BigDecimal("7"));

        PhRange phRange = new PhRange(PhValue.of("6.0"), PhValue.of("7.5"));
        assertThat(phRange.getLowerBound().getValue()).isEqualTo(new BigDecimal("6"));
        assertThat(phRange.getUpperBound().getValue()).isEqualTo(new BigDecimal("7.5"));
    }

    @Test
    void testPhObservationRequiresSolventAndConcentration() {
        assertThatThrownBy(() -> new PhObservation(PhValue.of("7.0"), null, null, "0.1 M", null, null, null))
                .isInstanceOf(CompoundPhysicalPropertyException.class)
                .hasMessageContaining("requires a solvent compound reference");

        assertThatThrownBy(() -> new PhObservation(PhValue.of("7.0"), null, repository.findAll().get(0).getCompoundId(), "", null, null, null))
                .isInstanceOf(CompoundPhysicalPropertyException.class)
                .hasMessageContaining("requires a concentration condition");
    }

    @Test
    void testWaterRepresentativePropertyProfile() {
        CompoundPhysicalPropertyProfile waterProfile = repository.findByCompoundCode("COMP-H2O").orElseThrow();
        assertThat(waterProfile.getAvailabilityMap().get(PhysicalPropertyType.DENSITY)).isEqualTo(PropertyAvailability.AVAILABLE);
        assertThat(waterProfile.getDensityData().get(0).getDensity().getValueInKgPerM3()).isEqualTo(new BigDecimal("997.047"));

        assertThat(waterProfile.getAvailabilityMap().get(PhysicalPropertyType.PH_OBSERVATION)).isEqualTo(PropertyAvailability.AVAILABLE);
        assertThat(waterProfile.getPhObservations().get(0).getValue().getValue()).isEqualTo(new BigDecimal("7"));
    }

    @Test
    void testIsomerPropertyProfilesEthanolVsDimethylEther() {
        CompoundPhysicalPropertyProfile ethanolProfile = repository.findByCompoundCode("COMP-ETHANOL").orElseThrow();
        CompoundPhysicalPropertyProfile dmeProfile = repository.findByCompoundCode("COMP-DIMETHYL-ETHER").orElseThrow();

        assertThat(ethanolProfile.getStateData().get(0).getState()).isEqualTo(com.ailab.chemistry.domain.element.MatterState.LIQUID);
        assertThat(dmeProfile.getStateData().get(0).getState()).isEqualTo(com.ailab.chemistry.domain.element.MatterState.GAS);

        assertThat(ethanolProfile.getDensityData().get(0).getDensity().getValueInKgPerM3()).isEqualTo(new BigDecimal("789.2"));
        assertThat(dmeProfile.getDensityData().get(0).getDensity().getValueInKgPerM3()).isEqualTo(new BigDecimal("2.11"));
    }
}

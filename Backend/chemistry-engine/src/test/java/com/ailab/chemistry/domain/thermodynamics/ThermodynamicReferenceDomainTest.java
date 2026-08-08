package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.MolarEnergy;
import com.ailab.chemistry.domain.measurement.MolarEnergyUnit;
import com.ailab.chemistry.domain.measurement.MolarEntropy;
import com.ailab.chemistry.domain.measurement.MolarEntropyUnit;
import com.ailab.chemistry.domain.measurement.MolarHeatCapacity;
import com.ailab.chemistry.domain.measurement.MolarHeatCapacityUnit;
import com.ailab.chemistry.domain.measurement.Pressure;
import com.ailab.chemistry.domain.measurement.PressureUnit;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ThermodynamicReferenceDomainTest {

    private static final Temperature T25 = Temperature.of("25.0", TemperatureUnit.CELSIUS);
    private static final Pressure P1BAR = Pressure.of("1.000", PressureUnit.BAR);

    @Test
    void storesPhaseSensitiveFormationValuesAndLeavesMissingDataUnavailable() {
        ThermodynamicReferenceRepository repository = repository(new ThermodynamicProfile(
                "COMP-H2O",
                new ThermodynamicDatasetVersion("thermodynamic-reference-v1.0.0"),
                List.of(enthalpy("COMP-H2O", MatterState.LIQUID), new ThermodynamicPropertyRecord(
                        ThermodynamicPropertyType.STANDARD_ENTHALPY_OF_FORMATION,
                        MolarEnergy.of("-241.826", MolarEnergyUnit.KILOJOULE_PER_MOLE),
                        null,
                        null,
                        conditions(MatterState.GAS),
                        ThermodynamicEvidenceStatus.EVALUATED,
                        provenance()))));

        var liquidWater = repository.findExact("COMP-H2O", ThermodynamicPropertyType.STANDARD_ENTHALPY_OF_FORMATION,
                MatterState.LIQUID, T25, P1BAR).orElseThrow();
        var gasWater = repository.findExact("COMP-H2O", ThermodynamicPropertyType.STANDARD_ENTHALPY_OF_FORMATION,
                MatterState.GAS, T25, P1BAR).orElseThrow();
        var missingMethaneLiquid = repository.findExact("COMP-CH4", ThermodynamicPropertyType.STANDARD_ENTHALPY_OF_FORMATION,
                MatterState.LIQUID, T25, P1BAR);

        assertThat(liquidWater.energyValue().orElseThrow().in(MolarEnergyUnit.KILOJOULE_PER_MOLE))
                .isEqualByComparingTo("-285.830");
        assertThat(gasWater.energyValue().orElseThrow().in(MolarEnergyUnit.KILOJOULE_PER_MOLE))
                .isEqualByComparingTo("-241.826");
        assertThat(missingMethaneLiquid).isEmpty();
    }

    @Test
    void acceptsNegativeZeroAndPositiveFormationValuesButHeatCapacityMustBePositive() {
        var provenance = provenance();
        var conditions = conditions(MatterState.GAS);

        assertThat(new ThermodynamicPropertyRecord(
                ThermodynamicPropertyType.STANDARD_GIBBS_ENERGY_OF_FORMATION,
                MolarEnergy.of("-394.359", MolarEnergyUnit.KILOJOULE_PER_MOLE),
                null,
                null,
                conditions,
                ThermodynamicEvidenceStatus.EVALUATED,
                provenance).energyValue().orElseThrow().in(MolarEnergyUnit.JOULE_PER_MOLE))
                .isEqualByComparingTo("-394359");

        assertThat(new ThermodynamicPropertyRecord(
                ThermodynamicPropertyType.STANDARD_ENTHALPY_OF_FORMATION,
                MolarEnergy.of("0", MolarEnergyUnit.KILOJOULE_PER_MOLE),
                null,
                null,
                conditions,
                ThermodynamicEvidenceStatus.REFERENCE_STATE_DEFINED,
                provenance).energyValue().orElseThrow().in(MolarEnergyUnit.KILOJOULE_PER_MOLE))
                .isEqualByComparingTo("0");

        assertThatThrownBy(() -> new ThermodynamicPropertyRecord(
                ThermodynamicPropertyType.MOLAR_HEAT_CAPACITY,
                null,
                null,
                MolarHeatCapacity.of("0", MolarHeatCapacityUnit.JOULE_PER_MOLE_KELVIN),
                conditions,
                ThermodynamicEvidenceStatus.EVALUATED,
                provenance))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void convertsMolarEnergyAndEntropyUnitsExplicitly() {
        assertThat(MolarEnergy.of("-285.830", MolarEnergyUnit.KILOJOULE_PER_MOLE)
                .in(MolarEnergyUnit.JOULE_PER_MOLE)).isEqualByComparingTo("-285830");
        assertThat(MolarEnergy.of("-285830", MolarEnergyUnit.JOULE_PER_MOLE)
                .in(MolarEnergyUnit.KILOJOULE_PER_MOLE)).isEqualByComparingTo("-285.83");
        assertThat(MolarEntropy.of("0.21379", MolarEntropyUnit.KILOJOULE_PER_MOLE_KELVIN)
                .in(MolarEntropyUnit.JOULE_PER_MOLE_KELVIN)).isEqualByComparingTo("213.79");
    }

    @Test
    void rejectsDuplicatePropertyRecordsForSameTypePhaseAndConditions() {
        var profile = new ThermodynamicProfile(
                "COMP-H2O",
                new ThermodynamicDatasetVersion("thermodynamic-reference-v1.0.0"),
                List.of(enthalpy("COMP-H2O", MatterState.LIQUID), enthalpy("COMP-H2O", MatterState.LIQUID))
        );

        assertThatThrownBy(profile::validateNoDuplicateRecords)
                .isInstanceOf(ThermodynamicException.class)
                .extracting("errorCode")
                .isEqualTo(ThermodynamicErrorCode.DUPLICATE_PROPERTY_RECORD);
    }

    @Test
    void requiresCompleteProvenanceAndConditionsForEveryRecord() {
        assertThatThrownBy(() -> new ThermodynamicPropertyRecord(
                ThermodynamicPropertyType.STANDARD_MOLAR_ENTROPY,
                null,
                MolarEntropy.of("69.91", MolarEntropyUnit.JOULE_PER_MOLE_KELVIN),
                null,
                conditions(MatterState.GAS),
                ThermodynamicEvidenceStatus.EVALUATED,
                new ThermodynamicProvenance("", "NIST Chemistry WebBook", "minimal cited educational subset")))
                .isInstanceOf(ThermodynamicException.class)
                .extracting("errorCode")
                .isEqualTo(ThermodynamicErrorCode.INCOMPLETE_PROVENANCE);

        assertThatThrownBy(() -> new ThermodynamicReferenceConditions(T25, P1BAR, null, StandardStateConvention.PURE_SUBSTANCE_STANDARD_STATE))
                .isInstanceOf(ThermodynamicException.class)
                .extracting("errorCode")
                .isEqualTo(ThermodynamicErrorCode.INVALID_REFERENCE_CONDITIONS);
    }

    private static ThermodynamicPropertyRecord enthalpy(String compoundCode, MatterState state) {
        return new ThermodynamicPropertyRecord(
                ThermodynamicPropertyType.STANDARD_ENTHALPY_OF_FORMATION,
                MolarEnergy.of("-285.830", MolarEnergyUnit.KILOJOULE_PER_MOLE),
                null,
                null,
                conditions(state),
                ThermodynamicEvidenceStatus.EVALUATED,
                provenance());
    }

    private static ThermodynamicReferenceConditions conditions(MatterState state) {
        return new ThermodynamicReferenceConditions(T25, P1BAR, state, StandardStateConvention.PURE_SUBSTANCE_STANDARD_STATE);
    }

    private static ThermodynamicProvenance provenance() {
        return new ThermodynamicProvenance("NIST-WEBBOOK", "NIST Chemistry WebBook thermochemistry table", "minimal cited educational subset");
    }

    private static ThermodynamicReferenceRepository repository(ThermodynamicProfile profile) {
        return new ThermodynamicReferenceRepository() {
            @Override
            public java.util.Optional<ThermodynamicProfile> findProfile(String compoundCode) {
                return profile.compoundCode().equals(compoundCode) ? java.util.Optional.of(profile) : java.util.Optional.empty();
            }

            @Override
            public List<ThermodynamicProfile> findAllProfiles() {
                return List.of(profile);
            }
        };
    }
}

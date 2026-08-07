package com.ailab.chemistry.domain.thermodynamics;

import com.ailab.chemistry.domain.measurement.MolarEnergy;
import com.ailab.chemistry.domain.measurement.MolarEntropy;
import com.ailab.chemistry.domain.measurement.MolarHeatCapacity;

import java.util.Objects;
import java.util.Optional;

public final class ThermodynamicPropertyRecord {
    private final ThermodynamicPropertyType type;
    private final MolarEnergy energyValue;
    private final MolarEntropy entropyValue;
    private final MolarHeatCapacity heatCapacityValue;
    private final ThermodynamicReferenceConditions conditions;
    private final ThermodynamicEvidenceStatus evidenceStatus;
    private final ThermodynamicProvenance provenance;

    public ThermodynamicPropertyRecord(ThermodynamicPropertyType type,
                                       MolarEnergy energyValue,
                                       MolarEntropy entropyValue,
                                       MolarHeatCapacity heatCapacityValue,
                                       ThermodynamicReferenceConditions conditions,
                                       ThermodynamicEvidenceStatus evidenceStatus,
                                       ThermodynamicProvenance provenance) {
        if (type == null || conditions == null || evidenceStatus == null || provenance == null) {
            throw new ThermodynamicException(ThermodynamicErrorCode.INVALID_PROPERTY_RECORD,
                    "Thermodynamic property records require type, conditions, evidence and provenance");
        }
        int valueCount = (energyValue == null ? 0 : 1) + (entropyValue == null ? 0 : 1) + (heatCapacityValue == null ? 0 : 1);
        if (valueCount != 1) {
            throw new ThermodynamicException(ThermodynamicErrorCode.INVALID_PROPERTY_RECORD,
                    "Exactly one thermodynamic value must be present");
        }
        if ((type == ThermodynamicPropertyType.STANDARD_ENTHALPY_OF_FORMATION || type == ThermodynamicPropertyType.STANDARD_GIBBS_ENERGY_OF_FORMATION)
                && energyValue == null) {
            throw new ThermodynamicException(ThermodynamicErrorCode.INVALID_PROPERTY_RECORD, "Formation properties require molar energy");
        }
        if (type == ThermodynamicPropertyType.STANDARD_MOLAR_ENTROPY && entropyValue == null) {
            throw new ThermodynamicException(ThermodynamicErrorCode.INVALID_PROPERTY_RECORD, "Entropy records require molar entropy");
        }
        if (type == ThermodynamicPropertyType.MOLAR_HEAT_CAPACITY && heatCapacityValue == null) {
            throw new ThermodynamicException(ThermodynamicErrorCode.INVALID_PROPERTY_RECORD, "Heat-capacity records require molar heat capacity");
        }
        this.type = type;
        this.energyValue = energyValue;
        this.entropyValue = entropyValue;
        this.heatCapacityValue = heatCapacityValue;
        this.conditions = conditions;
        this.evidenceStatus = evidenceStatus;
        this.provenance = provenance;
    }

    public ThermodynamicPropertyType type() {
        return type;
    }

    public Optional<MolarEnergy> energyValue() {
        return Optional.ofNullable(energyValue);
    }

    public Optional<MolarEntropy> entropyValue() {
        return Optional.ofNullable(entropyValue);
    }

    public Optional<MolarHeatCapacity> heatCapacityValue() {
        return Optional.ofNullable(heatCapacityValue);
    }

    public ThermodynamicReferenceConditions conditions() {
        return conditions;
    }

    public ThermodynamicEvidenceStatus evidenceStatus() {
        return evidenceStatus;
    }

    public ThermodynamicProvenance provenance() {
        return provenance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThermodynamicPropertyRecord that = (ThermodynamicPropertyRecord) o;
        return type == that.type && Objects.equals(energyValue, that.energyValue)
                && Objects.equals(entropyValue, that.entropyValue)
                && Objects.equals(heatCapacityValue, that.heatCapacityValue)
                && Objects.equals(conditions, that.conditions)
                && evidenceStatus == that.evidenceStatus
                && Objects.equals(provenance, that.provenance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, energyValue, entropyValue, heatCapacityValue, conditions, evidenceStatus, provenance);
    }
}

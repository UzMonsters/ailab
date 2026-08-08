package com.ailab.chemistry.domain.thermodynamics;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record ThermodynamicProfile(
        String compoundCode,
        ThermodynamicDatasetVersion datasetVersion,
        List<ThermodynamicPropertyRecord> records) {

    public ThermodynamicProfile {
        if (compoundCode == null || compoundCode.isBlank()) {
            throw new IllegalArgumentException("Compound code is required");
        }
        if (datasetVersion == null) {
            throw new IllegalArgumentException("Dataset version is required");
        }
        records = List.copyOf(records == null ? List.of() : records);
    }

    public List<ThermodynamicPropertyRecord> properties() {
        return records;
    }

    public void validateNoDuplicateRecords() {
        validateNoDuplicateRecords(records);
    }

    private static void validateNoDuplicateRecords(List<ThermodynamicPropertyRecord> records) {
        Set<String> seen = new HashSet<>();
        for (ThermodynamicPropertyRecord record : records) {
            String key = record.type() + "|" + record.conditions().state() + "|" + record.conditions().temperature()
                    + "|" + record.conditions().pressure() + "|" + record.conditions().standardStateConvention();
            if (!seen.add(key)) {
                throw new ThermodynamicException(ThermodynamicErrorCode.DUPLICATE_PROPERTY_RECORD,
                        "Duplicate thermodynamic property record for " + key);
            }
        }
    }
}

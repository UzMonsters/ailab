package com.ailab.chemistry.api;

import java.util.Map;
import java.util.UUID;

public class CompoundPhysicalPropertyDetails {
    private final UUID compoundId;
    private final String compoundCode;
    private final String primaryName;
    private final String datasetVersion;
    private final Map<String, String> availability;

    public CompoundPhysicalPropertyDetails(UUID compoundId, String compoundCode, String primaryName, String datasetVersion, Map<String, String> availability) {
        this.compoundId = compoundId;
        this.compoundCode = compoundCode;
        this.primaryName = primaryName;
        this.datasetVersion = datasetVersion;
        this.availability = Map.copyOf(availability);
    }

    public UUID getCompoundId() { return compoundId; }
    public String getCompoundCode() { return compoundCode; }
    public String getPrimaryName() { return primaryName; }
    public String getDatasetVersion() { return datasetVersion; }
    public Map<String, String> getAvailability() { return availability; }
}

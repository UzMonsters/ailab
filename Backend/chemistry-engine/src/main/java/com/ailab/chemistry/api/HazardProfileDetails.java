package com.ailab.chemistry.api;

import com.ailab.chemistry.domain.hazard.HazardSummaryFlag;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class HazardProfileDetails {
    private final UUID compoundId;
    private final String compoundCode;
    private final String primaryName;
    private final String datasetVersion;
    private final Map<String, String> availability;
    private final Set<HazardSummaryFlag> summaryFlags;

    public HazardProfileDetails(UUID compoundId, String compoundCode, String primaryName, String datasetVersion, Map<String, String> availability, Set<HazardSummaryFlag> summaryFlags) {
        this.compoundId = compoundId;
        this.compoundCode = compoundCode;
        this.primaryName = primaryName;
        this.datasetVersion = datasetVersion;
        this.availability = Map.copyOf(availability);
        this.summaryFlags = Set.copyOf(summaryFlags);
    }

    public UUID getCompoundId() { return compoundId; }
    public String getCompoundCode() { return compoundCode; }
    public String getPrimaryName() { return primaryName; }
    public String getDatasetVersion() { return datasetVersion; }
    public Map<String, String> getAvailability() { return availability; }
    public Set<HazardSummaryFlag> getSummaryFlags() { return summaryFlags; }
}

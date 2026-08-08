package com.ailab.chemistry.api;

import java.util.List;

public record ThermodynamicProfileDetails(
        String compoundCode,
        String datasetVersion,
        List<ThermodynamicPropertyDetails> properties) {
}

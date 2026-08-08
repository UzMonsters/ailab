package com.ailab.chemistry.domain.kinetics;

import java.util.Objects;

public record KineticProvenance(
        String sourceId,
        String description,
        String citation,
        String nistSquib,
        String paperTitle,
        String authors,
        String journalName,
        Integer publicationYear,
        String pages,
        String recordUrl,
        String dataType,
        String experimentalMethod,
        String uncertainty,
        String originalAValue,
        String originalAUnit,
        String originalKValue,
        String originalKUnit,
        String conversionFactor) {

    public KineticProvenance {
        Objects.requireNonNull(sourceId, "sourceId must not be null");
        description = description == null ? "" : description;
        citation = citation == null ? "" : citation;
    }

    public KineticProvenance(String sourceId, String description, String citation) {
        this(sourceId, description, citation, null, null, null, null, null, null, null, "EXPERIMENTAL", "EXPERIMENTAL", null, null, null, null, null, "6.02214076E20");
    }
}

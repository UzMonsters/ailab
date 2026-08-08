package com.ailab.chemistry.domain.element.property;

import java.util.Objects;

public final class PropertyDatasetVersion {
    private final String versionId;
    private final String description;
    private final String publicationDate;

    public PropertyDatasetVersion(String versionId, String description, String publicationDate) {
        this.versionId = Objects.requireNonNull(versionId, "Version ID must not be null");
        this.description = Objects.requireNonNull(description, "Description must not be null");
        this.publicationDate = Objects.requireNonNull(publicationDate, "Publication date must not be null");
    }

    public static final PropertyDatasetVersion V1_0_0 = new PropertyDatasetVersion(
            "extended-properties-v1.0.0",
            "IUPAC / CRC / NIST Extended Element Properties Dataset",
            "2026-08-04"
    );

    public String getVersionId() { return versionId; }
    public String getDescription() { return description; }
    public String getPublicationDate() { return publicationDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyDatasetVersion that = (PropertyDatasetVersion) o;
        return Objects.equals(versionId, that.versionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(versionId);
    }
}

package com.ailab.chemistry.domain.element.property;

import java.util.Objects;

public final class PropertyProvenance {
    private final String sourceIdentifier;
    private final String sourceTitle;
    private final String publisher;
    private final String publicationYearOrVersion;
    private final String accessDate;
    private final String fieldsSupplied;
    private final String licenseNotes;

    public PropertyProvenance(
            String sourceIdentifier,
            String sourceTitle,
            String publisher,
            String publicationYearOrVersion,
            String accessDate,
            String fieldsSupplied,
            String licenseNotes) {
        this.sourceIdentifier = Objects.requireNonNull(sourceIdentifier, "Source identifier must not be null");
        this.sourceTitle = Objects.requireNonNull(sourceTitle, "Source title must not be null");
        this.publisher = Objects.requireNonNull(publisher, "Publisher must not be null");
        this.publicationYearOrVersion = Objects.requireNonNull(publicationYearOrVersion, "Publication year/version must not be null");
        this.accessDate = Objects.requireNonNull(accessDate, "Access date must not be null");
        this.fieldsSupplied = Objects.requireNonNull(fieldsSupplied, "Fields supplied must not be null");
        this.licenseNotes = licenseNotes != null ? licenseNotes : "Standard scientific reference use";
    }

    public static PropertyProvenance defaultProvenance(String sourceIdentifier, String sourceTitle) {
        return new PropertyProvenance(
                sourceIdentifier,
                sourceTitle,
                "IUPAC / NIST / CRC Handbook",
                "2024",
                "2026-08-04",
                "All properties",
                "Open reference data"
        );
    }

    public String getSourceIdentifier() { return sourceIdentifier; }
    public String getSourceTitle() { return sourceTitle; }
    public String getPublisher() { return publisher; }
    public String getPublicationYearOrVersion() { return publicationYearOrVersion; }
    public String getAccessDate() { return accessDate; }
    public String getFieldsSupplied() { return fieldsSupplied; }
    public String getLicenseNotes() { return licenseNotes; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyProvenance that = (PropertyProvenance) o;
        return Objects.equals(sourceIdentifier, that.sourceIdentifier) &&
                Objects.equals(sourceTitle, that.sourceTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceIdentifier, sourceTitle);
    }
}

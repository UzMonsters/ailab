package com.ailab.chemistry.domain.classification;

import java.util.Objects;

public final class ClassificationTaxonomyVersion {
    private final String version;
    private final String name;
    private final String publicationDate;

    public ClassificationTaxonomyVersion(String version, String name, String publicationDate) {
        if (version == null || version.isBlank()) {
            throw new ClassificationException(ClassificationErrorCode.CLASSIFICATION_TAXONOMY_NOT_FOUND, "Taxonomy version cannot be blank");
        }
        if (name == null || name.isBlank()) {
            throw new ClassificationException(ClassificationErrorCode.CLASSIFICATION_TAXONOMY_NOT_FOUND, "Taxonomy name cannot be blank");
        }
        this.version = version.trim();
        this.name = name.trim();
        this.publicationDate = publicationDate != null ? publicationDate.trim() : "2026-08-05";
    }

    public String getVersion() { return version; }
    public String getName() { return name; }
    public String getPublicationDate() { return publicationDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassificationTaxonomyVersion that = (ClassificationTaxonomyVersion) o;
        return Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version);
    }

    @Override
    public String toString() {
        return version;
    }
}

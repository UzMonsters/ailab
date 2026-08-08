package com.ailab.chemistry.domain.compound;

import java.util.Objects;

public final class CompoundCatalogVersion {
    private final String version;
    private final String name;
    private final String publicationDate;

    public CompoundCatalogVersion(String version, String name, String publicationDate) {
        if (version == null || version.isBlank()) {
            throw new CompoundException(CompoundErrorCode.COMPOUND_CATALOG_VERSION_NOT_FOUND, "Catalog version cannot be blank");
        }
        this.version = version.trim();
        this.name = name != null ? name.trim() : version;
        this.publicationDate = publicationDate != null ? publicationDate.trim() : "";
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompoundCatalogVersion that = (CompoundCatalogVersion) o;
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

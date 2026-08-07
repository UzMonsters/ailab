package com.ailab.chemistry.domain.reaction;

import java.time.LocalDate;
import java.util.Objects;

public final class ReactionCatalogVersion {
    private final String versionCode;
    private final String description;
    private final LocalDate publicationDate;

    public ReactionCatalogVersion(String versionCode, String description, LocalDate publicationDate) {
        Objects.requireNonNull(versionCode, "Version code must not be null");
        if (versionCode.isBlank()) {
            throw new ReactionException(ReactionErrorCode.REACTION_CATALOG_VERSION_NOT_FOUND, "Version code must not be blank");
        }
        this.versionCode = versionCode.trim();
        this.description = description != null ? description.trim() : "";
        this.publicationDate = publicationDate != null ? publicationDate : LocalDate.now();
    }

    public String getVersionCode() {
        return versionCode;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionCatalogVersion that = (ReactionCatalogVersion) o;
        return Objects.equals(versionCode, that.versionCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(versionCode);
    }
}

package com.ailab.chemistry.domain.hazard;

import java.util.Objects;

public final class HazardDatasetVersion {
    private final String id;
    private final String name;
    private final String publicationDate;

    public HazardDatasetVersion(String id, String name, String publicationDate) {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(publicationDate, "Publication date cannot be null");
        this.id = id;
        this.name = name;
        this.publicationDate = publicationDate;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPublicationDate() { return publicationDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HazardDatasetVersion that = (HazardDatasetVersion) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

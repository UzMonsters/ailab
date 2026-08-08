package com.ailab.chemistry.domain.acidbase;

import java.util.Objects;

public final class AcidBaseDatasetVersion {

    public static final AcidBaseDatasetVersion V1_0_0 = new AcidBaseDatasetVersion("1.0.0");

    private final String version;

    public AcidBaseDatasetVersion(String version) {
        if (version == null || version.isBlank()) {
            throw new AcidBaseException(AcidBaseErrorCode.DATASET_ERROR, "Dataset version must not be null or blank");
        }
        this.version = version.trim();
    }

    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcidBaseDatasetVersion that = (AcidBaseDatasetVersion) o;
        return version.equalsIgnoreCase(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version.toUpperCase());
    }

    @Override
    public String toString() {
        return version;
    }
}

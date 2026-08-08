package com.ailab.chemistry.domain.kinetics;

public enum KineticDatasetVersion {
    VERSION_1_0_0("kinetic-reference-v1.0.0");

    private final String value;

    KineticDatasetVersion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

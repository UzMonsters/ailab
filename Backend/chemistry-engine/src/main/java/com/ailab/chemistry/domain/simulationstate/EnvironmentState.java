package com.ailab.chemistry.domain.simulationstate;

public record EnvironmentState(String ventilationMode, boolean fumeHoodOperating) {
    public static EnvironmentState unknown() {
        return new EnvironmentState("", false);
    }
}

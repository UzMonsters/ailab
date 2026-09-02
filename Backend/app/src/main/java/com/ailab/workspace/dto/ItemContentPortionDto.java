package com.ailab.workspace.dto;

public record ItemContentPortionDto(
        String materialCode,
        String name,
        Double amountMl,
        Double massG,
        Double concentrationMolar,
        String phase,
        String mixtureState
) {
    public static ItemContentPortionDto of(String code, String name, Double amountMl, Double massG, Double molarity, String phase, String mixtureState) {
        return new ItemContentPortionDto(code, name, amountMl, massG, molarity, phase != null ? phase.toUpperCase() : "LIQUID", mixtureState != null ? mixtureState.toUpperCase() : "HOMOGENEOUS");
    }
}

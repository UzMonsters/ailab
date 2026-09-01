package com.ailab.workspace.dto;

public record ItemAppearanceDto(
        String color,
        Double opacity,
        Boolean bubbles,
        String gas,
        String precipitate,
        String note
) {
    public static ItemAppearanceDto defaultLiquid(String color) {
        return new ItemAppearanceDto(color != null ? color : "#3B82F6", 0.75, false, null, null, null);
    }

    public static ItemAppearanceDto transparent() {
        return new ItemAppearanceDto("transparent", 0.0, false, null, null, null);
    }
}

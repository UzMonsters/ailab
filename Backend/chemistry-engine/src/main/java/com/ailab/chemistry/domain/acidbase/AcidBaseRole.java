package com.ailab.chemistry.domain.acidbase;

public enum AcidBaseRole {
    ACID,
    BASE,
    AMPHIPROTIC,
    NEUTRAL,
    NEUTRAL_SPECIES;

    public static AcidBaseRole fromString(String str) {
        if (str == null) return NEUTRAL;
        String upper = str.trim().toUpperCase();
        if ("NEUTRAL_SPECIES".equals(upper) || "NEUTRAL".equals(upper)) {
            return NEUTRAL;
        }
        return AcidBaseRole.valueOf(upper);
    }
}

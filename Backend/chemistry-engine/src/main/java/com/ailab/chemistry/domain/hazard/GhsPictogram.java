package com.ailab.chemistry.domain.hazard;

public enum GhsPictogram {
    GHS01("GHS01", "Exploding Bomb"),
    GHS02("GHS02", "Flame"),
    GHS03("GHS03", "Flame Over Circle"),
    GHS04("GHS04", "Gas Cylinder"),
    GHS05("GHS05", "Corrosion"),
    GHS06("GHS06", "Skull and Crossbones"),
    GHS07("GHS07", "Exclamation Mark"),
    GHS08("GHS08", "Health Hazard"),
    GHS09("GHS09", "Environment");

    private final String code;
    private final String name;

    GhsPictogram(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public String getName() { return name; }

    public static GhsPictogram fromCode(String code) {
        for (GhsPictogram p : values()) {
            if (p.code.equalsIgnoreCase(code)) return p;
        }
        throw new IllegalArgumentException("Unknown GHS Pictogram code: " + code);
    }
}

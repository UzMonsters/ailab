package com.ailab.workspace.dto;

import java.util.Map;

public record WorkspacePortDto(
        String id,
        String name,
        String type, // FLUID, THERMAL, SENSOR, GAS, POWER
        String direction, // INPUT, OUTPUT, BIDIRECTIONAL
        String connector, // temperature-probe, ph-electrode, luer-lock, standard-taper, tubing-barb, thermal-pad
        Map<String, Double> anchor2d,
        String anchor3d,
        Double capacity,
        Boolean open,
        Boolean busy
) {
}

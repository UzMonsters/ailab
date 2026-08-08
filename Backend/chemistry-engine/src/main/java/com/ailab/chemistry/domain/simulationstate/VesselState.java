package com.ailab.chemistry.domain.simulationstate;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record VesselState(
        String vesselId,
        String containerProfileId,
        BigDecimal workingVolume,
        Map<String, MaterialPortion> contents,
        String lastMixingNote
) {
    public VesselState {
        if (vesselId == null || vesselId.isBlank()) {
            throw new IllegalArgumentException("Vessel id is required");
        }
        workingVolume = workingVolume == null ? BigDecimal.ZERO : workingVolume;
        contents = Map.copyOf(contents == null ? Map.of() : contents);
        lastMixingNote = lastMixingNote == null ? "" : lastMixingNote;
    }

    public static VesselState empty(String vesselId, String containerProfileId, BigDecimal workingVolume) {
        return new VesselState(vesselId, containerProfileId, workingVolume, Map.of(), "");
    }

    public BigDecimal quantity(String compoundCode, String unit) {
        return contents.values().stream()
                .filter(portion -> portion.compoundCode().equals(compoundCode) && portion.unit().equals(unit))
                .map(MaterialPortion::quantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal quantity(String compoundCode, String unit, String physicalState) {
        return contents.values().stream()
                .filter(portion -> portion.compoundCode().equals(compoundCode) && portion.unit().equals(unit) && portion.physicalState().equals(physicalState))
                .map(MaterialPortion::quantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public VesselState add(MaterialPortion portion) {
        Map<String, MaterialPortion> next = new LinkedHashMap<>(contents);
        MaterialPortion existing = next.get(portion.key());
        BigDecimal quantity = existing == null ? portion.quantity() : existing.quantity().add(portion.quantity());
        next.put(portion.key(), new MaterialPortion(portion.compoundCode(), quantity, portion.unit(),
                portion.physicalState(), portion.sourceEventId()));
        assertWithinWorkingVolume(next.values().stream().toList());
        return new VesselState(vesselId, containerProfileId, workingVolume, next, lastMixingNote);
    }

    public VesselState subtract(String compoundCode, BigDecimal quantity, String unit, String physicalState) {
        String key = compoundCode + "|" + unit + "|" + physicalState;
        MaterialPortion existing = contents.get(key);
        if (existing == null || existing.quantity().compareTo(quantity) < 0) {
            throw new SimulationStateException(SimulationStateErrorCode.MATERIAL_TRANSFER_EXCEEDS_AVAILABLE,
                    "Material transfer amount cannot exceed source contents");
        }
        Map<String, MaterialPortion> next = new LinkedHashMap<>(contents);
        BigDecimal remaining = existing.quantity().subtract(quantity);
        if (remaining.compareTo(BigDecimal.ZERO) == 0) {
            next.remove(key);
        } else {
            next.put(key, new MaterialPortion(compoundCode, remaining, unit, physicalState, existing.sourceEventId()));
        }
        return new VesselState(vesselId, containerProfileId, workingVolume, next, lastMixingNote);
    }

    public VesselState withMixingNote(String note) {
        return new VesselState(vesselId, containerProfileId, workingVolume, contents, note);
    }

    private void assertWithinWorkingVolume(List<MaterialPortion> portions) {
        if (workingVolume.compareTo(BigDecimal.ZERO) == 0) {
            return;
        }
        BigDecimal mlTotal = portions.stream()
                .filter(portion -> "mL".equals(portion.unit()))
                .map(MaterialPortion::quantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (mlTotal.compareTo(workingVolume) > 0) {
            throw new SimulationStateException(SimulationStateErrorCode.VESSEL_OVERFILL,
                    "Vessel contents exceed working volume");
        }
    }
}

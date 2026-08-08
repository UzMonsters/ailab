package com.ailab.chemistry.domain.solution;

import com.ailab.chemistry.domain.measurement.MolarConcentration;
import com.ailab.chemistry.domain.measurement.Volume;

import java.util.Objects;
import java.util.Optional;

public final class DilutionRequest {

    private final String soluteCompoundCode;
    private final MolarConcentration initialConcentration;
    private final Volume initialVolume;
    private final MolarConcentration targetConcentration;
    private final Volume targetVolume;
    private final SolutionVolumeAssumption volumeAssumption;

    public DilutionRequest(
            String soluteCompoundCode,
            MolarConcentration initialConcentration,
            Volume initialVolume,
            MolarConcentration targetConcentration,
            Volume targetVolume,
            SolutionVolumeAssumption volumeAssumption) {
        if (soluteCompoundCode == null || soluteCompoundCode.isBlank()) {
            throw new SolutionException(SolutionErrorCode.COMPOUND_NOT_FOUND, "Solute compound code must not be null or blank");
        }
        this.soluteCompoundCode = soluteCompoundCode.trim();
        this.initialConcentration = initialConcentration;
        this.initialVolume = initialVolume;
        this.targetConcentration = targetConcentration;
        this.targetVolume = targetVolume;
        this.volumeAssumption = volumeAssumption != null ? volumeAssumption : SolutionVolumeAssumption.ADDITIVE_VOLUMES;
    }

    public static DilutionRequest fromInitialToTargetConcentration(String soluteCompoundCode, MolarConcentration c1, Volume v1, MolarConcentration c2) {
        return new DilutionRequest(soluteCompoundCode, c1, v1, c2, null, SolutionVolumeAssumption.ADDITIVE_VOLUMES);
    }

    public static DilutionRequest fromTargetVolume(String soluteCompoundCode, MolarConcentration c1, MolarConcentration c2, Volume v2) {
        return new DilutionRequest(soluteCompoundCode, c1, null, c2, v2, SolutionVolumeAssumption.ADDITIVE_VOLUMES);
    }

    public String getSoluteCompoundCode() {
        return soluteCompoundCode;
    }

    public Optional<MolarConcentration> getInitialConcentration() {
        return Optional.ofNullable(initialConcentration);
    }

    public Optional<Volume> getInitialVolume() {
        return Optional.ofNullable(initialVolume);
    }

    public Optional<MolarConcentration> getTargetConcentration() {
        return Optional.ofNullable(targetConcentration);
    }

    public Optional<Volume> getTargetVolume() {
        return Optional.ofNullable(targetVolume);
    }

    public SolutionVolumeAssumption getVolumeAssumption() {
        return volumeAssumption;
    }
}

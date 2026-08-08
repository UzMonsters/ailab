package com.ailab.chemistry.domain.physicalproperty;

import com.ailab.chemistry.domain.compound.CompoundId;

import java.util.*;

public final class CompoundPhysicalPropertyProfile {

    private final CompoundId compoundId;
    private final String datasetVersion;
    private final Map<PhysicalPropertyType, PropertyAvailability> availabilityMap;
    private final List<CompoundStateDatum> stateData;
    private final List<CompoundDensityDatum> densityData;
    private final List<CompoundPhaseTransitionDatum> phaseTransitions;
    private final List<CompoundSolubilityDatum> solubilityData;
    private final List<HeatCapacityDatum> heatCapacityData;
    private final List<ThermalConductivityDatum> thermalConductivityData;
    private final List<ElectricalConductivityDatum> electricalConductivityData;
    private final List<ViscosityDatum> viscosityData;
    private final List<RefractiveIndexDatum> refractiveIndexData;
    private final List<SurfaceTensionDatum> surfaceTensionData;
    private final List<VaporPressureDatum> vaporPressureData;
    private final List<CompoundAppearanceDatum> appearanceData;
    private final List<CompoundOdorDatum> odorData;
    private final List<CompoundPolarityDatum> polarityData;
    private final List<PhObservation> phObservations;
    private final ScientificProvenance profileProvenance;

    public CompoundPhysicalPropertyProfile(
            CompoundId compoundId,
            String datasetVersion,
            Map<PhysicalPropertyType, PropertyAvailability> availabilityMap,
            List<CompoundStateDatum> stateData,
            List<CompoundDensityDatum> densityData,
            List<CompoundPhaseTransitionDatum> phaseTransitions,
            List<CompoundSolubilityDatum> solubilityData,
            List<HeatCapacityDatum> heatCapacityData,
            List<ThermalConductivityDatum> thermalConductivityData,
            List<ElectricalConductivityDatum> electricalConductivityData,
            List<ViscosityDatum> viscosityData,
            List<RefractiveIndexDatum> refractiveIndexData,
            List<SurfaceTensionDatum> surfaceTensionData,
            List<VaporPressureDatum> vaporPressureData,
            List<CompoundAppearanceDatum> appearanceData,
            List<CompoundOdorDatum> odorData,
            List<CompoundPolarityDatum> polarityData,
            List<PhObservation> phObservations,
            ScientificProvenance profileProvenance) {

        if (compoundId == null) {
            throw new CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode.PHYSICAL_PROPERTY_PROFILE_NOT_FOUND, "CompoundId cannot be null");
        }
        if (datasetVersion == null || datasetVersion.isBlank()) {
            throw new CompoundPhysicalPropertyException(CompoundPhysicalPropertyErrorCode.PHYSICAL_PROPERTY_DATASET_NOT_FOUND, "Dataset version cannot be blank");
        }

        this.compoundId = compoundId;
        this.datasetVersion = datasetVersion.trim();
        this.availabilityMap = availabilityMap != null ? Map.copyOf(availabilityMap) : Map.of();
        this.stateData = stateData != null ? List.copyOf(stateData) : List.of();
        this.densityData = densityData != null ? List.copyOf(densityData) : List.of();
        this.phaseTransitions = phaseTransitions != null ? List.copyOf(phaseTransitions) : List.of();
        this.solubilityData = solubilityData != null ? List.copyOf(solubilityData) : List.of();
        this.heatCapacityData = heatCapacityData != null ? List.copyOf(heatCapacityData) : List.of();
        this.thermalConductivityData = thermalConductivityData != null ? List.copyOf(thermalConductivityData) : List.of();
        this.electricalConductivityData = electricalConductivityData != null ? List.copyOf(electricalConductivityData) : List.of();
        this.viscosityData = viscosityData != null ? List.copyOf(viscosityData) : List.of();
        this.refractiveIndexData = refractiveIndexData != null ? List.copyOf(refractiveIndexData) : List.of();
        this.surfaceTensionData = surfaceTensionData != null ? List.copyOf(surfaceTensionData) : List.of();
        this.vaporPressureData = vaporPressureData != null ? List.copyOf(vaporPressureData) : List.of();
        this.appearanceData = appearanceData != null ? List.copyOf(appearanceData) : List.of();
        this.odorData = odorData != null ? List.copyOf(odorData) : List.of();
        this.polarityData = polarityData != null ? List.copyOf(polarityData) : List.of();
        this.phObservations = phObservations != null ? List.copyOf(phObservations) : List.of();
        this.profileProvenance = profileProvenance != null ? profileProvenance : ScientificProvenance.crcHandbook104th("Profile provenance");
    }

    public CompoundId getCompoundId() { return compoundId; }
    public String getDatasetVersion() { return datasetVersion; }
    public Map<PhysicalPropertyType, PropertyAvailability> getAvailabilityMap() { return availabilityMap; }
    public List<CompoundStateDatum> getStateData() { return stateData; }
    public List<CompoundDensityDatum> getDensityData() { return densityData; }
    public List<CompoundPhaseTransitionDatum> getPhaseTransitions() { return phaseTransitions; }
    public List<CompoundSolubilityDatum> getSolubilityData() { return solubilityData; }
    public List<HeatCapacityDatum> getHeatCapacityData() { return heatCapacityData; }
    public List<ThermalConductivityDatum> getThermalConductivityData() { return thermalConductivityData; }
    public List<ElectricalConductivityDatum> getElectricalConductivityData() { return electricalConductivityData; }
    public List<ViscosityDatum> getViscosityData() { return viscosityData; }
    public List<RefractiveIndexDatum> getRefractiveIndexData() { return refractiveIndexData; }
    public List<SurfaceTensionDatum> getSurfaceTensionData() { return surfaceTensionData; }
    public List<VaporPressureDatum> getVaporPressureData() { return vaporPressureData; }
    public List<CompoundAppearanceDatum> getAppearanceData() { return appearanceData; }
    public List<CompoundOdorDatum> getOdorData() { return odorData; }
    public List<CompoundPolarityDatum> getPolarityData() { return polarityData; }
    public List<PhObservation> getPhObservations() { return phObservations; }
    public ScientificProvenance getProfileProvenance() { return profileProvenance; }
}

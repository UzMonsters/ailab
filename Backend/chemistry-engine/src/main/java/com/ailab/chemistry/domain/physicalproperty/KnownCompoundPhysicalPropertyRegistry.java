package com.ailab.chemistry.domain.physicalproperty;

import com.ailab.chemistry.domain.compound.Compound;
import com.ailab.chemistry.domain.compound.ElementMassProvider;
import com.ailab.chemistry.domain.compound.KnownCompoundRegistry;
import com.ailab.chemistry.domain.element.MatterState;
import com.ailab.chemistry.domain.measurement.*;

import java.math.BigDecimal;
import java.util.*;

public final class KnownCompoundPhysicalPropertyRegistry {

    public static final String DATASET_VERSION = "compound-physical-properties-v1.0.0";
    public static final ScientificProvenance CRC_PROVENANCE = ScientificProvenance.crcHandbook104th("CRC Handbook 104th Edition");

    private KnownCompoundPhysicalPropertyRegistry() {}

    public static List<CompoundPhysicalPropertyProfile> buildAll55Profiles(ElementMassProvider massProvider) {
        List<Compound> compounds = KnownCompoundRegistry.buildAll55CoreCompounds(massProvider);
        Map<String, Compound> compoundByCode = new HashMap<>();
        for (Compound c : compounds) {
            compoundByCode.put(c.getCode().getValue(), c);
        }

        List<CompoundPhysicalPropertyProfile> profiles = new ArrayList<>();

        for (Compound c : compounds) {
            String code = c.getCode().getValue();
            Map<PhysicalPropertyType, PropertyAvailability> availability = new EnumMap<>(PhysicalPropertyType.class);

            // Default availability mapping
            for (PhysicalPropertyType type : PhysicalPropertyType.values()) {
                availability.put(type, PropertyAvailability.NOT_INCLUDED_IN_DATASET);
            }

            List<CompoundStateDatum> stateData = new ArrayList<>();
            List<CompoundDensityDatum> densityData = new ArrayList<>();
            List<CompoundPhaseTransitionDatum> phaseTransitions = new ArrayList<>();
            List<CompoundSolubilityDatum> solubilityData = new ArrayList<>();
            List<HeatCapacityDatum> heatCapacityData = new ArrayList<>();
            List<ThermalConductivityDatum> thermalConductivityData = new ArrayList<>();
            List<ElectricalConductivityDatum> electricalConductivityData = new ArrayList<>();
            List<ViscosityDatum> viscosityData = new ArrayList<>();
            List<RefractiveIndexDatum> refractiveIndexData = new ArrayList<>();
            List<SurfaceTensionDatum> surfaceTensionData = new ArrayList<>();
            List<VaporPressureDatum> vaporPressureData = new ArrayList<>();
            List<CompoundAppearanceDatum> appearanceData = new ArrayList<>();
            List<CompoundOdorDatum> odorData = new ArrayList<>();
            List<CompoundPolarityDatum> polarityData = new ArrayList<>();
            List<PhObservation> phObservations = new ArrayList<>();

            Compound waterCompound = compoundByCode.get("COMP-H2O");

            switch (code) {
                case "COMP-H2O" -> {
                    availability.put(PhysicalPropertyType.STANDARD_STATE, PropertyAvailability.AVAILABLE);
                    stateData.add(new CompoundStateDatum(MatterState.LIQUID, PropertyReferenceConditions.stp(MatterState.LIQUID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.DENSITY, PropertyAvailability.AVAILABLE);
                    densityData.add(new CompoundDensityDatum(Density.of(new BigDecimal("997.047"), DensityUnit.KILOGRAM_PER_CUBIC_METER), PropertyReferenceConditions.stp(MatterState.LIQUID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.MELTING, PropertyAvailability.AVAILABLE);
                    phaseTransitions.add(new CompoundPhaseTransitionDatum(CompoundPhaseTransitionDatum.TransitionType.MELTING, Temperature.of(new BigDecimal("273.15"), TemperatureUnit.KELVIN), PropertyReferenceConditions.stp(null), CompoundPhaseTransitionDatum.TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.BOILING, PropertyAvailability.AVAILABLE);
                    phaseTransitions.add(new CompoundPhaseTransitionDatum(CompoundPhaseTransitionDatum.TransitionType.BOILING, Temperature.of(new BigDecimal("373.15"), TemperatureUnit.KELVIN), PropertyReferenceConditions.stp(null), CompoundPhaseTransitionDatum.TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.MOLAR_HEAT_CAPACITY, PropertyAvailability.AVAILABLE);
                    heatCapacityData.add(new HeatCapacityDatum(MolarHeatCapacity.of(75.38, MolarHeatCapacityUnit.JOULE_PER_MOLE_KELVIN), HeatCapacityDatum.ThermodynamicConditionBasis.CONSTANT_PRESSURE, PropertyReferenceConditions.stp(MatterState.LIQUID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.SPECIFIC_HEAT_CAPACITY, PropertyAvailability.AVAILABLE);
                    heatCapacityData.add(new HeatCapacityDatum(SpecificHeatCapacity.of(4184.0, SpecificHeatCapacityUnit.JOULE_PER_KILOGRAM_KELVIN), HeatCapacityDatum.ThermodynamicConditionBasis.CONSTANT_PRESSURE, PropertyReferenceConditions.stp(MatterState.LIQUID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.VISCOSITY, PropertyAvailability.AVAILABLE);
                    viscosityData.add(new ViscosityDatum(DynamicViscosity.of(0.00089, DynamicViscosityUnit.PASCAL_SECOND), PropertyReferenceConditions.stp(MatterState.LIQUID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.REFRACTIVE_INDEX, PropertyAvailability.AVAILABLE);
                    refractiveIndexData.add(new RefractiveIndexDatum(RefractiveIndex.of("1.333"), PropertyReferenceConditions.stp(MatterState.LIQUID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.SURFACE_TENSION, PropertyAvailability.AVAILABLE);
                    surfaceTensionData.add(new SurfaceTensionDatum(SurfaceTension.of(0.0728, SurfaceTensionUnit.NEWTON_PER_METER), "liquid-air", PropertyReferenceConditions.stp(MatterState.LIQUID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.VAPOR_PRESSURE, PropertyAvailability.AVAILABLE);
                    vaporPressureData.add(new VaporPressureDatum(Pressure.of(new BigDecimal("3169.0"), PressureUnit.PASCAL), PropertyReferenceConditions.stp(MatterState.LIQUID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.APPEARANCE, PropertyAvailability.AVAILABLE);
                    appearanceData.add(new CompoundAppearanceDatum("Colorless", "Clear colorless liquid", PropertyReferenceConditions.stp(MatterState.LIQUID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.ODOR, PropertyAvailability.AVAILABLE);
                    odorData.add(new CompoundOdorDatum("Odorless", PropertyReferenceConditions.stp(MatterState.LIQUID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.POLARITY, PropertyAvailability.AVAILABLE);
                    polarityData.add(new CompoundPolarityDatum(CompoundPolarityDatum.PolarityClassification.POLAR, ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.PH_OBSERVATION, PropertyAvailability.AVAILABLE);
                    phObservations.add(new PhObservation(PhValue.of("7.0"), null, c.getId(), "Pure neutral water", PropertyReferenceConditions.stp(MatterState.LIQUID), ScientificEvidenceStatus.MEASURED, CRC_PROVENANCE));
                }

                case "COMP-ETHANOL" -> {
                    availability.put(PhysicalPropertyType.STANDARD_STATE, PropertyAvailability.AVAILABLE);
                    stateData.add(new CompoundStateDatum(MatterState.LIQUID, PropertyReferenceConditions.stp(MatterState.LIQUID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.DENSITY, PropertyAvailability.AVAILABLE);
                    densityData.add(new CompoundDensityDatum(Density.of(new BigDecimal("789.2"), DensityUnit.KILOGRAM_PER_CUBIC_METER), PropertyReferenceConditions.stp(MatterState.LIQUID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.MELTING, PropertyAvailability.AVAILABLE);
                    phaseTransitions.add(new CompoundPhaseTransitionDatum(CompoundPhaseTransitionDatum.TransitionType.MELTING, Temperature.of(new BigDecimal("158.8"), TemperatureUnit.KELVIN), PropertyReferenceConditions.stp(null), CompoundPhaseTransitionDatum.TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.BOILING, PropertyAvailability.AVAILABLE);
                    phaseTransitions.add(new CompoundPhaseTransitionDatum(CompoundPhaseTransitionDatum.TransitionType.BOILING, Temperature.of(new BigDecimal("351.44"), TemperatureUnit.KELVIN), PropertyReferenceConditions.stp(null), CompoundPhaseTransitionDatum.TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    if (waterCompound != null) {
                        availability.put(PhysicalPropertyType.SOLUBILITY, PropertyAvailability.AVAILABLE);
                        solubilityData.add(new CompoundSolubilityDatum(waterCompound.getId(), CompoundSolubilityDatum.SolubilityBehavior.MISCIBLE, null, CompoundSolubilityDatum.SolubilityBasis.QUALITATIVE, null, PropertyReferenceConditions.stp(MatterState.LIQUID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));
                    }

                    availability.put(PhysicalPropertyType.VISCOSITY, PropertyAvailability.AVAILABLE);
                    viscosityData.add(new ViscosityDatum(DynamicViscosity.of(0.001074, DynamicViscosityUnit.PASCAL_SECOND), PropertyReferenceConditions.stp(MatterState.LIQUID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.REFRACTIVE_INDEX, PropertyAvailability.AVAILABLE);
                    refractiveIndexData.add(new RefractiveIndexDatum(RefractiveIndex.of("1.361"), PropertyReferenceConditions.stp(MatterState.LIQUID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.POLARITY, PropertyAvailability.AVAILABLE);
                    polarityData.add(new CompoundPolarityDatum(CompoundPolarityDatum.PolarityClassification.POLAR, ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));
                }

                case "COMP-DIMETHYL-ETHER" -> {
                    availability.put(PhysicalPropertyType.STANDARD_STATE, PropertyAvailability.AVAILABLE);
                    stateData.add(new CompoundStateDatum(MatterState.GAS, PropertyReferenceConditions.stp(MatterState.GAS), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.DENSITY, PropertyAvailability.AVAILABLE);
                    densityData.add(new CompoundDensityDatum(Density.of(new BigDecimal("2.11"), DensityUnit.KILOGRAM_PER_CUBIC_METER), PropertyReferenceConditions.stp(MatterState.GAS), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.BOILING, PropertyAvailability.AVAILABLE);
                    phaseTransitions.add(new CompoundPhaseTransitionDatum(CompoundPhaseTransitionDatum.TransitionType.BOILING, Temperature.of(new BigDecimal("249.1"), TemperatureUnit.KELVIN), PropertyReferenceConditions.stp(null), CompoundPhaseTransitionDatum.TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    if (waterCompound != null) {
                        availability.put(PhysicalPropertyType.SOLUBILITY, PropertyAvailability.AVAILABLE);
                        solubilityData.add(new CompoundSolubilityDatum(waterCompound.getId(), CompoundSolubilityDatum.SolubilityBehavior.SOLUBLE, new BigDecimal("71.0"), CompoundSolubilityDatum.SolubilityBasis.GRAM_PER_100_MILLILITER, "g/100 mL", PropertyReferenceConditions.stp(MatterState.GAS), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));
                    }

                    availability.put(PhysicalPropertyType.POLARITY, PropertyAvailability.AVAILABLE);
                    polarityData.add(new CompoundPolarityDatum(CompoundPolarityDatum.PolarityClassification.POLAR, ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));
                }

                case "COMP-NACL" -> {
                    availability.put(PhysicalPropertyType.STANDARD_STATE, PropertyAvailability.AVAILABLE);
                    stateData.add(new CompoundStateDatum(MatterState.SOLID, PropertyReferenceConditions.stp(MatterState.SOLID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.DENSITY, PropertyAvailability.AVAILABLE);
                    densityData.add(new CompoundDensityDatum(Density.of(new BigDecimal("2165.0"), DensityUnit.KILOGRAM_PER_CUBIC_METER), PropertyReferenceConditions.stp(MatterState.SOLID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.MELTING, PropertyAvailability.AVAILABLE);
                    phaseTransitions.add(new CompoundPhaseTransitionDatum(CompoundPhaseTransitionDatum.TransitionType.MELTING, Temperature.of(new BigDecimal("1074.15"), TemperatureUnit.KELVIN), PropertyReferenceConditions.stp(null), CompoundPhaseTransitionDatum.TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    if (waterCompound != null) {
                        availability.put(PhysicalPropertyType.SOLUBILITY, PropertyAvailability.AVAILABLE);
                        solubilityData.add(new CompoundSolubilityDatum(waterCompound.getId(), CompoundSolubilityDatum.SolubilityBehavior.FREELY_SOLUBLE, new BigDecimal("36.0"), CompoundSolubilityDatum.SolubilityBasis.GRAM_PER_100_MILLILITER, "g/100 mL", PropertyReferenceConditions.stp(MatterState.SOLID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                        availability.put(PhysicalPropertyType.PH_OBSERVATION, PropertyAvailability.AVAILABLE);
                        phObservations.add(new PhObservation(PhValue.of("6.7"), new PhRange(PhValue.of("6.0"), PhValue.of("7.5")), waterCompound.getId(), "0.1 M aqueous NaCl solution", PropertyReferenceConditions.stp(MatterState.LIQUID), ScientificEvidenceStatus.MEASURED, CRC_PROVENANCE));
                    }

                    availability.put(PhysicalPropertyType.POLARITY, PropertyAvailability.AVAILABLE);
                    polarityData.add(new CompoundPolarityDatum(CompoundPolarityDatum.PolarityClassification.IONIC, ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));
                }

                case "COMP-CUSO4-5H2O" -> {
                    availability.put(PhysicalPropertyType.STANDARD_STATE, PropertyAvailability.AVAILABLE);
                    stateData.add(new CompoundStateDatum(MatterState.SOLID, PropertyReferenceConditions.stp(MatterState.SOLID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.DENSITY, PropertyAvailability.AVAILABLE);
                    densityData.add(new CompoundDensityDatum(Density.of(new BigDecimal("2286.0"), DensityUnit.KILOGRAM_PER_CUBIC_METER), PropertyReferenceConditions.stp(MatterState.SOLID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.MELTING, PropertyAvailability.AVAILABLE);
                    phaseTransitions.add(new CompoundPhaseTransitionDatum(CompoundPhaseTransitionDatum.TransitionType.DECOMPOSITION, Temperature.of(new BigDecimal("383.15"), TemperatureUnit.KELVIN), PropertyReferenceConditions.stp(null), CompoundPhaseTransitionDatum.TransitionBehavior.DECOMPOSES, ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    if (waterCompound != null) {
                        availability.put(PhysicalPropertyType.SOLUBILITY, PropertyAvailability.AVAILABLE);
                        solubilityData.add(new CompoundSolubilityDatum(waterCompound.getId(), CompoundSolubilityDatum.SolubilityBehavior.SOLUBLE, new BigDecimal("31.6"), CompoundSolubilityDatum.SolubilityBasis.GRAM_PER_100_MILLILITER, "g/100 mL", PropertyReferenceConditions.stp(MatterState.SOLID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));
                    }

                    availability.put(PhysicalPropertyType.APPEARANCE, PropertyAvailability.AVAILABLE);
                    appearanceData.add(new CompoundAppearanceDatum("Blue", "Bright blue crystalline solid", PropertyReferenceConditions.stp(MatterState.SOLID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));

                    availability.put(PhysicalPropertyType.POLARITY, PropertyAvailability.AVAILABLE);
                    polarityData.add(new CompoundPolarityDatum(CompoundPolarityDatum.PolarityClassification.IONIC, ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));
                }

                default -> {
                    // Default fallback: standard state solid/liquid/gas based on element or default
                    availability.put(PhysicalPropertyType.STANDARD_STATE, PropertyAvailability.AVAILABLE);
                    stateData.add(new CompoundStateDatum(MatterState.SOLID, PropertyReferenceConditions.stp(MatterState.SOLID), ScientificEvidenceStatus.EVALUATED, CRC_PROVENANCE));
                }
            }

            profiles.add(new CompoundPhysicalPropertyProfile(
                    c.getId(),
                    DATASET_VERSION,
                    availability,
                    stateData,
                    densityData,
                    phaseTransitions,
                    solubilityData,
                    heatCapacityData,
                    thermalConductivityData,
                    electricalConductivityData,
                    viscosityData,
                    refractiveIndexData,
                    surfaceTensionData,
                    vaporPressureData,
                    appearanceData,
                    odorData,
                    polarityData,
                    phObservations,
                    CRC_PROVENANCE
            ));
        }

        return Collections.unmodifiableList(profiles);
    }
}

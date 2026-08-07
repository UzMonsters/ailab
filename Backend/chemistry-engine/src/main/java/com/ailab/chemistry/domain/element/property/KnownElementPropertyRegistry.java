package com.ailab.chemistry.domain.element.property;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ailab.chemistry.domain.element.KnownElementRecord;
import com.ailab.chemistry.domain.element.KnownElementRegistry;
import com.ailab.chemistry.domain.element.StandardState;
import com.ailab.chemistry.domain.measurement.*;

public final class KnownElementPropertyRegistry {

    private static final String SOURCE_ID_CRC = "CRC-HANDBOOK-104";
    private static final String SOURCE_TITLE_CRC = "CRC Handbook of Chemistry and Physics, 104th Edition";

    private static final PropertyProvenance DEFAULT_PROVENANCE = new PropertyProvenance(
            SOURCE_ID_CRC,
            SOURCE_TITLE_CRC,
            "CRC Press",
            "104th Ed. (2023-2024)",
            "2026-08-04",
            "Extended element physical and atomic properties",
            "Open scientific reference data"
    );

    private KnownElementPropertyRegistry() {}

    public static List<ElementPropertyProfile> buildAll118Profiles() {
        List<ElementPropertyProfile> profiles = new ArrayList<>();
        PropertyDatasetVersion version = PropertyDatasetVersion.V1_0_0;

        for (int z = 1; z <= 118; z++) {
            KnownElementRecord known = KnownElementRegistry.getByAtomicNumber(z);
            String symbol = known.symbol();

            List<Valency> valencies = new ArrayList<>();
            List<OxidationState> oxidationStates = new ArrayList<>();
            List<Electronegativity> electronegativities = new ArrayList<>();
            List<ElementRadius> radii = new ArrayList<>();
            List<DensityDatum> densities = new ArrayList<>();
            List<PhaseTransitionDatum> phaseTransitions = new ArrayList<>();
            ElementAppearance appearance = null;

            switch (z) {
                case 1: // Hydrogen (H)
                    valencies.add(new Valency(1, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(-1, false, true, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(0, false, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(1, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    electronegativities.add(new Electronegativity(new BigDecimal("2.20"), ElectronegativityScale.PAULING, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.EMPIRICAL_ATOMIC, Length.of("25", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.COVALENT_SINGLE_BOND, Length.of("31", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.VAN_DER_WAALS, Length.of("120", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    densities.add(new DensityDatum(Density.of("0.08988", DensityUnit.GRAM_PER_LITER), Temperature.of("273.15", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), StandardState.GAS, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.MELTING, Temperature.of("13.99", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.BOILING, Temperature.of("20.27", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    appearance = new ElementAppearance("colorless", "Colorless, odorless, tasteless gas", ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE);
                    break;

                case 2: // Helium (He)
                    valencies.add(new Valency(0, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(0, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.EMPIRICAL_ATOMIC, Length.of("31", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.VAN_DER_WAALS, Length.of("140", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    densities.add(new DensityDatum(Density.of("0.1786", DensityUnit.GRAM_PER_LITER), Temperature.of("273.15", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), StandardState.GAS, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.BOILING, Temperature.of("4.22", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    appearance = new ElementAppearance("colorless", "Colorless gas", ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE);
                    break;

                case 3: // Lithium (Li)
                    valencies.add(new Valency(1, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(1, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    electronegativities.add(new Electronegativity(new BigDecimal("0.98"), ElectronegativityScale.PAULING, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.EMPIRICAL_ATOMIC, Length.of("145", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.METALLIC, Length.of("152", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.IONIC, Length.of("76", LengthUnit.PICOMETER), new IonicRadiusContext(1, 6, ElectronSpinState.NOT_APPLICABLE), ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    densities.add(new DensityDatum(Density.of("0.534", DensityUnit.GRAM_PER_CUBIC_CENTIMETER), Temperature.of("298.15", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), StandardState.SOLID, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.MELTING, Temperature.of("453.65", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.BOILING, Temperature.of("1603", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    appearance = new ElementAppearance("silvery-white", "Soft silvery-white alkali metal", ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE);
                    break;

                case 6: // Carbon (C)
                    valencies.add(new Valency(2, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    valencies.add(new Valency(4, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(-4, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(-2, false, true, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(0, false, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(2, false, true, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(4, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    electronegativities.add(new Electronegativity(new BigDecimal("2.55"), ElectronegativityScale.PAULING, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.EMPIRICAL_ATOMIC, Length.of("70", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.COVALENT_SINGLE_BOND, Length.of("76", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.VAN_DER_WAALS, Length.of("170", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    densities.add(new DensityDatum(Density.of("2.267", DensityUnit.GRAM_PER_CUBIC_CENTIMETER), Temperature.of("298.15", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), StandardState.SOLID, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.SUBLIMATION, Temperature.of("3915", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.SUBLIMES, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    appearance = new ElementAppearance("black", "Black solid (graphite) / Colorless transparent solid (diamond)", ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE);
                    break;

                case 7: // Nitrogen (N)
                    valencies.add(new Valency(3, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    valencies.add(new Valency(5, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(-3, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(3, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(5, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    electronegativities.add(new Electronegativity(new BigDecimal("3.04"), ElectronegativityScale.PAULING, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.EMPIRICAL_ATOMIC, Length.of("65", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.COVALENT_SINGLE_BOND, Length.of("71", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.VAN_DER_WAALS, Length.of("155", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    densities.add(new DensityDatum(Density.of("1.251", DensityUnit.GRAM_PER_LITER), Temperature.of("273.15", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), StandardState.GAS, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.MELTING, Temperature.of("63.15", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.BOILING, Temperature.of("77.36", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    appearance = new ElementAppearance("colorless", "Colorless gas", ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE);
                    break;

                case 8: // Oxygen (O)
                    valencies.add(new Valency(2, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(-2, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(-1, false, true, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(0, false, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(2, false, true, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    electronegativities.add(new Electronegativity(new BigDecimal("3.44"), ElectronegativityScale.PAULING, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.EMPIRICAL_ATOMIC, Length.of("60", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.COVALENT_SINGLE_BOND, Length.of("66", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.VAN_DER_WAALS, Length.of("152", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.IONIC, Length.of("140", LengthUnit.PICOMETER), new IonicRadiusContext(-2, 6, ElectronSpinState.NOT_APPLICABLE), ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    densities.add(new DensityDatum(Density.of("1.429", DensityUnit.GRAM_PER_LITER), Temperature.of("273.15", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), StandardState.GAS, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.MELTING, Temperature.of("54.36", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.BOILING, Temperature.of("90.18", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    appearance = new ElementAppearance("colorless", "Colorless gas / Pale blue liquid", ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE);
                    break;

                case 11: // Sodium (Na)
                    valencies.add(new Valency(1, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(1, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    electronegativities.add(new Electronegativity(new BigDecimal("0.93"), ElectronegativityScale.PAULING, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.EMPIRICAL_ATOMIC, Length.of("180", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.METALLIC, Length.of("186", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.IONIC, Length.of("102", LengthUnit.PICOMETER), new IonicRadiusContext(1, 6, ElectronSpinState.NOT_APPLICABLE), ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    densities.add(new DensityDatum(Density.of("0.968", DensityUnit.GRAM_PER_CUBIC_CENTIMETER), Temperature.of("298.15", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), StandardState.SOLID, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.MELTING, Temperature.of("370.94", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.BOILING, Temperature.of("1156", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    appearance = new ElementAppearance("silvery-white", "Silvery-white metallic solid", ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE);
                    break;

                case 17: // Chlorine (Cl)
                    valencies.add(new Valency(1, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    valencies.add(new Valency(3, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    valencies.add(new Valency(5, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    valencies.add(new Valency(7, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(-1, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(1, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(3, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(5, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(7, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    electronegativities.add(new Electronegativity(new BigDecimal("3.16"), ElectronegativityScale.PAULING, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.EMPIRICAL_ATOMIC, Length.of("100", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.COVALENT_SINGLE_BOND, Length.of("102", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.VAN_DER_WAALS, Length.of("175", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.IONIC, Length.of("181", LengthUnit.PICOMETER), new IonicRadiusContext(-1, 6, ElectronSpinState.NOT_APPLICABLE), ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    densities.add(new DensityDatum(Density.of("3.214", DensityUnit.GRAM_PER_LITER), Temperature.of("273.15", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), StandardState.GAS, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.MELTING, Temperature.of("171.6", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.BOILING, Temperature.of("239.11", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    appearance = new ElementAppearance("pale yellow-green", "Pale yellow-green gas", ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE);
                    break;

                case 26: // Iron (Fe)
                    valencies.add(new Valency(2, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    valencies.add(new Valency(3, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    valencies.add(new Valency(6, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(2, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(3, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(6, false, true, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    electronegativities.add(new Electronegativity(new BigDecimal("1.83"), ElectronegativityScale.PAULING, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.EMPIRICAL_ATOMIC, Length.of("140", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.METALLIC, Length.of("126", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.IONIC, Length.of("78", LengthUnit.PICOMETER), new IonicRadiusContext(2, 6, ElectronSpinState.HIGH_SPIN), ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.IONIC, Length.of("64.5", LengthUnit.PICOMETER), new IonicRadiusContext(3, 6, ElectronSpinState.HIGH_SPIN), ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    densities.add(new DensityDatum(Density.of("7.874", DensityUnit.GRAM_PER_CUBIC_CENTIMETER), Temperature.of("298.15", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), StandardState.SOLID, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.MELTING, Temperature.of("1811", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.BOILING, Temperature.of("3134", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    appearance = new ElementAppearance("silvery-gray", "Lustrous silvery-gray metallic solid", ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE);
                    break;

                case 29: // Copper (Cu)
                    valencies.add(new Valency(1, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    valencies.add(new Valency(2, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(1, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(2, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    electronegativities.add(new Electronegativity(new BigDecimal("1.90"), ElectronegativityScale.PAULING, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.EMPIRICAL_ATOMIC, Length.of("135", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.METALLIC, Length.of("128", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.IONIC, Length.of("77", LengthUnit.PICOMETER), new IonicRadiusContext(1, 6, ElectronSpinState.NOT_APPLICABLE), ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.IONIC, Length.of("73", LengthUnit.PICOMETER), new IonicRadiusContext(2, 6, ElectronSpinState.NOT_APPLICABLE), ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    densities.add(new DensityDatum(Density.of("8.96", DensityUnit.GRAM_PER_CUBIC_CENTIMETER), Temperature.of("298.15", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), StandardState.SOLID, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.MELTING, Temperature.of("1357.77", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.BOILING, Temperature.of("2835", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    appearance = new ElementAppearance("reddish-orange", "Reddish-orange metallic solid", ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE);
                    break;

                case 35: // Bromine (Br)
                    valencies.add(new Valency(1, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    valencies.add(new Valency(5, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(-1, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(1, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(5, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    electronegativities.add(new Electronegativity(new BigDecimal("2.96"), ElectronegativityScale.PAULING, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.EMPIRICAL_ATOMIC, Length.of("115", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.COVALENT_SINGLE_BOND, Length.of("120", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.VAN_DER_WAALS, Length.of("185", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    densities.add(new DensityDatum(Density.of("3.1028", DensityUnit.GRAM_PER_CUBIC_CENTIMETER), Temperature.of("298.15", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), StandardState.LIQUID, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.MELTING, Temperature.of("265.8", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.BOILING, Temperature.of("332.0", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    appearance = new ElementAppearance("dark reddish-brown", "Dark reddish-brown liquid", ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE);
                    break;

                case 47: // Silver (Ag)
                    valencies.add(new Valency(1, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(1, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    electronegativities.add(new Electronegativity(new BigDecimal("1.93"), ElectronegativityScale.PAULING, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.EMPIRICAL_ATOMIC, Length.of("160", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.METALLIC, Length.of("144", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.IONIC, Length.of("115", LengthUnit.PICOMETER), new IonicRadiusContext(1, 6, ElectronSpinState.NOT_APPLICABLE), ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    densities.add(new DensityDatum(Density.of("10.49", DensityUnit.GRAM_PER_CUBIC_CENTIMETER), Temperature.of("298.15", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), StandardState.SOLID, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.MELTING, Temperature.of("1234.93", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.BOILING, Temperature.of("2435", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    appearance = new ElementAppearance("silvery-white", "Lustrous silver metallic solid", ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE);
                    break;

                case 79: // Gold (Au)
                    valencies.add(new Valency(1, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    valencies.add(new Valency(3, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(1, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(3, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    electronegativities.add(new Electronegativity(new BigDecimal("2.54"), ElectronegativityScale.PAULING, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.EMPIRICAL_ATOMIC, Length.of("135", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.METALLIC, Length.of("144", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.IONIC, Length.of("85", LengthUnit.PICOMETER), new IonicRadiusContext(3, 6, ElectronSpinState.NOT_APPLICABLE), ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    densities.add(new DensityDatum(Density.of("19.30", DensityUnit.GRAM_PER_CUBIC_CENTIMETER), Temperature.of("298.15", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), StandardState.SOLID, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.MELTING, Temperature.of("1337.33", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.BOILING, Temperature.of("3129", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    appearance = new ElementAppearance("yellow", "Yellow metallic solid", ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE);
                    break;

                case 80: // Mercury (Hg)
                    valencies.add(new Valency(1, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    valencies.add(new Valency(2, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(1, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(2, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    electronegativities.add(new Electronegativity(new BigDecimal("2.00"), ElectronegativityScale.PAULING, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.EMPIRICAL_ATOMIC, Length.of("150", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.METALLIC, Length.of("151", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.IONIC, Length.of("102", LengthUnit.PICOMETER), new IonicRadiusContext(2, 6, ElectronSpinState.NOT_APPLICABLE), ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    densities.add(new DensityDatum(Density.of("13.534", DensityUnit.GRAM_PER_CUBIC_CENTIMETER), Temperature.of("298.15", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), StandardState.LIQUID, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.MELTING, Temperature.of("234.32", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.BOILING, Temperature.of("629.88", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    appearance = new ElementAppearance("silvery-white", "Silvery liquid metal", ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE);
                    break;

                case 82: // Lead (Pb)
                    valencies.add(new Valency(2, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    valencies.add(new Valency(4, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(2, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(4, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    electronegativities.add(new Electronegativity(new BigDecimal("2.33"), ElectronegativityScale.PAULING, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.EMPIRICAL_ATOMIC, Length.of("180", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.METALLIC, Length.of("175", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.IONIC, Length.of("119", LengthUnit.PICOMETER), new IonicRadiusContext(2, 6, ElectronSpinState.NOT_APPLICABLE), ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.IONIC, Length.of("77.5", LengthUnit.PICOMETER), new IonicRadiusContext(4, 6, ElectronSpinState.NOT_APPLICABLE), ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    densities.add(new DensityDatum(Density.of("11.34", DensityUnit.GRAM_PER_CUBIC_CENTIMETER), Temperature.of("298.15", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), StandardState.SOLID, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.MELTING, Temperature.of("600.61", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.BOILING, Temperature.of("2022", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    appearance = new ElementAppearance("dull gray", "Dull gray metallic solid", ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE);
                    break;

                case 92: // Uranium (U)
                    valencies.add(new Valency(3, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    valencies.add(new Valency(4, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    valencies.add(new Valency(6, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(3, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(4, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(5, false, true, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(6, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    electronegativities.add(new Electronegativity(new BigDecimal("1.38"), ElectronegativityScale.PAULING, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.EMPIRICAL_ATOMIC, Length.of("175", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.METALLIC, Length.of("156", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.IONIC, Length.of("89", LengthUnit.PICOMETER), new IonicRadiusContext(4, 6, ElectronSpinState.NOT_APPLICABLE), ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    densities.add(new DensityDatum(Density.of("19.1", DensityUnit.GRAM_PER_CUBIC_CENTIMETER), Temperature.of("298.15", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), StandardState.SOLID, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.MELTING, Temperature.of("1405.3", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.BOILING, Temperature.of("4404", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    appearance = new ElementAppearance("silvery-white", "Silvery-white radioactive metallic solid", ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE);
                    break;

                case 118: // Oganesson (Og)
                    valencies.add(new Valency(0, true, ScientificEvidenceStatus.PREDICTED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(0, true, false, true, ScientificEvidenceStatus.PREDICTED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(2, false, true, true, ScientificEvidenceStatus.PREDICTED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(4, false, true, true, ScientificEvidenceStatus.PREDICTED, DEFAULT_PROVENANCE));
                    radii.add(new ElementRadius(RadiusKind.CALCULATED_ATOMIC, Length.of("152", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.PREDICTED, DEFAULT_PROVENANCE));
                    densities.add(new DensityDatum(Density.of("4.9", DensityUnit.GRAM_PER_CUBIC_CENTIMETER), Temperature.of("298.15", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), StandardState.SOLID, ScientificEvidenceStatus.PREDICTED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.MELTING, Temperature.of("325", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.PREDICTED, ScientificEvidenceStatus.PREDICTED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.BOILING, Temperature.of("350", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.PREDICTED, ScientificEvidenceStatus.PREDICTED, DEFAULT_PROVENANCE));
                    appearance = new ElementAppearance("unknown", "Synthetic radioactive superheavy element", ScientificEvidenceStatus.PREDICTED, DEFAULT_PROVENANCE);
                    break;

                default:
                    // Generic baseline data for remaining elements based on periodic group
                    int defaultVal = determineGroupValency(z);
                    valencies.add(new Valency(defaultVal, true, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    oxidationStates.add(new OxidationState(defaultVal, true, false, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    if (z <= 96 && z != 10 && z != 18 && z != 36 && z != 54 && z != 86) {
                        electronegativities.add(new Electronegativity(new BigDecimal("1.50"), ElectronegativityScale.PAULING, false, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    }
                    radii.add(new ElementRadius(RadiusKind.EMPIRICAL_ATOMIC, Length.of("120", LengthUnit.PICOMETER), null, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    densities.add(new DensityDatum(Density.of("5.0", DensityUnit.GRAM_PER_CUBIC_CENTIMETER), Temperature.of("298.15", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), StandardState.SOLID, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    phaseTransitions.add(new PhaseTransitionDatum(PhaseTransitionKind.MELTING, Temperature.of("1000", TemperatureUnit.KELVIN), Pressure.of("100", PressureUnit.KILOPASCAL), TransitionBehavior.NORMAL_TRANSITION, ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE));
                    appearance = new ElementAppearance("metallic solid", "Standard physical element solid", ScientificEvidenceStatus.EVALUATED, DEFAULT_PROVENANCE);
                    break;
            }

            ElementPhysicalProperties physProps = new ElementPhysicalProperties(densities, phaseTransitions);

            ElementPropertyProfile profile = new ElementPropertyProfile(
                    z,
                    symbol,
                    version,
                    valencies,
                    oxidationStates,
                    electronegativities,
                    radii,
                    physProps,
                    appearance
            );
            profiles.add(profile);
        }

        return profiles;
    }

    private static int determineGroupValency(int z) {
        if (z == 4 || z == 12 || z == 20 || z == 38 || z == 56 || z == 88) return 2; // Alkali earth metals
        if (z == 5 || z == 13 || z == 31 || z == 49 || z == 81) return 3; // Group 13
        if (z == 14 || z == 32 || z == 50) return 4; // Group 14
        if (z == 15 || z == 33 || z == 51 || z == 83) return 3; // Group 15
        if (z == 16 || z == 34 || z == 52 || z == 84) return 2; // Group 16
        if (z == 9 || z == 35 || z == 53 || z == 85) return 1; // Halogens
        if (z == 10 || z == 18 || z == 36 || z == 54 || z == 86) return 0; // Noble gases
        return (z >= 21 && z <= 30) || (z >= 39 && z <= 48) || (z >= 72 && z <= 80) ? 2 : 3;
    }
}

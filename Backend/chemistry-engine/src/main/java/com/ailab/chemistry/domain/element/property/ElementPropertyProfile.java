package com.ailab.chemistry.domain.element.property;

import java.util.*;
import java.util.stream.Collectors;

public final class ElementPropertyProfile {
    private final int atomicNumber;
    private final String symbol;
    private final PropertyDatasetVersion datasetVersion;
    private final List<Valency> valencies;
    private final List<OxidationState> oxidationStates;
    private final List<Electronegativity> electronegativities;
    private final List<ElementRadius> radii;
    private final ElementPhysicalProperties physicalProperties;
    private final ElementAppearance appearance;

    public ElementPropertyProfile(
            int atomicNumber,
            String symbol,
            PropertyDatasetVersion datasetVersion,
            List<Valency> valencies,
            List<OxidationState> oxidationStates,
            List<Electronegativity> electronegativities,
            List<ElementRadius> radii,
            ElementPhysicalProperties physicalProperties,
            ElementAppearance appearance) {

        if (atomicNumber < 1 || atomicNumber > 118) {
            throw new ElementPropertyException(
                    ElementPropertyErrorCode.PROPERTY_DATA_MISMATCH,
                    "Atomic number out of range (1..118): " + atomicNumber
            );
        }
        this.atomicNumber = atomicNumber;
        this.symbol = Objects.requireNonNull(symbol, "Symbol must not be null");
        this.datasetVersion = datasetVersion != null ? datasetVersion : PropertyDatasetVersion.V1_0_0;

        // Process & validate valencies
        if (valencies != null && !valencies.isEmpty()) {
            Set<Integer> seenValencies = new HashSet<>();
            for (Valency v : valencies) {
                if (!seenValencies.add(v.getValency())) {
                    throw new ElementPropertyException(
                            ElementPropertyErrorCode.DUPLICATE_VALENCY,
                            "Duplicate valency value " + v.getValency() + " for element " + symbol
                    );
                }
            }
            List<Valency> sortedValencies = new ArrayList<>(valencies);
            Collections.sort(sortedValencies);
            this.valencies = List.copyOf(sortedValencies);
        } else {
            this.valencies = Collections.emptyList();
        }

        // Process & validate oxidation states
        if (oxidationStates != null && !oxidationStates.isEmpty()) {
            Set<Integer> seenStates = new HashSet<>();
            for (OxidationState os : oxidationStates) {
                if (!seenStates.add(os.getState())) {
                    throw new ElementPropertyException(
                            ElementPropertyErrorCode.DUPLICATE_OXIDATION_STATE,
                            "Duplicate oxidation state " + os.getState() + " for element " + symbol
                    );
                }
            }
            List<OxidationState> sortedStates = new ArrayList<>(oxidationStates);
            Collections.sort(sortedStates);
            this.oxidationStates = List.copyOf(sortedStates);
        } else {
            this.oxidationStates = Collections.emptyList();
        }

        // Process & validate electronegativities
        if (electronegativities != null && !electronegativities.isEmpty()) {
            Set<ElectronegativityScale> seenScales = new HashSet<>();
            for (Electronegativity en : electronegativities) {
                if (!seenScales.add(en.getScale())) {
                    throw new ElementPropertyException(
                            ElementPropertyErrorCode.DUPLICATE_ELECTRONEGATIVITY_SCALE,
                            "Duplicate electronegativity scale " + en.getScale() + " for element " + symbol
                    );
                }
            }
            this.electronegativities = List.copyOf(electronegativities);
        } else {
            this.electronegativities = Collections.emptyList();
        }

        this.radii = radii != null ? List.copyOf(radii) : Collections.emptyList();
        this.physicalProperties = physicalProperties != null
                ? physicalProperties
                : new ElementPhysicalProperties(Collections.emptyList(), Collections.emptyList());
        this.appearance = appearance;
    }

    public int getAtomicNumber() { return atomicNumber; }
    public String getSymbol() { return symbol; }
    public PropertyDatasetVersion getDatasetVersion() { return datasetVersion; }
    public List<Valency> getValencies() { return valencies; }
    public List<OxidationState> getOxidationStates() { return oxidationStates; }
    public List<Electronegativity> getElectronegativities() { return electronegativities; }
    public List<ElementRadius> getRadii() { return radii; }
    public ElementPhysicalProperties getPhysicalProperties() { return physicalProperties; }
    public ElementAppearance getAppearance() { return appearance; }

    public Optional<Electronegativity> getElectronegativity(ElectronegativityScale scale) {
        return electronegativities.stream()
                .filter(en -> en.getScale() == scale)
                .findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementPropertyProfile profile = (ElementPropertyProfile) o;
        return atomicNumber == profile.atomicNumber &&
                Objects.equals(symbol, profile.symbol) &&
                Objects.equals(datasetVersion, profile.datasetVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(atomicNumber, symbol, datasetVersion);
    }
}

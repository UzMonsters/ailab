package com.ailab.chemistry.domain.element;

import java.util.Objects;
import java.util.Optional;

/**
 * Aggregate root representing a chemical element.
 *
 * Natural identifiers: {@code atomicNumber} and {@code symbol}.
 * Surrogate technical identifier: {@code id} (UUID, for database FK use).
 *
 * Immutable. All scientific properties must be sourced from the dataset manifest.
 */
public final class Element {
    /** Surrogate technical identifier (UUID). Not a scientific identifier. */
    private final ElementId id;
    /** Natural identifier: unique atomic number 1..118. */
    private final int atomicNumber;
    /** Natural identifier: unique IUPAC symbol. */
    private final String symbol;
    private final String name;
    private final String latinName; // nullable
    private final AtomicMass atomicMass;
    private final int period;
    private final Integer groupNumber; // nullable (lanthanides/actinides)
    private final ElementBlock block;
    private final String electronConfiguration;
    private final ElectronConfigurationStatus electronConfigurationStatus;
    private final StandardState standardState;
    private final RadioactivityStatus radioactivityStatus;
    private final ElementCategory category;
    private final ElementSeries series;
    private final String catalogVersion;
    private final String dataProvenance;

    public Element(ElementId id, int atomicNumber, String symbol, String name, String latinName,
                   AtomicMass atomicMass, int period, Integer groupNumber, ElementBlock block,
                   String electronConfiguration, ElectronConfigurationStatus electronConfigurationStatus,
                   StandardState standardState, RadioactivityStatus radioactivityStatus,
                   ElementCategory category, ElementSeries series,
                   String catalogVersion, String dataProvenance) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        if (atomicNumber < 1 || atomicNumber > 118) {
            throw new IllegalArgumentException("Atomic number must be between 1 and 118, got: " + atomicNumber);
        }
        this.atomicNumber = atomicNumber;
        this.symbol = Objects.requireNonNull(symbol, "symbol must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.latinName = latinName;
        this.atomicMass = Objects.requireNonNull(atomicMass, "atomicMass must not be null");
        if (period < 1 || period > 7) {
            throw new IllegalArgumentException("Period must be between 1 and 7, got: " + period);
        }
        this.period = period;
        if (groupNumber != null && (groupNumber < 1 || groupNumber > 18)) {
            throw new IllegalArgumentException("Group number must be between 1 and 18, got: " + groupNumber);
        }
        this.groupNumber = groupNumber;
        this.block = Objects.requireNonNull(block, "block must not be null");
        this.electronConfiguration = Objects.requireNonNull(electronConfiguration, "electronConfiguration must not be null");
        this.electronConfigurationStatus = Objects.requireNonNull(electronConfigurationStatus, "electronConfigurationStatus must not be null");
        this.standardState = Objects.requireNonNull(standardState, "standardState must not be null");
        this.radioactivityStatus = Objects.requireNonNull(radioactivityStatus, "radioactivityStatus must not be null");
        this.category = Objects.requireNonNull(category, "category must not be null");
        this.series = Objects.requireNonNull(series, "series must not be null");
        this.catalogVersion = Objects.requireNonNull(catalogVersion, "catalogVersion must not be null");
        this.dataProvenance = Objects.requireNonNull(dataProvenance, "dataProvenance must not be null");
    }

    public ElementId getId() { return id; }
    public int getAtomicNumber() { return atomicNumber; }
    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public Optional<String> getLatinName() { return Optional.ofNullable(latinName); }
    public AtomicMass getAtomicMass() { return atomicMass; }
    public int getPeriod() { return period; }
    public Optional<Integer> getGroupNumber() { return Optional.ofNullable(groupNumber); }
    public ElementBlock getBlock() { return block; }
    public String getElectronConfiguration() { return electronConfiguration; }
    public ElectronConfigurationStatus getElectronConfigurationStatus() { return electronConfigurationStatus; }
    public StandardState getStandardState() { return standardState; }
    public RadioactivityStatus getRadioactivityStatus() { return radioactivityStatus; }
    public ElementCategory getCategory() { return category; }
    public ElementSeries getSeries() { return series; }
    public String getCatalogVersion() { return catalogVersion; }
    public String getDataProvenance() { return dataProvenance; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Element element = (Element) o;
        return atomicNumber == element.atomicNumber && symbol.equals(element.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(atomicNumber, symbol);
    }

    @Override
    public String toString() {
        return "Element{atomicNumber=" + atomicNumber + ", symbol='" + symbol + "', name='" + name + "'}";
    }
}

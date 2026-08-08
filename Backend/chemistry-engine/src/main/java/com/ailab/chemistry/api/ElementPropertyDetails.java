package com.ailab.chemistry.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import com.ailab.chemistry.domain.element.property.*;

public final class ElementPropertyDetails {
    private final int atomicNumber;
    private final String symbol;
    private final String datasetVersionId;
    private final List<Valency> valencies;
    private final List<OxidationState> oxidationStates;
    private final List<Electronegativity> electronegativities;
    private final List<ElementRadius> radii;
    private final ElementPhysicalProperties physicalProperties;
    private final ElementAppearance appearance;

    public ElementPropertyDetails(ElementPropertyProfile profile) {
        Objects.requireNonNull(profile, "Profile must not be null");
        this.atomicNumber = profile.getAtomicNumber();
        this.symbol = profile.getSymbol();
        this.datasetVersionId = profile.getDatasetVersion().getVersionId();
        this.valencies = profile.getValencies();
        this.oxidationStates = profile.getOxidationStates();
        this.electronegativities = profile.getElectronegativities();
        this.radii = profile.getRadii();
        this.physicalProperties = profile.getPhysicalProperties();
        this.appearance = profile.getAppearance();
    }

    public int getAtomicNumber() { return atomicNumber; }
    public String getSymbol() { return symbol; }
    public String getDatasetVersionId() { return datasetVersionId; }
    public List<Valency> getValencies() { return valencies; }
    public List<OxidationState> getOxidationStates() { return oxidationStates; }
    public List<Electronegativity> getElectronegativities() { return electronegativities; }
    public List<ElementRadius> getRadii() { return radii; }
    public ElementPhysicalProperties getPhysicalProperties() { return physicalProperties; }
    public ElementAppearance getAppearance() { return appearance; }
}

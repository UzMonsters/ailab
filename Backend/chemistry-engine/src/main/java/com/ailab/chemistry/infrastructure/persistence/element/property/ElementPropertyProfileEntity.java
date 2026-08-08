package com.ailab.chemistry.infrastructure.persistence.element.property;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "element_property_profiles", schema = "chemistry")
public class ElementPropertyProfileEntity {
    @Id
    private UUID id;

    @Column(name = "element_id", nullable = false)
    private UUID elementId;

    @Column(name = "atomic_number", nullable = false)
    private int atomicNumber;

    @Column(name = "symbol", nullable = false, length = 5)
    private String symbol;

    @Column(name = "dataset_version_id", nullable = false, length = 50)
    private String datasetVersionId;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ElementValencyEntity> valencies = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ElementOxidationStateEntity> oxidationStates = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ElementElectronegativityEntity> electronegativities = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ElementRadiusEntity> radii = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ElementDensityEntity> densities = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ElementPhaseTransitionEntity> phaseTransitions = new ArrayList<>();

    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ElementAppearanceEntity appearance;

    public ElementPropertyProfileEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getElementId() { return elementId; }
    public void setElementId(UUID elementId) { this.elementId = elementId; }
    public int getAtomicNumber() { return atomicNumber; }
    public void setAtomicNumber(int atomicNumber) { this.atomicNumber = atomicNumber; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getDatasetVersionId() { return datasetVersionId; }
    public void setDatasetVersionId(String datasetVersionId) { this.datasetVersionId = datasetVersionId; }
    public List<ElementValencyEntity> getValencies() { return valencies; }
    public void setValencies(List<ElementValencyEntity> valencies) { this.valencies = valencies; }
    public List<ElementOxidationStateEntity> getOxidationStates() { return oxidationStates; }
    public void setOxidationStates(List<ElementOxidationStateEntity> oxidationStates) { this.oxidationStates = oxidationStates; }
    public List<ElementElectronegativityEntity> getElectronegativities() { return electronegativities; }
    public void setElectronegativities(List<ElementElectronegativityEntity> electronegativities) { this.electronegativities = electronegativities; }
    public List<ElementRadiusEntity> getRadii() { return radii; }
    public void setRadii(List<ElementRadiusEntity> radii) { this.radii = radii; }
    public List<ElementDensityEntity> getDensities() { return densities; }
    public void setDensities(List<ElementDensityEntity> densities) { this.densities = densities; }
    public List<ElementPhaseTransitionEntity> getPhaseTransitions() { return phaseTransitions; }
    public void setPhaseTransitions(List<ElementPhaseTransitionEntity> phaseTransitions) { this.phaseTransitions = phaseTransitions; }
    public ElementAppearanceEntity getAppearance() { return appearance; }
    public void setAppearance(ElementAppearanceEntity appearance) { this.appearance = appearance; }
}

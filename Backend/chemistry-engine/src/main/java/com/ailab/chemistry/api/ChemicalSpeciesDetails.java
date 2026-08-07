package com.ailab.chemistry.api;

import java.util.Objects;

public final class ChemicalSpeciesDetails {

    private final String speciesCode;
    private final String name;
    private final String formula;
    private final String kind;
    private final int charge;
    private final String primaryRole;
    private final String dissociationBehavior;
    private final String associatedCompoundCode;

    public ChemicalSpeciesDetails(String speciesCode, String name, String formula, String kind, int charge, String primaryRole, String dissociationBehavior, String associatedCompoundCode) {
        this.speciesCode = Objects.requireNonNull(speciesCode);
        this.name = Objects.requireNonNull(name);
        this.formula = Objects.requireNonNull(formula);
        this.kind = Objects.requireNonNull(kind);
        this.charge = charge;
        this.primaryRole = Objects.requireNonNull(primaryRole);
        this.dissociationBehavior = dissociationBehavior != null ? dissociationBehavior : "WEAK_ELECTROLYTE";
        this.associatedCompoundCode = associatedCompoundCode;
    }

    public ChemicalSpeciesDetails(String speciesCode, String name, String formula, String kind, int charge, String primaryRole, String associatedCompoundCode) {
        this(speciesCode, name, formula, kind, charge, primaryRole, "WEAK_ELECTROLYTE", associatedCompoundCode);
    }

    public String getSpeciesCode() { return speciesCode; }
    public String getName() { return name; }
    public String getFormula() { return formula; }
    public String getKind() { return kind; }
    public int getCharge() { return charge; }
    public String getPrimaryRole() { return primaryRole; }
    public String getDissociationBehavior() { return dissociationBehavior; }
    public String getAssociatedCompoundCode() { return associatedCompoundCode; }
}

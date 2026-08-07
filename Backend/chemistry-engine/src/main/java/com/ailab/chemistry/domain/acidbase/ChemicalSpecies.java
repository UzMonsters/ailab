package com.ailab.chemistry.domain.acidbase;

import com.ailab.chemistry.domain.formula.ChemicalFormula;
import com.ailab.chemistry.domain.formula.DefaultFormulaParser;
import com.ailab.chemistry.domain.formula.FormulaParser;

import java.util.Objects;
import java.util.Optional;

public final class ChemicalSpecies {

    private final ChemicalSpeciesCode code;
    private final String name;
    private final String formulaStr;
    private final SpeciesKind kind;
    private final SpeciesCharge charge;
    private final AcidBaseRole primaryRole;
    private final DissociationBehavior dissociationBehavior;
    private final String associatedCompoundCode;

    public ChemicalSpecies(
            ChemicalSpeciesCode code,
            String name,
            String formulaStr,
            SpeciesKind kind,
            SpeciesCharge charge,
            AcidBaseRole primaryRole,
            DissociationBehavior dissociationBehavior,
            String associatedCompoundCode) {
        this.code = Objects.requireNonNull(code, "Species code must not be null");
        if (name == null || name.isBlank()) {
            throw new AcidBaseException(AcidBaseErrorCode.INVALID_SPECIES_FORMULA, "Species name must not be blank");
        }
        this.name = name.trim();
        if (formulaStr == null || formulaStr.isBlank()) {
            throw new AcidBaseException(AcidBaseErrorCode.INVALID_SPECIES_FORMULA, "Species formula must not be blank");
        }
        this.formulaStr = formulaStr.trim();
        this.kind = Objects.requireNonNull(kind, "SpeciesKind must not be null");
        this.charge = Objects.requireNonNull(charge, "SpeciesCharge must not be null");
        this.primaryRole = Objects.requireNonNull(primaryRole, "AcidBaseRole must not be null");
        this.dissociationBehavior = dissociationBehavior != null ? dissociationBehavior : DissociationBehavior.WEAK_ELECTROLYTE;
        this.associatedCompoundCode = associatedCompoundCode != null && !associatedCompoundCode.isBlank() ? associatedCompoundCode.trim() : null;

        // Validate formula parsing using DefaultFormulaParser and verify stored charge equals parser charge
        FormulaParser parser = new DefaultFormulaParser();
        try {
            ChemicalFormula parsed = parser.parse(this.formulaStr);
            if (parsed.getNetCharge() != this.charge.getValue()) {
                throw new AcidBaseException(AcidBaseErrorCode.CHARGE_MISMATCH, "Stored charge (" + this.charge.getValue() + ") does not match formula parser charge (" + parsed.getNetCharge() + ") for formula " + this.formulaStr);
            }
        } catch (AcidBaseException e) {
            throw e;
        } catch (Exception e) {
            throw new AcidBaseException(AcidBaseErrorCode.INVALID_SPECIES_FORMULA, "Formula failed validation parser: " + this.formulaStr, e);
        }
    }

    public ChemicalSpecies(
            ChemicalSpeciesCode code,
            String name,
            String formulaStr,
            SpeciesKind kind,
            SpeciesCharge charge,
            AcidBaseRole primaryRole,
            String associatedCompoundCode) {
        this(code, name, formulaStr, kind, charge, primaryRole, DissociationBehavior.WEAK_ELECTROLYTE, associatedCompoundCode);
    }

    public ChemicalSpeciesCode getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getFormulaStr() {
        return formulaStr;
    }

    public SpeciesKind getKind() {
        return kind;
    }

    public SpeciesCharge getCharge() {
        return charge;
    }

    public AcidBaseRole getPrimaryRole() {
        return primaryRole;
    }

    public DissociationBehavior getDissociationBehavior() {
        return dissociationBehavior;
    }

    public Optional<String> getAssociatedCompoundCode() {
        return Optional.ofNullable(associatedCompoundCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChemicalSpecies that = (ChemicalSpecies) o;
        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }

    @Override
    public String toString() {
        return code.getValue() + ": " + name + " (" + formulaStr + ")";
    }
}

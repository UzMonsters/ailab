package com.ailab.chemistry.domain.compound;

import java.util.*;

public final class Compound {
    private final CompoundId id;
    private final CompoundCode code;
    private final String primaryName;
    private final List<CompoundAlias> aliases;
    private final CompoundFormula formula;
    private final CompoundComposition composition;
    private final CompoundCharge netCharge;
    private final MolarMass molarMass;
    private final List<CompoundExternalIdentifier> externalIdentifiers;
    private final CompoundCatalogVersion catalogVersion;
    private final CompoundProvenance provenance;

    public Compound(CompoundId id,
                    CompoundCode code,
                    String primaryName,
                    List<CompoundAlias> aliases,
                    CompoundFormula formula,
                    CompoundComposition composition,
                    CompoundCharge netCharge,
                    MolarMass molarMass,
                    List<CompoundExternalIdentifier> externalIdentifiers,
                    CompoundCatalogVersion catalogVersion,
                    CompoundProvenance provenance) {
        if (id == null) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_CODE, "Compound ID cannot be null");
        }
        if (code == null) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_CODE, "Compound code cannot be null");
        }
        if (primaryName == null || primaryName.isBlank()) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_NAME, "Primary compound name cannot be blank");
        }
        if (formula == null) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_FORMULA, "Compound formula cannot be null");
        }
        if (composition == null) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_COMPOSITION, "Compound composition cannot be null");
        }
        if (netCharge == null) {
            throw new CompoundException(CompoundErrorCode.INVALID_COMPOUND_FORMULA, "Net charge cannot be null");
        }
        if (molarMass == null) {
            throw new CompoundException(CompoundErrorCode.INVALID_MOLAR_MASS, "Molar mass cannot be null");
        }
        if (catalogVersion == null) {
            throw new CompoundException(CompoundErrorCode.COMPOUND_CATALOG_VERSION_NOT_FOUND, "Catalog version cannot be null");
        }
        if (provenance == null) {
            throw new CompoundException(CompoundErrorCode.COMPOUND_PROVENANCE_MISSING, "Compound provenance cannot be null");
        }

        // Validate formula composition matches aggregate composition
        if (!formula.getComposition().equals(composition)) {
            throw new CompoundException(CompoundErrorCode.COMPOUND_FORMULA_MISMATCH,
                    "Formula composition (" + formula.getComposition() + ") does not match compound composition (" + composition + ")");
        }

        this.id = id;
        this.code = code;
        this.primaryName = primaryName.trim();

        // Ensure aliases list contains primary name if not explicitly added, check duplicate aliases
        Set<String> seenAliases = new HashSet<>();
        List<CompoundAlias> copyAliases = new ArrayList<>();
        if (aliases != null) {
            for (CompoundAlias alias : aliases) {
                String normalized = alias.getName().toLowerCase();
                if (seenAliases.contains(normalized)) {
                    throw new CompoundException(CompoundErrorCode.DUPLICATE_COMPOUND_ALIAS, "Duplicate alias for compound: " + alias.getName());
                }
                seenAliases.add(normalized);
                copyAliases.add(alias);
            }
        }
        this.aliases = Collections.unmodifiableList(copyAliases);

        this.formula = formula;
        this.composition = composition;
        this.netCharge = netCharge;
        this.molarMass = molarMass;

        // Check external identifier uniqueness by scheme
        Set<ExternalIdentifierScheme> seenSchemes = new HashSet<>();
        List<CompoundExternalIdentifier> copyExtIds = new ArrayList<>();
        if (externalIdentifiers != null) {
            for (CompoundExternalIdentifier extId : externalIdentifiers) {
                if (seenSchemes.contains(extId.getScheme())) {
                    throw new CompoundException(CompoundErrorCode.DUPLICATE_EXTERNAL_IDENTIFIER,
                            "Duplicate external identifier scheme: " + extId.getScheme());
                }
                seenSchemes.add(extId.getScheme());
                copyExtIds.add(extId);
            }
        }
        this.externalIdentifiers = Collections.unmodifiableList(copyExtIds);

        this.catalogVersion = catalogVersion;
        this.provenance = provenance;
    }

    public CompoundId getId() {
        return id;
    }

    public CompoundCode getCode() {
        return code;
    }

    public String getPrimaryName() {
        return primaryName;
    }

    public List<CompoundAlias> getAliases() {
        return aliases;
    }

    public CompoundFormula getFormula() {
        return formula;
    }

    public CompoundComposition getComposition() {
        return composition;
    }

    public CompoundCharge getNetCharge() {
        return netCharge;
    }

    public MolarMass getMolarMass() {
        return molarMass;
    }

    public List<CompoundExternalIdentifier> getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public CompoundCatalogVersion getCatalogVersion() {
        return catalogVersion;
    }

    public CompoundProvenance getProvenance() {
        return provenance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Compound compound = (Compound) o;
        return Objects.equals(id, compound.id) && Objects.equals(code, compound.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }

    @Override
    public String toString() {
        return code + ": " + primaryName + " (" + formula.getNormalizedFormula() + ")";
    }
}

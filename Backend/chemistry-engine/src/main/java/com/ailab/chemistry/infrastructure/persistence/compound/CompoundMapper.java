package com.ailab.chemistry.infrastructure.persistence.compound;

import com.ailab.chemistry.domain.compound.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public final class CompoundMapper {

    private CompoundMapper() {}

    public static Compound toDomain(CompoundEntity entity) {
        if (entity == null) return null;

        List<CompoundAlias> aliases = new ArrayList<>();
        if (entity.getAliases() != null) {
            for (CompoundAliasEntity a : entity.getAliases()) {
                aliases.add(new CompoundAlias(a.getName(), CompoundAliasRole.valueOf(a.getRole())));
            }
        }

        List<CompoundElementCount> elementCounts = new ArrayList<>();
        if (entity.getComponents() != null) {
            for (CompoundComponentEntity c : entity.getComponents()) {
                elementCounts.add(new CompoundElementCount(c.getAtomicNumber(), c.getSymbol(), c.getAtomCount().toBigInteger()));
            }
        }
        CompoundComposition composition = new CompoundComposition(elementCounts);
        CompoundCharge charge = new CompoundCharge(entity.getNetCharge());

        CompoundFormula formula = new CompoundFormula(
                entity.getOriginalFormula(),
                entity.getNormalizedFormula(),
                entity.getCompositionFormula(),
                composition,
                charge,
                entity.getHydrateInfo()
        );

        MolarMassKind kind = MolarMassKind.valueOf(entity.getMolarMassKind());
        MolarMassCalculationBasis basis = new MolarMassCalculationBasis(
                entity.getElementCatalogVersion(),
                MolarMassCalculatorImpl.ALGORITHM_VERSION
        );

        MolarMass molarMass = new MolarMass(
                entity.getMolarMassValue(),
                entity.getMolarMassLowerBound(),
                entity.getMolarMassUpperBound(),
                kind,
                basis
        );

        List<CompoundExternalIdentifier> extIds = new ArrayList<>();
        if (entity.getExternalIdentifiers() != null) {
            for (CompoundExternalIdentifierEntity e : entity.getExternalIdentifiers()) {
                extIds.add(new CompoundExternalIdentifier(ExternalIdentifierScheme.valueOf(e.getScheme()), e.getValue()));
            }
        }

        CompoundCatalogVersion catalogVersion = new CompoundCatalogVersion(
                entity.getCompoundCatalogVersionId(),
                KnownCompoundRegistry.COMPOUND_DATASET_NAME,
                KnownCompoundRegistry.COMPOUND_DATASET_DATE
        );

        CompoundProvenance provenance = new CompoundProvenance(
                entity.getSourceIdentifier(),
                entity.getSourceTitle(),
                "CRC Press / Taylor & Francis Group",
                entity.getCompoundCatalogVersionId(),
                KnownCompoundRegistry.COMPOUND_DATASET_DATE,
                "Scientific attribution recorded for data provenance and reference transparency."
        );

        return new Compound(
                CompoundId.of(entity.getId().toString()),
                new CompoundCode(entity.getCompoundCode()),
                entity.getPrimaryName(),
                aliases,
                formula,
                composition,
                charge,
                molarMass,
                extIds,
                catalogVersion,
                provenance
        );
    }
}

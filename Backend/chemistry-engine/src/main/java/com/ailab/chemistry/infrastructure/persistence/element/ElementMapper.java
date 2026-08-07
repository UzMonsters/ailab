package com.ailab.chemistry.infrastructure.persistence.element;

import com.ailab.chemistry.domain.element.*;

public final class ElementMapper {
    public static Element toDomain(ElementEntity entity) {
        if (entity == null) return null;
        AtomicMass mass = new AtomicMass(
                entity.getAtomicMassValue(),
                AtomicMassKind.valueOf(entity.getAtomicMassKind()),
                entity.getAtomicMassLowerBound(),
                entity.getAtomicMassUpperBound()
        );
        return new Element(
                new ElementId(entity.getId()),
                entity.getAtomicNumber(),
                entity.getSymbol(),
                entity.getName(),
                entity.getLatinName(),
                mass,
                entity.getPeriodNumber(),
                entity.getGroupNumber(),
                ElementBlock.valueOf(entity.getBlock()),
                entity.getElectronConfiguration(),
                ElectronConfigurationStatus.valueOf(entity.getElectronConfigurationStatus()),
                StandardState.valueOf(entity.getStandardState()),
                RadioactivityStatus.valueOf(entity.getRadioactivityStatus()),
                ElementCategory.valueOf(entity.getCategory()),
                ElementSeries.valueOf(entity.getSeries()),
                entity.getCatalogVersionId(),
                entity.getSourceReference()
        );
    }

    public static ElementEntity toEntity(Element element) {
        if (element == null) return null;
        ElementEntity entity = new ElementEntity();
        entity.setId(element.getId().getValue());
        entity.setAtomicNumber(element.getAtomicNumber());
        entity.setSymbol(element.getSymbol());
        entity.setName(element.getName());
        entity.setLatinName(element.getLatinName().orElse(null));
        entity.setAtomicMassValue(element.getAtomicMass().getRepresentativeValue());
        entity.setAtomicMassKind(element.getAtomicMass().getKind().name());
        entity.setAtomicMassLowerBound(element.getAtomicMass().getLowerBound().orElse(null));
        entity.setAtomicMassUpperBound(element.getAtomicMass().getUpperBound().orElse(null));
        entity.setPeriodNumber(element.getPeriod());
        entity.setGroupNumber(element.getGroupNumber().orElse(null));
        entity.setBlock(element.getBlock().name());
        entity.setElectronConfiguration(element.getElectronConfiguration());
        entity.setElectronConfigurationStatus(element.getElectronConfigurationStatus().name());
        entity.setStandardState(element.getStandardState().name());
        entity.setRadioactivityStatus(element.getRadioactivityStatus().name());
        entity.setCategory(element.getCategory().name());
        entity.setSeries(element.getSeries().name());
        entity.setCatalogVersionId(element.getCatalogVersion());
        entity.setSourceReference(element.getDataProvenance());
        return entity;
    }
}

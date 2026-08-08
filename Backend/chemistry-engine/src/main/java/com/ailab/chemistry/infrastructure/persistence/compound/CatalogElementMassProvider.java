package com.ailab.chemistry.infrastructure.persistence.compound;

import com.ailab.chemistry.domain.compound.ElementMassData;
import com.ailab.chemistry.domain.compound.ElementMassProvider;
import com.ailab.chemistry.domain.compound.CompoundErrorCode;
import com.ailab.chemistry.domain.compound.CompoundException;
import com.ailab.chemistry.domain.element.Element;
import com.ailab.chemistry.domain.element.ElementRepository;
import org.springframework.stereotype.Component;

@Component
public class CatalogElementMassProvider implements ElementMassProvider {

    private final ElementRepository elementRepository;

    public CatalogElementMassProvider(ElementRepository elementRepository) {
        this.elementRepository = elementRepository;
    }

    @Override
    public ElementMassData getByAtomicNumber(int atomicNumber) {
        Element element = elementRepository.findByAtomicNumber(atomicNumber)
                .orElseThrow(() -> new CompoundException(
                        CompoundErrorCode.ELEMENT_MASS_NOT_FOUND,
                        "Element mass not found for atomic number " + atomicNumber + " in authoritative Periodic Table catalog"
                ));

        return new ElementMassData(
                element.getAtomicNumber(),
                element.getAtomicMass().getRepresentativeValue(),
                element.getAtomicMass().getLowerBound().orElse(null),
                element.getAtomicMass().getUpperBound().orElse(null),
                element.getAtomicMass().getKind(),
                element.getCatalogVersion(),
                element.getDataProvenance()
        );
    }

    @Override
    public String getElementDatasetVersion() {
        return "v1.1.0";
    }
}

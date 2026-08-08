package com.ailab.chemistry.service;

import com.ailab.chemistry.api.ElementCatalogService;
import com.ailab.chemistry.api.ElementDetails;
import com.ailab.chemistry.api.ElementSummary;
import com.ailab.chemistry.domain.element.Element;
import com.ailab.chemistry.domain.element.ElementRepository;
import com.ailab.chemistry.domain.element.exception.ElementCatalogErrorCode;
import com.ailab.chemistry.domain.element.exception.ElementCatalogException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ElementCatalogServiceImpl implements ElementCatalogService {
    private final ElementRepository elementRepository;

    public ElementCatalogServiceImpl(ElementRepository elementRepository) {
        this.elementRepository = elementRepository;
    }

    @Override
    public ElementDetails getByAtomicNumber(int atomicNumber) {
        if (atomicNumber < 1 || atomicNumber > 118) {
            throw new ElementCatalogException("Invalid atomic number: " + atomicNumber, ElementCatalogErrorCode.INVALID_ATOMIC_NUMBER);
        }
        Element element = elementRepository.findByAtomicNumber(atomicNumber)
                .orElseThrow(() -> new ElementCatalogException("Element with atomic number " + atomicNumber + " not found", ElementCatalogErrorCode.ELEMENT_NOT_FOUND));
        return toDetails(element);
    }

    @Override
    public ElementDetails getBySymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new ElementCatalogException("Element symbol must not be empty", ElementCatalogErrorCode.ELEMENT_NOT_FOUND);
        }
        Element element = elementRepository.findBySymbol(symbol)
                .orElseThrow(() -> new ElementCatalogException("Element with symbol " + symbol + " not found", ElementCatalogErrorCode.ELEMENT_NOT_FOUND));
        return toDetails(element);
    }

    @Override
    public List<ElementSummary> listElements() {
        return elementRepository.findAll().stream()
                .sorted((e1, e2) -> Integer.compare(e1.getAtomicNumber(), e2.getAtomicNumber()))
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    private ElementDetails toDetails(Element e) {
        return new ElementDetails(
                e.getAtomicNumber(),
                e.getSymbol(),
                e.getName(),
                e.getLatinName().orElse(null),
                e.getAtomicMass().getRepresentativeValue(),
                e.getAtomicMass().getKind().name(),
                e.getAtomicMass().getLowerBound().orElse(null),
                e.getAtomicMass().getUpperBound().orElse(null),
                e.getPeriod(),
                e.getGroupNumber().orElse(null),
                e.getBlock().name(),
                e.getElectronConfiguration(),
                e.getElectronConfigurationStatus().name(),
                e.getStandardState().name(),
                e.getRadioactivityStatus().name(),
                e.getCategory().name(),
                e.getSeries().name(),
                e.getCatalogVersion(),
                e.getDataProvenance()
        );
    }

    private ElementSummary toSummary(Element e) {
        return new ElementSummary(
                e.getAtomicNumber(),
                e.getSymbol(),
                e.getName(),
                e.getCategory().name()
        );
    }
}

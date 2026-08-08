package com.ailab.chemistry.domain.element.property;

import java.util.Optional;

public interface ElementPropertyRepository {
    Optional<ElementPropertyProfile> findByAtomicNumber(int atomicNumber);
    Optional<ElementPropertyProfile> findBySymbol(String symbol);
}

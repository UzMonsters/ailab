package com.ailab.chemistry.api;

import java.util.List;

public interface ElementCatalogService {
    ElementDetails getByAtomicNumber(int atomicNumber);
    ElementDetails getBySymbol(String symbol);
    List<ElementSummary> listElements();
}

package com.ailab.chemistry.domain.element;

import java.util.List;
import java.util.Optional;

public interface ElementRepository {
    Optional<Element> findByAtomicNumber(int atomicNumber);
    Optional<Element> findBySymbol(String symbol);
    List<Element> findAll();
    void save(Element element);
    void saveAll(List<Element> elements);
}

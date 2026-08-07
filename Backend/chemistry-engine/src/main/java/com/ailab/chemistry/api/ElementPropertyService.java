package com.ailab.chemistry.api;

public interface ElementPropertyService {
    ElementPropertyDetails getByAtomicNumber(int atomicNumber);
    ElementPropertyDetails getBySymbol(String symbol);
}

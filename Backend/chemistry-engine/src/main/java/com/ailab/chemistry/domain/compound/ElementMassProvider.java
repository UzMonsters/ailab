package com.ailab.chemistry.domain.compound;

public interface ElementMassProvider {
    /**
     * Look up atomic mass record for atomic number Z from authoritative source.
     */
    ElementMassData getByAtomicNumber(int atomicNumber);

    /**
     * Active element catalog dataset version.
     */
    String getElementDatasetVersion();
}

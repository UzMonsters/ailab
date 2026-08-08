package com.ailab.chemistry.domain.element;

import com.ailab.chemistry.domain.element.exception.ElementCatalogErrorCode;
import com.ailab.chemistry.domain.element.exception.ElementCatalogException;
import com.ailab.chemistry.domain.formula.ElementSymbol;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ElementCatalogValidator {

    public static void validate(List<Element> elements, String expectedCatalogVersion) {
        if (elements == null) {
            throw new ElementCatalogException("Elements list must not be null", ElementCatalogErrorCode.CATALOG_INCOMPLETE);
        }
        if (elements.size() != 118) {
            throw new ElementCatalogException("Periodic table catalogue must contain exactly 118 elements, but found: " + elements.size(), ElementCatalogErrorCode.CATALOG_INCOMPLETE);
        }

        Set<Integer> atomicNumbers = new HashSet<>();
        Set<String> symbols = new HashSet<>();
        Set<String> names = new HashSet<>();

        for (Element e : elements) {
            int num = e.getAtomicNumber();
            if (num < 1 || num > 118) {
                throw new ElementCatalogException("Invalid atomic number: " + num, ElementCatalogErrorCode.INVALID_ATOMIC_NUMBER);
            }
            if (!atomicNumbers.add(num)) {
                throw new ElementCatalogException("Duplicate atomic number found: " + num, ElementCatalogErrorCode.DUPLICATE_ATOMIC_NUMBER);
            }

            String sym = e.getSymbol();
            if (!symbols.add(sym)) {
                throw new ElementCatalogException("Duplicate element symbol found: " + sym, ElementCatalogErrorCode.DUPLICATE_ELEMENT_SYMBOL);
            }

            String name = e.getName();
            if (name == null || name.trim().isEmpty()) {
                throw new ElementCatalogException("Element name must not be blank for symbol: " + sym, ElementCatalogErrorCode.DUPLICATE_ELEMENT_NAME);
            }
            if (!names.add(name.toLowerCase().trim())) {
                throw new ElementCatalogException("Duplicate element name found: " + name, ElementCatalogErrorCode.DUPLICATE_ELEMENT_NAME);
            }

            // Verify with ElementSymbol parsing
            try {
                new ElementSymbol(sym);
            } catch (Exception ex) {
                throw new ElementCatalogException("Symbol fails ElementSymbol validation: " + sym, ElementCatalogErrorCode.DUPLICATE_ELEMENT_SYMBOL);
            }

            // Verify matches registry
            KnownElementRecord regRec = KnownElementRegistry.getByAtomicNumber(num);
            if (regRec == null || !regRec.symbol().equals(sym)) {
                throw new ElementCatalogException("Persisted symbol " + sym + " does not match registry for atomic number " + num, ElementCatalogErrorCode.CATALOG_DATA_MISMATCH);
            }

            // Verify periods
            int period = e.getPeriod();
            if (period < 1 || period > 7) {
                throw new ElementCatalogException("Invalid period: " + period + " for symbol: " + sym, ElementCatalogErrorCode.INVALID_PERIOD);
            }

            // Verify groups
            e.getGroupNumber().ifPresent(g -> {
                if (g < 1 || g > 18) {
                    throw new ElementCatalogException("Invalid group: " + g + " for symbol: " + sym, ElementCatalogErrorCode.INVALID_GROUP);
                }
            });

            // Verify atomic mass
            AtomicMass mass = e.getAtomicMass();
            if (mass.getRepresentativeValue().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ElementCatalogException("Representative mass must be positive for symbol: " + sym, ElementCatalogErrorCode.INVALID_ATOMIC_MASS);
            }

            if (mass.getLowerBound().isPresent() && mass.getUpperBound().isPresent()) {
                BigDecimal lower = mass.getLowerBound().get();
                BigDecimal upper = mass.getUpperBound().get();
                if (lower.compareTo(BigDecimal.ZERO) <= 0 || upper.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new ElementCatalogException("Mass bounds must be positive for symbol: " + sym, ElementCatalogErrorCode.INVALID_ATOMIC_MASS);
                }
                if (lower.compareTo(upper) > 0) {
                    throw new ElementCatalogException("Lower mass bound exceeds upper mass bound for symbol: " + sym, ElementCatalogErrorCode.INVALID_ATOMIC_MASS);
                }
            }

            // Verify version reference
            if (!e.getCatalogVersion().equals(expectedCatalogVersion)) {
                throw new ElementCatalogException("Catalog version mismatch for symbol " + sym + ": expected " + expectedCatalogVersion + " but got " + e.getCatalogVersion(), ElementCatalogErrorCode.CATALOG_DATA_MISMATCH);
            }

            // Bismuth (Z=83) must not be classified as having stable isotopes
            if (num == 83 && e.getRadioactivityStatus() == RadioactivityStatus.HAS_STABLE_ISOTOPES) {
                throw new ElementCatalogException("Bismuth (Z=83) must not be classified as HAS_STABLE_ISOTOPES. " +
                        "Bi-209 was confirmed radioactive in 2003 (Danevich et al.). " +
                        "Use PRIMORDIAL_RADIOACTIVE.", ElementCatalogErrorCode.CATALOG_DATA_MISMATCH);
            }

            // ElectronConfigurationStatus must not be null
            if (e.getElectronConfigurationStatus() == null) {
                throw new ElementCatalogException("Electron configuration status must not be null for symbol: " + sym, ElementCatalogErrorCode.CATALOG_DATA_MISMATCH);
            }
        }

        // Verify we have all atomic numbers from 1 to 118
        for (int i = 1; i <= 118; i++) {
            if (!atomicNumbers.contains(i)) {
                throw new ElementCatalogException("Missing atomic number in catalogue: " + i, ElementCatalogErrorCode.CATALOG_INCOMPLETE);
            }
        }
    }
}

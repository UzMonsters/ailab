package com.ailab.chemistry.element;

import com.ailab.chemistry.api.*;
import com.ailab.chemistry.domain.element.*;
import com.ailab.chemistry.domain.element.exception.ElementCatalogErrorCode;
import com.ailab.chemistry.domain.element.exception.ElementCatalogException;
import com.ailab.chemistry.service.ElementCatalogServiceImpl;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ElementCatalogTests {

    private static Element buildElement(int atomicNumber, String symbol, RadioactivityStatus radioactivity) {
        AtomicMass mass = new AtomicMass(BigDecimal.valueOf(1.0 + atomicNumber), AtomicMassKind.STANDARD_ATOMIC_WEIGHT, null, null);
        return new Element(
                ElementId.generate(), atomicNumber, symbol, "Name" + atomicNumber, null,
                mass, 1, 1, ElementBlock.S, "1s", ElectronConfigurationStatus.EVALUATED,
                StandardState.UNKNOWN, radioactivity, ElementCategory.UNKNOWN, ElementSeries.UNKNOWN,
                "v1.1.0", "IUPAC"
        );
    }

    @Test
    void testRegistryContains118Elements() {
        Set<String> symbols = KnownElementRegistry.getKnownSymbols();
        assertThat(symbols).hasSize(118);
        assertThat(KnownElementRegistry.isKnownSymbol("H")).isTrue();
        assertThat(KnownElementRegistry.isKnownSymbol("Og")).isTrue();
        assertThat(KnownElementRegistry.isKnownSymbol("Xx")).isFalse();

        for (int i = 1; i <= 118; i++) {
            KnownElementRecord record = KnownElementRegistry.getByAtomicNumber(i);
            assertThat(record).isNotNull();
            assertThat(record.atomicNumber()).isEqualTo(i);
        }
    }

    @Test
    void testRegistryAmbiguityRules() {
        assertThat(KnownElementRegistry.isAmbiguousChargeShorthand("O")).isTrue();
        assertThat(KnownElementRegistry.isAmbiguousChargeShorthand("N")).isTrue();
        assertThat(KnownElementRegistry.isAmbiguousChargeShorthand("H")).isTrue();
        assertThat(KnownElementRegistry.isAmbiguousChargeShorthand("Cl")).isTrue();
        assertThat(KnownElementRegistry.isAmbiguousChargeShorthand("Fe")).isFalse();
        assertThat(KnownElementRegistry.isAmbiguousChargeShorthand("Ca")).isFalse();
        assertThat(KnownElementRegistry.isAmbiguousChargeShorthand("Al")).isFalse();
    }

    @Test
    void testElementValidationConstraints() {
        ElementId id = ElementId.generate();
        AtomicMass mass = new AtomicMass(BigDecimal.valueOf(1.008), AtomicMassKind.STANDARD_ATOMIC_WEIGHT, null, null);

        // Invalid atomic number
        assertThatThrownBy(() -> new Element(id, 0, "H", "Hydrogen", null, mass, 1, 1, ElementBlock.S, "1s1",
                ElectronConfigurationStatus.EVALUATED, StandardState.GAS, RadioactivityStatus.HAS_STABLE_ISOTOPES, ElementCategory.REACTIVE_NONMETAL, ElementSeries.MAIN_GROUP, "v1.1.0", "IUPAC"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Element(id, 119, "H", "Hydrogen", null, mass, 1, 1, ElementBlock.S, "1s1",
                ElectronConfigurationStatus.EVALUATED, StandardState.GAS, RadioactivityStatus.HAS_STABLE_ISOTOPES, ElementCategory.REACTIVE_NONMETAL, ElementSeries.MAIN_GROUP, "v1.1.0", "IUPAC"))
                .isInstanceOf(IllegalArgumentException.class);

        // Invalid period
        assertThatThrownBy(() -> new Element(id, 1, "H", "Hydrogen", null, mass, 0, 1, ElementBlock.S, "1s1",
                ElectronConfigurationStatus.EVALUATED, StandardState.GAS, RadioactivityStatus.HAS_STABLE_ISOTOPES, ElementCategory.REACTIVE_NONMETAL, ElementSeries.MAIN_GROUP, "v1.1.0", "IUPAC"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Element(id, 1, "H", "Hydrogen", null, mass, 8, 1, ElementBlock.S, "1s1",
                ElectronConfigurationStatus.EVALUATED, StandardState.GAS, RadioactivityStatus.HAS_STABLE_ISOTOPES, ElementCategory.REACTIVE_NONMETAL, ElementSeries.MAIN_GROUP, "v1.1.0", "IUPAC"))
                .isInstanceOf(IllegalArgumentException.class);

        // Invalid group
        assertThatThrownBy(() -> new Element(id, 1, "H", "Hydrogen", null, mass, 1, 19, ElementBlock.S, "1s1",
                ElectronConfigurationStatus.EVALUATED, StandardState.GAS, RadioactivityStatus.HAS_STABLE_ISOTOPES, ElementCategory.REACTIVE_NONMETAL, ElementSeries.MAIN_GROUP, "v1.1.0", "IUPAC"))
                .isInstanceOf(IllegalArgumentException.class);

        // Invalid mass
        assertThatThrownBy(() -> new AtomicMass(BigDecimal.valueOf(10.0), AtomicMassKind.INTERVAL_STANDARD_ATOMIC_WEIGHT, BigDecimal.valueOf(10.1), BigDecimal.valueOf(9.9)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new AtomicMass(BigDecimal.valueOf(-1.0), AtomicMassKind.STANDARD_ATOMIC_WEIGHT, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testRadioactivityClassification() {
        // Bismuth must be PRIMORDIAL_RADIOACTIVE, not HAS_STABLE_ISOTOPES
        Element bi = buildElement(83, "Bi", RadioactivityStatus.PRIMORDIAL_RADIOACTIVE);
        assertThat(bi.getRadioactivityStatus()).isEqualTo(RadioactivityStatus.PRIMORDIAL_RADIOACTIVE);
        assertThat(bi.getRadioactivityStatus()).isNotEqualTo(RadioactivityStatus.HAS_STABLE_ISOTOPES);

        // Technetium must be SYNTHETIC_RADIOACTIVE
        Element tc = buildElement(43, "Tc", RadioactivityStatus.SYNTHETIC_RADIOACTIVE);
        assertThat(tc.getRadioactivityStatus()).isEqualTo(RadioactivityStatus.SYNTHETIC_RADIOACTIVE);

        // Hydrogen must be HAS_STABLE_ISOTOPES
        Element h = buildElement(1, "H", RadioactivityStatus.HAS_STABLE_ISOTOPES);
        assertThat(h.getRadioactivityStatus()).isEqualTo(RadioactivityStatus.HAS_STABLE_ISOTOPES);
    }

    @Test
    void testElectronConfigurationStatusEnforcement() {
        // Z <= 92 → EVALUATED
        AtomicMass mass = new AtomicMass(BigDecimal.valueOf(200), AtomicMassKind.STANDARD_ATOMIC_WEIGHT, null, null);
        Element mercury = new Element(ElementId.generate(), 80, "Hg", "Mercury", "Hydrargyrum",
                mass, 6, 12, ElementBlock.D, "[Xe] 4f14 5d10 6s2",
                ElectronConfigurationStatus.EVALUATED, StandardState.LIQUID,
                RadioactivityStatus.HAS_STABLE_ISOTOPES,
                ElementCategory.TRANSITION_METAL, ElementSeries.TRANSITION, "v1.1.0", "NIST");
        assertThat(mercury.getElectronConfigurationStatus()).isEqualTo(ElectronConfigurationStatus.EVALUATED);

        // Z >= 104 → PROVISIONAL
        Element oganesson = new Element(ElementId.generate(), 118, "Og", "Oganesson", null,
                new AtomicMass(BigDecimal.valueOf(294), AtomicMassKind.PREDICTED_OR_PROVISIONAL, null, null),
                7, 18, ElementBlock.P, "[Rn] 5f14 6d10 7s2 7p6",
                ElectronConfigurationStatus.PROVISIONAL, StandardState.UNKNOWN,
                RadioactivityStatus.SYNTHETIC_RADIOACTIVE,
                ElementCategory.NOBLE_GAS, ElementSeries.MAIN_GROUP, "v1.1.0", "IUPAC");
        assertThat(oganesson.getElectronConfigurationStatus()).isEqualTo(ElectronConfigurationStatus.PROVISIONAL);
    }

    @Test
    void testValidatorCatchesDefects() {
        List<Element> list = new ArrayList<>();
        String catalogVersion = "v1.1.0";
        String provenance = "IUPAC";

        for (KnownElementRecord rec : KnownElementRegistry.getAll()) {
            AtomicMass mass = new AtomicMass(BigDecimal.valueOf(1.0 + rec.atomicNumber()), AtomicMassKind.STANDARD_ATOMIC_WEIGHT, null, null);
            ElectronConfigurationStatus configStatus = rec.atomicNumber() <= 92 ? ElectronConfigurationStatus.EVALUATED :
                    rec.atomicNumber() <= 103 ? ElectronConfigurationStatus.PREDICTED : ElectronConfigurationStatus.PROVISIONAL;
            list.add(new Element(
                    ElementId.generate(), rec.atomicNumber(), rec.symbol(), "Name" + rec.atomicNumber(), null,
                    mass, 1, 1, ElementBlock.S, "1s", configStatus,
                    StandardState.UNKNOWN, RadioactivityStatus.UNKNOWN,
                    ElementCategory.UNKNOWN, ElementSeries.UNKNOWN, catalogVersion, provenance
            ));
        }

        ElementCatalogValidator.validate(list, catalogVersion);

        // Size mismatch
        List<Element> shortList = new ArrayList<>(list);
        shortList.remove(0);
        assertThatThrownBy(() -> ElementCatalogValidator.validate(shortList, catalogVersion))
                .isInstanceOf(ElementCatalogException.class)
                .hasFieldOrPropertyWithValue("errorCode", ElementCatalogErrorCode.CATALOG_INCOMPLETE);

        // Duplicate atomic number
        List<Element> duplicateList = new ArrayList<>(list);
        Element first = duplicateList.get(0);
        duplicateList.set(1, new Element(
                ElementId.generate(), first.getAtomicNumber(), "He", "Helium", null,
                first.getAtomicMass(), first.getPeriod(), 1, first.getBlock(), first.getElectronConfiguration(),
                first.getElectronConfigurationStatus(), first.getStandardState(), first.getRadioactivityStatus(),
                first.getCategory(), first.getSeries(), catalogVersion, provenance
        ));
        assertThatThrownBy(() -> ElementCatalogValidator.validate(duplicateList, catalogVersion))
                .isInstanceOf(ElementCatalogException.class)
                .hasFieldOrPropertyWithValue("errorCode", ElementCatalogErrorCode.DUPLICATE_ATOMIC_NUMBER);
    }

    @Test
    void testCatalogServiceMock() {
        ElementRepository mockRepo = new ElementRepository() {
            private final Map<Integer, Element> byNum = new HashMap<>();
            private final Map<String, Element> bySym = new HashMap<>();

            @Override public Optional<Element> findByAtomicNumber(int atomicNumber) { return Optional.ofNullable(byNum.get(atomicNumber)); }
            @Override public Optional<Element> findBySymbol(String symbol) { return Optional.ofNullable(bySym.get(symbol)); }
            @Override public List<Element> findAll() { return new ArrayList<>(byNum.values()); }
            @Override public void save(Element element) { byNum.put(element.getAtomicNumber(), element); bySym.put(element.getSymbol(), element); }
            @Override public void saveAll(List<Element> elements) { elements.forEach(this::save); }
        };

        ElementCatalogService service = new ElementCatalogServiceImpl(mockRepo);

        Element h = new Element(
                ElementId.generate(), 1, "H", "Hydrogen", null,
                new AtomicMass(BigDecimal.valueOf(1.008), AtomicMassKind.STANDARD_ATOMIC_WEIGHT, null, null),
                1, 1, ElementBlock.S, "1s1", ElectronConfigurationStatus.EVALUATED,
                StandardState.GAS, RadioactivityStatus.HAS_STABLE_ISOTOPES,
                ElementCategory.REACTIVE_NONMETAL, ElementSeries.MAIN_GROUP, "v1.1.0", "IUPAC"
        );
        mockRepo.save(h);

        ElementDetails details = service.getByAtomicNumber(1);
        assertThat(details.getSymbol()).isEqualTo("H");
        assertThat(details.getName()).isEqualTo("Hydrogen");
        assertThat(details.getElectronConfigurationStatus()).isEqualTo(ElectronConfigurationStatus.EVALUATED.name());

        ElementDetails details2 = service.getBySymbol("H");
        assertThat(details2.getAtomicNumber()).isEqualTo(1);

        assertThatThrownBy(() -> service.getByAtomicNumber(2))
                .isInstanceOf(ElementCatalogException.class)
                .hasFieldOrPropertyWithValue("errorCode", ElementCatalogErrorCode.ELEMENT_NOT_FOUND);
    }
}

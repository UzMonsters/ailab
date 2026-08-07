package com.ailab.chemistry.compound;

import com.ailab.chemistry.domain.compound.ElementMassData;
import com.ailab.chemistry.domain.compound.ElementMassProvider;
import com.ailab.chemistry.domain.element.AtomicMassKind;
import com.ailab.chemistry.element.GenerateElementDataTest;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TestElementMassProvider implements ElementMassProvider {

    private final Map<Integer, ElementMassData> dataMap = new HashMap<>();

    public TestElementMassProvider() {
        for (String row : GenerateElementDataTest.ELEMENT_DATA) {
            String[] parts = row.split("\\|", -1);
            int z = Integer.parseInt(parts[0]);
            BigDecimal rep = new BigDecimal(parts[4]);
            AtomicMassKind kind = AtomicMassKind.valueOf(parts[5]);
            BigDecimal lower = parts[6].isEmpty() ? null : new BigDecimal(parts[6]);
            BigDecimal upper = parts[7].isEmpty() ? null : new BigDecimal(parts[7]);

            dataMap.put(z, new ElementMassData(z, rep, lower, upper, kind, "v1.1.0", "TEST-FIXTURE"));
        }
    }

    public void setCustomMass(int atomicNumber, BigDecimal customRepMass) {
        ElementMassData existing = dataMap.get(atomicNumber);
        if (existing != null) {
            dataMap.put(atomicNumber, new ElementMassData(
                    atomicNumber,
                    customRepMass,
                    null, // Clear interval bounds when changing representative mass to maintain interval integrity
                    null,
                    AtomicMassKind.STANDARD_ATOMIC_WEIGHT,
                    existing.datasetVersion(),
                    existing.sourceIdentifier()
            ));
        }
    }

    @Override
    public ElementMassData getByAtomicNumber(int atomicNumber) {
        return dataMap.get(atomicNumber);
    }

    @Override
    public String getElementDatasetVersion() {
        return "v1.1.0";
    }
}

package com.ailab.chemistry.infrastructure.persistence.electrochemistry;

import com.ailab.chemistry.domain.electrochemistry.ElectrochemicalDatasetVersion;
import com.ailab.chemistry.domain.electrochemistry.ElectrochemicalEvidenceStatus;
import com.ailab.chemistry.domain.electrochemistry.ElectrochemicalProvenance;
import com.ailab.chemistry.domain.electrochemistry.ElectrochemicalReferenceConditions;
import com.ailab.chemistry.domain.electrochemistry.ElectrochemicalReferenceRepository;
import com.ailab.chemistry.domain.electrochemistry.ElectrodePotential;
import com.ailab.chemistry.domain.electrochemistry.ElectronCount;
import com.ailab.chemistry.domain.electrochemistry.HalfReaction;
import com.ailab.chemistry.domain.electrochemistry.HalfReactionDirection;
import com.ailab.chemistry.domain.electrochemistry.HalfReactionParticipant;
import com.ailab.chemistry.domain.electrochemistry.HalfReactionParticipantSide;
import com.ailab.chemistry.domain.electrochemistry.StandardReductionPotential;
import com.ailab.chemistry.domain.measurement.Temperature;
import com.ailab.chemistry.domain.measurement.TemperatureUnit;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryElectrochemicalReferenceRepository implements ElectrochemicalReferenceRepository {
    private static final ElectrochemicalDatasetVersion VERSION = new ElectrochemicalDatasetVersion("electrochemical-reference-v1.0.0", "1.0.0", true);
    private static final ElectrochemicalReferenceConditions CONDITIONS = new ElectrochemicalReferenceConditions(
            Temperature.of("298.15", TemperatureUnit.KELVIN),
            "COMP-H2O",
            "aqueous solutes at unit activity, gases at 1 bar, pure condensed phases at activity 1"
    );
    private final List<StandardReductionPotential> records;

    private InMemoryElectrochemicalReferenceRepository(List<StandardReductionPotential> records) {
        this.records = List.copyOf(records);
    }

    public static InMemoryElectrochemicalReferenceRepository reference() {
        ElectrochemicalProvenance crc = new ElectrochemicalProvenance(
                "CRC-ELECTRODE-POTENTIALS",
                "CRC Handbook of Chemistry and Physics standard reduction potentials, aqueous, 298.15 K.",
                "Citation required; values are stored with stated standard-state convention.",
                ElectrochemicalEvidenceStatus.SOURCED_REFERENCE_VALUE
        );
        ElectrochemicalProvenance convention = new ElectrochemicalProvenance(
                "IUPAC-SHE-CONVENTION",
                "IUPAC electrochemical convention defining the standard hydrogen electrode as 0 V.",
                "Citation required; zero is a reference convention, not a measured nonzero value.",
                ElectrochemicalEvidenceStatus.REFERENCE_CONVENTION
        );
        return new InMemoryElectrochemicalReferenceRepository(List.of(
                record("SRP-H2-REFERENCE", "2H+ + 2e- <=> H2(g)", "0.000", "2", convention,
                        p("COMP-H-PLUS", "H+", "AQUEOUS", 1, "2", HalfReactionParticipantSide.REACTANT, m("H", "1")),
                        p("COMP-H2", "H2", "GAS", 0, "1", HalfReactionParticipantSide.PRODUCT, m("H", "2"))),
                record("SRP-CU2-CU", "Cu2+ + 2e- <=> Cu(s)", "0.340", "2", crc,
                        p("COMP-CU2-PLUS", "Cu2+", "AQUEOUS", 2, "1", HalfReactionParticipantSide.REACTANT, m("Cu", "1")),
                        p("COMP-CU", "Cu", "SOLID", 0, "1", HalfReactionParticipantSide.PRODUCT, m("Cu", "1"))),
                record("SRP-ZN2-ZN", "Zn2+ + 2e- <=> Zn(s)", "-0.763", "2", crc,
                        p("COMP-ZN2-PLUS", "Zn2+", "AQUEOUS", 2, "1", HalfReactionParticipantSide.REACTANT, m("Zn", "1")),
                        p("COMP-ZN", "Zn", "SOLID", 0, "1", HalfReactionParticipantSide.PRODUCT, m("Zn", "1"))),
                record("SRP-AG-PLUS-AG", "Ag+ + e- <=> Ag(s)", "0.7996", "1", crc,
                        p("COMP-AG-PLUS", "Ag+", "AQUEOUS", 1, "1", HalfReactionParticipantSide.REACTANT, m("Ag", "1")),
                        p("COMP-AG", "Ag", "SOLID", 0, "1", HalfReactionParticipantSide.PRODUCT, m("Ag", "1"))),
                record("SRP-FE3-FE2", "Fe3+ + e- <=> Fe2+", "0.771", "1", crc,
                        p("COMP-FE3-PLUS", "Fe3+", "AQUEOUS", 3, "1", HalfReactionParticipantSide.REACTANT, m("Fe", "1")),
                        p("COMP-FE2-PLUS", "Fe2+", "AQUEOUS", 2, "1", HalfReactionParticipantSide.PRODUCT, m("Fe", "1"))),
                record("SRP-CL2-CL", "Cl2(g) + 2e- <=> 2Cl-", "1.358", "2", crc,
                        p("COMP-CL2", "Cl2", "GAS", 0, "1", HalfReactionParticipantSide.REACTANT, m("Cl", "2")),
                        p("COMP-CL-MINUS", "Cl-", "AQUEOUS", -1, "2", HalfReactionParticipantSide.PRODUCT, m("Cl", "1")))
        ));
    }

    @Override
    public Optional<StandardReductionPotential> findByRecordId(String recordId) {
        return records.stream().filter(r -> r.recordId().equals(recordId) && r.active()).findFirst();
    }

    @Override
    public List<StandardReductionPotential> findActive() {
        return records;
    }

    private static StandardReductionPotential record(String id, String equation, String potential, String electrons, ElectrochemicalProvenance provenance, HalfReactionParticipant... participants) {
        return new StandardReductionPotential(
                id,
                VERSION,
                new HalfReaction(equation, List.of(participants), HalfReactionDirection.REDUCTION, ElectronCount.of(electrons)),
                ElectrodePotential.ofVolts(potential),
                CONDITIONS,
                provenance,
                true
        );
    }

    private static HalfReactionParticipant p(String code, String display, String phase, int charge, String coefficient, HalfReactionParticipantSide side, Map<String, BigDecimal> formula) {
        return new HalfReactionParticipant(code, display, formula, new BigDecimal(coefficient), phase, charge, side);
    }

    private static Map<String, BigDecimal> m(String element, String count) {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        map.put(element, new BigDecimal(count));
        return map;
    }
}

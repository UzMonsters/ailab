package com.ailab.chemistry.infrastructure.persistence.solubility;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "solubility_dissolution_terms", schema = "chemistry")
public class JpaSolubilityDissolutionTermEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equilibrium_id", nullable = false)
    private JpaSolubilityEquilibriumEntity equilibrium;

    @Column(name = "term_order", nullable = false)
    private int termOrder;

    @Column(name = "species_code", nullable = false)
    private String speciesCode;

    @Column(name = "formula", nullable = false)
    private String formula;

    @Column(name = "charge", nullable = false)
    private int charge;

    @Column(name = "coefficient", nullable = false)
    private int coefficient;

    public int getTermOrder() { return termOrder; }
    public String getSpeciesCode() { return speciesCode; }
    public String getFormula() { return formula; }
    public int getCharge() { return charge; }
    public int getCoefficient() { return coefficient; }
}

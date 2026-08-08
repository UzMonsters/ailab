package com.ailab.chemistry.infrastructure.persistence.solubility;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "solubility_equilibria", schema = "chemistry")
public class JpaSolubilityEquilibriumEntity {
    @Id
    private UUID id;

    @Column(name = "equilibrium_code", nullable = false)
    private String equilibriumCode;

    @Column(name = "solid_compound_code", nullable = false)
    private String solidCompoundCode;

    @Column(name = "ksp_value", nullable = false)
    private BigDecimal kspValue;

    @Column(name = "temperature_celsius", nullable = false)
    private BigDecimal temperatureCelsius;

    @Column(name = "solvent_code", nullable = false)
    private String solventCode;

    @Column(name = "activity_convention", nullable = false)
    private String activityConvention;

    @Column(name = "dataset_version", nullable = false)
    private String datasetVersion;

    @Column(name = "source_identifier", nullable = false)
    private String sourceIdentifier;

    @Column(name = "citation", nullable = false)
    private String citation;

    @Column(name = "reuse_limitations", nullable = false)
    private String reuseLimitations;

    @OneToMany(mappedBy = "equilibrium", fetch = FetchType.EAGER)
    @OrderBy("termOrder ASC")
    private List<JpaSolubilityDissolutionTermEntity> terms;

    public String getEquilibriumCode() { return equilibriumCode; }
    public String getSolidCompoundCode() { return solidCompoundCode; }
    public BigDecimal getKspValue() { return kspValue; }
    public BigDecimal getTemperatureCelsius() { return temperatureCelsius; }
    public String getSolventCode() { return solventCode; }
    public String getActivityConvention() { return activityConvention; }
    public String getDatasetVersion() { return datasetVersion; }
    public String getSourceIdentifier() { return sourceIdentifier; }
    public String getCitation() { return citation; }
    public String getReuseLimitations() { return reuseLimitations; }
    public List<JpaSolubilityDissolutionTermEntity> getTerms() { return terms; }
}

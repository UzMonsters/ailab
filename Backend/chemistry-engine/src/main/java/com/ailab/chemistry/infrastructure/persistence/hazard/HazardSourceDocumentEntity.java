package com.ailab.chemistry.infrastructure.persistence.hazard;

import jakarta.persistence.*;

@Entity
@Table(name = "hazard_source_documents", schema = "chemistry")
public class HazardSourceDocumentEntity {

    @Id
    private String id;

    @Column(name = "document_type", nullable = false)
    private String documentType;

    @Column(name = "issuer_or_supplier", nullable = false)
    private String issuerOrSupplier;

    @Column(name = "document_title", nullable = false)
    private String documentTitle;

    @Column(name = "classification_system", nullable = false)
    private String classificationSystem;

    @Column(name = "revision_or_edition")
    private String revisionOrEdition;

    @Column(name = "jurisdiction")
    private String jurisdiction;

    public HazardSourceDocumentEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    public String getIssuerOrSupplier() { return issuerOrSupplier; }
    public void setIssuerOrSupplier(String issuerOrSupplier) { this.issuerOrSupplier = issuerOrSupplier; }
    public String getDocumentTitle() { return documentTitle; }
    public void setDocumentTitle(String documentTitle) { this.documentTitle = documentTitle; }
    public String getClassificationSystem() { return classificationSystem; }
    public void setClassificationSystem(String classificationSystem) { this.classificationSystem = classificationSystem; }
    public String getRevisionOrEdition() { return revisionOrEdition; }
    public void setRevisionOrEdition(String revisionOrEdition) { this.revisionOrEdition = revisionOrEdition; }
    public String getJurisdiction() { return jurisdiction; }
    public void setJurisdiction(String jurisdiction) { this.jurisdiction = jurisdiction; }
}

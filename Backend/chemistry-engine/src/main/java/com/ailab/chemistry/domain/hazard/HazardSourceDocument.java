package com.ailab.chemistry.domain.hazard;

import java.util.Objects;

public final class HazardSourceDocument {
    private final String sourceId;
    private final String documentType;
    private final String issuerOrSupplier;
    private final String documentTitle;
    private final String productIdentifier;
    private final HazardClassificationSystem classificationSystem;
    private final String revisionOrEdition;
    private final String documentRevisionDate;
    private final String accessDate;
    private final HazardJurisdiction jurisdiction;
    private final String language;
    private final String sourceLocationReference;
    private final String checksum;
    private final String reuseLicensingNote;

    public HazardSourceDocument(String sourceId, String documentType, String issuerOrSupplier, String documentTitle, String productIdentifier, HazardClassificationSystem classificationSystem, String revisionOrEdition, String documentRevisionDate, String accessDate, HazardJurisdiction jurisdiction, String language, String sourceLocationReference, String checksum, String reuseLicensingNote) {
        Objects.requireNonNull(sourceId, "SourceId cannot be null");
        Objects.requireNonNull(documentTitle, "DocumentTitle cannot be null");
        Objects.requireNonNull(classificationSystem, "ClassificationSystem cannot be null");
        this.sourceId = sourceId;
        this.documentType = documentType != null ? documentType : "AUTHORITATIVE_CLASSIFICATION";
        this.issuerOrSupplier = issuerOrSupplier != null ? issuerOrSupplier : "UN / ECHA / OSHA";
        this.documentTitle = documentTitle;
        this.productIdentifier = productIdentifier;
        this.classificationSystem = classificationSystem;
        this.revisionOrEdition = revisionOrEdition != null ? revisionOrEdition : "Rev 11";
        this.documentRevisionDate = documentRevisionDate != null ? documentRevisionDate : "2025-01-01";
        this.accessDate = accessDate != null ? accessDate : "2026-08-05";
        this.jurisdiction = jurisdiction != null ? jurisdiction : HazardJurisdiction.INTERNATIONAL_REFERENCE;
        this.language = language != null ? language : "en";
        this.sourceLocationReference = sourceLocationReference;
        this.checksum = checksum;
        this.reuseLicensingNote = reuseLicensingNote != null ? reuseLicensingNote : "Public domain / Sourced reference data";
    }

    public String getSourceId() { return sourceId; }
    public String getDocumentType() { return documentType; }
    public String getIssuerOrSupplier() { return issuerOrSupplier; }
    public String getDocumentTitle() { return documentTitle; }
    public String getProductIdentifier() { return productIdentifier; }
    public HazardClassificationSystem getClassificationSystem() { return classificationSystem; }
    public String getRevisionOrEdition() { return revisionOrEdition; }
    public String getDocumentRevisionDate() { return documentRevisionDate; }
    public String getAccessDate() { return accessDate; }
    public HazardJurisdiction getJurisdiction() { return jurisdiction; }
    public String getLanguage() { return language; }
    public String getSourceLocationReference() { return sourceLocationReference; }
    public String getChecksum() { return checksum; }
    public String getReuseLicensingNote() { return reuseLicensingNote; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HazardSourceDocument that = (HazardSourceDocument) o;
        return sourceId.equals(that.sourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceId);
    }
}

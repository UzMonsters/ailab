package com.ailab.chemistry.domain.compound;

import java.util.Objects;

public final class CompoundProvenance {
    private final String sourceIdentifier;
    private final String sourceTitle;
    private final String publisher;
    private final String datasetVersion;
    private final String accessDate;
    private final String licensingNote;

    public CompoundProvenance(String sourceIdentifier, String sourceTitle, String publisher, String datasetVersion, String accessDate, String licensingNote) {
        if (sourceIdentifier == null || sourceIdentifier.isBlank()) {
            throw new CompoundException(CompoundErrorCode.COMPOUND_PROVENANCE_MISSING, "Source identifier cannot be blank");
        }
        if (sourceTitle == null || sourceTitle.isBlank()) {
            throw new CompoundException(CompoundErrorCode.COMPOUND_PROVENANCE_MISSING, "Source title cannot be blank");
        }
        this.sourceIdentifier = sourceIdentifier.trim();
        this.sourceTitle = sourceTitle.trim();
        this.publisher = publisher != null ? publisher.trim() : "";
        this.datasetVersion = datasetVersion != null ? datasetVersion.trim() : "";
        this.accessDate = accessDate != null ? accessDate.trim() : "";
        this.licensingNote = licensingNote != null ? licensingNote.trim() : "";
    }

    public String getSourceIdentifier() {
        return sourceIdentifier;
    }

    public String getSourceTitle() {
        return sourceTitle;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getDatasetVersion() {
        return datasetVersion;
    }

    public String getAccessDate() {
        return accessDate;
    }

    public String getLicensingNote() {
        return licensingNote;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompoundProvenance that = (CompoundProvenance) o;
        return Objects.equals(sourceIdentifier, that.sourceIdentifier) && Objects.equals(datasetVersion, that.datasetVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceIdentifier, datasetVersion);
    }

    @Override
    public String toString() {
        return sourceIdentifier + " (" + sourceTitle + ")";
    }
}

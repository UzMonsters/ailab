package com.ailab.chemistry.domain.reaction;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ReactionProvenance {
    private final String sourceDocumentId;
    private final List<String> fieldsSupplied;
    private final String notes;

    public ReactionProvenance(String sourceDocumentId, List<String> fieldsSupplied, String notes) {
        Objects.requireNonNull(sourceDocumentId, "Source document ID must not be null");
        if (sourceDocumentId.isBlank()) {
            throw new ReactionException(ReactionErrorCode.REACTION_PROVENANCE_MISSING, "Source document ID must not be blank");
        }
        this.sourceDocumentId = sourceDocumentId.trim();
        this.fieldsSupplied = fieldsSupplied != null ? List.copyOf(fieldsSupplied) : Collections.emptyList();
        this.notes = notes != null ? notes.trim() : "";
    }

    public String getSourceDocumentId() {
        return sourceDocumentId;
    }

    public List<String> getFieldsSupplied() {
        return fieldsSupplied;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReactionProvenance that = (ReactionProvenance) o;
        return Objects.equals(sourceDocumentId, that.sourceDocumentId) &&
                Objects.equals(fieldsSupplied, that.fieldsSupplied) &&
                Objects.equals(notes, that.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceDocumentId, fieldsSupplied, notes);
    }
}

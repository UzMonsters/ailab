package com.ailab.chemistry.domain.hazard;

import java.util.Objects;

public final class PrecautionaryStatement {

    public enum PrecautionaryType {
        GENERAL,
        PREVENTION,
        RESPONSE,
        STORAGE,
        DISPOSAL
    }

    private final String statementCode;
    private final PrecautionaryType type;
    private final String statementText;
    private final HazardClassificationSystem system;
    private final String revision;
    private final String sourceDocumentId;

    public PrecautionaryStatement(String statementCode, PrecautionaryType type, String statementText, HazardClassificationSystem system, String revision, String sourceDocumentId) {
        Objects.requireNonNull(statementCode, "Statement code cannot be null");
        Objects.requireNonNull(type, "PrecautionaryType cannot be null");
        Objects.requireNonNull(statementText, "Statement text cannot be null");
        this.statementCode = statementCode;
        this.type = type;
        this.statementText = statementText;
        this.system = system != null ? system : HazardClassificationSystem.UN_GHS;
        this.revision = revision != null ? revision : "UN-GHS-REV11-2025";
        this.sourceDocumentId = sourceDocumentId != null ? sourceDocumentId : "UN-GHS-REV11-2025";
    }

    public String getStatementCode() { return statementCode; }
    public PrecautionaryType getType() { return type; }
    public String getStatementText() { return statementText; }
    public HazardClassificationSystem getSystem() { return system; }
    public String getRevision() { return revision; }
    public String getSourceDocumentId() { return sourceDocumentId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrecautionaryStatement that = (PrecautionaryStatement) o;
        return statementCode.equals(that.statementCode) && system == that.system && Objects.equals(revision, that.revision);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statementCode, system, revision);
    }
}

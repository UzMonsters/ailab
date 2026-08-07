package com.ailab.chemistry.domain.hazard;

import java.util.Objects;

public final class SafetyInstruction {
    private final SafetyInstructionType type;
    private final String text;
    private final String sourceDocumentId;
    private final String sourceSection;

    public SafetyInstruction(SafetyInstructionType type, String text, String sourceDocumentId, String sourceSection) {
        Objects.requireNonNull(type, "Type cannot be null");
        Objects.requireNonNull(text, "Text cannot be null");
        if (text.isBlank()) {
            throw new HazardException(HazardErrorCode.INVALID_SAFETY_INSTRUCTION, "SafetyInstruction text cannot be blank");
        }
        Objects.requireNonNull(sourceDocumentId, "Source document ID cannot be null");
        this.type = type;
        this.text = text;
        this.sourceDocumentId = sourceDocumentId;
        this.sourceSection = sourceSection != null ? sourceSection : "Section 7/8/13 SDS";
    }

    public SafetyInstructionType getType() { return type; }
    public String getText() { return text; }
    public String getSourceDocumentId() { return sourceDocumentId; }
    public String getSourceSection() { return sourceSection; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SafetyInstruction that = (SafetyInstruction) o;
        return type == that.type && text.equals(that.text) && sourceDocumentId.equals(that.sourceDocumentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, text, sourceDocumentId);
    }
}

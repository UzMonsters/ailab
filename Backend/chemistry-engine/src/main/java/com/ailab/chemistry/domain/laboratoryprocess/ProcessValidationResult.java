package com.ailab.chemistry.domain.laboratoryprocess;

import java.util.List;

public record ProcessValidationResult(List<ProcessValidationError> errors) {
    public ProcessValidationResult {
        errors = List.copyOf(errors == null ? List.of() : errors);
    }

    public boolean valid() {
        return errors.isEmpty();
    }

    public List<String> errorCodes() {
        return errors.stream().map(ProcessValidationError::code).distinct().toList();
    }
}

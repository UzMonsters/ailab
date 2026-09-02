package com.ailab.learning.exception;

public class LevelVersionChangedException extends RuntimeException {
    private final long expectedVersion;
    private final long actualVersion;

    public LevelVersionChangedException(String message, long expectedVersion, long actualVersion) {
        super(message);
        this.expectedVersion = expectedVersion;
        this.actualVersion = actualVersion;
    }

    public long getExpectedVersion() {
        return expectedVersion;
    }

    public long getActualVersion() {
        return actualVersion;
    }
}

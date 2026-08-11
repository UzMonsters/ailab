package com.ailab.workspace.exception;

public class VersionConflictException extends RuntimeException {
    private final long expectedVersion;
    private final long actualVersion;

    public VersionConflictException(long expectedVersion, long actualVersion) {
        super("Version conflict: expected " + expectedVersion + " but current state version is " + actualVersion);
        this.expectedVersion = expectedVersion;
        this.actualVersion = actualVersion;
    }

    public long getExpectedVersion() { return expectedVersion; }
    public long getActualVersion() { return actualVersion; }
}

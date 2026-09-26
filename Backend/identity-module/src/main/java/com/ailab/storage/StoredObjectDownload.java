package com.ailab.storage;

import java.io.IOException;
import java.io.InputStream;

public record StoredObjectDownload(
        String contentType,
        long sizeBytes,
        InputStream inputStream
) implements AutoCloseable {
    @Override
    public void close() throws IOException {
        inputStream.close();
    }
}

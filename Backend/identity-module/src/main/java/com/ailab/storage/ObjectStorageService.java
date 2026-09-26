package com.ailab.storage;

import java.net.URI;
import java.time.Duration;

public interface ObjectStorageService {
    StoredObject upload(StorageUpload upload);

    StoredObjectDownload download(String storageKey);

    boolean exists(String storageKey);

    void delete(String storageKey);

    URI createPresignedDownloadUrl(String storageKey, String downloadFileName, Duration ttl);
}

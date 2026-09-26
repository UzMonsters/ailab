package com.ailab.admin.assets;

import java.util.List;
import java.util.Map;

public interface AdminAssetService {
    default Map<String, Object> generateUploadUrls(List<Map<String, Object>> files) {
        return generateUploadUrls(files, "system");
    }

    Map<String, Object> generateUploadUrls(List<Map<String, Object>> files, String actorId);

    default Map<String, Object> completeAsset(String assetId, Map<String, Object> request) {
        return completeAsset(assetId, request, "system");
    }

    Map<String, Object> completeAsset(String assetId, Map<String, Object> request, String actorId);

    Map<String, Object> getAsset(String assetId);
}

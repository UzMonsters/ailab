package com.ailab.admin.assets;

import java.util.List;
import java.util.Map;

public interface AdminAssetService {
    Map<String, Object> generateUploadUrls(List<Map<String, Object>> files);
    Map<String, Object> completeAsset(String assetId, Map<String, Object> request);
    Map<String, Object> getAsset(String assetId);
}

package com.ailab.storage;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class StorageKeyFactory {
    private static final Pattern SAFE_SEGMENT = Pattern.compile("[A-Za-z0-9._-]+");

    public String assetKey(String assetId) {
        return "assets/" + safe(assetId);
    }

    public String avatarKey(String userId, String assetId) {
        return "avatars/" + safe(userId) + "/" + safe(assetId);
    }

    public String workspacePreviewKey(String workspaceId, String previewId, String assetId) {
        return "workspaces/" + safe(workspaceId) + "/previews/" + safe(previewId) + "/" + safe(assetId);
    }

    private String safe(String segment) {
        if (segment == null || segment.isBlank() || !SAFE_SEGMENT.matcher(segment).matches()) {
            throw new IllegalArgumentException("Invalid storage key segment");
        }
        return segment;
    }
}

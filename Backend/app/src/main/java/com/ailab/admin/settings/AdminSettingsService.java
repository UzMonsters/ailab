package com.ailab.admin.settings;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface AdminSettingsService {

    Map<String, Object> getSettings();

    Map<String, Object> patchSettings(Map<String, Object> patch, String ifMatch, String actorId, String actorName);

    Map<String, Object> getSchema(String locale);

    Map<String, Object> getHistory(int page, int size, Instant from, Instant to, String actorId);

    Map<String, Object> restoreVersion(Long version, String reason, String actorId, String actorName);

    List<Map<String, Object>> getSubjects();

    Map<String, Object> patchSubject(String id, Map<String, Object> patch, String actorId, String actorName);
}

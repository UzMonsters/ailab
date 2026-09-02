package com.ailab.admin.catalog;

import java.util.Map;

public interface AdminCatalogService {

    Map<String, Object> listDrafts(String entityType, int page, int size, String q, String status, String sort);

    Map<String, Object> getDraft(String entityType, String id);

    Map<String, Object> createDraft(String entityType, Map<String, Object> body, String actorId, String actorName);

    Map<String, Object> patchDraft(String entityType, String id, Map<String, Object> patch, String ifMatch, String actorId, String actorName);

    Map<String, Object> savePorts(String id, Map<String, Object> request, String ifMatch, String actorId, String actorName);

    Map<String, Object> saveCompatibility(String id, Map<String, Object> request, String ifMatch, String actorId, String actorName);

    Map<String, Object> validateDraft(String entityType, String id, Long version);

    Map<String, Object> publishDraft(String entityType, String id, Long version, String idempotencyKey, String actorId, String actorName);
}

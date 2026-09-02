package com.ailab.admin.laboratory;

import java.time.Instant;
import java.util.Map;

public interface AdminLaboratoryMonitoringService {

    Map<String, Object> getSessions(int page, int size, String q, String science, String status, String ownerId, Instant startedFrom);

    Map<String, Object> getSessionDetails(String id);

    Map<String, Object> pauseSession(String id, String reason, String actorId, String actorName);

    Map<String, Object> terminateSession(String id, String reason, boolean notifyOwner, String actorId, String actorName);
}

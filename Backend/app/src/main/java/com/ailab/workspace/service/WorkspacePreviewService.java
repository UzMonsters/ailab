package com.ailab.workspace.service;

import com.ailab.workspace.domain.WorkspaceEntity;
import com.ailab.workspace.domain.WorkspacePreviewEntity;
import com.ailab.workspace.dto.*;
import com.ailab.workspace.repository.WorkspacePreviewRepository;
import com.ailab.workspace.repository.WorkspaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;

@Service
public class WorkspacePreviewService {

    private final WorkspacePreviewRepository previewRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberService memberService;

    public WorkspacePreviewService(
            WorkspacePreviewRepository previewRepository,
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberService memberService
    ) {
        this.previewRepository = previewRepository;
        this.workspaceRepository = workspaceRepository;
        this.memberService = memberService;
    }

    public PreviewUploadUrlsResponse createUploadUrls(String workspaceId, String actorUserId, PreviewUploadUrlsRequest request) {
        memberService.requirePermission(workspaceId, actorUserId, "EDIT_SCENE");
        WorkspaceEntity ws = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found: " + workspaceId));

        long stateVer = request.sourceStateVersion() != null ? request.sourceStateVersion() : ws.getStateVersion();
        String previewId = "prev_" + UUID.randomUUID().toString().substring(0, 12);

        List<PreviewUploadUrlsResponse.UploadTarget> uploads = new ArrayList<>();
        List<PreviewUploadUrlsRequest.VariantRequest> variants = request.variants() != null ? request.variants() : List.of(
                new PreviewUploadUrlsRequest.VariantRequest("DARK", "image/webp", 960, 540, null),
                new PreviewUploadUrlsRequest.VariantRequest("LIGHT", "image/webp", 960, 540, null)
        );

        for (PreviewUploadUrlsRequest.VariantRequest v : variants) {
            String theme = v.theme() != null ? v.theme().toUpperCase() : "DARK";
            String assetId = "asset_" + theme.toLowerCase() + "_" + previewId;
            String uploadUrl = "/api/v1/workspaces/" + workspaceId + "/previews/" + previewId + "/assets/" + assetId + "/upload";
            uploads.add(new PreviewUploadUrlsResponse.UploadTarget(theme, assetId, uploadUrl, Instant.now().plusSeconds(900)));
        }

        return new PreviewUploadUrlsResponse(previewId, stateVer, uploads);
    }

    @Transactional
    public WorkspacePreviewDto completePreview(String workspaceId, String actorUserId, String previewId, CompletePreviewRequest request) {
        memberService.requirePermission(workspaceId, actorUserId, "EDIT_SCENE");
        WorkspaceEntity ws = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found: " + workspaceId));

        if (request.sourceStateVersion() != null && request.sourceStateVersion() < ws.getStateVersion()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "STALE_PREVIEW: sourceStateVersion " + request.sourceStateVersion() + " is older than current version " + ws.getStateVersion());
        }

        String darkUrl = null;
        String lightUrl = null;

        if (request.assets() != null) {
            for (CompletePreviewRequest.AssetResult a : request.assets()) {
                if ("DARK".equalsIgnoreCase(a.theme())) darkUrl = a.url();
                if ("LIGHT".equalsIgnoreCase(a.theme())) lightUrl = a.url();
            }
        }

        if (darkUrl == null && lightUrl != null) darkUrl = lightUrl;
        if (lightUrl == null && darkUrl != null) lightUrl = darkUrl;

        WorkspacePreviewEntity preview = new WorkspacePreviewEntity(
                previewId, workspaceId,
                request.sourceStateVersion() != null ? request.sourceStateVersion() : ws.getStateVersion(),
                "READY", darkUrl, lightUrl, request.fallbackKey()
        );
        previewRepository.save(preview);

        // Also update workspace thumbnail string for backward compatibility
        if (darkUrl != null) {
            ws.setThumbnail(darkUrl);
            workspaceRepository.save(ws);
        }

        return WorkspacePreviewDto.of(preview.getSourceStateVersion(), preview.getDarkUrl(), preview.getLightUrl(), preview.getFallbackKey());
    }

    public WorkspacePreviewDto getPreview(String workspaceId, String actorUserId) {
        memberService.requirePermission(workspaceId, actorUserId, "READ_WORKSPACE");
        Optional<WorkspacePreviewEntity> previewOpt = previewRepository.findTopByWorkspaceIdOrderBySourceStateVersionDesc(workspaceId);
        if (previewOpt.isPresent()) {
            WorkspacePreviewEntity p = previewOpt.get();
            return WorkspacePreviewDto.of(p.getSourceStateVersion(), p.getDarkUrl(), p.getLightUrl(), p.getFallbackKey());
        }
        WorkspaceEntity ws = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found: " + workspaceId));
        if (ws.getThumbnail() != null) {
            return WorkspacePreviewDto.of(ws.getStateVersion(), ws.getThumbnail(), ws.getThumbnail(), "chemistry-default-01");
        }
        return WorkspacePreviewDto.fallback("chemistry-default-01");
    }
}

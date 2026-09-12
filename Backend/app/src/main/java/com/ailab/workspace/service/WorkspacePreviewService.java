package com.ailab.workspace.service;

import com.ailab.workspace.domain.WorkspaceEntity;
import com.ailab.workspace.domain.WorkspacePreviewEntity;
import com.ailab.workspace.dto.*;
import com.ailab.workspace.repository.WorkspacePreviewRepository;
import com.ailab.workspace.repository.WorkspaceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WorkspacePreviewService {

    private final WorkspacePreviewRepository previewRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberService memberService;
    private final Map<String, PendingPreviewAsset> pendingAssets = new ConcurrentHashMap<>();
    private final Path assetRoot;

    public WorkspacePreviewService(
            WorkspacePreviewRepository previewRepository,
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberService memberService
    ) {
        this(previewRepository, workspaceRepository, memberService, "./storage/previews");
    }

    @org.springframework.beans.factory.annotation.Autowired
    public WorkspacePreviewService(
            WorkspacePreviewRepository previewRepository,
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberService memberService,
            @org.springframework.beans.factory.annotation.Value("${app.storage.preview-dir:./storage/previews}") String previewDir
    ) {
        this.previewRepository = previewRepository;
        this.workspaceRepository = workspaceRepository;
        this.memberService = memberService;
        Path root = Path.of(previewDir != null ? previewDir : "./storage/previews");
        try {
            Files.createDirectories(root);
        } catch (Exception ignored) {}
        this.assetRoot = root;
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

        Instant expiresAt = Instant.now().plusSeconds(900);
        for (PreviewUploadUrlsRequest.VariantRequest v : variants) {
            String theme = v.theme() != null ? v.theme().toUpperCase() : "DARK";
            String assetId = "asset_" + theme.toLowerCase() + "_" + previewId;
            String uploadUrl = "/api/v1/workspaces/" + workspaceId + "/previews/" + previewId + "/assets/" + assetId + "/upload";
            pendingAssets.put(assetKey(workspaceId, previewId, assetId), new PendingPreviewAsset(
                    workspaceId,
                    previewId,
                    assetId,
                    theme,
                    v.mimeType() != null ? v.mimeType() : "image/webp",
                    v.width(),
                    v.height(),
                    v.checksum(),
                    null,
                    null,
                    expiresAt
            ));
            uploads.add(new PreviewUploadUrlsResponse.UploadTarget(theme, assetId, uploadUrl, expiresAt));
        }

        previewRepository.save(new WorkspacePreviewEntity(previewId, workspaceId, stateVer, "PROCESSING", null, null, null));
        return new PreviewUploadUrlsResponse(previewId, stateVer, uploads);
    }

    public Map<String, Object> uploadAsset(String workspaceId, String actorUserId, String previewId, String assetId, byte[] bytes, String contentType) {
        memberService.requirePermission(workspaceId, actorUserId, "EDIT_SCENE");
        PendingPreviewAsset pending = pendingAssets.get(assetKey(workspaceId, previewId, assetId));
        if (pending == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PREVIEW_UPLOAD_NOT_FOUND: Preview upload target not found");
        }
        if (pending.expiresAt() != null && pending.expiresAt().isBefore(Instant.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "PREVIEW_UPLOAD_EXPIRED: Preview upload ticket has expired");
        }
        if (bytes == null || bytes.length == 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "PREVIEW_UPLOAD_EMPTY: Preview asset body is empty");
        }

        String detectedMime = com.ailab.admin.assets.AssetUploadTicketService.detectMimeType(bytes);
        if (detectedMime == null || !detectedMime.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "INVALID_PREVIEW_ASSET: Asset is not a valid image format");
        }

        try {
            Files.createDirectories(assetRoot.resolve(workspaceId).resolve(previewId));
            Path path = assetPath(workspaceId, previewId, assetId);
            Files.write(path, bytes);
            PendingPreviewAsset uploaded = pending.withUpload(path, checksum(bytes));
            pendingAssets.put(assetKey(workspaceId, previewId, assetId), uploaded);
            return Map.of(
                    "assetId", assetId,
                    "previewId", previewId,
                    "checksum", uploaded.actualChecksum(),
                    "url", assetUrl(workspaceId, previewId, assetId),
                    "uploadedAt", Instant.now().toString()
            );
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "PREVIEW_UPLOAD_FAILED: Failed to store preview asset");
        }
    }

    @Transactional
    public WorkspacePreviewDto completePreview(String workspaceId, String actorUserId, String previewId, CompletePreviewRequest request) {
        memberService.requirePermission(workspaceId, actorUserId, "EDIT_SCENE");
        WorkspaceEntity ws = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found: " + workspaceId));

        if (request.sourceStateVersion() != null && request.sourceStateVersion() < ws.getStateVersion()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "STALE_PREVIEW: sourceStateVersion " + request.sourceStateVersion() + " is older than current version " + ws.getStateVersion());
        }

        WorkspacePreviewEntity existing = previewRepository.findByIdAndWorkspaceId(previewId, workspaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PREVIEW_UPLOAD_NOT_FOUND: Preview not found"));
        if ("READY".equalsIgnoreCase(existing.getStatus())) {
            return WorkspacePreviewDto.of(existing.getSourceStateVersion(), existing.getDarkUrl(), existing.getLightUrl(), existing.getFallbackKey());
        }

        String darkUrl = null;
        String lightUrl = null;
        if (request.assets() != null) {
            for (CompletePreviewRequest.AssetResult a : request.assets()) {
                PendingPreviewAsset uploaded = pendingAssets.get(assetKey(workspaceId, previewId, a.assetId()));
                if (uploaded == null || uploaded.path() == null || !Files.exists(uploaded.path())) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "PREVIEW_UPLOAD_INCOMPLETE: Asset has not been uploaded: " + a.assetId());
                }
                String requestedChecksum = a.checksum() != null ? a.checksum() : uploaded.expectedChecksum();
                if (requestedChecksum != null && !requestedChecksum.isBlank()
                        && !normalizeChecksum(requestedChecksum).equals(normalizeChecksum(uploaded.actualChecksum()))) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "CHECKSUM_MISMATCH: Preview asset checksum mismatch");
                }
                String url = assetUrl(workspaceId, previewId, a.assetId());
                if ("DARK".equalsIgnoreCase(a.theme()) || "DARK".equalsIgnoreCase(uploaded.theme())) darkUrl = url;
                if ("LIGHT".equalsIgnoreCase(a.theme()) || "LIGHT".equalsIgnoreCase(uploaded.theme())) lightUrl = url;
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

    public ResponseEntity<ByteArrayResource> getAsset(String workspaceId, String actorUserId, String previewId, String assetId) {
        memberService.requirePermission(workspaceId, actorUserId, "READ_WORKSPACE");
        Path path = assetPath(workspaceId, previewId, assetId);
        if (!Files.exists(path)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PREVIEW_ASSET_NOT_FOUND: Preview asset not found");
        }
        try {
            PendingPreviewAsset pending = pendingAssets.get(assetKey(workspaceId, previewId, assetId));
            MediaType mediaType = MediaType.parseMediaType(pending != null ? pending.mimeType() : "image/webp");
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(new ByteArrayResource(Files.readAllBytes(path)));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "PREVIEW_ASSET_READ_FAILED: Failed to read preview asset");
        }
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

    private String assetKey(String workspaceId, String previewId, String assetId) {
        return workspaceId + ":" + previewId + ":" + assetId;
    }

    private Path assetPath(String workspaceId, String previewId, String assetId) {
        String safeAssetId = assetId.replaceAll("[^A-Za-z0-9_.-]", "_");
        return assetRoot.resolve(workspaceId).resolve(previewId).resolve(safeAssetId);
    }

    private String assetUrl(String workspaceId, String previewId, String assetId) {
        return "/api/v1/workspaces/" + workspaceId + "/previews/" + previewId + "/assets/" + assetId;
    }

    private String checksum(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return "sha256:" + hex;
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    private String normalizeChecksum(String value) {
        return value == null ? "" : (value.startsWith("sha256:") ? value : "sha256:" + value).toLowerCase(Locale.ROOT);
    }

    private record PendingPreviewAsset(
            String workspaceId,
            String previewId,
            String assetId,
            String theme,
            String mimeType,
            Integer width,
            Integer height,
            String expectedChecksum,
            Path path,
            String actualChecksum,
            Instant expiresAt
    ) {
        PendingPreviewAsset withUpload(Path path, String checksum) {
            return new PendingPreviewAsset(workspaceId, previewId, assetId, theme, mimeType, width, height, expectedChecksum, path, checksum, expiresAt);
        }
    }
}

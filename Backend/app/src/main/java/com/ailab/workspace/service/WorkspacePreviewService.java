package com.ailab.workspace.service;

import com.ailab.storage.LocalObjectStorageService;
import com.ailab.storage.ObjectStorageService;
import com.ailab.storage.StorageKeyFactory;
import com.ailab.storage.StorageProperties;
import com.ailab.storage.StorageUpload;
import com.ailab.storage.StoredObjectDownload;
import com.ailab.storage.upload.InspectedUpload;
import com.ailab.storage.upload.TransientUploadTicketStore;
import com.ailab.storage.upload.UploadContentInspector;
import com.ailab.storage.upload.UploadScope;
import com.ailab.storage.upload.UploadTicketClaimCommand;
import com.ailab.storage.upload.UploadTicketEntity;
import com.ailab.storage.upload.UploadTicketIssueCommand;
import com.ailab.storage.upload.UploadTicketService;
import com.ailab.workspace.domain.WorkspaceEntity;
import com.ailab.workspace.domain.WorkspacePreviewEntity;
import com.ailab.workspace.dto.CompletePreviewRequest;
import com.ailab.workspace.dto.PreviewUploadUrlsRequest;
import com.ailab.workspace.dto.PreviewUploadUrlsResponse;
import com.ailab.workspace.dto.WorkspacePreviewDto;
import com.ailab.workspace.repository.WorkspacePreviewRepository;
import com.ailab.workspace.repository.WorkspaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.unit.DataSize;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class WorkspacePreviewService {

    private static final Logger log = LoggerFactory.getLogger(WorkspacePreviewService.class);
    private static final List<String> ALLOWED_PREVIEW_MIME_TYPES = List.of("image/webp", "image/png", "image/jpeg");
    private static final Duration UPLOAD_TTL = Duration.ofMinutes(15);

    private final WorkspacePreviewRepository previewRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberService memberService;
    private final UploadTicketService uploadTicketService;
    private final ObjectStorageService objectStorageService;
    private final StorageKeyFactory storageKeyFactory;
    private final StorageProperties storageProperties;
    private final Path assetRoot;

    public WorkspacePreviewService(
            WorkspacePreviewRepository previewRepository,
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberService memberService
    ) {
        this(previewRepository, workspaceRepository, memberService, "./storage/previews");
    }

    public WorkspacePreviewService(
            WorkspacePreviewRepository previewRepository,
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberService memberService,
            String previewDir
    ) {
        this(previewRepository, workspaceRepository, memberService, previewDir,
                null, null, null, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public WorkspacePreviewService(
            WorkspacePreviewRepository previewRepository,
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberService memberService,
            @org.springframework.beans.factory.annotation.Value("${app.storage.preview-dir:./storage/previews}") String previewDir,
            @org.springframework.beans.factory.annotation.Autowired(required = false) UploadTicketService uploadTicketService,
            @org.springframework.beans.factory.annotation.Autowired(required = false) ObjectStorageService objectStorageService,
            @org.springframework.beans.factory.annotation.Autowired(required = false) StorageKeyFactory storageKeyFactory,
            @org.springframework.beans.factory.annotation.Autowired(required = false) StorageProperties storageProperties
    ) {
        this.previewRepository = previewRepository;
        this.workspaceRepository = workspaceRepository;
        this.memberService = memberService;
        this.assetRoot = initializeAssetRoot(previewDir);
        this.uploadTicketService = uploadTicketService != null
                ? uploadTicketService
                : new UploadTicketService(new TransientUploadTicketStore());
        this.objectStorageService = objectStorageService != null
                ? objectStorageService
                : new LocalObjectStorageService(this.assetRoot.toString());
        this.storageKeyFactory = storageKeyFactory != null ? storageKeyFactory : new StorageKeyFactory();
        this.storageProperties = storageProperties != null ? storageProperties : defaultStorageProperties(this.assetRoot);
    }

    public Path getAssetRoot() {
        return assetRoot;
    }

    public PreviewUploadUrlsResponse createUploadUrls(String workspaceId, String actorUserId, PreviewUploadUrlsRequest request) {
        memberService.requirePermission(workspaceId, actorUserId, "EDIT_SCENE");
        WorkspaceEntity ws = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found: " + workspaceId));

        long stateVer = request.sourceStateVersion() != null ? request.sourceStateVersion() : ws.getStateVersion();
        String previewId = "prev_" + UUID.randomUUID().toString().substring(0, 12);
        long maxPreviewBytes = storageProperties.maxPreviewSize().toBytes();

        List<PreviewUploadUrlsResponse.UploadTarget> uploads = new ArrayList<>();
        List<PreviewUploadUrlsRequest.VariantRequest> variants = request.variants() != null ? request.variants() : List.of(
                new PreviewUploadUrlsRequest.VariantRequest("DARK", "image/webp", 960, 540, null),
                new PreviewUploadUrlsRequest.VariantRequest("LIGHT", "image/webp", 960, 540, null)
        );

        for (PreviewUploadUrlsRequest.VariantRequest v : variants) {
            String theme = normalizeTheme(v.theme());
            String mimeType = normalizeMime(v.mimeType() != null ? v.mimeType() : "image/webp");
            if (!ALLOWED_PREVIEW_MIME_TYPES.contains(mimeType)) {
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                        "INVALID_PREVIEW_ASSET: Preview assets must be image/webp, image/png, or image/jpeg");
            }
            String assetId = "asset_" + theme.toLowerCase(Locale.ROOT) + "_" + previewId;
            IssuedPreviewTicket ticket = issuePreviewTicket(workspaceId, actorUserId, previewId, assetId, theme, mimeType,
                    maxPreviewBytes, v.checksum());
            String uploadUrl = "/api/v1/workspaces/" + workspaceId + "/previews/" + previewId + "/assets/" + assetId
                    + "/upload?ticket=" + ticket.token();
            uploads.add(new PreviewUploadUrlsResponse.UploadTarget(theme, assetId, uploadUrl, ticket.expiresAt()));
        }

        previewRepository.save(new WorkspacePreviewEntity(previewId, workspaceId, stateVer, "PROCESSING", null, null, null));
        return new PreviewUploadUrlsResponse(previewId, stateVer, uploads);
    }

    public Map<String, Object> uploadAsset(String workspaceId, String actorUserId, String previewId, String assetId,
                                           byte[] bytes, String contentType) {
        return uploadAsset(workspaceId, actorUserId, previewId, assetId,
                new ByteArrayInputStream(bytes != null ? bytes : new byte[0]),
                bytes != null ? bytes.length : 0L,
                contentType,
                null);
    }

    public Map<String, Object> uploadAsset(String workspaceId, String actorUserId, String previewId, String assetId,
                                           InputStream body, long contentLength, String contentType, String rawTicket) {
        memberService.requirePermission(workspaceId, actorUserId, "EDIT_SCENE");
        UploadTicketEntity ticket = resolvePreviewTicket(rawTicket, assetId, actorUserId, workspaceId, previewId);
        InspectedUpload inspected = UploadContentInspector.inspect(
                body,
                contentLength,
                contentType,
                ticket.getAllowedMime(),
                ticket.getMaxSizeBytes(),
                ticket.getExpectedChecksum()
        );
        objectStorageService.upload(new StorageUpload(
                ticket.getStorageKey(),
                inspected.detectedMime(),
                inspected.bytes().length,
                new ByteArrayInputStream(inspected.bytes())
        ));
        try {
            UploadTicketEntity uploaded = uploadTicketService.markUploaded(ticket, inspected.sha256(), inspected.bytes().length, inspected.detectedMime());
            return Map.of(
                    "assetId", assetId,
                    "previewId", previewId,
                    "checksum", uploaded.getActualChecksum(),
                    "url", assetUrl(workspaceId, previewId, assetId),
                    "uploadedAt", Objects.toString(uploaded.getUploadedAt(), "")
            );
        } catch (RuntimeException exception) {
            objectStorageService.delete(ticket.getStorageKey());
            throw exception;
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
                String theme = normalizeTheme(a.theme());
                UploadTicketEntity uploaded = uploadTicketService.validateForComplete(
                        a.assetId(),
                        actorUserId,
                        UploadScope.WORKSPACE_PREVIEW,
                        workspaceId,
                        previewId,
                        theme
                );
                if (!objectStorageService.exists(uploaded.getStorageKey())) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                            "PREVIEW_UPLOAD_INCOMPLETE: Asset has not been uploaded: " + a.assetId());
                }
                String requestedChecksum = a.checksum() != null ? a.checksum() : uploaded.getExpectedChecksum();
                if (requestedChecksum != null && !requestedChecksum.isBlank()
                        && !normalizeChecksum(requestedChecksum).equals(normalizeChecksum(uploaded.getActualChecksum()))) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "CHECKSUM_MISMATCH: Preview asset checksum mismatch");
                }
                String url = assetUrl(workspaceId, previewId, a.assetId());
                if ("DARK".equals(theme)) {
                    darkUrl = url;
                } else if ("LIGHT".equals(theme)) {
                    lightUrl = url;
                }
                uploadTicketService.markCompleted(uploaded);
            }
        }

        if (darkUrl == null && lightUrl != null) darkUrl = lightUrl;
        if (lightUrl == null && darkUrl != null) lightUrl = darkUrl;

        WorkspacePreviewEntity preview = new WorkspacePreviewEntity(
                previewId,
                workspaceId,
                request.sourceStateVersion() != null ? request.sourceStateVersion() : ws.getStateVersion(),
                "READY",
                darkUrl,
                lightUrl,
                request.fallbackKey()
        );
        previewRepository.save(preview);

        if (darkUrl != null) {
            ws.setThumbnail(darkUrl);
            workspaceRepository.save(ws);
        }

        return WorkspacePreviewDto.of(preview.getSourceStateVersion(), preview.getDarkUrl(), preview.getLightUrl(), preview.getFallbackKey());
    }

    public ResponseEntity<ByteArrayResource> getAsset(String workspaceId, String actorUserId, String previewId, String assetId) {
        memberService.requirePermission(workspaceId, actorUserId, "READ_WORKSPACE");
        UploadTicketEntity ticket = uploadTicketService.findLatestByAssetId(assetId);
        if (ticket.getScope() != UploadScope.WORKSPACE_PREVIEW
                || !Objects.equals(ticket.getWorkspaceId(), workspaceId)
                || !Objects.equals(ticket.getPreviewId(), previewId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "PREVIEW_ASSET_NOT_FOUND: Preview asset not found");
        }
        try (StoredObjectDownload download = objectStorageService.download(ticket.getStorageKey())) {
            MediaType mediaType = MediaType.parseMediaType(
                    download.contentType() != null ? download.contentType() : "image/webp");
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .contentLength(download.sizeBytes())
                    .body(new ByteArrayResource(download.inputStream().readAllBytes()));
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "PREVIEW_ASSET_READ_FAILED: Failed to read preview asset", exception);
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

    private UploadTicketEntity resolvePreviewTicket(String rawTicket, String assetId, String actorUserId,
                                                    String workspaceId, String previewId) {
        UploadTicketEntity candidate = uploadTicketService.findByRawToken(rawTicket);
        return uploadTicketService.claimForUpload(new UploadTicketClaimCommand(
                rawTicket,
                assetId,
                actorUserId,
                UploadScope.WORKSPACE_PREVIEW,
                workspaceId,
                previewId,
                candidate.getVariant()
        ));
    }

    private IssuedPreviewTicket issuePreviewTicket(String workspaceId, String actorUserId, String previewId,
                                                   String assetId, String theme, String mimeType,
                                                   long maxPreviewBytes, String checksum) {
        var issued = uploadTicketService.issue(new UploadTicketIssueCommand(
                assetId,
                actorUserId,
                UploadScope.WORKSPACE_PREVIEW,
                storageKeyFactory.workspacePreviewKey(workspaceId, previewId, assetId),
                mimeType,
                maxPreviewBytes,
                checksum,
                UPLOAD_TTL,
                workspaceId,
                previewId,
                theme
        ));
        return new IssuedPreviewTicket(issued.token(), issued.expiresAt());
    }

    private Path initializeAssetRoot(String previewDir) {
        try {
            Path root = Path.of(previewDir != null && !previewDir.isBlank() ? previewDir : "./storage/previews");
            Files.createDirectories(root);
            log.info("Initialized workspace preview storage directory at: {}", root.toAbsolutePath());
            return root;
        } catch (Exception e) {
            log.warn("Failed to create configured preview storage directory for path '{}': {}. Falling back to system temp directory.",
                    previewDir, e.getMessage());
            Path fallback = Path.of(System.getProperty("java.io.tmpdir"), "ailab-storage", "previews");
            try {
                Files.createDirectories(fallback);
                log.info("Workspace preview storage gracefully initialized with fallback directory at: {}", fallback.toAbsolutePath());
                return fallback;
            } catch (Exception fallbackEx) {
                log.error("Failed to create fallback preview storage directory at '{}'", fallback, fallbackEx);
                return Path.of("./storage/previews");
            }
        }
    }

    private StorageProperties defaultStorageProperties(Path root) {
        return new StorageProperties("local", null, "us-east-1", "ailab-local", null, null, false, false,
                Duration.ofMinutes(10), DataSize.ofMegabytes(10), DataSize.ofMegabytes(2), DataSize.ofMegabytes(10),
                root.toString());
    }

    private String assetUrl(String workspaceId, String previewId, String assetId) {
        return "/api/v1/workspaces/" + workspaceId + "/previews/" + previewId + "/assets/" + assetId;
    }

    private String normalizeTheme(String theme) {
        if (theme == null || theme.isBlank()) {
            return "DARK";
        }
        return theme.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeMime(String value) {
        return value == null ? "" : value.split(";")[0].trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeChecksum(String value) {
        return value == null ? "" : (value.startsWith("sha256:") ? value : "sha256:" + value).toLowerCase(Locale.ROOT);
    }

    private record IssuedPreviewTicket(String token, java.time.Instant expiresAt) {
    }
}

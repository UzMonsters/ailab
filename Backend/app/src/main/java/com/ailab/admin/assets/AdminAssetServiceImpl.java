package com.ailab.admin.assets;

import com.ailab.book.domain.AssetKind;
import com.ailab.book.domain.AssetStatus;
import com.ailab.book.domain.BookAsset;
import com.ailab.book.repository.BookAssetRepository;
import com.ailab.storage.LocalObjectStorageService;
import com.ailab.storage.ObjectStorageService;
import com.ailab.storage.StorageKeyFactory;
import com.ailab.storage.StorageProperties;
import com.ailab.storage.upload.IssuedUploadTicket;
import com.ailab.storage.upload.TransientUploadTicketStore;
import com.ailab.storage.upload.UploadScope;
import com.ailab.storage.upload.UploadTicketEntity;
import com.ailab.storage.upload.UploadTicketIssueCommand;
import com.ailab.storage.upload.UploadTicketService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.*;

@Service
public class AdminAssetServiceImpl implements AdminAssetService {

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/png",
            "image/webp",
            "image/svg+xml",
            "image/jpeg",
            "application/pdf",
            "application/json"
    );

    private final BookAssetRepository assetRepository;
    private final ObjectStorageService objectStorageService;
    private final UploadTicketService uploadTicketService;
    private final StorageKeyFactory storageKeyFactory;
    private final long maxAssetBytes;

    public AdminAssetServiceImpl() {
        this(null,
                new LocalObjectStorageService("./storage/objects"),
                new UploadTicketService(new TransientUploadTicketStore()),
                new StorageKeyFactory(),
                5_242_880L);
    }

    public AdminAssetServiceImpl(BookAssetRepository assetRepository) {
        this(assetRepository,
                new LocalObjectStorageService("./storage/objects"),
                new UploadTicketService(new TransientUploadTicketStore()),
                new StorageKeyFactory(),
                5_242_880L);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public AdminAssetServiceImpl(BookAssetRepository assetRepository,
                                 ObjectStorageService objectStorageService,
                                 UploadTicketService uploadTicketService,
                                 StorageKeyFactory storageKeyFactory,
                                 StorageProperties storageProperties) {
        this(assetRepository,
                objectStorageService,
                uploadTicketService,
                storageKeyFactory,
                storageProperties.maxAssetSize().toBytes());
    }

    private AdminAssetServiceImpl(BookAssetRepository assetRepository,
                                  ObjectStorageService objectStorageService,
                                  UploadTicketService uploadTicketService,
                                  StorageKeyFactory storageKeyFactory,
                                  long maxAssetBytes) {
        this.assetRepository = assetRepository;
        this.objectStorageService = objectStorageService;
        this.uploadTicketService = uploadTicketService;
        this.storageKeyFactory = storageKeyFactory;
        this.maxAssetBytes = maxAssetBytes;
    }

    @Override
    @Transactional
    public Map<String, Object> generateUploadUrls(List<Map<String, Object>> files, String actorId) {
        if (files == null || files.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR: Files list cannot be empty");
        }

        String effectiveActor = requireActor(actorId);
        List<Map<String, Object>> uploads = new ArrayList<>();

        for (Map<String, Object> file : files) {
            String filename = file.get("filename") != null ? String.valueOf(file.get("filename"))
                    : (file.get("name") != null ? String.valueOf(file.get("name")) : "unnamed_file");
            String contentType = file.get("contentType") != null ? String.valueOf(file.get("contentType")).toLowerCase()
                    : (file.get("mimeType") != null ? String.valueOf(file.get("mimeType")).toLowerCase() : "application/octet-stream");
            long sizeBytes = file.get("sizeBytes") != null ? ((Number) file.get("sizeBytes")).longValue()
                    : (file.get("size") != null ? ((Number) file.get("size")).longValue() : 0L);
            String kindStr = file.get("kind") != null ? String.valueOf(file.get("kind")) : null;
            String checksum = file.get("checksum") != null ? String.valueOf(file.get("checksum")) : null;

            if (!ALLOWED_MIME_TYPES.contains(contentType)) {
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE: MIME type not allowed: " + contentType);
            }
            if (sizeBytes > maxAssetBytes) {
                throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "ASSET_TOO_LARGE: File size exceeds maximum limit of " + maxAssetBytes + " bytes");
            }

            String fileId = "ast_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            IssuedUploadTicket ticket = uploadTicketService.issue(new UploadTicketIssueCommand(
                    fileId,
                    effectiveActor,
                    UploadScope.ADMIN_ASSET,
                    storageKeyFactory.assetKey(fileId),
                    contentType,
                    maxAssetBytes,
                    checksum,
                    Duration.ofHours(1),
                    null,
                    null,
                    null));
            String uploadUrl = "/api/v1/assets/upload/" + fileId + "?ticket=" + ticket.token();
            String downloadUrl = "/api/v1/assets/raw/" + fileId + "/" + filename;

            if (assetRepository != null) {
                AssetKind kind = "SVG".equalsIgnoreCase(kindStr) || contentType.contains("svg") ? AssetKind.SVG : AssetKind.IMAGE;
                BookAsset asset = new BookAsset(fileId, kind, contentType, sizeBytes, checksum, uploadUrl, downloadUrl);
                asset.setStatus(AssetStatus.PENDING);
                if (file.get("theme") != null) {
                    asset.setVariants(Map.of("theme", file.get("theme")));
                }
                assetRepository.save(asset);
            }

            uploads.add(Map.of(
                    "fileId", fileId,
                    "assetId", fileId,
                    "filename", filename,
                    "contentType", contentType,
                    "uploadUrl", uploadUrl,
                    "downloadUrl", downloadUrl,
                    "expiresAt", ticket.expiresAt()
            ));
        }

        return Map.of("uploads", uploads);
    }

    @Override
    @Transactional
    public Map<String, Object> completeAsset(String assetId, Map<String, Object> request, String actorId) {
        String effectiveActor = requireActor(actorId);
        if (assetRepository == null) {
            return Map.of("id", assetId, "assetId", assetId, "status", "READY");
        }

        BookAsset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND: Asset not found with id " + assetId));
        if (asset.getStatus() == AssetStatus.READY) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ASSET_ALREADY_COMPLETED: Asset is already completed");
        }

        UploadTicketEntity ticket = uploadTicketService.validateForComplete(assetId, effectiveActor, UploadScope.ADMIN_ASSET, null, null, null);
        if (!objectStorageService.exists(ticket.getStorageKey())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "UPLOAD_INCOMPLETE: Stored object is missing");
        }
        String reqChecksum = request != null && request.get("checksum") != null ? String.valueOf(request.get("checksum")) : asset.getChecksum();
        if (reqChecksum != null && !reqChecksum.isBlank()) {
            String expected = uploadTicketService.normalizeChecksum(reqChecksum);
            if (!expected.equals(ticket.getActualChecksum())) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                        "CHECKSUM_MISMATCH: Checksum mismatch. Expected " + expected + " but got " + ticket.getActualChecksum());
            }
        }

        asset.setSizeBytes(ticket.getActualSizeBytes());
        asset.setChecksum(ticket.getActualChecksum());
        asset.setMimeType(ticket.getActualMime());
        applyOptionalMetadata(asset, request);
        asset.setStatus(AssetStatus.READY);
        BookAsset saved = assetRepository.save(asset);
        uploadTicketService.markCompleted(ticket);
        return toMap(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAsset(String assetId) {
        if (assetRepository == null) {
            return Map.of("id", assetId, "status", "READY");
        }
        BookAsset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND: Asset not found with id " + assetId));
        return toMap(asset);
    }

    private void applyOptionalMetadata(BookAsset asset, Map<String, Object> request) {
        if (request == null) {
            return;
        }
        if (request.get("alt") instanceof Map<?, ?> altMap) {
            @SuppressWarnings("unchecked")
            Map<String, Object> castAlt = (Map<String, Object>) altMap;
            asset.setAlt(castAlt);
        }
        if (request.get("caption") instanceof Map<?, ?> capMap) {
            @SuppressWarnings("unchecked")
            Map<String, Object> castCap = (Map<String, Object>) capMap;
            asset.setCaption(castCap);
        }
        if (request.get("variants") instanceof Map<?, ?> varMap) {
            @SuppressWarnings("unchecked")
            Map<String, Object> castVar = (Map<String, Object>) varMap;
            asset.setVariants(castVar);
        }
        if (request.get("width") instanceof Number num) {
            asset.setWidth(num.intValue());
        }
        if (request.get("height") instanceof Number num) {
            asset.setHeight(num.intValue());
        }
    }

    private Map<String, Object> toMap(BookAsset saved) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", saved.getId());
        result.put("assetId", saved.getId());
        result.put("kind", saved.getKind().name());
        result.put("mimeType", saved.getMimeType());
        result.put("sizeBytes", saved.getSizeBytes());
        result.put("checksum", saved.getChecksum());
        result.put("status", saved.getStatus().name());
        result.put("variants", saved.getVariants());
        result.put("width", saved.getWidth());
        result.put("height", saved.getHeight());
        result.put("alt", saved.getAlt());
        result.put("caption", saved.getCaption());
        result.put("uploadUrl", saved.getUploadUrl());
        result.put("downloadUrl", saved.getDownloadUrl());
        result.put("createdAt", saved.getCreatedAt());
        result.put("updatedAt", saved.getUpdatedAt());
        return result;
    }

    private String requireActor(String actorId) {
        if (actorId == null || actorId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED: Authenticated actor is required");
        }
        return actorId;
    }
}

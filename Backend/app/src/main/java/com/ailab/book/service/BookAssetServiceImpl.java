package com.ailab.book.service;

import com.ailab.book.domain.AssetKind;
import com.ailab.book.domain.AssetStatus;
import com.ailab.book.domain.BookAsset;
import com.ailab.book.dto.BookDtos;
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
import org.springframework.util.unit.DataSize;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class BookAssetServiceImpl implements BookAssetService {

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/png",
            "image/webp",
            "image/svg+xml",
            "image/jpeg",
            "application/pdf",
            "application/json"
    );

    private final BookAssetRepository assetRepository;
    private final UploadTicketService uploadTicketService;
    private final ObjectStorageService objectStorageService;
    private final StorageKeyFactory storageKeyFactory;
    private final StorageProperties storageProperties;

    public BookAssetServiceImpl(BookAssetRepository assetRepository) {
        this(assetRepository, null, null, null, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public BookAssetServiceImpl(BookAssetRepository assetRepository,
                                @org.springframework.beans.factory.annotation.Autowired(required = false) UploadTicketService uploadTicketService,
                                @org.springframework.beans.factory.annotation.Autowired(required = false) ObjectStorageService objectStorageService,
                                @org.springframework.beans.factory.annotation.Autowired(required = false) StorageKeyFactory storageKeyFactory,
                                @org.springframework.beans.factory.annotation.Autowired(required = false) StorageProperties storageProperties) {
        this.assetRepository = assetRepository;
        this.uploadTicketService = uploadTicketService != null
                ? uploadTicketService
                : new UploadTicketService(new TransientUploadTicketStore());
        this.storageProperties = storageProperties != null ? storageProperties : defaultStorageProperties();
        this.objectStorageService = objectStorageService != null
                ? objectStorageService
                : new LocalObjectStorageService(this.storageProperties);
        this.storageKeyFactory = storageKeyFactory != null ? storageKeyFactory : new StorageKeyFactory();
    }

    @Override
    @Transactional
    public BookDtos.AssetUploadUrlsResponse generateUploadUrls(List<BookDtos.FileUploadSpec> files) {
        return generateUploadUrls(files, "system");
    }

    @Override
    @Transactional
    public BookDtos.AssetUploadUrlsResponse generateUploadUrls(List<BookDtos.FileUploadSpec> files, String actorId) {
        if (files == null || files.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR: Files list cannot be empty");
        }

        List<BookDtos.AssetUploadTicket> uploads = new ArrayList<>();
        long maxAssetBytes = storageProperties.maxAssetSize().toBytes();

        for (BookDtos.FileUploadSpec file : files) {
            String filename = file.filename() != null ? file.filename() : (file.name() != null ? file.name() : "unnamed_file");
            String contentType = file.contentType() != null ? file.contentType().toLowerCase() : (file.mimeType() != null ? file.mimeType().toLowerCase() : "image/png");
            long sizeBytes = file.sizeBytes() != null ? file.sizeBytes() : (file.size() != null ? file.size() : 0L);

            if (!ALLOWED_MIME_TYPES.contains(contentType)) {
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE: MIME type not allowed: " + contentType);
            }

            if (sizeBytes > maxAssetBytes) {
                throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "ASSET_TOO_LARGE: File size exceeds limit of " + maxAssetBytes + " bytes");
            }

            String assetId = "ast_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            IssuedUploadTicket ticket = uploadTicketService.issue(new UploadTicketIssueCommand(
                    assetId,
                    requireActor(actorId),
                    UploadScope.BOOK_ASSET,
                    storageKeyFactory.assetKey(assetId),
                    contentType,
                    maxAssetBytes,
                    file.checksum(),
                    Duration.ofMinutes(15),
                    null,
                    null,
                    null
            ));
            String uploadUrl = "/api/v1/assets/upload/" + assetId + "?ticket=" + ticket.token();
            String downloadUrl = "/api/v1/assets/raw/" + assetId + "/" + filename;

            AssetKind kind = "SVG".equalsIgnoreCase(file.kind()) || contentType.contains("svg") ? AssetKind.SVG : AssetKind.IMAGE;

            BookAsset asset = new BookAsset(assetId, kind, contentType, sizeBytes, file.checksum(), uploadUrl, downloadUrl);
            asset.setStatus(AssetStatus.PENDING);
            if (file.theme() != null) {
                asset.setVariants(Map.of("theme", file.theme()));
            }
            assetRepository.save(asset);

            uploads.add(new BookDtos.AssetUploadTicket(assetId, assetId, uploadUrl, downloadUrl, ticket.expiresAt()));
        }

        return new BookDtos.AssetUploadUrlsResponse(uploads);
    }

    @Override
    @Transactional
    public BookDtos.AssetResponse completeAsset(String assetId, BookDtos.CompleteAssetRequest request) {
        return completeAsset(assetId, request, "system");
    }

    @Override
    @Transactional
    public BookDtos.AssetResponse completeAsset(String assetId, BookDtos.CompleteAssetRequest request, String actorId) {
        BookAsset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ASSET_NOT_FOUND: Asset not found with id " + assetId));

        UploadTicketEntity ticket = uploadTicketService.validateForComplete(assetId, requireActor(actorId), UploadScope.BOOK_ASSET, null, null, null);
        if (!objectStorageService.exists(ticket.getStorageKey())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "UPLOAD_INCOMPLETE: Stored book asset object is missing");
        }

        if (request.checksum() != null) {
            if (!normalizeChecksum(request.checksum()).equals(normalizeChecksum(ticket.getActualChecksum()))) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "CHECKSUM_MISMATCH: Asset checksum mismatch");
            }
            asset.setChecksum(request.checksum());
        } else {
            asset.setChecksum(ticket.getActualChecksum());
        }
        asset.setMimeType(ticket.getActualMime());
        asset.setSizeBytes(ticket.getActualSizeBytes());
        if (request.alt() != null) {
            asset.setAlt(request.alt());
        }
        if (request.caption() != null) {
            asset.setCaption(request.caption());
        }
        if (request.variants() != null) {
            asset.setVariants(request.variants());
        }
        if (request.width() != null) {
            asset.setWidth(request.width());
        }
        if (request.height() != null) {
            asset.setHeight(request.height());
        }
        asset.setStatus(AssetStatus.READY);
        uploadTicketService.markCompleted(ticket);

        BookAsset saved = assetRepository.save(asset);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDtos.AssetResponse getAsset(String assetId) {
        BookAsset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ASSET_NOT_FOUND: Asset not found with id " + assetId));
        return toDto(asset);
    }

    private BookDtos.AssetResponse toDto(BookAsset asset) {
        return new BookDtos.AssetResponse(
                asset.getId(),
                asset.getKind(),
                asset.getMimeType(),
                asset.getSizeBytes(),
                asset.getChecksum(),
                asset.getStatus(),
                asset.getVariants(),
                asset.getWidth(),
                asset.getHeight(),
                asset.getAlt(),
                asset.getCaption(),
                asset.getUploadUrl(),
                asset.getDownloadUrl(),
                asset.getCreatedAt(),
                asset.getUpdatedAt()
        );
    }

    private StorageProperties defaultStorageProperties() {
        return new StorageProperties("local", null, "us-east-1", "ailab-local", null, null, false, false,
                Duration.ofMinutes(10), DataSize.ofMegabytes(10), DataSize.ofMegabytes(2), DataSize.ofMegabytes(10),
                "./storage/objects");
    }

    private String requireActor(String actorId) {
        return actorId != null && !actorId.isBlank() ? actorId : "system";
    }

    private String normalizeChecksum(String value) {
        return value == null ? "" : (value.startsWith("sha256:") ? value : "sha256:" + value).toLowerCase(Locale.ROOT);
    }
}

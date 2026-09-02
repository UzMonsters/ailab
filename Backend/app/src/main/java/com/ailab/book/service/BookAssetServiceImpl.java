package com.ailab.book.service;

import com.ailab.book.domain.AssetKind;
import com.ailab.book.domain.AssetStatus;
import com.ailab.book.domain.BookAsset;
import com.ailab.book.dto.BookDtos;
import com.ailab.book.repository.BookAssetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    private static final long MAX_ASSET_BYTES = 10485760L;

    private final BookAssetRepository assetRepository;

    public BookAssetServiceImpl(BookAssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    @Transactional
    public BookDtos.AssetUploadUrlsResponse generateUploadUrls(List<BookDtos.FileUploadSpec> files) {
        if (files == null || files.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR: Files list cannot be empty");
        }

        List<BookDtos.AssetUploadTicket> uploads = new ArrayList<>();

        for (BookDtos.FileUploadSpec file : files) {
            String filename = file.filename() != null ? file.filename() : (file.name() != null ? file.name() : "unnamed_file");
            String contentType = file.contentType() != null ? file.contentType().toLowerCase() : (file.mimeType() != null ? file.mimeType().toLowerCase() : "image/png");
            long sizeBytes = file.sizeBytes() != null ? file.sizeBytes() : (file.size() != null ? file.size() : 0L);

            if (!ALLOWED_MIME_TYPES.contains(contentType)) {
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE: MIME type not allowed: " + contentType);
            }

            if (sizeBytes > MAX_ASSET_BYTES) {
                throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "ASSET_TOO_LARGE: File size exceeds limit of " + MAX_ASSET_BYTES + " bytes");
            }

            String assetId = "ast_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            Instant expiresAt = Instant.now().plus(1, ChronoUnit.HOURS);
            String uploadUrl = "https://storage.jasscience.dev/uploads/" + assetId + "/" + filename;
            String downloadUrl = "https://storage.jasscience.dev/assets/" + assetId + "/" + filename;

            AssetKind kind = "SVG".equalsIgnoreCase(file.kind()) || contentType.contains("svg") ? AssetKind.SVG : AssetKind.IMAGE;

            BookAsset asset = new BookAsset(assetId, kind, contentType, sizeBytes, file.checksum(), uploadUrl, downloadUrl);
            asset.setStatus(AssetStatus.PENDING);
            if (file.theme() != null) {
                asset.setVariants(Map.of("theme", file.theme()));
            }
            assetRepository.save(asset);

            uploads.add(new BookDtos.AssetUploadTicket(assetId, assetId, uploadUrl, downloadUrl, expiresAt));
        }

        return new BookDtos.AssetUploadUrlsResponse(uploads);
    }

    @Override
    @Transactional
    public BookDtos.AssetResponse completeAsset(String assetId, BookDtos.CompleteAssetRequest request) {
        BookAsset asset = assetRepository.findById(assetId)
                .orElseGet(() -> {
                    BookAsset newAsset = new BookAsset(
                            assetId,
                            AssetKind.IMAGE,
                            "image/png",
                            0L,
                            request.checksum(),
                            "https://storage.jasscience.dev/uploads/" + assetId,
                            "https://storage.jasscience.dev/assets/" + assetId
                    );
                    return assetRepository.save(newAsset);
                });

        if (request.checksum() != null) {
            asset.setChecksum(request.checksum());
        }
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
}

package com.ailab.admin.assets;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/assets")
public class PublicAssetController {

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/webp",
            "image/svg+xml",
            "application/pdf",
            "application/json"
    );
    private static final long MAX_ASSET_SIZE_BYTES = 10485760L; // 10MB

    private final AssetStorageService storageService;
    private final AssetUploadTicketService ticketService;
    private final com.ailab.book.repository.BookAssetRepository assetRepository;

    public PublicAssetController(AssetStorageService storageService,
                                 AssetUploadTicketService ticketService,
                                 @org.springframework.beans.factory.annotation.Autowired(required = false) com.ailab.book.repository.BookAssetRepository assetRepository) {
        this.storageService = storageService;
        this.ticketService = ticketService;
        this.assetRepository = assetRepository;
    }

    @PutMapping("/upload/{fileId}")
    public ResponseEntity<Void> uploadBinary(
            @PathVariable String fileId,
            @RequestBody byte[] data,
            @RequestParam(required = false) String ticket,
            @RequestHeader(value = "X-Upload-Ticket", required = false) String headerTicket,
            HttpServletRequest request
    ) {
        String effectiveTicket = (ticket != null && !ticket.isBlank()) ? ticket : headerTicket;
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());

        if (effectiveTicket == null && !isAuthenticated) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED: Authentication or valid upload ticket is required");
        }

        long maxSize = MAX_ASSET_SIZE_BYTES;
        if (effectiveTicket != null) {
            try {
                AssetUploadTicketService.UploadTicket parsedTicket = ticketService.validateAndConsumeTicket(effectiveTicket, fileId);
                if (parsedTicket.maxSizeBytes() > 0) {
                    maxSize = parsedTicket.maxSizeBytes();
                }
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "FORBIDDEN: " + e.getMessage());
            } catch (IllegalStateException e) {
                throw new ResponseStatusException(HttpStatus.GONE, "TICKET_EXPIRED: " + e.getMessage());
            }
        }

        if (assetRepository != null) {
            assetRepository.findById(fileId).ifPresent(asset -> {
                if (asset.getStatus() == com.ailab.book.domain.AssetStatus.READY) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "ASSET_ALREADY_COMPLETED: Completed asset cannot be overwritten");
                }
            });
        }

        if (data == null || data.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR: Upload payload cannot be empty");
        }
        if (data.length > maxSize) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "ASSET_TOO_LARGE: Upload exceeds maximum size limit of " + maxSize + " bytes");
        }

        String rawContentType = request.getContentType();
        String contentType = rawContentType != null ? rawContentType.split(";")[0].trim().toLowerCase() : "application/octet-stream";

        if (!ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE: MIME type not allowed: " + contentType);
        }

        if (!validateMagicBytes(data, contentType)) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE: Content does not match declared MIME type: " + contentType);
        }

        try {
            storageService.store(fileId, data, contentType);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR: Failed to store asset");
        }
    }

    private boolean validateMagicBytes(byte[] data, String mimeType) {
        if (data == null || data.length == 0) return false;
        return switch (mimeType) {
            case "image/png" -> data.length >= 8 && data[0] == (byte) 0x89 && data[1] == 'P' && data[2] == 'N' && data[3] == 'G';
            case "image/jpeg" -> data.length >= 3 && data[0] == (byte) 0xFF && data[1] == (byte) 0xD8 && data[2] == (byte) 0xFF;
            case "image/webp" -> data.length >= 12 && data[0] == 'R' && data[1] == 'I' && data[2] == 'F' && data[3] == 'F'
                    && data[8] == 'W' && data[9] == 'E' && data[10] == 'B' && data[11] == 'P';
            case "image/svg+xml" -> {
                String text = new String(data, 0, Math.min(data.length, 512), java.nio.charset.StandardCharsets.UTF_8).trim().toLowerCase();
                yield text.startsWith("<?xml") || text.startsWith("<svg") || text.contains("<svg");
            }
            case "application/pdf" -> data.length >= 5 && data[0] == '%' && data[1] == 'P' && data[2] == 'D' && data[3] == 'F' && data[4] == '-';
            case "application/json" -> {
                String text = new String(data, 0, Math.min(data.length, 64), java.nio.charset.StandardCharsets.UTF_8).trim();
                yield text.startsWith("{") || text.startsWith("[");
            }
            default -> true;
        };
    }

    @GetMapping({"/raw/{fileId}", "/raw/{fileId}/{filename}", "/download/{fileId}", "/download/{fileId}/{filename}"})
    public ResponseEntity<byte[]> downloadAsset(
            @PathVariable String fileId,
            @PathVariable(required = false) String filename
    ) {
        try {
            byte[] data = storageService.load(fileId);
            if (data == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND: Asset file not found");
            }
            AssetStorageService.StoredAssetMeta meta = storageService.getMeta(fileId);
            String ct = (meta != null && meta.contentType() != null) ? meta.contentType() : "application/octet-stream";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(ct));
            if (meta != null) {
                headers.setETag("\"" + meta.sha256Hex() + "\"");
            }
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR: Failed to read asset");
        }
    }
}

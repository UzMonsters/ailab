package com.ailab.admin.assets;

import com.ailab.storage.ObjectStorageService;
import com.ailab.storage.StorageException;
import com.ailab.storage.StorageUpload;
import com.ailab.storage.StoredObject;
import com.ailab.storage.StoredObjectDownload;
import com.ailab.storage.upload.InspectedUpload;
import com.ailab.storage.upload.UploadContentInspector;
import com.ailab.storage.upload.UploadTicketClaimCommand;
import com.ailab.storage.upload.UploadTicketEntity;
import com.ailab.storage.upload.UploadTicketService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/assets")
public class PublicAssetController {
    private static final Logger log = LoggerFactory.getLogger(PublicAssetController.class);

    private final ObjectStorageService objectStorageService;
    private final UploadTicketService uploadTicketService;

    public PublicAssetController(ObjectStorageService objectStorageService,
                                 UploadTicketService uploadTicketService) {
        this.objectStorageService = objectStorageService;
        this.uploadTicketService = uploadTicketService;
    }

    @PutMapping("/upload/{fileId}")
    public ResponseEntity<Void> uploadBinary(
            @PathVariable String fileId,
            @RequestParam(required = false) String ticket,
            @RequestHeader(value = "X-Upload-Ticket", required = false) String headerTicket,
            HttpServletRequest request
    ) {
        String rawTicket = (ticket != null && !ticket.isBlank()) ? ticket : headerTicket;
        if (rawTicket == null || rawTicket.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "AUTH_REQUIRED: Upload ticket is required");
        }

        UploadTicketEntity metadata = uploadTicketService.findByRawToken(rawTicket);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())
                && !metadata.getActorId().equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "UPLOAD_TICKET_ACTOR_MISMATCH: Upload ticket belongs to a different actor");
        }

        UploadTicketEntity claimed = uploadTicketService.claimForUpload(new UploadTicketClaimCommand(
                rawTicket,
                fileId,
                metadata.getActorId(),
                metadata.getScope(),
                metadata.getWorkspaceId(),
                metadata.getPreviewId(),
                metadata.getVariant()));

        InspectedUpload inspected;
        try {
            inspected = UploadContentInspector.inspect(
                    request.getInputStream(),
                    request.getContentLengthLong(),
                    request.getContentType(),
                    claimed.getAllowedMime(),
                    claimed.getMaxSizeBytes(),
                    claimed.getExpectedChecksum());
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UPLOAD_READ_FAILED: Upload body could not be read", exception);
        }

        StoredObject stored = null;
        try {
            stored = objectStorageService.upload(new StorageUpload(
                    claimed.getStorageKey(),
                    inspected.detectedMime(),
                    inspected.bytes().length,
                    new ByteArrayInputStream(inspected.bytes())));
            uploadTicketService.markUploaded(claimed, inspected.sha256(), stored.sizeBytes(), inspected.detectedMime());
            return ResponseEntity.ok().build();
        } catch (RuntimeException exception) {
            if (stored != null) {
                try {
                    objectStorageService.delete(stored.storageKey());
                } catch (RuntimeException cleanup) {
                    log.warn("Failed to compensate object upload storageKey={} assetId={}",
                            stored.storageKey(), claimed.getAssetId(), cleanup);
                }
            }
            throw exception;
        }
    }

    @GetMapping({"/raw/{fileId}", "/raw/{fileId}/{filename}", "/download/{fileId}", "/download/{fileId}/{filename}"})
    public ResponseEntity<byte[]> downloadAsset(
            @PathVariable String fileId,
            @PathVariable(required = false) String filename
    ) {
        UploadTicketEntity metadata = uploadTicketService.findLatestByAssetId(fileId);
        try (StoredObjectDownload download = objectStorageService.download(metadata.getStorageKey())) {
            byte[] bytes = download.inputStream().readAllBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(download.contentType()));
            headers.setContentLength(download.sizeBytes());
            if (metadata.getActualChecksum() != null) {
                headers.setETag("\"" + metadata.getActualChecksum().replace("sha256:", "") + "\"");
            }
            if (filename != null && !filename.isBlank()) {
                headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename.replace("\"", "") + "\"");
            }
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (StorageException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "STORAGE_OBJECT_NOT_FOUND: Asset file not found", exception);
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR: Failed to read asset", exception);
        }
    }
}

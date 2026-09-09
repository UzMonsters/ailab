package com.ailab.admin.assets;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/assets")
public class PublicAssetController {

    private final AssetStorageService storageService;

    public PublicAssetController(AssetStorageService storageService) {
        this.storageService = storageService;
    }

    @PutMapping("/upload/{fileId}")
    public ResponseEntity<Void> uploadBinary(
            @PathVariable String fileId,
            @RequestBody byte[] data,
            HttpServletRequest request
    ) {
        if (data == null || data.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR: Upload payload cannot be empty");
        }
        String contentType = request.getContentType();
        try {
            storageService.store(fileId, data, contentType);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR: Failed to store asset");
        }
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

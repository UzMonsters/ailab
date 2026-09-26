package com.ailab.storage.upload;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

public final class UploadContentInspector {
    private UploadContentInspector() {
    }

    public static InspectedUpload inspect(InputStream inputStream,
                                          long contentLength,
                                          String declaredMime,
                                          String allowedMime,
                                          long maxBytes,
                                          String expectedChecksum) {
        if (contentLength > maxBytes) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "UPLOAD_TOO_LARGE: Upload exceeds maximum size");
        }
        String normalizedDeclared = normalizeMime(declaredMime);
        String normalizedAllowed = normalizeMime(allowedMime);
        if (normalizedDeclared == null || !normalizedDeclared.equals(normalizedAllowed)) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "UPLOAD_CONTENT_TYPE_MISMATCH: Declared MIME does not match upload ticket");
        }
        byte[] bytes = readBounded(inputStream, maxBytes);
        if (bytes.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UPLOAD_EMPTY: Upload payload cannot be empty");
        }
        String detected = detectMimeType(bytes);
        if (!mimeMatches(normalizedAllowed, detected)) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    "UPLOAD_CONTENT_TYPE_MISMATCH: Content does not match declared MIME");
        }
        String checksum = "sha256:" + sha256Hex(bytes);
        String expected = normalizeChecksum(expectedChecksum);
        if (expected != null && !expected.equals(checksum)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "UPLOAD_CHECKSUM_MISMATCH: Upload checksum does not match expected checksum");
        }
        return new InspectedUpload(bytes, normalizedDeclared, detected, checksum);
    }

    public static String detectMimeType(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        if (data.length >= 8
                && data[0] == (byte) 0x89 && data[1] == 'P' && data[2] == 'N' && data[3] == 'G'
                && data[4] == 0x0D && data[5] == 0x0A && data[6] == 0x1A && data[7] == 0x0A) {
            return "image/png";
        }
        if (data.length >= 3 && data[0] == (byte) 0xFF && data[1] == (byte) 0xD8 && data[2] == (byte) 0xFF) {
            return "image/jpeg";
        }
        if (data.length >= 12 && data[0] == 'R' && data[1] == 'I' && data[2] == 'F' && data[3] == 'F'
                && data[8] == 'W' && data[9] == 'E' && data[10] == 'B' && data[11] == 'P') {
            return "image/webp";
        }
        if (data.length >= 5 && data[0] == '%' && data[1] == 'P' && data[2] == 'D' && data[3] == 'F' && data[4] == '-') {
            return "application/pdf";
        }
        String text = new String(data, 0, Math.min(data.length, 512), StandardCharsets.UTF_8).trim();
        String lower = text.toLowerCase();
        if (lower.startsWith("<?xml") || lower.startsWith("<svg") || lower.contains("<svg")) {
            return "image/svg+xml";
        }
        if (text.startsWith("{") || text.startsWith("[")) {
            return "application/json";
        }
        return "application/octet-stream";
    }

    public static String normalizeMime(String mime) {
        if (mime == null || mime.isBlank()) {
            return null;
        }
        return mime.split(";")[0].trim().toLowerCase();
    }

    public static String normalizeChecksum(String checksum) {
        if (checksum == null || checksum.isBlank()) {
            return null;
        }
        String clean = checksum.trim().toLowerCase();
        return clean.startsWith("sha256:") ? clean : "sha256:" + clean;
    }

    public static String sha256Hex(byte[] data) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(data));
        } catch (Exception exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    private static boolean mimeMatches(String expected, String detected) {
        return expected != null && expected.equals(detected);
    }

    private static byte[] readBounded(InputStream inputStream, long maxBytes) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            long total = 0;
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                total += read;
                if (total > maxBytes) {
                    throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,
                            "UPLOAD_TOO_LARGE: Upload exceeds maximum size");
                }
                out.write(buffer, 0, read);
            }
            return out.toByteArray();
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "UPLOAD_READ_FAILED: Upload body could not be read", exception);
        }
    }
}

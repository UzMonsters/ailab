package com.ailab.storage.upload;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UploadContentInspectorTest {

    @Test
    void readsOnlyWithinLimitAndComputesChecksumAndMime() throws Exception {
        byte[] png = new byte[]{(byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 1, 2, 3};

        InspectedUpload inspected = UploadContentInspector.inspect(
                new ByteArrayInputStream(png),
                png.length,
                "image/png",
                "image/png",
                32,
                null);

        assertThat(inspected.bytes()).isEqualTo(png);
        assertThat(inspected.detectedMime()).isEqualTo("image/png");
        assertThat(inspected.declaredMime()).isEqualTo("image/png");
        assertThat(inspected.sha256()).startsWith("sha256:");
    }

    @Test
    void rejectsOversizedContentBeforeAcceptingIt() {
        byte[] bytes = new byte[6];

        assertThatThrownBy(() -> UploadContentInspector.inspect(
                new ByteArrayInputStream(bytes),
                6,
                "image/png",
                "image/png",
                5,
                null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("UPLOAD_TOO_LARGE");
    }

    @Test
    void rejectsSpoofedMime() {
        byte[] json = "{\"ok\":true}".getBytes(java.nio.charset.StandardCharsets.UTF_8);

        assertThatThrownBy(() -> UploadContentInspector.inspect(
                new ByteArrayInputStream(json),
                json.length,
                "image/png",
                "image/png",
                64,
                null))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("UPLOAD_CONTENT_TYPE_MISMATCH");
    }

    @Test
    void rejectsChecksumMismatch() {
        byte[] json = "{\"ok\":true}".getBytes(java.nio.charset.StandardCharsets.UTF_8);

        assertThatThrownBy(() -> UploadContentInspector.inspect(
                new ByteArrayInputStream(json),
                json.length,
                "application/json",
                "application/json",
                64,
                "sha256:wrong"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("UPLOAD_CHECKSUM_MISMATCH");
    }
}

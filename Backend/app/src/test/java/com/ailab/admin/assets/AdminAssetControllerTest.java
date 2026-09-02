package com.ailab.admin.assets;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminAssetControllerTest {

    @Mock
    AdminAssetService service;

    @InjectMocks
    AdminAssetController controller;

    @Test
    void testUploadUrls() {
        List<Map<String, Object>> files = List.of(
                Map.of("filename", "beaker.png", "contentType", "image/png", "sizeBytes", 1024)
        );
        when(service.generateUploadUrls(files)).thenReturn(Map.of("uploads", List.of(
                Map.of("filename", "beaker.png", "uploadUrl", "https://storage.jasscience.dev/uploads/1")
        )));

        Map<String, Object> result = controller.generateUploadUrls(Map.of("files", files));
        assertThat(result.get("uploads")).isInstanceOf(List.class);
    }

    @Test
    void testAssetServiceImplValidation() {
        AdminAssetServiceImpl assetService = new AdminAssetServiceImpl();

        List<Map<String, Object>> valid = List.of(
                Map.of("filename", "beaker.png", "contentType", "image/png", "sizeBytes", 1024)
        );
        Map<String, Object> res = assetService.generateUploadUrls(valid);
        assertThat(res.get("uploads")).isInstanceOf(List.class);

        List<Map<String, Object>> invalidMime = List.of(
                Map.of("filename", "bad.exe", "contentType", "application/x-msdownload", "sizeBytes", 1024)
        );
        assertThatThrownBy(() -> assetService.generateUploadUrls(invalidMime))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("VALIDATION_ERROR");

        List<Map<String, Object>> tooLarge = List.of(
                Map.of("filename", "large.png", "contentType", "image/png", "sizeBytes", 10000000L)
        );
        assertThatThrownBy(() -> assetService.generateUploadUrls(tooLarge))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("VALIDATION_ERROR");
    }
}

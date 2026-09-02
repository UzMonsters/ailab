package com.ailab.admin.assets;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/assets")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
public class AdminAssetController {

    private final AdminAssetService assetService;

    public AdminAssetController(AdminAssetService assetService) {
        this.assetService = assetService;
    }

    @PostMapping("/upload-urls")
    public Map<String, Object> generateUploadUrls(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> files = request != null && request.get("files") instanceof List
                ? (List<Map<String, Object>>) request.get("files")
                : List.of();
        return assetService.generateUploadUrls(files);
    }
}

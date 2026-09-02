package com.ailab.admin.catalog;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminMaterialsCatalogControllerTest {

    @Mock
    AdminCatalogService service;

    @InjectMocks
    AdminMaterialsCatalogController controller;

    @Test
    void testMaterialsEndpoints() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("admin-1", "pass");

        when(service.listDrafts("MATERIAL", 0, 50, null, null, null)).thenReturn(Map.of("items", List.of()));
        when(service.createDraft(eq("MATERIAL"), anyMap(), eq("admin-1"), anyString())).thenReturn(Map.of("id", "mat_1"));
        when(service.getDraft("MATERIAL", "mat_1")).thenReturn(Map.of("id", "mat_1"));
        when(service.patchDraft(eq("MATERIAL"), eq("mat_1"), anyMap(), eq("1"), eq("admin-1"), anyString())).thenReturn(Map.of("id", "mat_1"));
        when(service.publishDraft(eq("MATERIAL"), eq("mat_1"), eq(1L), any(), eq("admin-1"), anyString())).thenReturn(Map.of("publishedVersion", 1L));

        assertThat(controller.listMaterials(0, 50, null, null, null)).isEqualTo(Map.of("items", List.of()));

        ResponseEntity<Map<String, Object>> created = controller.createMaterial(Map.of("code", "hcl"), auth);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(controller.getMaterial("mat_1")).isEqualTo(Map.of("id", "mat_1"));
        assertThat(controller.patchMaterial("mat_1", Map.of(), "1", auth)).isEqualTo(Map.of("id", "mat_1"));

        ResponseEntity<Map<String, Object>> pub = controller.publishMaterial("mat_1", Map.of("version", 1L), auth);
        assertThat(pub.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}

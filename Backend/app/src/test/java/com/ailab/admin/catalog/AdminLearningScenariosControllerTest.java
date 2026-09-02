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
class AdminLearningScenariosControllerTest {

    @Mock
    AdminCatalogService service;

    @InjectMocks
    AdminLearningScenariosController controller;

    @Test
    void testLevelsEndpoints() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("admin-1", "pass");

        when(service.listDrafts("SCENARIO", 0, 50, null, null, null)).thenReturn(Map.of("items", List.of()));
        when(service.createDraft(eq("SCENARIO"), anyMap(), eq("admin-1"), anyString())).thenReturn(Map.of("id", "lvl_1"));
        when(service.getDraft("SCENARIO", "lvl_1")).thenReturn(Map.of("id", "lvl_1"));
        when(service.patchDraft(eq("SCENARIO"), eq("lvl_1"), anyMap(), eq("1"), eq("admin-1"), anyString())).thenReturn(Map.of("id", "lvl_1"));
        when(service.validateDraft("SCENARIO", "lvl_1", 1L)).thenReturn(Map.of("valid", true));
        when(service.publishDraft(eq("SCENARIO"), eq("lvl_1"), eq(1L), any(), eq("admin-1"), anyString())).thenReturn(Map.of("publishedVersion", 1L));

        assertThat(controller.listLevels(0, 50, null, null, null)).isEqualTo(Map.of("items", List.of()));

        ResponseEntity<Map<String, Object>> created = controller.createLevel(Map.of("trackId", "chem"), auth);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(controller.getLevel("lvl_1")).isEqualTo(Map.of("id", "lvl_1"));
        assertThat(controller.patchLevel("lvl_1", Map.of(), "1", auth)).isEqualTo(Map.of("id", "lvl_1"));
        assertThat(controller.validateLevel("lvl_1", Map.of("version", 1L))).isEqualTo(Map.of("valid", true));

        ResponseEntity<Map<String, Object>> pub = controller.publishLevel("lvl_1", Map.of("version", 1L), auth);
        assertThat(pub.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}

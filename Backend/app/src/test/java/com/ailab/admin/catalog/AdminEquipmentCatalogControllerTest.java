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
class AdminEquipmentCatalogControllerTest {

    @Mock
    AdminCatalogService service;

    @InjectMocks
    AdminEquipmentCatalogController controller;

    @Test
    void testEquipmentEndpoints() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("admin-1", "pass");

        when(service.listDrafts("EQUIPMENT", 0, 50, null, null, null)).thenReturn(Map.of("items", List.of()));
        when(service.createDraft(eq("EQUIPMENT"), anyMap(), eq("admin-1"), anyString())).thenReturn(Map.of("id", "eq_1"));
        when(service.getDraft("EQUIPMENT", "eq_1")).thenReturn(Map.of("id", "eq_1"));
        when(service.patchDraft(eq("EQUIPMENT"), eq("eq_1"), anyMap(), eq("1"), eq("admin-1"), anyString())).thenReturn(Map.of("id", "eq_1"));
        when(service.savePorts(eq("eq_1"), anyMap(), eq("1"), eq("admin-1"), anyString())).thenReturn(Map.of("ports", List.of()));
        when(service.saveCompatibility(eq("eq_1"), anyMap(), eq("1"), eq("admin-1"), anyString())).thenReturn(Map.of("rules", List.of()));
        when(service.publishDraft(eq("EQUIPMENT"), eq("eq_1"), eq(1L), any(), eq("admin-1"), anyString())).thenReturn(Map.of("publishedVersion", 1L));

        assertThat(controller.listEquipment(0, 50, null, null, null)).isEqualTo(Map.of("items", List.of()));

        ResponseEntity<Map<String, Object>> created = controller.createEquipment(Map.of("code", "beaker"), auth);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(controller.getEquipment("eq_1")).isEqualTo(Map.of("id", "eq_1"));
        assertThat(controller.patchEquipment("eq_1", Map.of(), "1", auth)).isEqualTo(Map.of("id", "eq_1"));
        assertThat(controller.updatePorts("eq_1", Map.of("ports", List.of()), "1", auth)).isEqualTo(Map.of("ports", List.of()));
        assertThat(controller.updateCompatibility("eq_1", Map.of("rules", List.of()), "1", auth)).isEqualTo(Map.of("rules", List.of()));

        ResponseEntity<Map<String, Object>> pub = controller.publishEquipment("eq_1", Map.of("version", 1L), auth);
        assertThat(pub.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}

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
class AdminSafetyRulesControllerTest {

    @Mock
    AdminCatalogService service;

    @InjectMocks
    AdminSafetyRulesController controller;

    @Test
    void testSafetyRulesEndpoints() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("admin-1", "pass");

        when(service.listDrafts("SAFETY_RULE", 0, 50, null, null, null)).thenReturn(Map.of("items", List.of()));
        when(service.createDraft(eq("SAFETY_RULE"), anyMap(), eq("admin-1"), anyString())).thenReturn(Map.of("id", "rule_1"));
        when(service.getDraft("SAFETY_RULE", "rule_1")).thenReturn(Map.of("id", "rule_1"));
        when(service.patchDraft(eq("SAFETY_RULE"), eq("rule_1"), anyMap(), eq("1"), eq("admin-1"), anyString())).thenReturn(Map.of("id", "rule_1"));
        when(service.publishDraft(eq("SAFETY_RULE"), eq("rule_1"), eq(1L), any(), eq("admin-1"), anyString())).thenReturn(Map.of("publishedVersion", 1L));

        assertThat(controller.listSafetyRules(0, 50, null, null, null)).isEqualTo(Map.of("items", List.of()));

        ResponseEntity<Map<String, Object>> created = controller.createSafetyRule(Map.of("code", "RULE_1"), auth);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(controller.getSafetyRule("rule_1")).isEqualTo(Map.of("id", "rule_1"));
        assertThat(controller.patchSafetyRule("rule_1", Map.of(), "1", auth)).isEqualTo(Map.of("id", "rule_1"));

        ResponseEntity<Map<String, Object>> pub = controller.publishSafetyRule("rule_1", Map.of("version", 1L), auth);
        assertThat(pub.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}

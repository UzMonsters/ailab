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
class AdminChemistryCatalogControllerTest {

    @Mock
    AdminCatalogService service;

    @InjectMocks
    AdminChemistryCatalogController controller;

    @Test
    void testElementsEndpoints() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("admin-1", "pass");

        when(service.listDrafts("ELEMENT", 0, 50, null, null, null)).thenReturn(Map.of("items", List.of()));
        when(service.createDraft(eq("ELEMENT"), anyMap(), eq("admin-1"), anyString())).thenReturn(Map.of("id", "elt_1"));
        when(service.getDraft("ELEMENT", "elt_1")).thenReturn(Map.of("id", "elt_1"));
        when(service.patchDraft(eq("ELEMENT"), eq("elt_1"), anyMap(), eq("1"), eq("admin-1"), anyString())).thenReturn(Map.of("id", "elt_1"));
        when(service.publishDraft(eq("ELEMENT"), eq("elt_1"), eq(1L), any(), eq("admin-1"), anyString())).thenReturn(Map.of("publishedVersion", 1L));

        assertThat(controller.listElements(0, 50, null, null, null)).isEqualTo(Map.of("items", List.of()));
        ResponseEntity<Map<String, Object>> created = controller.createElement(Map.of("symbol", "H"), auth);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        assertThat(controller.getElement("elt_1")).isEqualTo(Map.of("id", "elt_1"));
        assertThat(controller.patchElement("elt_1", Map.of(), "1", auth)).isEqualTo(Map.of("id", "elt_1"));

        ResponseEntity<Map<String, Object>> pub = controller.publishElement("elt_1", Map.of("version", 1L), auth);
        assertThat(pub.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void testSubstancesAndReactionsEndpoints() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("admin-1", "pass");

        when(service.listDrafts("SUBSTANCE", 0, 50, null, null, null)).thenReturn(Map.of("items", List.of()));
        when(service.listDrafts("REACTION", 0, 50, null, null, null)).thenReturn(Map.of("items", List.of()));
        when(service.validateDraft("REACTION", "rxn_1", 1L)).thenReturn(Map.of("valid", true));

        assertThat(controller.listSubstances(0, 50, null, null, null)).isEqualTo(Map.of("items", List.of()));
        assertThat(controller.listReactions(0, 50, null, null, null)).isEqualTo(Map.of("items", List.of()));
        assertThat(controller.validateReaction("rxn_1", Map.of("version", 1L))).isEqualTo(Map.of("valid", true));
    }
}

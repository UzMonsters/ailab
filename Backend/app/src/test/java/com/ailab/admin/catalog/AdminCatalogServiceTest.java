package com.ailab.admin.catalog;

import com.ailab.admin.audit.AuditLogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminCatalogServiceTest {

    @Mock
    AdminCatalogDraftRepository repository;

    @Mock
    AuditLogService auditLogService;

    @InjectMocks
    AdminCatalogServiceImpl service;

    @Test
    void testListAndGetDrafts() {
        AdminCatalogDraftEntity entity = new AdminCatalogDraftEntity("ELEMENT", "H", "DRAFT", Map.of("symbol", "H"));
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(entity)));
        when(repository.findByEntityTypeAndId("ELEMENT", entity.getId())).thenReturn(Optional.of(entity));

        Map<String, Object> list = service.listDrafts("ELEMENT", 0, 50, null, null, null);
        assertThat(list.get("items")).isInstanceOf(List.class);

        Map<String, Object> draft = service.getDraft("ELEMENT", entity.getId());
        assertThat(draft.get("code")).isEqualTo("H");
    }

    @Test
    void testCreateAndPatchDraft() {
        AdminCatalogDraftEntity entity = new AdminCatalogDraftEntity("SUBSTANCE", "H2O", "DRAFT", Map.of("formula", "H2O"));
        when(repository.save(any(AdminCatalogDraftEntity.class))).thenReturn(entity);
        when(repository.findByEntityTypeAndId("SUBSTANCE", entity.getId())).thenReturn(Optional.of(entity));

        Map<String, Object> created = service.createDraft("SUBSTANCE", Map.of("code", "H2O", "formula", "H2O"), "admin-1", "Admin");
        assertThat(created.get("code")).isEqualTo("H2O");

        Map<String, Object> patched = service.patchDraft("SUBSTANCE", entity.getId(), Map.of("phase", "LIQUID"), "1", "admin-1", "Admin");
        assertThat(patched.get("phase")).isEqualTo("LIQUID");
    }

    @Test
    void testSavePortsValidation() {
        AdminCatalogDraftEntity entity = new AdminCatalogDraftEntity("EQUIPMENT", "beaker", "DRAFT", Map.of("name", "Beaker"));
        when(repository.findByEntityTypeAndId("EQUIPMENT", "beaker")).thenReturn(Optional.of(entity));

        Map<String, Object> validPort = Map.of("id", "INLET", "type", "FLUID", "direction", "INPUT", "connector", "spout");
        Map<String, Object> result = service.savePorts("beaker", Map.of("ports", List.of(validPort)), "1", "admin-1", "Admin");
        assertThat(result.get("ports")).isInstanceOf(List.class);

        Map<String, Object> invalidPort = Map.of("id", "INLET");
        assertThatThrownBy(() -> service.savePorts("beaker", Map.of("ports", List.of(invalidPort)), "2", "admin-1", "Admin"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("PORT_SCHEMA_INVALID");
    }

    @Test
    void testValidateAndPublishDraft() {
        AdminCatalogDraftEntity entity = new AdminCatalogDraftEntity("REACTION", "rxn-1", "DRAFT", Map.of(
                "reactants", List.of(Map.of("substance", "H2")),
                "products", List.of(Map.of("substance", "H2O"))
        ));
        when(repository.findByEntityTypeAndId("REACTION", entity.getId())).thenReturn(Optional.of(entity));

        Map<String, Object> val = service.validateDraft("REACTION", entity.getId(), 1L);
        assertThat(val.get("valid")).isEqualTo(true);

        Map<String, Object> pub = service.publishDraft("REACTION", entity.getId(), 1L, "idemp-1", "admin-1", "Admin");
        assertThat(pub.get("publishedVersion")).isNotNull();
    }
}

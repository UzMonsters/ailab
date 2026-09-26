package com.ailab.storage.upload;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UploadTicketServiceUnitTest {

    private final InMemoryUploadTicketRepository repository = new InMemoryUploadTicketRepository();
    private final UploadTicketService service = new UploadTicketService(repository);

    @Test
    void returnsOpaqueTokenAndStoresOnlyHash() {
        IssuedUploadTicket ticket = service.issue(new UploadTicketIssueCommand(
                "asset_1",
                "user_1",
                UploadScope.ADMIN_ASSET,
                "assets/asset_1",
                "image/png",
                1024,
                null,
                Duration.ofMinutes(15),
                null,
                null,
                null));

        UploadTicketEntity stored = repository.findOnly();

        assertThat(ticket.token()).isNotEqualTo(stored.getId());
        assertThat(stored.getTokenHash()).isNotBlank();
        assertThat(stored.getTokenHash()).isNotEqualTo(ticket.token());
    }

    @Test
    void atomicallyClaimsTicketOnlyOnce() {
        IssuedUploadTicket ticket = service.issue(new UploadTicketIssueCommand(
                "asset_1",
                "user_1",
                UploadScope.ADMIN_ASSET,
                "assets/asset_1",
                "image/png",
                1024,
                null,
                Duration.ofMinutes(15),
                null,
                null,
                null));

        UploadTicketEntity claimed = service.claimForUpload(new UploadTicketClaimCommand(
                ticket.token(),
                "asset_1",
                "user_1",
                UploadScope.ADMIN_ASSET,
                null,
                null,
                null));

        assertThat(claimed.getStatus()).isEqualTo(UploadTicketStatus.UPLOADING);

        assertThatThrownBy(() -> service.claimForUpload(new UploadTicketClaimCommand(
                ticket.token(),
                "asset_1",
                "user_1",
                UploadScope.ADMIN_ASSET,
                null,
                null,
                null)))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("UPLOAD_TICKET_ALREADY_USED");
    }

    @Test
    void concurrentClaimsAllowOnlyOneWinner() throws Exception {
        IssuedUploadTicket ticket = service.issue(new UploadTicketIssueCommand(
                "asset_1",
                "user_1",
                UploadScope.ADMIN_ASSET,
                "assets/asset_1",
                "image/png",
                1024,
                null,
                Duration.ofMinutes(15),
                null,
                null,
                null));

        var executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger winners = new AtomicInteger();
        List<Throwable> unexpectedFailures = java.util.Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < 2; i++) {
            executor.submit(() -> {
                ready.countDown();
                try {
                    start.await(5, TimeUnit.SECONDS);
                    service.claimForUpload(new UploadTicketClaimCommand(
                            ticket.token(),
                            "asset_1",
                            "user_1",
                            UploadScope.ADMIN_ASSET,
                            null,
                            null,
                            null));
                    winners.incrementAndGet();
                } catch (ResponseStatusException expected) {
                    assertThat(expected.getMessage()).contains("UPLOAD_TICKET_ALREADY_USED");
                } catch (Throwable failure) {
                    unexpectedFailures.add(failure);
                }
            });
        }

        assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
        start.countDown();
        executor.shutdown();
        assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
        assertThat(unexpectedFailures).isEmpty();
        assertThat(winners.get()).isEqualTo(1);
    }

    @Test
    void rejectsWrongActorScopeAssetAndTargetContext() {
        IssuedUploadTicket ticket = service.issue(new UploadTicketIssueCommand(
                "asset_1",
                "alice",
                UploadScope.WORKSPACE_PREVIEW,
                "workspaces/ws_1/previews/prev_1/asset_1",
                "image/webp",
                1024,
                null,
                Duration.ofMinutes(15),
                "ws_1",
                "prev_1",
                "DARK"));

        assertThatThrownBy(() -> service.claimForUpload(new UploadTicketClaimCommand(
                ticket.token(),
                "asset_1",
                "bob",
                UploadScope.WORKSPACE_PREVIEW,
                "ws_1",
                "prev_1",
                "DARK")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("UPLOAD_TICKET_ACTOR_MISMATCH");

        assertThatThrownBy(() -> service.claimForUpload(new UploadTicketClaimCommand(
                ticket.token(),
                "asset_1",
                "alice",
                UploadScope.ADMIN_ASSET,
                "ws_1",
                "prev_1",
                "DARK")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("UPLOAD_TICKET_SCOPE_MISMATCH");

        assertThatThrownBy(() -> service.claimForUpload(new UploadTicketClaimCommand(
                ticket.token(),
                "asset_2",
                "alice",
                UploadScope.WORKSPACE_PREVIEW,
                "ws_1",
                "prev_1",
                "DARK")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("UPLOAD_TICKET_TARGET_MISMATCH");

        assertThatThrownBy(() -> service.claimForUpload(new UploadTicketClaimCommand(
                ticket.token(),
                "asset_1",
                "alice",
                UploadScope.WORKSPACE_PREVIEW,
                "ws_2",
                "prev_1",
                "DARK")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("UPLOAD_TICKET_TARGET_MISMATCH");
    }
}

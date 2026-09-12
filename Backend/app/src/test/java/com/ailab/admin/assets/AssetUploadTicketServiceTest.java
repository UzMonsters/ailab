package com.ailab.admin.assets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AssetUploadTicketServiceTest {

    private AssetUploadTicketService ticketService;

    @BeforeEach
    void setUp() {
        ticketService = new AssetUploadTicketService("test-secret-key-that-is-sufficiently-long-for-hmac-sha256");
    }

    @Test
    void testGenerateAndValidateTicket() {
        String token = ticketService.issueTicket("asset-123", "usr-456", "asset", "image/png", 1024 * 1024, null, java.time.Instant.now().plusSeconds(300));
        assertThat(token).isNotBlank();

        AssetUploadTicketService.UploadTicket ticket = ticketService.validateAndConsumeTicket(token, "asset-123");
        assertThat(ticket.assetId()).isEqualTo("asset-123");
        assertThat(ticket.actorId()).isEqualTo("usr-456");
        assertThat(ticket.allowedMime()).isEqualTo("image/png");
        assertThat(ticket.maxSizeBytes()).isEqualTo(1024 * 1024);
    }

    @Test
    void testTicketReplayPrevented() {
        String token = ticketService.issueTicket("asset-replay", "usr-1", "asset", "image/png", 5000, null, java.time.Instant.now().plusSeconds(300));
        ticketService.validateAndConsumeTicket(token, "asset-replay");

        assertThatThrownBy(() -> ticketService.validateAndConsumeTicket(token, "asset-replay"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already been used");
    }

    @Test
    void testTamperedTicketRejected() {
        String token = ticketService.issueTicket("asset-tamper", "usr-1", "asset", "image/png", 5000, null, java.time.Instant.now().plusSeconds(300));
        String tampered = token.substring(0, token.length() - 4) + "XXXX";

        assertThatThrownBy(() -> ticketService.validateAndConsumeTicket(tampered, "asset-tamper"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid upload ticket signature");
    }

    @Test
    void testMismatchedAssetIdRejected() {
        String token = ticketService.issueTicket("asset-A", "usr-1", "asset", "image/png", 5000, null, java.time.Instant.now().plusSeconds(300));

        assertThatThrownBy(() -> ticketService.validateAndConsumeTicket(token, "asset-B"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not match target asset ID");
    }
}

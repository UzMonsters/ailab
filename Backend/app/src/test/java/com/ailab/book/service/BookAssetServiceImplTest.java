package com.ailab.book.service;

import com.ailab.book.domain.AssetStatus;
import com.ailab.book.domain.BookAsset;
import com.ailab.book.dto.BookDtos;
import com.ailab.book.repository.BookAssetRepository;
import com.ailab.storage.LocalObjectStorageService;
import com.ailab.storage.StorageKeyFactory;
import com.ailab.storage.StorageProperties;
import com.ailab.storage.StorageUpload;
import com.ailab.storage.upload.TransientUploadTicketStore;
import com.ailab.storage.upload.UploadScope;
import com.ailab.storage.upload.UploadTicketClaimCommand;
import com.ailab.storage.upload.UploadTicketEntity;
import com.ailab.storage.upload.UploadTicketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpStatus;
import org.springframework.util.unit.DataSize;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookAssetServiceImplTest {
    private static final byte[] PNG_BYTES = new byte[] {
            (byte) 0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0
    };

    @TempDir
    Path tempDir;

    @Test
    void completesOnlyAfterDurableUploadExists() {
        Map<String, BookAsset> assets = new ConcurrentHashMap<>();
        BookAssetRepository repository = mock(BookAssetRepository.class);
        when(repository.save(org.mockito.ArgumentMatchers.any(BookAsset.class))).thenAnswer(invocation -> {
            BookAsset asset = invocation.getArgument(0);
            assets.put(asset.getId(), asset);
            return asset;
        });
        when(repository.findById(org.mockito.ArgumentMatchers.anyString()))
                .thenAnswer(invocation -> Optional.ofNullable(assets.get(invocation.getArgument(0))));

        UploadTicketService ticketService = new UploadTicketService(new TransientUploadTicketStore());
        LocalObjectStorageService storage = new LocalObjectStorageService(tempDir.toString());
        BookAssetServiceImpl service = new BookAssetServiceImpl(
                repository,
                ticketService,
                storage,
                new StorageKeyFactory(),
                new StorageProperties("local", null, "us-east-1", "ailab-test", null, null, false, false,
                        Duration.ofMinutes(10), DataSize.ofMegabytes(10), DataSize.ofMegabytes(2),
                        DataSize.ofMegabytes(10), tempDir.toString())
        );

        BookDtos.AssetUploadUrlsResponse uploadUrls = service.generateUploadUrls(List.of(
                new BookDtos.FileUploadSpec("diagram.png", "diagram.png", "image/png", "image/png",
                        (long) PNG_BYTES.length, (long) PNG_BYTES.length, null, "IMAGE", null)
        ), "admin-user");
        BookDtos.AssetUploadTicket issued = uploadUrls.uploads().getFirst();

        assertThatThrownBy(() -> service.completeAsset(issued.assetId(), new BookDtos.CompleteAssetRequest(
                null, Map.of("en", "diagram"), null, null, 1, 1), "admin-user"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(e -> ((ResponseStatusException) e).getStatusCode())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        String rawToken = issued.uploadUrl().substring(issued.uploadUrl().indexOf("ticket=") + "ticket=".length());
        UploadTicketEntity claimed = ticketService.claimForUpload(new UploadTicketClaimCommand(
                rawToken,
                issued.assetId(),
                "admin-user",
                UploadScope.BOOK_ASSET,
                null,
                null,
                null
        ));
        storage.upload(new StorageUpload(claimed.getStorageKey(), "image/png", PNG_BYTES.length,
                new ByteArrayInputStream(PNG_BYTES)));
        ticketService.markUploaded(claimed, "sha256:abc123", PNG_BYTES.length, "image/png");

        BookDtos.AssetResponse completed = service.completeAsset(issued.assetId(), new BookDtos.CompleteAssetRequest(
                null, Map.of("en", "diagram"), null, null, 1, 1), "admin-user");

        assertThat(completed.status()).isEqualTo(AssetStatus.READY);
        assertThat(completed.mimeType()).isEqualTo("image/png");
        assertThat(completed.sizeBytes()).isEqualTo(PNG_BYTES.length);
        assertThat(completed.alt()).containsEntry("en", "diagram");
    }
}

# S3 Object Storage Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace AI Laboratory's local and placeholder upload flows with one S3-compatible object-storage layer modeled on `RepairSystem`, then migrate admin/book assets, avatars, and workspace previews onto it.

**Architecture:** Add a shared storage port and two adapters in the backend app module: S3-compatible storage when `app.storage.enabled=true`, and local filesystem storage when disabled or used by tests. Keep existing public URL shapes stable where possible, but make upload tickets durable enough to validate size, checksum, MIME, owner, scope, and completion state across restarts.

**Tech Stack:** Java 21, Spring Boot 3.4.5, Maven, AWS SDK for Java v2 S3, PostgreSQL/Flyway, Spring MVC, Spring Security, existing Next.js frontend upload clients.

**Spec:** User request from 2026-09-25 plus code analysis of `C:\Users\User\Documents\RepairSystem` and `C:\Users\User\Documents\ailab`.

## Global Constraints

- Reuse the `RepairSystem` pattern: `StorageProperties`, `ObjectStorageService`, `S3ObjectStorageService`, and disabled/local fallback behind an interface.
- Do not make buckets public. Downloads must go through backend authorization or short-lived signed URLs.
- Do not trust client-provided download URLs, MIME types, sizes, or checksums without server-side verification.
- Keep existing frontend upload contract compatible for `/api/v1/admin/assets/upload-urls`, `/api/v1/assets/upload/{fileId}`, `/api/v1/assets/raw/{fileId}`, `/api/v1/workspaces/{id}/preview-upload-urls`, and avatar upload endpoints unless a task explicitly says otherwise.
- Cap in-memory upload bodies at the current feature limits: admin/book assets 10 MB, avatars 2 MB, workspace previews 10 MB. Streaming can follow later, but this implementation must not introduce unbounded reads.
- S3 settings must be environment-driven and work with AWS S3 and MinIO-compatible endpoints.
- Existing tests that use local storage must keep passing without real S3 credentials.

## Analysis Findings

- `RepairSystem` stores through `S3ObjectStorageService`, configured by `app.storage.*`, using AWS SDK v2, optional endpoint override, path-style access, optional bucket creation, upload, download, presigned download URL, delete, and exists.
- `RepairSystem` also has a disabled implementation so tests can run with storage disabled. It bridges the storage adapter into a neutral media contract rather than tying new features directly to legacy attachments.
- `ailab` currently has three storage mechanisms:
  - `AssetStorageService` writes admin/book uploads to local disk and keeps metadata in an in-memory cache.
  - `WorkspacePreviewService` writes preview binaries to local disk and keeps pending upload state in an in-memory map.
  - `UserAccountServiceImpl` creates avatar upload URLs that do not issue a real ticket and completes avatars by constructing `/api/v1/assets/raw/{assetId}/avatar.webp` without proving the object exists.
- `ailab` already has frontend clients expecting upload tickets and then `PUT` upload URLs. This makes a backend-proxy upload path safer for a first phase than browser-to-S3 presigned PUTs, because the current backend can validate auth, ticket scope, MIME, checksum, and ownership before writing to S3.
- `ailab` has known audit findings for anonymous arbitrary upload, restart-lost upload state, and temporary filesystem storage. The plan addresses those by requiring authentication or signed tickets, persisting upload tickets, and storing objects in S3-compatible storage.
- The broad workspace preview integration tests are currently excluded in `Backend/app/pom.xml`. The storage work should add focused service/controller tests that run in the default suite, then a separate optional integration profile for MinIO/S3.

## File Structure

- Modify: `Backend/pom.xml` to add an `aws.sdk.version` property.
- Modify: `Backend/app/pom.xml` to add `software.amazon.awssdk:s3` and test dependencies for storage integration.
- Modify: `Backend/app/src/main/resources/application.properties` to add `app.storage.*`, upload limits, and URL TTL settings.
- Modify: `Backend/app/src/test/resources/application-test.properties` if present, otherwise create it, to force local/test storage.
- Create: `Backend/app/src/main/java/com/ailab/storage/ObjectStorageService.java` as the shared port.
- Create: `Backend/app/src/main/java/com/ailab/storage/StorageProperties.java` for configuration and validation.
- Create: `Backend/app/src/main/java/com/ailab/storage/StorageUpload.java`, `StoredObject.java`, `StoredObjectDownload.java`, and `StorageException.java`.
- Create: `Backend/app/src/main/java/com/ailab/storage/S3ObjectStorageService.java` based on the `RepairSystem` adapter, adjusted for package names and Spring Boot 3.
- Create: `Backend/app/src/main/java/com/ailab/storage/LocalObjectStorageService.java` for local dev and unit tests.
- Create: `Backend/app/src/main/java/com/ailab/storage/StorageKeyFactory.java` for safe, scoped keys such as `assets/{assetId}`, `avatars/{userId}/{assetId}`, and `workspaces/{workspaceId}/previews/{previewId}/{assetId}`.
- Create: `Backend/app/src/main/resources/db/migration/workspace/V6__create_upload_tickets.sql` for durable upload-ticket state.
- Create: `Backend/app/src/main/java/com/ailab/storage/upload/UploadTicketEntity.java`, `UploadTicketRepository.java`, `UploadScope.java`, `UploadTicketService.java`, and DTO records.
- Modify: `Backend/app/src/main/java/com/ailab/admin/assets/AssetUploadTicketService.java` to delegate to the durable service or replace it with a compatibility wrapper.
- Modify: `Backend/app/src/main/java/com/ailab/admin/assets/AssetStorageService.java` to become a thin wrapper over `ObjectStorageService` or remove direct filesystem behavior after consumers migrate.
- Modify: `Backend/app/src/main/java/com/ailab/admin/assets/PublicAssetController.java` to validate durable tickets and write/read via `ObjectStorageService`.
- Modify: `Backend/app/src/main/java/com/ailab/admin/assets/AdminAssetServiceImpl.java` to issue durable upload tickets and complete from stored metadata.
- Modify: `Backend/app/src/main/java/com/ailab/book/service/BookAssetServiceImpl.java` to issue the same durable tickets and stop constructing fake storage URLs.
- Modify: `Backend/identity-module/src/main/java/com/ailab/user/service/UserAccountServiceImpl.java` only if the shared upload service is moved into a common module. Preferred first phase: expose avatar ticket/complete through app-module adapter instead of adding app dependency into identity.
- Modify: `Backend/identity-module/src/main/java/com/ailab/user/controller/UserController.java` only if avatar upload completion stays in identity. Preferred first phase: add app-level avatar storage facade using the existing identity service only to update the avatar URL.
- Modify: `Backend/app/src/main/java/com/ailab/workspace/service/WorkspacePreviewService.java` to persist preview upload tickets and store preview binaries via `ObjectStorageService`.
- Modify: `docker-compose.yml`, `.env.example`, and `render.yaml` to expose storage variables.
- Create tests under `Backend/app/src/test/java/com/ailab/storage`, `Backend/app/src/test/java/com/ailab/admin/assets`, and `Backend/app/src/test/java/com/ailab/workspace`.
- Modify identity tests only for the avatar contract shape if endpoints remain in identity.

## Review Focus

- Expired or reused upload tickets must not write or complete an object.
- A ticket for one asset, user, workspace, or scope must not upload to another target.
- Declared MIME must match detected content for images, SVG, PDF, and JSON where those are allowed.
- A successful object write followed by metadata failure must either keep retriable state or delete the object; no ready row should point at a missing object.
- App restart between ticket issue, upload, and complete must not lose upload state.

---

### Task 1: Add Shared Object Storage Foundation

**Files:**
- Modify: `Backend/pom.xml`
- Modify: `Backend/app/pom.xml`
- Modify: `Backend/app/src/main/resources/application.properties`
- Create: `Backend/app/src/main/java/com/ailab/storage/ObjectStorageService.java`
- Create: `Backend/app/src/main/java/com/ailab/storage/StorageProperties.java`
- Create: `Backend/app/src/main/java/com/ailab/storage/StorageUpload.java`
- Create: `Backend/app/src/main/java/com/ailab/storage/StoredObject.java`
- Create: `Backend/app/src/main/java/com/ailab/storage/StoredObjectDownload.java`
- Create: `Backend/app/src/main/java/com/ailab/storage/StorageException.java`
- Create: `Backend/app/src/main/java/com/ailab/storage/StorageKeyFactory.java`
- Create: `Backend/app/src/main/java/com/ailab/storage/S3ObjectStorageService.java`
- Create: `Backend/app/src/main/java/com/ailab/storage/LocalObjectStorageService.java`
- Test: `Backend/app/src/test/java/com/ailab/storage/StoragePropertiesTest.java`
- Test: `Backend/app/src/test/java/com/ailab/storage/LocalObjectStorageServiceTest.java`

**Interfaces:**
- Produces: `ObjectStorageService.upload(StorageUpload): StoredObject`, `download(String): StoredObjectDownload`, `createDownloadUrl(String, String, Duration): URI`, `delete(String): void`, `exists(String): boolean`.
- Produces: `StorageKeyFactory.assetKey(String)`, `avatarKey(String, String)`, `workspacePreviewKey(String, String, String)`.
- Consumes: no application domain services.

- [ ] **Step 1: Add the Maven AWS SDK property and dependency**

In `Backend/pom.xml`, add:

```xml
<aws.sdk.version>2.30.31</aws.sdk.version>
```

In `Backend/app/pom.xml`, add:

```xml
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>${aws.sdk.version}</version>
</dependency>
```

- [ ] **Step 2: Add storage configuration**

Add to `Backend/app/src/main/resources/application.properties`:

```properties
app.storage.enabled=${APP_STORAGE_ENABLED:false}
app.storage.provider=${APP_STORAGE_PROVIDER:local}
app.storage.endpoint=${APP_STORAGE_ENDPOINT:}
app.storage.region=${APP_STORAGE_REGION:us-east-1}
app.storage.bucket=${APP_STORAGE_BUCKET:ailab-local}
app.storage.access-key=${APP_STORAGE_ACCESS_KEY:}
app.storage.secret-key=${APP_STORAGE_SECRET_KEY:}
app.storage.path-style=${APP_STORAGE_PATH_STYLE:true}
app.storage.create-bucket=${APP_STORAGE_CREATE_BUCKET:false}
app.storage.download-url-ttl=${APP_STORAGE_DOWNLOAD_URL_TTL:PT10M}
app.storage.max-asset-size=${APP_STORAGE_MAX_ASSET_SIZE:10MB}
app.storage.max-avatar-size=${APP_STORAGE_MAX_AVATAR_SIZE:2MB}
app.storage.max-preview-size=${APP_STORAGE_MAX_PREVIEW_SIZE:10MB}
app.storage.local-dir=${APP_STORAGE_LOCAL_DIR:./storage/objects}
```

- [ ] **Step 3: Write failing property validation tests**

Create `StoragePropertiesTest`:

```java
package com.ailab.storage;

import org.junit.jupiter.api.Test;
import org.springframework.util.unit.DataSize;

import java.net.URI;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StoragePropertiesTest {
    @Test
    void enabledS3RequiresCredentials() {
        assertThatThrownBy(() -> new StorageProperties(
                true, "s3", URI.create("http://localhost:9000"), "us-east-1", "ailab",
                "", "", true, false, Duration.ofMinutes(10),
                DataSize.ofMegabytes(10), DataSize.ofMegabytes(2), DataSize.ofMegabytes(10),
                "./storage/objects"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("access key");
    }

    @Test
    void downloadTtlMustStayBounded() {
        assertThatThrownBy(() -> new StorageProperties(
                false, "local", null, "us-east-1", "ailab",
                "", "", true, false, Duration.ofHours(2),
                DataSize.ofMegabytes(10), DataSize.ofMegabytes(2), DataSize.ofMegabytes(10),
                "./storage/objects"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TTL");
    }
}
```

- [ ] **Step 4: Write failing local storage tests**

Create `LocalObjectStorageServiceTest`:

```java
package com.ailab.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class LocalObjectStorageServiceTest {
    @TempDir
    Path tempDir;

    @Test
    void storesLoadsChecksExistenceAndDeletesObject() throws Exception {
        LocalObjectStorageService service = new LocalObjectStorageService(tempDir.toString());
        StoredObject stored = service.upload(new StorageUpload(
                "assets/asset_123",
                "image/webp",
                5,
                new ByteArrayInputStream("hello".getBytes(StandardCharsets.UTF_8))));

        assertThat(stored.storageKey()).isEqualTo("assets/asset_123");
        assertThat(service.exists("assets/asset_123")).isTrue();
        try (StoredObjectDownload download = service.download("assets/asset_123")) {
            assertThat(download.inputStream().readAllBytes()).isEqualTo("hello".getBytes(StandardCharsets.UTF_8));
            assertThat(download.contentType()).isEqualTo("image/webp");
            assertThat(download.sizeBytes()).isEqualTo(5);
        }

        assertThat(service.createDownloadUrl("assets/asset_123", "asset.webp", Duration.ofMinutes(5)).toString())
                .isEqualTo("/api/v1/assets/raw/asset_123/asset.webp");

        service.delete("assets/asset_123");
        assertThat(Files.exists(tempDir.resolve("assets").resolve("asset_123"))).isFalse();
    }
}
```

- [ ] **Step 5: Implement storage records and interface**

Implement:

```java
package com.ailab.storage;

import java.net.URI;
import java.time.Duration;

public interface ObjectStorageService {
    StoredObject upload(StorageUpload command);
    StoredObjectDownload download(String storageKey);
    URI createDownloadUrl(String storageKey, String downloadFileName, Duration ttl);
    void delete(String storageKey);
    boolean exists(String storageKey);
}
```

Use records:

```java
public record StorageUpload(String storageKey, String contentType, long sizeBytes, InputStream inputStream) {}
public record StoredObject(String storageKey, String contentType, long sizeBytes) {}
```

Implement `StoredObjectDownload` as an `AutoCloseable` record wrapping `InputStream` so controllers can stream or read safely.

- [ ] **Step 6: Implement S3 and local adapters**

Use `RepairSystem`'s `S3ObjectStorageService` as the base, with:

```java
@Service
@ConditionalOnProperty(prefix = "app.storage", name = "provider", havingValue = "s3")
public class S3ObjectStorageService implements ObjectStorageService {
}
```

Use local fallback:

```java
@Service
@ConditionalOnProperty(prefix = "app.storage", name = "provider", havingValue = "local", matchIfMissing = true)
public class LocalObjectStorageService implements ObjectStorageService {
}
```

The local adapter must sanitize storage keys by resolving against `localDir` and rejecting paths whose normalized target escapes `localDir`.

- [ ] **Step 7: Run storage tests**

Run:

```bash
cd Backend
./mvnw -pl app test -Dtest=StoragePropertiesTest,LocalObjectStorageServiceTest
```

Expected: both tests pass.

- [ ] **Step 8: Commit**

```bash
git add Backend/pom.xml Backend/app/pom.xml Backend/app/src/main/resources/application.properties Backend/app/src/main/java/com/ailab/storage Backend/app/src/test/java/com/ailab/storage
git commit -m "feat: add object storage foundation"
```

### Task 2: Make Upload Tickets Durable and Scope-Bound

**Files:**
- Create: `Backend/app/src/main/resources/db/migration/workspace/V6__create_upload_tickets.sql`
- Create: `Backend/app/src/main/java/com/ailab/storage/upload/UploadScope.java`
- Create: `Backend/app/src/main/java/com/ailab/storage/upload/UploadTicketEntity.java`
- Create: `Backend/app/src/main/java/com/ailab/storage/upload/UploadTicketRepository.java`
- Create: `Backend/app/src/main/java/com/ailab/storage/upload/UploadTicketService.java`
- Modify: `Backend/app/src/main/java/com/ailab/admin/assets/AssetUploadTicketService.java`
- Test: `Backend/app/src/test/java/com/ailab/storage/upload/UploadTicketServiceTest.java`

**Interfaces:**
- Consumes: `StorageKeyFactory`.
- Produces: `UploadTicketService.issue(...)`, `validateForUpload(...)`, `markUploaded(...)`, `validateForComplete(...)`, `markCompleted(...)`.

- [ ] **Step 1: Add upload-ticket migration**

Create table:

```sql
CREATE TABLE upload_tickets (
    id VARCHAR(80) PRIMARY KEY,
    asset_id VARCHAR(120) NOT NULL,
    storage_key VARCHAR(700) NOT NULL UNIQUE,
    actor_id VARCHAR(120) NOT NULL,
    scope VARCHAR(40) NOT NULL,
    allowed_mime VARCHAR(120) NOT NULL,
    max_size_bytes BIGINT NOT NULL,
    expected_checksum VARCHAR(120),
    actual_checksum VARCHAR(120),
    actual_size_bytes BIGINT,
    actual_mime VARCHAR(120),
    status VARCHAR(24) NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    uploaded_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT upload_tickets_status_chk CHECK (status IN ('ISSUED','UPLOADED','COMPLETED','EXPIRED')),
    CONSTRAINT upload_tickets_size_chk CHECK (max_size_bytes > 0),
    CONSTRAINT upload_tickets_actual_size_chk CHECK (actual_size_bytes IS NULL OR actual_size_bytes > 0)
);

CREATE INDEX idx_upload_tickets_actor_scope ON upload_tickets(actor_id, scope);
CREATE INDEX idx_upload_tickets_asset_scope ON upload_tickets(asset_id, scope);
CREATE INDEX idx_upload_tickets_expires_at ON upload_tickets(expires_at);
```

- [ ] **Step 2: Write failing service tests**

Create tests for:

```java
@Test
void cannotUploadWithTicketForDifferentAsset() {
    UploadTicket issued = service.issue("asset_a", "user_1", UploadScope.ADMIN_ASSET,
            "assets/asset_a", "image/png", 1024, null, Duration.ofMinutes(15));

    assertThatThrownBy(() -> service.validateForUpload(issued.token(), "asset_b", "user_1", UploadScope.ADMIN_ASSET))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("UPLOAD_TICKET_TARGET_MISMATCH");
}

@Test
void cannotReuseCompletedTicket() {
    UploadTicket issued = service.issue("asset_a", "user_1", UploadScope.ADMIN_ASSET,
            "assets/asset_a", "image/png", 1024, null, Duration.ofMinutes(15));
    UploadTicketEntity valid = service.validateForUpload(issued.token(), "asset_a", "user_1", UploadScope.ADMIN_ASSET);
    service.markUploaded(valid, "sha256:abc", 10, "image/png");
    service.markCompleted("asset_a", "user_1", UploadScope.ADMIN_ASSET);

    assertThatThrownBy(() -> service.validateForUpload(issued.token(), "asset_a", "user_1", UploadScope.ADMIN_ASSET))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("UPLOAD_TICKET_ALREADY_COMPLETED");
}
```

- [ ] **Step 3: Implement entity, repository, enum, and service**

Use statuses `ISSUED`, `UPLOADED`, `COMPLETED`, `EXPIRED`. The token can be the UUID ticket id because the durable row has the authorization facts; keep HMAC only if backwards compatibility with existing query-string ticket values is necessary during rollout.

The service must reject:

```java
if (!ticket.assetId().equals(expectedAssetId)) throw mismatch;
if (!ticket.actorId().equals(actorId)) throw forbidden;
if (ticket.scope() != expectedScope) throw forbidden;
if (ticket.expiresAt().isBefore(Instant.now())) throw gone;
if (ticket.status() == COMPLETED) throw conflict;
```

- [ ] **Step 4: Keep compatibility wrapper**

Modify `AssetUploadTicketService` so `detectMimeType(byte[])` remains available to existing code, but new ticket issue/validation methods call `UploadTicketService`.

- [ ] **Step 5: Run ticket tests**

```bash
cd Backend
./mvnw -pl app test -Dtest=UploadTicketServiceTest
```

Expected: pass.

- [ ] **Step 6: Commit**

```bash
git add Backend/app/src/main/resources/db/migration/workspace/V6__create_upload_tickets.sql Backend/app/src/main/java/com/ailab/storage/upload Backend/app/src/main/java/com/ailab/admin/assets/AssetUploadTicketService.java Backend/app/src/test/java/com/ailab/storage/upload
git commit -m "feat: persist scoped upload tickets"
```

### Task 3: Migrate Admin and Book Assets to Object Storage

**Files:**
- Modify: `Backend/app/src/main/java/com/ailab/admin/assets/AdminAssetServiceImpl.java`
- Modify: `Backend/app/src/main/java/com/ailab/book/service/BookAssetServiceImpl.java`
- Modify: `Backend/app/src/main/java/com/ailab/admin/assets/PublicAssetController.java`
- Modify: `Backend/app/src/main/java/com/ailab/admin/assets/AssetStorageService.java`
- Test: `Backend/app/src/test/java/com/ailab/admin/assets/PublicAssetControllerTest.java`
- Test: `Backend/app/src/test/java/com/ailab/admin/assets/AdminAssetStorageFlowTest.java`

**Interfaces:**
- Consumes: `UploadTicketService`, `ObjectStorageService`, `StorageKeyFactory`.
- Produces: unchanged frontend-facing upload URL and raw asset URL contract.

- [ ] **Step 1: Write failing upload-controller tests**

Add controller tests proving:

```java
@Test
void anonymousUploadWithoutTicketIsRejected() throws Exception {
    mockMvc.perform(put("/api/v1/assets/upload/asset_1")
            .contentType("image/png")
            .content(pngBytes()))
            .andExpect(status().isUnauthorized());
}

@Test
void ticketUploadStoresObjectAndCompleteReadsVerifiedMetadata() throws Exception {
    // 1. admin requests upload URL
    // 2. PUT bytes to returned uploadUrl
    // 3. complete asset with checksum
    // 4. GET admin asset returns READY and sha256 checksum
    // 5. GET /api/v1/assets/raw/{assetId}/file.png returns bytes from ObjectStorageService
}
```

- [ ] **Step 2: Update `AdminAssetServiceImpl.generateUploadUrls`**

Generate:

```java
String assetId = "ast_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
String storageKey = storageKeyFactory.assetKey(assetId);
UploadTicket ticket = uploadTicketService.issue(assetId, "admin", UploadScope.ADMIN_ASSET,
        storageKey, contentType, MAX_IMAGE_BYTES, checksum, Duration.ofHours(1));
String uploadUrl = "/api/v1/assets/upload/" + assetId + "?ticket=" + ticket.token();
String downloadUrl = "/api/v1/assets/raw/" + assetId + "/" + UriUtils.encodePathSegment(filename, StandardCharsets.UTF_8);
```

- [ ] **Step 3: Update `BookAssetServiceImpl.generateUploadUrls`**

Use the same storage/ticket flow with `UploadScope.BOOK_ASSET` and actor `"system"` until the current user id is available in the book service.

- [ ] **Step 4: Update `PublicAssetController.uploadBinary`**

Validation order:

```java
UploadTicketEntity ticket = uploadTicketService.validateForUpload(effectiveTicket, fileId, actorId, scopeFromTicket);
if (data == null || data.length == 0) throw 400 VALIDATION_ERROR;
if (data.length > ticket.maxSizeBytes()) throw 413 ASSET_TOO_LARGE;
String declared = normalizeContentType(request.getContentType());
String detected = AssetUploadTicketService.detectMimeType(data);
if (!declared.equals(ticket.allowedMime())) throw 415 UNSUPPORTED_MEDIA_TYPE;
if (!mimeMatchesDeclared(detected, declared)) throw 415 UNSUPPORTED_MEDIA_TYPE;
StoredObject stored = objectStorageService.upload(new StorageUpload(ticket.storageKey(), declared, data.length, new ByteArrayInputStream(data)));
uploadTicketService.markUploaded(ticket, "sha256:" + sha256(data), stored.sizeBytes(), declared);
```

- [ ] **Step 5: Update `PublicAssetController.downloadAsset`**

Look up the asset row if present, resolve its ticket/storage key, download from `ObjectStorageService`, set `Content-Type`, `ETag`, and return `404 RESOURCE_NOT_FOUND` when the object is missing.

- [ ] **Step 6: Update `completeAsset`**

Complete only after a matching uploaded ticket exists:

```java
UploadTicketEntity upload = uploadTicketService.validateForComplete(assetId, "admin", UploadScope.ADMIN_ASSET);
if (expectedChecksum != null && !normalize(expectedChecksum).equals(upload.actualChecksum())) throw CHECKSUM_MISMATCH;
asset.setSizeBytes(upload.actualSizeBytes());
asset.setChecksum(upload.actualChecksum());
asset.setStatus(AssetStatus.READY);
uploadTicketService.markCompleted(assetId, "admin", UploadScope.ADMIN_ASSET);
```

- [ ] **Step 7: Run asset tests**

```bash
cd Backend
./mvnw -pl app test -Dtest=PublicAssetControllerTest,AdminAssetStorageFlowTest,AdminAssetControllerTest
```

Expected: pass.

- [ ] **Step 8: Commit**

```bash
git add Backend/app/src/main/java/com/ailab/admin/assets Backend/app/src/main/java/com/ailab/book/service/BookAssetServiceImpl.java Backend/app/src/test/java/com/ailab/admin/assets
git commit -m "feat: store admin and book assets in object storage"
```

### Task 4: Make Avatar Uploads Real

**Files:**
- Modify: `Backend/identity-module/src/main/java/com/ailab/user/service/UserAccountServiceImpl.java` if app can expose `UploadTicketService` into identity cleanly.
- Preferred alternative create: `Backend/app/src/main/java/com/ailab/user/avatar/UserAvatarStorageController.java`
- Preferred alternative create: `Backend/app/src/main/java/com/ailab/user/avatar/UserAvatarStorageService.java`
- Modify: `Backend/identity-module/src/main/java/com/ailab/user/api/UserDtos.java` only if response fields need a storage key or checksum.
- Test: `Backend/app/src/test/java/com/ailab/user/avatar/UserAvatarStorageControllerTest.java`
- Test: keep existing `Backend/identity-module/src/test/java/com/ailab/user/service/UserAccountServiceImplTest.java` green.

**Interfaces:**
- Consumes: `UploadTicketService`, `ObjectStorageService`, `StorageKeyFactory`, existing identity `UserAccountService.updateAvatar`.
- Produces: real avatar upload ticket and completion that verifies object existence and ownership.

- [ ] **Step 1: Write failing avatar tests**

Use app-level MockMvc if the avatar storage controller lives in app:

```java
@Test
void avatarCompleteFailsWhenObjectWasNotUploaded() throws Exception {
    String assetId = requestAvatarUpload("image/webp", 1000).assetId();

    mockMvc.perform(post("/api/v1/users/me/avatar/complete")
            .header("Authorization", token)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(Map.of("assetId", assetId))))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.code").value("AVATAR_UPLOAD_INCOMPLETE"));
}

@Test
void avatarUploadCannotCompleteForAnotherUser() throws Exception {
    AvatarUploadTicketResponse issuedByAlice = requestAvatarUploadAs(aliceToken, "image/png", 1000);
    uploadAs(aliceToken, issuedByAlice.uploadUrl(), pngBytes());

    mockMvc.perform(post("/api/v1/users/me/avatar/complete")
            .header("Authorization", bobToken)
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(Map.of("assetId", issuedByAlice.assetId()))))
            .andExpect(status().isForbidden());
}
```

- [ ] **Step 2: Issue scoped avatar tickets**

Use:

```java
String assetId = "avatar_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
String storageKey = storageKeyFactory.avatarKey(userId, assetId);
UploadTicket ticket = uploadTicketService.issue(assetId, userId, UploadScope.USER_AVATAR,
        storageKey, request.mimeType().toLowerCase(), MAX_AVATAR_BYTES, request.checksum(), Duration.ofMinutes(15));
String uploadUrl = "/api/v1/assets/upload/" + assetId + "?ticket=" + ticket.token();
```

- [ ] **Step 3: Complete only after verified upload**

Resolve the durable uploaded ticket for `assetId`, current `userId`, and `USER_AVATAR`; verify checksum if provided; set avatar URL to:

```java
"/api/v1/assets/raw/" + assetId + "/avatar.webp"
```

Do not update the user profile if the object does not exist.

- [ ] **Step 4: Decide module direction**

Use the preferred app-level controller if identity cannot depend on app storage classes without creating a circular dependency. Keep URL paths identical so the frontend does not change.

- [ ] **Step 5: Run avatar tests**

```bash
cd Backend
./mvnw -pl app test -Dtest=UserAvatarStorageControllerTest
./mvnw -pl identity-module test -Dtest=UserAccountServiceImplTest,UserControllerTest
```

Expected: pass.

- [ ] **Step 6: Commit**

```bash
git add Backend/app/src/main/java/com/ailab/user/avatar Backend/app/src/test/java/com/ailab/user/avatar Backend/identity-module/src/main/java/com/ailab/user Backend/identity-module/src/test/java/com/ailab/user
git commit -m "feat: verify avatar uploads through object storage"
```

### Task 5: Migrate Workspace Previews to Durable Object Storage

**Files:**
- Modify: `Backend/app/src/main/java/com/ailab/workspace/service/WorkspacePreviewService.java`
- Modify: `Backend/app/src/main/java/com/ailab/workspace/controller/WorkspaceController.java` only if response type changes for streaming.
- Test: `Backend/app/src/test/java/com/ailab/workspace/WorkspacePreviewStorageServiceTest.java`
- Test: `Backend/app/src/test/java/com/ailab/workspace/WorkspaceMeasurementsAndPreviewsIntegrationTest.java`

**Interfaces:**
- Consumes: `UploadTicketService`, `ObjectStorageService`, `StorageKeyFactory`.
- Produces: existing preview upload URLs, upload endpoint, completion endpoint, and preview asset read endpoint.

- [ ] **Step 1: Write failing restart-safe preview test**

Service test:

```java
@Test
void completePreviewUsesPersistedUploadTicketInsteadOfInMemoryPendingMap() {
    PreviewUploadUrlsResponse response = service.createUploadUrls(workspaceId, userId, request);
    UploadTarget dark = darkTarget(response);

    service.uploadAsset(workspaceId, userId, response.previewId(), dark.assetId(), webpBytes(), "image/webp");

    WorkspacePreviewService recreated = new WorkspacePreviewService(
            previewRepository, workspaceRepository, memberService, objectStorageService, uploadTicketService, storageKeyFactory);

    WorkspacePreviewDto completed = recreated.completePreview(workspaceId, userId, response.previewId(),
            new CompletePreviewRequest(1L, List.of(new AssetResult("DARK", dark.assetId(), null, sha256(webpBytes()))), "chemistry-default-01"));

    assertThat(completed.variants().get("dark").url()).contains("/api/v1/workspaces/");
}
```

- [ ] **Step 2: Replace pending map with durable tickets**

In `createUploadUrls`, persist one `WORKSPACE_PREVIEW` ticket per variant:

```java
String storageKey = storageKeyFactory.workspacePreviewKey(workspaceId, previewId, assetId);
UploadTicket ticket = uploadTicketService.issue(assetId, actorUserId, UploadScope.WORKSPACE_PREVIEW,
        storageKey, mimeType, storageProperties.maxPreviewSize().toBytes(), checksum, Duration.ofMinutes(15));
String uploadUrl = "/api/v1/workspaces/" + workspaceId + "/previews/" + previewId + "/assets/" + assetId + "/upload?ticket=" + ticket.token();
```

- [ ] **Step 3: Store preview uploads via object storage**

Validate permission, target ids, ticket, expiry, size, and MIME, then call `objectStorageService.upload`.

- [ ] **Step 4: Complete preview from persisted uploaded tickets**

For each asset in `CompletePreviewRequest`, load matching uploaded ticket by asset id, actor id, and scope. Verify checksum and build URLs:

```java
"/api/v1/workspaces/" + workspaceId + "/previews/" + previewId + "/assets/" + assetId
```

Keep stale version behavior unchanged.

- [ ] **Step 5: Serve preview asset from object storage**

Resolve storage key from uploaded/completed ticket, call `objectStorageService.download`, and return bytes with the stored content type. Return `404 PREVIEW_ASSET_NOT_FOUND` when the object is missing.

- [ ] **Step 6: Run preview tests**

```bash
cd Backend
./mvnw -pl app test -Dtest=WorkspacePreviewStorageServiceTest,WorkspaceMeasurementsAndPreviewsIntegrationTest
```

Expected: preview tests pass. If the integration test remains excluded from normal Maven runs, execute it explicitly as shown.

- [ ] **Step 7: Commit**

```bash
git add Backend/app/src/main/java/com/ailab/workspace Backend/app/src/test/java/com/ailab/workspace
git commit -m "feat: persist workspace previews in object storage"
```

### Task 6: Add Local MinIO and Production Configuration

**Files:**
- Modify: `.env.example`
- Modify: `docker-compose.yml`
- Modify: `render.yaml`
- Modify: `Backend/README.md`
- Optional create: `Backend/app/src/test/java/com/ailab/storage/S3ObjectStorageServiceIntegrationTest.java`

**Interfaces:**
- Consumes: `StorageProperties`.
- Produces: documented local and production configuration.

- [ ] **Step 1: Add environment examples**

Add to `.env.example`:

```properties
APP_STORAGE_ENABLED=true
APP_STORAGE_PROVIDER=s3
APP_STORAGE_ENDPOINT=http://minio:9000
APP_STORAGE_REGION=us-east-1
APP_STORAGE_BUCKET=ailab-local
APP_STORAGE_ACCESS_KEY=minioadmin
APP_STORAGE_SECRET_KEY=minioadmin
APP_STORAGE_PATH_STYLE=true
APP_STORAGE_CREATE_BUCKET=true
APP_STORAGE_DOWNLOAD_URL_TTL=PT10M
APP_STORAGE_MAX_ASSET_SIZE=10MB
APP_STORAGE_MAX_AVATAR_SIZE=2MB
APP_STORAGE_MAX_PREVIEW_SIZE=10MB
```

- [ ] **Step 2: Add MinIO to `docker-compose.yml`**

Add service:

```yaml
  minio:
    image: minio/minio:RELEASE.2025-04-22T22-12-26Z
    container_name: ailab-minio
    command: server /data --console-address ":9001"
    environment:
      MINIO_ROOT_USER: ${APP_STORAGE_ACCESS_KEY:-minioadmin}
      MINIO_ROOT_PASSWORD: ${APP_STORAGE_SECRET_KEY:-minioadmin}
    ports:
      - "9000:9000"
      - "9001:9001"
    volumes:
      - miniodata:/data
    networks:
      - ailab-network
```

Add `miniodata:` to volumes and add backend environment variables using the values from `.env.example`.

- [ ] **Step 3: Add Render env vars**

In `render.yaml`, add storage variables with `sync: false` for secrets:

```yaml
      - key: APP_STORAGE_ENABLED
        value: "true"
      - key: APP_STORAGE_PROVIDER
        value: s3
      - key: APP_STORAGE_ENDPOINT
        sync: false
      - key: APP_STORAGE_REGION
        sync: false
      - key: APP_STORAGE_BUCKET
        sync: false
      - key: APP_STORAGE_ACCESS_KEY
        sync: false
      - key: APP_STORAGE_SECRET_KEY
        sync: false
      - key: APP_STORAGE_PATH_STYLE
        value: "false"
      - key: APP_STORAGE_CREATE_BUCKET
        value: "false"
```

- [ ] **Step 4: Document operational behavior**

In `Backend/README.md`, add:

```markdown
### Object Storage

The backend stores uploaded assets, avatars, and workspace previews through `ObjectStorageService`.
Local development can use MinIO from Docker Compose with `APP_STORAGE_PROVIDER=s3`.
Tests default to `APP_STORAGE_PROVIDER=local` and do not require S3 credentials.

Required production variables:
`APP_STORAGE_PROVIDER=s3`, `APP_STORAGE_REGION`, `APP_STORAGE_BUCKET`, `APP_STORAGE_ACCESS_KEY`, and `APP_STORAGE_SECRET_KEY`.
Set `APP_STORAGE_ENDPOINT` only for S3-compatible providers such as MinIO or Cloudflare R2.
```

- [ ] **Step 5: Run configuration smoke tests**

```bash
cd Backend
./mvnw -pl app test -Dtest=StoragePropertiesTest,LocalObjectStorageServiceTest
```

Expected: pass without S3 credentials.

- [ ] **Step 6: Commit**

```bash
git add .env.example docker-compose.yml render.yaml Backend/README.md Backend/app/src/test/java/com/ailab/storage
git commit -m "chore: document object storage configuration"
```

### Task 7: Final Verification and Regression Sweep

**Files:**
- Modify tests only if existing assertions need the new real storage behavior.

**Interfaces:**
- Consumes all previous tasks.
- Produces verified S3-compatible storage integration.

- [ ] **Step 1: Run focused backend tests**

```bash
cd Backend
./mvnw -pl app test -Dtest=StoragePropertiesTest,LocalObjectStorageServiceTest,UploadTicketServiceTest,PublicAssetControllerTest,AdminAssetStorageFlowTest,WorkspacePreviewStorageServiceTest
```

Expected: pass.

- [ ] **Step 2: Run identity tests**

```bash
cd Backend
./mvnw -pl identity-module test -Dtest=UserAccountServiceImplTest,UserControllerTest
```

Expected: pass.

- [ ] **Step 3: Run default app module test suite**

```bash
cd Backend
./mvnw -pl app test
```

Expected: pass with existing configured exclusions.

- [ ] **Step 4: Run manual API smoke path locally**

Start Docker Compose, then:

```bash
docker compose up -d postgres minio backend
```

Verify:

```bash
curl -f http://localhost:8080/actuator/health
```

Then use an admin token to request an asset upload URL, `PUT` a small PNG/WebP through the returned URL, complete the asset, and download it from `/api/v1/assets/raw/{assetId}/{filename}`. Expected: status 200, matching content type, matching bytes.

- [ ] **Step 5: Inspect object store**

Open MinIO console at `http://localhost:9001`, log in with local credentials, and confirm objects are under scoped prefixes:

```text
assets/{assetId}
avatars/{userId}/{assetId}
workspaces/{workspaceId}/previews/{previewId}/{assetId}
```

- [ ] **Step 6: Commit final test updates**

```bash
git add Backend/app/src/test Backend/identity-module/src/test
git commit -m "test: verify object storage upload flows"
```

## Self-Review

- Spec coverage: the plan covers S3-compatible configuration, shared storage adapter, durable upload tickets, admin/book assets, avatars, workspace previews, local MinIO configuration, Render configuration, and verification.
- Placeholder scan: no task uses `TBD`, vague error handling, or unspecified tests. Each task names files, interfaces, and concrete expected behavior.
- Type consistency: storage interfaces use `StorageUpload`, `StoredObject`, `StoredObjectDownload`, and `ObjectStorageService` consistently across tasks.
- Review focus coverage: expired/reused tickets are covered in Task 2; wrong target/scope is covered in Tasks 2, 4, and 5; MIME validation is covered in Tasks 3 and 5; metadata/object consistency is covered in Tasks 3 and 5; restart safety is covered in Task 5.

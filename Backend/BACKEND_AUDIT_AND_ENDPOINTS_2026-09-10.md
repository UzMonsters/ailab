# Backend Audit, Endpoint Change List, and Improvement Plan

Date of audit: 2026-09-10

Scope: `Backend/`, backend-related Docker configuration, Docker runtime, logs, and test suite.

Status: documentation only.

Backend source code was not modified for this audit.

This is the full English version of the previous Russian audit.

## 1. Executive summary

The backend is not ready for a production release.

The main risks are not cosmetic.

They affect security, correctness, data durability, authorization, and frontend reliability.

The highest-priority problems are:

1. Secrets and default passwords are present in configuration fallbacks.

2. Public asset upload permits unauthenticated writes.

3. Admin dashboard and laboratory monitoring expose fabricated product data.

4. Important state is stored in memory or temporary directories.

5. Learning-level search produces a confirmed PostgreSQL error.

6. Share-link limits and settings concurrency controls are not correct.

7. Frontend-facing API contracts are incomplete and inconsistent.

The backend should not be treated as the source of truth for product analytics until mock paths are removed.

The backend should not be horizontally scaled until process-local state is moved out of memory.

The endpoint list in this document is a delivery backlog, not merely an observation list.

## 2. Priority definitions

### P0 — release blockers

P0 means a security issue, data exposure, authorization bypass, or a failure that must be fixed before public use.

### P1 — acceptance blockers

P1 means false data, runtime failure, lost state, or business behavior that makes the feature untrustworthy.

### P2 — scale and maintainability blockers

P2 means contract drift, performance risk, observability gaps, or architecture that prevents reliable operation at scale.

### P3 — technical debt

P3 means cleanup or maintainability work that should be scheduled after functional and security work.

## 3. Audit method and limitations

The audit inspected available Java source, configuration, Docker runtime, endpoint behavior, logs, and tests.

The audit did not modify backend implementation.

The audit does not replace an external penetration test.

The audit does not replace DAST.

The audit does not replace dependency CVE review.

The audit does not replace load testing on production-like data.

The audit does not replace review of the future cloud infrastructure or secret manager.

Removing mock paths can expose additional defects in currently unexecuted real-data paths.

## 4. P0 findings

### BE-001 — committed secrets and predictable credentials

Evidence exists in these files:

- `Backend/app/src/main/resources/application-local.properties:3,6,15,18`

- `Backend/identity-module/src/main/resources/application-local.properties:3,5,14,17`

- `docker-compose.yml:8,33,34,42,45`

The configuration includes fallback database credentials.

The configuration includes a fallback JWT secret.

The configuration includes `Admin@12345`.

The configuration includes `User@12345`.

Compose permits `changeme` defaults.

Risk:

An attacker can obtain valid credentials from repository history.

An attacker can forge tokens if a reused JWT secret is exposed.

Environment separation becomes meaningless if defaults are copied to staging or production.

Required changes:

- Rotate every secret that may have been used outside a disposable local environment.

- Remove all working secret fallbacks from tracked configuration.

- Fail startup when a required production secret is missing.

- Fail startup when a JWT key is weak or below the required entropy threshold.

- Disable seed data by default.

- Create the first administrator through a one-time bootstrap process.

- Source all production credentials from a secret manager.

- Add secret scanning such as Gitleaks to CI.

- Add a CI assertion that rejects `changeme` and known seed passwords.

Acceptance criteria:

- A clean checkout cannot log in using a repository default password.

- Production refuses to start without explicit secure secrets.

- Rotated keys invalidate tokens created with previous leaked keys.

### BE-002 — anonymous arbitrary asset upload

Evidence:

- `SecurityConfig.java:94` permits `/api/v1/assets/**`.

- `PublicAssetController.java:22-31` receives a raw `PUT` body.

- The caller controls `fileId` in the URL.

- `AdminAssetServiceImpl.java:81` creates a normal relative upload path.

- `BookAssetServiceImpl.java:61` creates a normal relative upload path.

No signed upload ticket is required.

No owner binding is visible in the public upload route.

No hard file-size policy is enforced at the endpoint.

No MIME allow-list is enforced at the endpoint.

No magic-byte validation is enforced at the endpoint.

No immutable post-completion state is enforced.

Risk:

Anonymous clients can upload content.

Anonymous clients may overwrite guessed asset IDs.

Attackers can consume disk and memory.

Attackers can upload misleading or hostile content types.

Required changes:

- Split public download from protected upload.

- Require authentication for upload, edit, delete, and metadata mutation.

- Use a one-time signed upload ticket or object-store presigned URL.

- Bind the ticket to actor ID.

- Bind the ticket to asset ID.

- Bind the ticket to content scope, such as book, avatar, or workspace preview.

- Bind the ticket to allowed MIME type.

- Bind the ticket to maximum size.

- Bind the ticket to expected checksum when applicable.

- Verify magic bytes and actual MIME type server-side.

- Add rate limits and per-user/project quota.

- Make completed asset content immutable.

- Add malware scanning before publishing public content.

- Audit create, upload, complete, publish, delete, and download actions.

Acceptance criteria:

- Upload without authorization or ticket is 401 or 403.

- Ticket replay is rejected.

- A ticket cannot upload to another asset.

- Wrong MIME, wrong checksum, and oversized data are rejected.

- A completed asset cannot be silently overwritten.

### BE-003 — unknown laboratory ID returns a different workspace

Evidence:

`AdminLaboratoryMonitoringServiceImpl.java:74-77` falls back to the first workspace when the requested workspace is absent.

Risk:

The API violates the meaning of resource IDs.

The user may see data from another laboratory.

The behavior can expose resource metadata across authorization boundaries.

Required changes:

- Return 404 for a missing requested ID.

- Do not substitute another resource.

- Enforce workspace and tenant authorization before reading detail data.

- Test list, detail, events, and terminate endpoints for IDOR.

Acceptance criteria:

- A non-existent ID always returns 404.

- An unauthorized ID returns the selected authorization response without data leakage.

### BE-004 — unsafe share-session secret and query token transport

Evidence:

`WorkspaceShareSessionService.java:34` defaults to `dev-share-session-secret-change-me`.

`WorkspaceAccessResolver.java:71-73` accepts a share session token in the URL query string.

Risk:

Weak fallback signing enables token forgery if deployed incorrectly.

URL tokens can enter browser history.

URL tokens can enter reverse-proxy logs.

URL tokens can enter analytics tools.

URL tokens can leak through referrer headers.

Required changes:

- Remove the default signing secret.

- Fail startup without a strong configured secret.

- Support key rotation with key IDs.

- Use Authorization bearer transport, protected HttpOnly cookie, or WebSocket subprotocol.

- If a URL exchange is needed, use a single-use short-lived code rather than the session token.

- Redact tokens from logs and error messages.

- Add expiry, revocation, rotation, and forged-token tests.

Acceptance criteria:

- A service cannot start with the development secret.

- Session tokens are not accepted as ordinary URL query parameters.

- A revoked or expired link cannot create a valid session.

### BE-005 — cookie refresh/logout with CSRF disabled

Evidence:

`SecurityConfig.java:88` disables CSRF globally.

`AuthController.java:44-64` reads refresh tokens from cookies for refresh and logout.

Risk:

An unsafe SameSite or CORS configuration can enable cross-site logout or refresh behavior.

The present security depends heavily on configuration correctness.

Required changes:

- Document the cookie threat model.

- Protect state-changing cookie endpoints with CSRF or strict Origin validation.

- Require secure cookies in production.

- Enforce a safe SameSite setting.

- Use a narrow cookie path.

- Fail production startup for unsafe refresh-cookie configuration.

- Test cross-origin requests with credentials enabled and disabled.

Acceptance criteria:

- Cross-site refresh and logout are rejected.

- Production cannot run with an insecure cookie policy.

## 5. P1 findings — product correctness

### BE-101 — admin dashboard uses hardcoded data

Evidence in `AdminDashboardServiceImpl.java`:

- Lines 50-60 include fixed users, labs, and learning values.

- Fixed values include `1402`, `389`, `8011`, and `913`.

- Lines 82-107 include fixed activity-series and science-distribution data.

- Lines 120-154 include fixed summary values and `onlineNow = 14`.

- Lines 177-185 generate fixed CSV report content.

The current dashboard test asserts some mock numbers.

Risk:

Administrators are shown product data that is not from the backend database.

Reports can be exported with fabricated metrics.

Frontend cannot reliably distinguish unavailable data from real zero data.

Required changes:

- Freeze a typed versioned dashboard DTO.

- Publish the dashboard schema in OpenAPI.

- Query authoritative data sources only.

- Define the source of truth for every KPI.

- Define period comparison formulas.

- Define timezone handling.

- Use half-open time ranges: `[from,to)`.

- Define `null` for unavailable metrics and `0` only for real zero values.

- Compute report files from the exact same query definitions as UI data.

- Replace mock-number tests with fixture-based PostgreSQL integration tests.

Acceptance criteria:

- No product-facing fixed metric number remains in dashboard service code.

- Empty database responses are explicit and do not become invented metrics.

- UI and CSV report agree for the same request.

### BE-102 — laboratory monitoring is synthetic

Evidence in `AdminLaboratoryMonitoringServiceImpl.java`:

- Line 25 stores statuses in a `ConcurrentHashMap`.

- Lines 51-53 return fixed science and object count.

- Lines 85-105 return fixed equipment, materials, reaction, environment, and safety values.

- Filters are applied after page retrieval.

- Some documented filters are not implemented in the data query.

- Line 148 stores `TERMINATED`.

- Line 158 responds with `TERMINATING`.

Risk:

The list has incorrect page totals.

The session detail does not represent an actual laboratory.

Status disappears after restart.

Safety monitoring cannot be trusted.

Required changes:

- Create or use a persisted laboratory-session model.

- Persist the command/state transition history.

- Obtain scene objects and experiment state from workspace/simulation records.

- Obtain safety alerts from actual safety events.

- Obtain presence from a real presence service.

- Apply all filters in SQL before pagination.

- Use identical predicates for result query and count query.

- Add indexes for status, science, owner, and started time.

- Make terminate an idempotent asynchronous state machine.

- Define `ACTIVE -> TERMINATING -> TERMINATED` and failure transitions.

Acceptance criteria:

- Restart does not reset a session status.

- Filters produce correct page totals.

- Detail reflects actual persisted session values.

- Terminate response and persisted state are consistent.

### BE-103 — PostgreSQL learning search error

Evidence:

`LearningLevelRepository.java:27` uses `LOWER(CAST(l.translationsJson AS string))`.

Docker runtime logs show `function lower(bytea) does not exist`.

The endpoint returns HTTP 500 in PostgreSQL.

Risk:

Learning-level search fails in the actual database engine.

H2 tests create false confidence because they do not match PostgreSQL behavior.

Required changes:

- Use a valid PostgreSQL JSONB/text expression.

- Prefer a native query when JPA cannot express the cast correctly.

- Consider normalized translation rows for searchable fields.

- Add GIN, trigram, or full-text indexes based on search requirements.

- Add Testcontainers PostgreSQL coverage for query, filter, sort, and pagination.

Acceptance criteria:

- Search succeeds on PostgreSQL.

- Search does not scan unnecessary rows at expected dataset size.

### BE-104 — user progress, activity, and avatar are fabricated

Evidence in `UserAccountServiceImpl.java`:

- Lines 159-175 use fixed `Chemistry Fundamentals` and ten levels.

- Progress is derived from `user.level` instead of learning records.

- Line 196 returns `https://storage.ailab.local/...`.

- Lines 487-499 use `127.0.0.1` and `Mozilla/5.0` as activity data.

Risk:

Users and administrators see fictional progress and activity.

Avatar completion does not point to a real storage lifecycle.

Required changes:

- Read progress from enrollment, attempt, and completion entities.

- Read activity from an audit/security event store.

- Store IP and user agent only when actually collected and privacy-approved.

- Use the shared protected asset workflow for avatars.

- Return empty lists or null fields when no records exist.

Acceptance criteria:

- Progress equals persisted completion data.

- Activity entries correspond to actual stored events.

- Avatar URL resolves to a completed stored object.

### BE-105 — required state is in memory

Observed in-memory state includes:

- Dashboard report jobs and report files in `AdminDashboardServiceImpl.java:22-23`.

- Audit export jobs in `AuditLogServiceImpl.java:24`.

- Laboratory status map in `AdminLaboratoryMonitoringServiceImpl.java:25`.

- Re-auth tokens and rate limits in `ReAuthTokenService.java:21-22`.

- Preview upload tickets in `WorkspacePreviewService.java:30`.

- Asset metadata cache in `AssetStorageService.java:19`.

- WebSocket presence/session maps in `JwtStompChannelInterceptor.java`.

Risk:

Restart loses jobs, limits, statuses, and upload state.

Two replicas see different state.

Load balancing changes functionality.

Required changes:

- Use PostgreSQL for durable jobs, statuses, idempotency, and audit snapshots.

- Use Redis with TTL and atomic operations for rate limiting and ephemeral distributed presence.

- Use a durable queue/worker mechanism for long-running reports and exports.

- Define which data is intentionally ephemeral.

- Add restart tests.

- Add two-replica integration tests.

Acceptance criteria:

- A report started on replica A can be inspected/downloaded through replica B.

- A restart does not lose required upload/job state.

### BE-106 — temporary file storage and full-memory binary IO

Evidence:

- `AssetStorageService.java:29` uses `${java.io.tmpdir}/ailab-assets`.

- `WorkspacePreviewService.java:31` uses `${java.io.tmpdir}/ailab-preview-assets`.

- `PublicAssetController.java:26` accepts `byte[]`.

- `AssetStorageService.java:49,69` reads all file bytes into memory.

Risk:

Files disappear when containers are recreated.

Large files consume application heap.

No safe multi-replica storage behavior exists.

Required changes:

- Use S3, MinIO, or another object-storage implementation.

- Persist object metadata and scope in PostgreSQL.

- Stream uploads and downloads.

- Enforce server and proxy body-size limits.

- Support Range, ETag, and Cache-Control where appropriate.

- Compute checksum while streaming.

- Implement abandoned-upload cleanup.

Acceptance criteria:

- Files survive backend redeploy.

- Large-file requests stay within defined memory limits.

### BE-107 — preview upload lifecycle is incomplete

Evidence:

`WorkspacePreviewService.java:73` publishes an expiry timestamp.

The pending ticket is stored only in memory.

The expiry is not consistently enforced during later operations.

Client-declared MIME and dimensions are trusted.

Cleanup of pending uploads is not guaranteed.

Required changes:

- Persist upload ticket state and expiry.

- Validate workspace, actor, and preview ownership on every step.

- Validate actual image MIME and magic bytes.

- Decode image and validate dimensions.

- Enforce maximum bytes.

- Verify expected and actual checksum.

- Require all mandatory variants.

- Make complete idempotent.

- Expire and delete incomplete uploads by TTL.

Acceptance criteria:

- Expired tickets cannot upload or complete.

- Invalid images cannot be published.

- Retry of the same complete request is safe.

### BE-108 — share resolution uses default preview

Evidence:

`WorkspaceShareService.java:185-190` returns `WorkspacePreviewDto.fallback("chemistry-default-01")`.

Risk:

Shared viewers do not see the actual workspace preview.

Required changes:

- Find the latest READY preview for the workspace.

- Respect state version and selected theme.

- Use fallback only when no preview exists.

- Add an end-to-end share-preview contract test.

### BE-109 — share link usage limit is inconsistent and racy

Evidence:

`WorkspaceShareSessionService.java:135` checks `useCount > maxUses`.

`WorkspaceShareService.java:161` checks `useCount >= maxUses`.

`WorkspaceShareService.java:174-177` performs a read-modify-save increment.

Risk:

The same link can be used more than its configured maximum under concurrent requests.

Required changes:

- Standardize limit semantics as `useCount >= maxUses`.

- Use conditional SQL update or database row locking.

- Decide whether existing issued sessions remain valid after the limit is reached.

- Add concurrent resolution tests.

Acceptance criteria:

- Parallel requests never exceed configured use count.

### BE-110 — settings optimistic locking is bypassable

Evidence:

`AdminSettingsServiceImpl.java:341-350` allows absent `If-Match`.

Malformed `If-Match` values are ignored by an empty `NumberFormatException` catch.

`patchSubject` casts generic map values without validation.

Risk:

Concurrent writers can overwrite each other.

Invalid payload can produce a 500 rather than a 400.

Required changes:

- Require `If-Match` for every mutation.

- Return 428 when precondition is missing.

- Return 400 for malformed ETag.

- Return 412 or 409 for stale versions.

- Use `@Version` or atomic conditional update.

- Replace generic map patches with validated DTOs.

- Reject unknown fields.

Acceptance criteria:

- Two concurrent clients cannot silently overwrite settings.

- Invalid types always return 400.

### BE-111 — restore settings has an incorrect audit before-state

Evidence:

`AdminSettingsServiceImpl.java:274-280` updates current data before audit data is safely copied.

Risk:

The audit may show the restored state as both before and after.

Required changes:

- Deep-copy immutable `before` state first.

- Apply update.

- Deep-copy immutable `after` state.

- Save settings, history, and audit in one transaction.

- Validate restore reason.

Acceptance criteria:

- Audit displays the actual previous configuration.

- Historical snapshots cannot change after later mutations.

### BE-112 — current logout invalidates every device

Evidence:

`AuthServiceImpl.java:50-51` revokes one refresh token and invokes global `invalidateSessions`.

Risk:

Normal logout unexpectedly logs out every device.

Required changes:

- Revoke only the current refresh-token family/device for normal logout.

- Add explicit logout-all endpoint/command.

- Reserve global token version invalidation for compromise, password reset, ban, or logout-all.

Acceptance criteria:

- Logout from device A leaves device B authenticated.

- Logout-all invalidates every device intentionally.

### BE-113 — audit export and retention are incomplete

Evidence:

`AuditLogServiceImpl.java:24,146-166` keeps jobs in memory.

Retention values are returned as fixed values.

No full durable export/download/archive lifecycle is present.

Required changes:

- Persist job state.

- Run export in a worker.

- Persist progress and failure reason.

- Store generated artifact in object storage.

- Restrict download by owner/permission.

- Expire artifacts after TTL.

- Implement scheduled retention and archive.

- Audit export and deletion actions.

## 6. Endpoint change inventory

Every endpoint below must have typed request and response DTOs.

Every endpoint below must define permission, pagination, timezone, and error behavior in OpenAPI.

| Endpoint | Current issue | Required change | Access policy |
|---|---|---|---|
| `POST /api/v1/auth/register` | Default/seed risk and abuse controls unclear | Strong bootstrap, validation, rate limit, audit | Public with throttling |
| `POST /api/v1/auth/login` | Predictable defaults in config | Remove defaults, throttling, lockout/alerting | Public with throttling |
| `POST /api/v1/auth/refresh` | Cookie plus CSRF issue | Origin/CSRF control, durable rotation/reuse detection | Public cookie flow |
| `POST /api/v1/auth/logout` | Invalidates all devices | Current-session revoke and separate logout-all | Valid refresh/auth |
| `GET /api/v1/admin/dashboard/summary` | Hardcoded KPIs | Real DB aggregates and typed contract | `dashboard.read` |
| `GET /api/v1/admin/dashboard/activity-series` | Fixed dates and values | Query by metric/bucket/period | `dashboard.read` |
| `GET /api/v1/admin/dashboard/science-distribution` | Fixed percentages | Real aggregate and denominator | `dashboard.read` |
| `GET /api/v1/admin/dashboard/learning-summary` | Fixed attempts/completion | Enrollment/attempt aggregates | `dashboard.read` |
| `GET /api/v1/admin/dashboard/laboratory-summary` | Fixed session counts | Persisted session aggregate | `dashboard.read` |
| `GET /api/v1/admin/dashboard/activity-summary` | Synthetic activity/presence | Audit events and distributed presence | `dashboard.read` |
| `POST /api/v1/admin/dashboard/reports` | In-memory fixed report | Durable async idempotent job | `dashboard.export` |
| `GET /api/v1/admin/dashboard/reports/{jobId}` | No durable ownership state | Persisted status and owner check | `dashboard.export` |
| `GET /api/v1/admin/dashboard/reports/{jobId}/download` | In-memory byte array | Authorized streamed object download | `dashboard.export` |
| `GET /api/v1/admin/laboratories` | Synthetic data/post-page filters | SQL filter/count and real DTO | `laboratories.read` |
| `GET /api/v1/admin/laboratories/{id}` | Wrong fallback workspace | Strict 404 and real detail | `laboratories.read` |
| `GET /api/v1/admin/laboratories/{id}/events` | Fixed events | Persisted cursor-paginated events | `laboratories.read` |
| `POST /api/v1/admin/laboratories/{id}/terminate` | Inconsistent map state | Idempotent async state machine | `laboratories.terminate` |
| `GET /api/v1/admin/me/permissions` | Hardcoded duplicate names | Canonical permission catalog/store | Admin authenticated |
| `GET /api/v1/admin/settings` | Runtime defaults/map response | Typed schema and ETag response | `settings.read` |
| `PATCH /api/v1/admin/settings` | Optional ETag/untyped patch | Mandatory conditional validated write | `settings.write` |
| `GET /api/v1/admin/settings/history` | Snapshot integrity unclear | Immutable durable snapshots | `settings.read` |
| `POST /api/v1/admin/settings/restore/{version}` | Wrong before audit state | Transactional restore with reason | `settings.restore` |
| `GET /api/v1/admin/settings/subjects` | Runtime catalog behavior | Stable typed catalog/read policy | `settings.read` |
| `PATCH /api/v1/admin/settings/subjects/{id}` | Unsafe casts/lock unclear | Validated DTO, ETag, audit | `settings.write` |
| `PUT /api/v1/assets/upload/{fileId}` | Anonymous arbitrary upload | Signed ticket and content validation | Auth plus ticket |
| `GET /api/v1/assets/raw/{fileId}/{filename}` | Broad public access and relative URL | Explicit public/private scope and streaming | Published public or `assets.read` |
| `POST /api/v1/admin/assets` | Manual service construction/cache | Durable metadata/object storage injection | `assets.write` |
| `POST /api/v1/books/{bookId}/assets/upload-urls` | Relative URL/ticket lifecycle | Signed editor-bound object upload | Book editor |
| `POST /api/v1/workspaces/{id}/previews/upload-urls` | In-memory ticket | Durable expiring preview ticket | `EDIT_SCENE` |
| `PUT /api/v1/workspaces/{id}/previews/{previewId}/assets/{assetId}/upload` | Temp/raw body/expiry gap | Streaming ticket-validated upload | `EDIT_SCENE` plus ticket |
| `POST /api/v1/workspaces/{id}/previews/{previewId}/complete` | Variant/cleanup/idempotency gap | Validate READY assets/version transaction | `EDIT_SCENE` |
| `POST /api/v1/workspaces/share-links/{id}/resolve` | Fallback preview/max-use race | Atomic reservation and actual READY preview | Public link/password |
| `GET /api/v1/shared-workspaces/{workspaceId}/...` | Query-token session transport | Header/cookie transport, uniform REST/WS checks | Share session |
| `GET /api/v1/learning/levels` | PostgreSQL `lower(bytea)` 500 | Valid JSONB/native search query/indexes | Public/read policy |
| `GET /api/v1/users/me/progress` | Derived fixed course | Real completion aggregates | Authenticated user |
| `GET /api/v1/users/me/activity` | Synthetic IP/agent/events | Real event store/privacy policy | Authenticated user |
| `POST /api/v1/users/me/avatar/upload` | Nonexistent storage host | Signed durable avatar lifecycle | Authenticated user |
| `GET /api/v1/admin/audit/export` | In-memory export job | Durable worker/artifact lifecycle | `audit.export` |
| `GET /api/v1/admin/audit/retention` | Fixed policy values | Real policy/enforcement status | `audit.read` |

## 7. Contract and API requirements

Do not expose `Map<String,Object>` as a product API contract.

Use immutable response DTOs.

Use typed request DTOs.

Use enums instead of undocumented strings.

Use Bean Validation.

Reject unknown input fields.

Publish OpenAPI for every endpoint.

Generate examples for successful and failed responses.

Use one error envelope.

The error envelope should contain `code`.

The error envelope should contain `message`.

The error envelope should contain `details` when safe.

The error envelope should contain `requestId`.

Define pagination fields consistently.

Define sorting fields consistently.

Define nullability consistently.

Define locale fallback consistently.

Define all time intervals as `[from,to)`.

Store timestamps in UTC.

Use explicit IANA timezone for grouping.

Do not return relative asset URLs unless an API gateway guarantees same origin.

Choose one asset URL policy.

Option one is gateway same-origin paths.

Option two is configured absolute public API URL.

Option three is a centralized frontend asset resolver.

Never embed localhost or fixed ports in product DTOs.

## 8. Architecture and performance improvements

### Persistence model

Persist jobs, commands, audit snapshots, statuses, and idempotency records in PostgreSQL.

Use Redis for short-lived distributed rate limits, presence, and token-related ephemeral data.

Use object storage for assets, previews, exports, and avatars.

Use durable metadata rather than process caches as the source of truth.

### Query behavior

Apply all filters before pagination.

Use identical filters in count queries.

Index fields used by filter, sort, and period aggregates.

Inspect PostgreSQL query plans at realistic volume.

Avoid N+1 loading for dashboard and laboratory list/detail endpoints.

### Authentication performance

JWT authentication currently looks up user state through the repository per request.

This supports session invalidation but can be a database bottleneck.

Measure before optimizing.

If needed, use a short-lived Redis security-stamp cache.

Invalidate that cache when token version, role, or account status changes.

Do not weaken revoke semantics to avoid a database query.

### Binary behavior

Replace whole-body `byte[]` transfers with streaming.

Enforce body limits at the proxy and application layer.

Support Range downloads when the use case requires it.

Use ETag and Cache-Control intentionally.

Perform checksum verification during stream processing.

### WebSocket and presence

Local session maps are not cluster-wide presence.

Use heartbeat records with TTL.

Use Redis pub/sub or another shared mechanism.

Cleanup presence on disconnect and expiry.

Ensure role/token revocation applies to WebSocket sessions.

### Observability

Use structured logs.

Attach request/correlation IDs.

Do not log passwords.

Do not log bearer tokens.

Do not log share tokens.

Do not ignore exceptions silently.

Every 5xx must have an actionable server-side cause.

Publish metrics for errors, DB latency, jobs, storage, rate limits, and WebSocket connections.

Define alerts and SLOs for critical endpoints.

## 9. Required test plan

### Database and migrations

Add Testcontainers PostgreSQL integration tests.

Test clean migration from an empty database.

Test upgrade from the prior released schema.

Test JSONB search and pagination on PostgreSQL.

Test indexes and query plans for major lists.

### Security

Test anonymous asset upload rejection.

Test asset ID overwrite rejection.

Test IDOR for laboratory detail and events.

Test role matrix for all admin endpoints.

Test forged share token rejection.

Test expired and revoked share sessions.

Test CSRF and Origin behavior for refresh/logout.

Test token rotation and reuse detection.

### Concurrency

Test share max-use under concurrent resolution.

Test settings ETag conflict under concurrent updates.

Test report idempotency keys.

Test repeated laboratory terminate commands.

Test upload complete retries.

### Durability

Test restart while report is running.

Test restart between upload and complete.

Test restart with re-auth token and rate-limit state.

Test two application replicas against shared storage and Redis.

### Contract

Test OpenAPI schema against frontend expectations.

Test dashboard response fields and nullability.

Test laboratory response fields and pagination.

Test asset URL policy.

Test learning search response/error behavior.

Test settings ETag headers and error codes.

### Load and binary IO

Test large upload memory ceiling.

Test streaming download.

Test quota and size errors.

Test worker queue retry and expiration.

## 10. Delivery sequence

### Phase 0 — immediate stop-ship work

1. Rotate secrets and remove weak defaults.

2. Close anonymous asset upload.

3. Fix wrong-resource laboratory fallback.

4. Secure share token transport and signing key.

5. Fix share max-use atomicity.

6. Add P0 regression tests.

### Phase 1 — trustworthy product data

1. Freeze dashboard and laboratory OpenAPI contracts.

2. Replace dashboard hardcoded values with real aggregates.

3. Replace laboratory synthetic values with persisted data.

4. Fix PostgreSQL learning search.

5. Replace fabricated user progress/activity/avatar workflow.

6. Fix settings locking and audit snapshots.

7. Separate normal logout from logout-all.

### Phase 2 — durability and horizontal scale

1. Add object storage.

2. Add durable job state.

3. Add Redis for distributed ephemeral state.

4. Add workers, retry, leases, cleanup, and idempotency.

5. Add restart and multi-replica coverage.

### Phase 3 — quality and operations

1. Replace generic maps with DTOs.

2. Add OpenAPI contract CI.

3. Add metrics, tracing, logs, alerts, and SLOs.

4. Add database query performance testing.

5. Add secret, SAST, dependency, and image scans.

## 11. Definition of Done

The backend is ready only when all of the following are true:

- Repository configuration has no working production secrets or standard credentials.

- Anonymous clients cannot write assets or admin data.

- Dashboard, laboratory, and user endpoints contain no product-facing mock values.

- PostgreSQL integration tests pass on clean and upgraded schemas.

- Required state survives restart.

- Required state works consistently across replicas.

- Every endpoint has typed OpenAPI request and response contracts.

- Every mutation has authorization, validation, concurrency behavior, and audit.

- Asset processing is bounded and streaming-based.

- Export/report artifacts are durable and authorized.

- CI runs tests, migrations, secret scan, SAST, dependency scan, and image scan.

- Logs and metrics diagnose failures without exposing secrets.

## 12. Verification performed

Full Maven tests ran in Docker image `maven:3.9.5-eclipse-temurin-21`.

`identity-module` ran 55 tests.

`chemistry-engine` ran 403 tests.

`app` ran 87 tests.

Total tests: 545.

Failures: 0.

Errors: 0.

Maven reactor result: `BUILD SUCCESS`.

The Docker backend health endpoint returned `{"status":"UP"}`.

This successful test run does not negate the findings.

The application integration context uses H2 for part of the suite.

H2 did not reveal the PostgreSQL-specific `lower(bytea)` failure.

Test logs also showed warnings about Flyway/H2 version compatibility.

Test logs also showed Mockito self-attaching warnings.

Test logs also showed repository-selection contexts with missing JPA beans.

These warnings should be tracked, and PostgreSQL Testcontainers must become mandatory in CI.

## 13. Final note

This document is intentionally implementation-oriented.

It identifies each confirmed issue, the risk, the required backend change, affected endpoint families, verification work, and release criteria.

It should be used as the backend delivery checklist before the frontend is asked to rely exclusively on live API data.

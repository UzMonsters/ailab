# Backend report — remaining work and improvements

Date: 2026-09-03

This is a read-only frontend integration audit. No backend code was changed.

## Priority 0 — blocks complete frontend behaviour

### 1. Make a resolved share session valid for REST workspace APIs

`POST /api/v1/shared-workspaces/resolve` issues a `guest_sess_*` token. The WebSocket interceptor recognises it, but the normal workspace REST controllers do not have an equivalent authentication path. A guest can therefore pass the access screen but fail when the frontend tries to load the shared workspace state, events, chat or comments.

Required contract:

- document the transport (`Authorization: Bearer <shareSessionToken>` is preferred);
- authenticate the token for workspace read operations and enforce link capabilities;
- return a clear `403 SHARE_CAPABILITY_DENIED` for disallowed write actions;
- make expiry/revocation return a stable `410 SHARE_LINK_EXPIRED_OR_REVOKED` response.

Evidence: `WorkspaceShareService` creates the guest token; `JwtStompChannelInterceptor` handles it only for STOMP.

### 2. Implement binary upload for workspace preview targets

The preview lifecycle creates upload targets such as:

`/api/v1/workspaces/{workspaceId}/previews/{previewId}/assets/{assetId}/upload`

but no matching controller upload handler was found. The frontend cannot upload generated dark/light WebP preview blobs, so it must retain its current fallback preview behaviour.

Required contract:

- authenticated `PUT` or presigned-object-storage URL accepting `image/webp` binary;
- size, MIME and checksum validation;
- completion result which includes dark/light URLs, dimensions, source state version and timestamp;
- idempotent retry for an already uploaded asset.

### 3. Return the current preview from a shared link

Share resolution currently returns `WorkspacePreviewDto.fallback("chemistry-default-01")`, rather than the latest workspace preview. The public shared-workspace card consequently cannot reliably show the owner’s actual canvas.

Required change: resolve the latest `WorkspacePreviewEntity` by workspace ID and expose the correct allowed variant for the link.

## Priority 1 — integration correctness

### 4. Catalog IDs must be canonical across admin, catalog and workspace events

The workspace event failure `Unknown laboratory material: KMnO4(aq)` occurred because the client sent a display/formula-like identifier instead of the backend material ID expected by the laboratory domain.

Required improvements:

- publish a dedicated workspace catalog DTO containing canonical `materialId`/`equipmentId`, localized display name, state, renderer and availability;
- make event validation errors include the expected ID field and a list or link to valid alternatives when safe;
- provide a stable `published`/`unavailable` state for catalog objects referenced by an existing workspace or scenario.

### 5. Publish formal API schemas and field-level validation

The frontend currently infers several generic resource fields from runtime JSON. A versioned OpenAPI schema for admin resources, scenarios, book blocks and settings would permit a durable form generator and client validation.

Important additions:

- explicit request/response schemas for each create/patch endpoint;
- `fieldErrors` keyed by input field paths for all `422`/`409` responses;
- enum metadata and localized labels/descriptions;
- optimistic-locking / version requirements consistently documented.

### 6. Book asset lifecycle needs a usable browser upload endpoint

Book APIs issue storage-looking upload URLs, but the frontend needs a browser-safe signed upload or proxy endpoint plus completion state. This is required before Book Studio can persist uploaded image assets rather than temporary data URLs.

## Priority 2 — product and operational improvements

### 7. Preview freshness and regeneration

Expose `generatedAt`, `sourceStateVersion`, variant dimensions, status and a server-side regeneration request. Dashboard cards can then show whether their preview is stale and offer an admin action.

### 8. Scenario authoring contract

The frontend can select real published equipment/material records, but scenario step DTOs should formally express their references, quantities, target ports, validation rules and localisation. A backend scenario preview/validation endpoint should return step-level problems rather than only a generic invalid result.

### 9. Learning administration paging and filtering

Provide server-side filters for user, level, status and time range on attempts/progress, with stable cursor/page metadata and links to the referenced attempt/level. This allows the Learning page to avoid client-only filtering and technical payload views.

### 10. Reliability, observability and security

- Apply a reconnect/backoff policy to WebSocket upgrades and expose a connection-health event; repeated short-lived `101` sockets should be diagnosable.
- Ensure every failed API request returns correlation/trace IDs in a consistent problem-details payload.
- Rate-limit password attempts for share resolution and audit link use/expiry/revocation events.
- Add contract tests for share expiry, upload lifecycle, catalog availability and optimistic concurrency.

## Recommended delivery order

1. Guest REST authentication and capability enforcement.
2. Preview binary upload endpoint and shared preview resolution.
3. Canonical workspace catalog DTOs and event validation errors.
4. OpenAPI/field-error schemas for admin and book authoring.
5. Scenario and learning authoring/query refinements.


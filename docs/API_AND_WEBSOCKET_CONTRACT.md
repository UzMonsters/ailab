# AI Laboratory — API и WebSocket контракт

Документ разделяет:
- `Required` — нужен для полного MVP, но в текущем backend отсутствует или не подключён.

## Backend Services — 7

| # | Service | Responsibility | Owner |
|---:|---|---|---|
| 1 | API Gateway | Единственная публичная точка входа, JWT, CORS, routing и WebSocket proxy | — |
| 2 | Authentication Service | Регистрация, login, logout, refresh, password recovery и email verification | Аминжон |
| 3 | User Service | Профили, preferences, avatar, activity, timezone и account security | Аминжон |
| 4 | Laboratory Service | Laboratories, workspaces, equipment objects, materials, connections и simulation state | Sardor |
| 5 | Chemistry Engine | Расчёты реакций, pH, температуры, давления, концентраций и физических свойств | Sardor |
| 6 | AI Service | Chat, explanation, analysis и generation экспериментов | Аминжон |
| 7 | Admin Service | Users, chemicals, elements, equipment, laboratories и audit management | Аминжон |

## API Gateway

API Gateway — единственная публичная точка входа для REST и WebSocket. Он проверяет JWT, применяет CORS, маршрутизирует запросы во внутренние сервисы и не должен содержать chemistry business logic.

### Public routes

| Protocol | Public route | Internal target | Responsibility |
|---|---|---|---|
| REST | `/api/v1/auth/**` | Authentication Service | Authentication |
| REST | `/api/v1/users/**` | User Service | User data |
| REST | `/api/v1/workspaces/**`, `/api/v1/laboratories/**` | Laboratory Service | Laboratory and workspace persistence |
| REST | `/api/v1/chemistry/**` | Chemistry Engine / Laboratory Service | Chemistry and simulation |
| REST | `/api/v1/assistant/**` | AI Service | AI requests |
| REST | `/api/v1/admin/**` | Admin Service | Admin operations |
| WebSocket | `/ws/workspaces/{workspaceId}` | Laboratory Service | Realtime laboratory events |

## 1. Общие правила

| Правило | Значение |
|---|---|
| Base URL | `https://ailab-api-1h23.onrender.com` production, `http://localhost:8080` local |
| API prefix | `/api/v1` |
| Auth | `Authorization: Bearer <accessToken>` для защищённых endpoint |
| Refresh cookie | `HttpOnly`, `Secure`, `SameSite=None` для cross-origin production |
| Content-Type | `application/json` |
| Date/time | ISO-8601 UTC, например `2026-08-10T12:00:00Z` |
| Idempotency | Для операций Sandbox передавать `idempotencyKey` |
| Concurrency | Передавать `expectedVersion` или `expectedStateVersion`; конфликт возвращает `409` |

### Общая ошибка

| Field | Type | Description |
|---|---|---|
| `timestamp` | string | Время ошибки |
| `status` | number | HTTP status |
| `error` | string | Класс/тип ошибки |
| `message` | string | Сообщение для пользователя или fallback |
| `path` | string | API path |
| `fieldViolations` | `{field, message}[]` | Ошибки валидации полей |
| `errors` | `Record<string,string>` | Ошибки полей в удобном формате |
| `correlationId` | string? | Рекомендуется добавить для production tracing |

## Authentication Service — REST API

### Not implemented

| Status | Method | Endpoint | Request | Response | Backend purpose |
|---|---|---|---|---|---|
| Not implemented | POST | `/api/v1/auth/forgot-password` | `{ email }` | `{ message, requestId }` | Forgot password form |
| Not implemented | POST | `/api/v1/auth/reset-password` | `{ token, newPassword }` | `{ message }` | Password reset form |
| Not implemented | POST | `/api/v1/auth/verify-email` | `{ token }` | `{ message, verifiedAt }` | Email verification flow |
| Not implemented | POST | `/api/v1/auth/resend-verification` | `{ email }` | `{ message }` | Resend verification email |

## User Service — REST API — Аминжон

### Not implemented

| Status | Method | Endpoint | Request | Response | Backend purpose |
|---|---|---|---|
| Not implemented | GET | `/api/v1/users/me/activity` | `page, size, cursor?` | `{ items: ActivityItem[], nextCursor? }` | Activity timeline |
| Not implemented | POST | `/api/v1/users/me/avatar` | `multipart/form-data: file` | `{ avatarUrl, width, height, mimeType }` | Avatar upload |
| Not implemented | POST | `/api/v1/users/me/change-password` | `{ currentPassword, newPassword }` | `{ message }` | Account security |

## Laboratory Service — Workspace REST API — Sardor

### Workspace model

| Field | Type | Description |
|---|---|---|
| `id` | string | Stable workspace ID |
| `name` | string | Workspace name |
| `science` | `chemistry \| physics \| biology` | Science type |
| `thumbnail` | string? | URL or generated preview |
| `stateVersion` | number | Optimistic locking version |
| `createdAt` | string | Created timestamp |
| `updatedAt` | string | Last changed timestamp |
| `isFavorite` | boolean | Favorite flag |
| `isDeleted` | boolean | Soft-delete/trash flag |
| `experimentSessionId` | string? | Linked Sandbox simulation session |

### Not implemented

| Status | Method | Endpoint | Request | Response | Backend purpose |
|---|---|---|---|---|---|
| Not implemented | GET | `/api/v1/workspaces` | Query: `science?, search?, view?, sort?, page?, size?, includeDeleted?` | `{ items: Workspace[], page, size, total }` | Dashboard cards/list, search, sort |
| Not implemented | GET | `/api/v1/workspaces/{id}` | — | `WorkspaceDetails` with metadata and current state reference | Open workspace |
| Not implemented | POST | `/api/v1/workspaces` | `{ name, science }` | Created `WorkspaceDetails` | New Workspace |
| Not implemented | PUT | `/api/v1/workspaces/{id}` | `{ name?, isFavorite?, isDeleted?, thumbnail? }` + `If-Match`/`stateVersion` | Updated workspace | Rename/favorite/trash |
| Not implemented | POST | `/api/v1/workspaces/{id}/duplicate` | `{ name? }` | New `WorkspaceDetails` | Duplicate/context menu |
| Not implemented | DELETE | `/api/v1/workspaces/{id}` | — | `{ message }` | Permanent delete after trash |
| Not implemented | POST | `/api/v1/workspaces/{id}/restore` | — | Updated workspace | Restore from trash |
| Not implemented | POST | `/api/v1/workspaces/{id}/thumbnail` | `{ svg or image metadata }` | `{ thumbnailUrl, updatedAt }` | Real dashboard thumbnail |

Authorization: every workspace query must be scoped by authenticated owner; an ID belonging to another user must return `404` or `403`, never its data.

## Laboratory Service — Sandbox / Experiment REST API — Sardor

Backend controller: `/api/v1/chemistry/experiments`.

### Simulation state response

| Field | Type | Description |
|---|---|---|
| `sessionId` | string | Experiment session |
| `processCode` | string | Experiment template/process |
| `processVersion` | number | Process schema version |
| `version` | number | Current optimistic-lock version |
| `status` | string | `ACTIVE`, `PAUSED`, `COMPLETED`, `FAILED` |
| `temperature` | object | Environment/device temperatures |
| `pressure` | object | Environment/device pressures |
| `containers` | object[] | Vessels, contents, volumes, liquid state |
| `apparatus` | object[] | Equipment, geometry, ports, attached devices |
| `createdAt` | string | Session creation time |
| `updatedAt` | string | Last state update |

### Scientific operation

### Required Sandbox event payloads

| Event type | Request payload | State effect |
|---|---|---|
| `ITEM_ADDED` | `{ itemId, equipmentType, position, size, rotation }` | Add apparatus/container |
| `ITEM_MOVED` | `{ itemId, x, y }` | Move item on infinite canvas |
| `ITEM_RESIZED` | `{ itemId, width, height }` | Resize equipment |
| `ITEM_ROTATED` | `{ itemId, rotation }` | Rotate equipment |
| `MATERIAL_ADDED` | `{ itemId, materialId, amountMl, phase, concentration? }` | Add material into vessel |
| `POUR` | `{ sourceId, targetId, amountMl, materialId }` | Transfer liquid and volume |
| `CONNECT` | `{ sourceItemId, sourcePort, targetItemId, targetPort, connectionType, direction }` | Create pipe/wire/thermal link |
| `DISCONNECT` | `{ connectionId }` | Remove link |
| `HEAT_START` | `{ equipmentId, vesselId, targetTemperatureC, rateCPerSecond }` | Start gradual heating |
| `HEAT_STOP` | `{ equipmentId, vesselId }` | Stop heating |
| `COOL` | `{ equipmentId, vesselId, targetTemperatureC }` | Start cooling |
| `STIR_START` | `{ equipmentId, vesselId, rpm }` | Start stirring |
| `STIR_STOP` | `{ equipmentId, vesselId }` | Stop stirring |

Every event response must contain the authoritative `newVersion`, resulting state delta, safety warnings and server timestamp. The client must not treat a local optimistic state as persisted until this response or its WebSocket acknowledgement arrives.

## Chemistry Engine — REST API — Sardor

**Module: chemistry-engine — Owner: Sardor**

### Not implemented extensions

| Status | Method | Endpoint | Request body/query | Response | Backend purpose |
|---|---|---|---|---|
| Not implemented | GET | `/api/v1/chemistry/equipment` | `query?, category?, page?` | `EquipmentSummary[]` | Dynamic Sandbox equipment library |
| Not implemented | GET | `/api/v1/chemistry/materials` | `query?, phase?, page?` | `MaterialSummary[]` | Dynamic Materials tab |
| Not implemented | GET | `/api/v1/chemistry/equipment/{identifier}` | — | `EquipmentDetails` with ports | Equipment inspector and connections |

All response models must be published through OpenAPI and covered by backend contract tests.

### Chemistry Engine — gRPC API

The Chemistry Engine exposes internal gRPC methods for deterministic calculations. These methods are not public gateway routes.

| RPC method | Request | Response | Responsibility |
|---|---|---|---|
| `RunSimulation` | `workspaceId`, objects, chemicals, temperature, pressure, timeScale | reactions, chemicals, temperature, pressure, pH, events | Execute one simulation step |
| `ValidateReaction` | chemicals and current state | `{ valid, message, warnings[] }` | Validate reaction and safety constraints |
| `CalculateReaction` | reactants, conditions and quantities | products, releasedEnergy, warnings[] | Calculate reaction products |
| `CalculatePH` | solution composition, volume, temperature | pH, concentration, method | Calculate solution pH |
| `CalculateTemperature` | materials, energy, mass, heat capacity | temperature, deltaTemperature | Calculate temperature changes |
| `CalculatePressure` | gas state, volume, temperature | pressure, deltaPressure | Calculate gas pressure |
| `CalculateEnergy` | materials, reaction and temperature | energy, unit, warnings[] | Calculate energy transfer |
| `CalculateConcentration` | solute amount, volume, unit | concentration, unit | Calculate concentration |
| `GetChemicalProperties` | chemical identifier | physical and safety properties | Return chemical properties |
| `GetElement` | element identifier | element details | Return periodic-table data |
| `GetCompound` | compound identifier | compound details | Return compound data |

Every gRPC request must include `requestId`, authenticated actor context, schema version and deadline. Errors must map to stable codes: `INVALID_ARGUMENT`, `NOT_FOUND`, `FAILED_PRECONDITION`, `RESOURCE_EXHAUSTED` and `INTERNAL`.

## Admin Service — REST API — Аминжон

The existing admin user endpoints are intentionally omitted. Only missing admin APIs are listed below.

### Not implemented admin chemistry management

| Status | Method | Endpoint | Request | Response | Backend purpose |
|---|---|---|---|---|
| Not implemented | GET | `/api/v1/admin/chemicals` | Filters, pagination | `Page<ChemicalAdminRow>` | Chemicals |
| Not implemented | POST | `/api/v1/admin/chemicals` | Chemical catalog record | Created record | Chemicals |
| Not implemented | PUT | `/api/v1/admin/chemicals/{id}` | Editable fields + version | Updated record | Chemicals |
| Not implemented | DELETE | `/api/v1/admin/chemicals/{id}` | — | `{ message }` | Chemicals |
| Not implemented | GET/POST/PUT/DELETE | `/api/v1/admin/elements[/{id}]` | Element record | Element record/result | Elements |
| Not implemented | GET/POST/PUT/DELETE | `/api/v1/admin/equipment[/{id}]` | Equipment, ports, SVG metadata | Equipment record/result | Equipment |
| Not implemented | GET/POST/PUT/DELETE | `/api/v1/admin/laboratories[/{id}]` | Lab record | Lab record/result | Laboratories |
| Not implemented | GET | `/api/v1/admin/audit` | `actor?, action?, from?, to?, page?` | `Page<AuditEntry>` | Admin audit |

All admin mutation endpoints need validation, audit history, pagination, optimistic locking and server-side role enforcement.

## AI Service — REST API — Аминжон

| Method | Endpoint | Request | Response |
|---|---|---|---|
| POST | `/api/v1/assistant/conversations` | `{ workspaceId?, sessionId?, locale }` | `{ conversationId, createdAt }` |
| GET | `/api/v1/assistant/conversations/{id}` | — | `{ conversationId, messages[], usage }` |
| POST | `/api/v1/assistant/conversations/{id}/messages` | `{ message, context: { selectedItemId?, stateVersion? } }` | `{ messageId, role: "assistant", content, safetyNotes?, createdAt }` |
| DELETE | `/api/v1/assistant/conversations/{id}` | — | `{ message }` |

The service must enforce rate limits, redact secrets, validate experiment context, apply chemistry safety rules and return a deterministic fallback when the provider is unavailable.

## Laboratory Service — WebSocket contract — Sardor

Current backend audit found no WebSocket/STOMP controller or broker configuration. Use WebSocket for live collaboration and simulation progress; keep REST as the source of truth for initial load, replay and recovery.

### Connection

| Item | Contract |
|---|---|
| URL production | `wss://ailab-api-1h23.onrender.com/ws` |
| URL local | `ws://localhost:8080/ws` |
| Protocol | STOMP over WebSocket, JSON payloads |
| Auth | Send Bearer token in STOMP `CONNECT` headers; alternatively authenticate the handshake with a secure cookie |
| Heartbeat | Client `10s`, server `10s` |
| Reconnect | Exponential backoff; after reconnect call REST `GET` state and resume from `stateVersion` |

### Client destinations

| Destination | Request body | Server response/event | Usage |
|---|---|---|---|
| `/app/workspaces/{workspaceId}/events` | `SandboxEventCommand` | `WorkspaceEventAck` | Persisted canvas events |
| `/app/experiments/{sessionId}/commands` | `{ commandId, expectedStateVersion, command }` | `SimulationCommandAck` | Heat/cool/stir/mix progress |
| `/app/assistant/{conversationId}/message` | `{ message, context }` | Streamed `AssistantToken` + `AssistantCompleted` | AI chat |
| `/app/workspaces/{workspaceId}/presence` | `{ status: ONLINE \| IDLE \| OFFLINE }` | Presence event | Future collaboration |

### Server subscriptions

| Subscribe destination | Event body | Consumer |
|---|---|---|
| `/topic/workspaces/{workspaceId}` | `WorkspaceEvent` | All workspace clients |
| `/topic/experiments/{sessionId}` | `SimulationStateDelta` | Sandbox canvas, inspector, log |
| `/user/queue/acks` | `WorkspaceEventAck` or `SimulationCommandAck` | Originating client |
| `/user/queue/errors` | `RealtimeError` | Toast/error state |
| `/topic/workspaces/{workspaceId}/presence` | `{ userId, username, status, at }` | Collaboration indicators |

### WebSocket message envelope

| Field | Type | Required | Description |
|---|---|---|---|
| `eventId` | string | yes | Unique server event ID |
| `eventType` | string | yes | `ITEM_MOVED`, `POUR`, `STATE_DELTA`, etc. |
| `workspaceId` | string | yes | Workspace scope |
| `sessionId` | string? | no | Experiment scope |
| `actorId` | string | yes | User who caused event |
| `clientEventId` | string? | no | Client idempotency ID |
| `stateVersion` | number | yes | Authoritative version after event |
| `payload` | object | yes | Event-specific data |
| `occurredAt` | string | yes | Server timestamp |
Example request:

```json
{
  "clientEventId": "evt_client_123",
  "expectedVersion": 18,
  "eventType": "POUR",
  "payload": {
    "sourceId": "flask-1",
    "targetId": "beaker-1",
    "amountMl": 25,
    "materialId": "water"
  }
}
```

Example acknowledgement:

```json
{
  "eventId": "evt_server_456",
  "eventType": "POUR_ACK",
  "workspaceId": "ws_123",
  "sessionId": "exp_123",
  "stateVersion": 19,
  "payload": {
    "sourceVolumeMl": 75,
    "targetVolumeMl": 25,
    "safetyWarnings": []
  },
  "occurredAt": "2026-08-10T12:00:00Z"
}
```

## 10. Recommended backend implementation order

1. Implement Workspace entity, migration, controller and owner authorization.
2. Link each workspace to an experiment session and persist Sandbox events with optimistic locking.
3. Add REST safety evaluation to every risky operation and return warnings in the state delta.
4. Add WebSocket/STOMP for state deltas, acknowledgements and simulation progress; REST remains recovery path.
5. Add admin chemistry CRUD and audit endpoints.
6. Add assistant API with safety/rate limiting.
7. Export OpenAPI and run backend contract validation in CI.

## 11. Backend implementation checklist — exact scope

Этот раздел — короткий список того, что backend должен реализовать. Existing API выше не переписываются: новые endpoint добавляются поверх текущих controller/service модулей.

### P0 — Workspace persistence

| Status | Type | Exact API | Request | Response | What it does |
|---|---|---|---|---|---|
| Not implemented | REST GET | `/api/v1/workspaces` | `science?, search?, sort?, page?, size?, includeDeleted?` | `{ items: Workspace[], page, size, total }` | Dashboard cards/list/search/sort |
| Not implemented | REST GET | `/api/v1/workspaces/{workspaceId}` | Authenticated user only | `WorkspaceDetails` | Opens workspace and restores metadata |
| Not implemented | REST POST | `/api/v1/workspaces` | `{ name, science }` | Created `WorkspaceDetails` | Creates owned workspace |
| Not implemented | REST PUT | `/api/v1/workspaces/{workspaceId}` | `{ name?, isFavorite?, isDeleted?, thumbnail? }`, `stateVersion` | Updated workspace | Rename/favorite/trash with optimistic locking |
| Not implemented | REST POST | `/api/v1/workspaces/{workspaceId}/duplicate` | `{ name? }` | New workspace | Duplicates metadata and Sandbox state |
| Not implemented | REST DELETE | `/api/v1/workspaces/{workspaceId}` | — | `{ message }` | Permanent delete after soft-delete |
| Not implemented | REST POST | `/api/v1/workspaces/{workspaceId}/restore` | — | Updated workspace | Restores from trash |
| Not implemented | REST POST | `/api/v1/workspaces/{workspaceId}/thumbnail` | `{ svg, width, height }` or image upload | `{ thumbnailUrl, updatedAt }` | Real dashboard thumbnail |

Required backend data: `workspaces`, `workspace_members` (optional future), `workspace_state`, owner foreign key, soft-delete flag, favorite flag, `stateVersion`, timestamps and linked `experimentSessionId`.

### P0 — Sandbox state and scientific events

The existing experiment session, operation, event, replay and audit endpoints are implemented and excluded. Missing workspace-state persistence, autosave, event linkage and conflict recovery are specified in section 13.

Minimum event types: `ITEM_ADDED`, `ITEM_MOVED`, `ITEM_RESIZED`, `ITEM_ROTATED`, `MATERIAL_ADDED`, `POUR`, `CONNECT`, `DISCONNECT`, `HEAT_START`, `HEAT_STOP`, `COOL`, `FREEZE`, `BOIL`, `STIR_START`, `STIR_STOP`, `MIX`, `WASH`, `DRY`.

### P0 — WebSocket/STOMP realtime

| Status | Type | Exact destination | Request/message | Response/event | What it does |
|---|---|---|---|---|---|
| Not implemented | WS publish | `/app/workspaces/{workspaceId}/events` | `SandboxEventCommand` | `WorkspaceEventAck` | Sends persisted canvas event and acknowledgement |
| Not implemented | WS subscribe | `/topic/workspaces/{workspaceId}` | — | `WorkspaceEvent` | Broadcasts changes to open workspace clients |
| Not implemented | WS publish | `/app/experiments/{sessionId}/commands` | `{ commandId, expectedStateVersion, command }` | `SimulationCommandAck` | Streams heating/cooling/stirring progress |
| Not implemented | WS subscribe | `/topic/experiments/{sessionId}` | — | `SimulationStateDelta` | Updates canvas, inspector and log in realtime |
| Not implemented | WS subscribe | `/user/queue/acks` | — | `WorkspaceEventAck` or `SimulationCommandAck` | Confirms the originating client event |
| Not implemented | WS subscribe | `/user/queue/errors` | — | `{ code, message, eventId?, expectedVersion?, actualVersion? }` | Handles conflicts and safety errors |
| Not implemented | WS publish/subscribe | `/app/workspaces/{workspaceId}/presence`, `/topic/workspaces/{workspaceId}/presence` | `{ status: ONLINE\|IDLE\|OFFLINE }` | `{ userId, username, status, at }` | Future collaboration indicators |

WebSocket connection URLs: `wss://ailab-api-1h23.onrender.com/ws` production and `ws://localhost:8080/ws` local. Use STOMP heartbeats `10s/10s`, Bearer token in `CONNECT`, reconnect backoff and REST state reload after reconnect.

### P1 — AI Assistant

| Status | Type | Exact API | Request | Response | What it does |
|---|---|---|---|---|---|
| Not implemented | REST POST | `/api/v1/assistant/conversations` | `{ workspaceId?, sessionId?, locale }` | `{ conversationId, createdAt }` | Starts assistant context |
| Not implemented | REST GET | `/api/v1/assistant/conversations/{conversationId}` | — | `{ conversationId, messages[], usage }` | Restores AI chat |
| Not implemented | REST POST | `/api/v1/assistant/conversations/{conversationId}/messages` | `{ message, context: { selectedItemId?, stateVersion? } }` | `{ messageId, role, content, safetyNotes?, createdAt }` | Explains or plans experiment |
| Not implemented | WS publish/subscribe | `/app/assistant/{conversationId}/message` | `{ message, context }` | `/user/queue/assistant`: `AssistantToken`, then `AssistantCompleted` | Streams assistant response |
| Not implemented | REST DELETE | `/api/v1/assistant/conversations/{conversationId}` | — | `{ message }` | Removes chat history |

Assistant backend must validate chemistry safety, rate-limit requests, hide provider secrets, store conversation state and return a safe fallback when provider is unavailable.

### P1 — Admin catalogue and audit

| Status | Type | Exact API | Request | Response | What it does |
|---|---|---|---|---|---|
| Not implemented | REST GET | `/api/v1/admin/chemicals` | `search?, phase?, active?, page?, size?` | `Page<ChemicalAdminRow>` | Admin chemicals table |
| Not implemented | REST POST | `/api/v1/admin/chemicals` | Chemical catalogue record | Created record | Adds material/compound |
| Not implemented | REST PUT | `/api/v1/admin/chemicals/{id}` | Editable fields + `version` | Updated record | Maintains catalogue |
| Not implemented | REST DELETE | `/api/v1/admin/chemicals/{id}` | — | `{ message }` | Deactivates/removes record |
| Not implemented | REST GET/POST/PUT/DELETE | `/api/v1/admin/elements[/{id}]` | Element record/filter | Element record/result | Manages periodic table |
| Not implemented | REST GET/POST/PUT/DELETE | `/api/v1/admin/equipment[/{id}]` | Equipment, ports, SVG metadata | Equipment record/result | Manages Sandbox library |
| Not implemented | REST GET/POST/PUT/DELETE | `/api/v1/admin/laboratories[/{id}]` | Laboratory record | Laboratory record/result | Manages labs/templates |
| Not implemented | REST GET | `/api/v1/admin/audit` | `actor?, action?, from?, to?, page?, size?` | `Page<AuditEntry>` | Tracks every admin mutation |

Every admin endpoint must require `ROLE_ADMIN`, validate input server-side, write audit records and use optimistic locking for catalogue edits.

### P1 — Profile, settings and production support

| Status | Type | Exact API | Request | Response | What it does |
|---|---|---|---|---|---|
| Not implemented | REST GET | `/api/v1/users/me/activity` | `page?, size?, cursor?` | `{ items: ActivityItem[], nextCursor? }` | Real profile timeline |
| Not implemented | REST POST | `/api/v1/users/me/avatar` | `multipart/form-data: file` | `{ avatarUrl, width, height, mimeType }` | Real avatar upload |
| Not implemented | REST POST | `/api/v1/users/me/change-password` | `{ currentPassword, newPassword }` | `{ message }` | Account security |
| Not implemented | REST GET | `/api/v1/users/me/timezone` | — | `{ timezone, offsetMinutes }` | Settings timezone selector |
| Not implemented | REST PUT | `/api/v1/users/me/timezone` | `{ timezone }` | Updated timezone | Persists timezone preference |

### Backend non-API requirements

| Requirement | Implementation |
|---|---|
| Security | Owner scoping for workspaces/experiments; `ROLE_ADMIN` for admin APIs |
| Persistence | Flyway migrations for workspace, event, assistant, audit and activity tables |
| Errors | `400` validation, `401` unauthenticated, `403` forbidden, `404` not found, `409` version conflict, `422` unsafe operation, `429` rate limit |
| Production | `PORT` environment variable, configured allowed web origins, secure refresh cookie, disabled seed by default |
| Observability | Correlation ID, structured logs, audit event ID and health/readiness endpoints |
| Contract | OpenAPI export plus backend contract tests in CI |

## 12. Sandbox persistence contract — exact data that must be saved

Этот раздел является обязательным дополнением к существующим experiment API. Цель — после refresh, повторного входа и открытия workspace восстановить не только название workspace, но и весь canvas.

### 12.1 Canonical workspace state

Backend должен хранить состояние как версионируемый документ. Клиентское состояние не является источником истины.

```json
{
  "workspaceId": "ws_123",
  "sessionId": "exp_123",
  "stateVersion": 42,
  "viewport": { "x": 0, "y": 0, "zoom": 1 },
  "grid": { "enabled": true, "size": 20, "snap": true },
  "items": [
    {
      "id": "flask-1",
      "equipmentType": "ERLENMEYER_FLASK",
      "name": "Erlenmeyer Flask",
      "position": { "x": 420, "y": 240 },
      "size": { "width": 120, "height": 150 },
      "scale": 1,
      "rotation": 0,
      "capacityMl": 250,
      "volumeMl": 50,
      "liquidLevel": 0.2,
      "materialId": "water",
      "temperatureC": 24.5,
      "pressureAtm": 1,
      "operation": "IDLE",
      "attachedTo": null,
      "broken": false
    }
  ],
  "connections": [
    {
      "id": "conn-1",
      "fromItemId": "burner-1",
      "toItemId": "flask-1",
      "fromPort": "THERMAL",
      "toPort": "THERMAL",
      "connectionType": "THERMAL",
      "direction": "SOURCE_TO_TARGET",
      "curve": { "controlPoint1": { "x": 0, "y": 0 }, "controlPoint2": { "x": 0, "y": 0 } },
      "active": true
    }
  ],
  "log": [],
  "updatedAt": "2026-08-10T12:00:00Z"
}
```

Обязательные поля persistence-модели:

| Данные | Поля | Требование |
|---|---|---|
| Canvas item | `id`, `equipmentType`, `position.x/y`, `size.width/height`, `scale`, `rotation` | Сохранять при add/move/resize/rotate |
| Vessel contents | `capacityMl`, `volumeMl`, `liquidLevel`, `materialId`, `temperatureC`, `pressureAtm` | Backend пересчитывает `liquidLevel` и не доверяет клиентскому физическому состоянию |
| Equipment state | `operation`, `targetTemperatureC`, `attachedTo`, `broken` | Сохранять активный нагрев, охлаждение, перемешивание и attachment |
| Connection | endpoints, ports, type, direction, bezier control points, `active` | Валидировать совместимость портов на backend |
| View state | `viewport`, grid/snap settings | Восстанавливать рабочее положение canvas |
| History | event ID, actor, timestamp, state version, operation log | Replay, undo/redo, audit |

### 12.2 Workspace REST API to implement

| Method | Endpoint | Request | Response | Что делает |
|---|---|---|---|---|
| `GET` | `/api/v1/workspaces/{workspaceId}/state` | `?includeLog=true` | `WorkspaceState` | Загружает весь Sandbox после открытия |
| `PUT` | `/api/v1/workspaces/{workspaceId}/state` | `{ expectedVersion, state }` | `{ state, stateVersion, savedAt }` | Сохраняет snapshot canvas; нужен debounce autosave |
| `POST` | `/api/v1/workspaces/{workspaceId}/events` | `SandboxEventCommand` | `WorkspaceEventAck` | Атомарно применяет и сохраняет одно действие |
| `GET` | `/api/v1/workspaces/{workspaceId}/events` | `?afterVersion=42&limit=500` | `{ events[], nextVersion }` | Восстанавливает пропущенные события |
| `POST` | `/api/v1/workspaces/{workspaceId}/undo` | `{ expectedVersion }` | `WorkspaceState` | Отменяет последнее действие пользователя |
| `POST` | `/api/v1/workspaces/{workspaceId}/redo` | `{ expectedVersion }` | `WorkspaceState` | Повторяет отменённое действие |
| `POST` | `/api/v1/workspaces/{workspaceId}/publish` | `{ title?, description? }` | `{ workspaceId, shareUrl, publishedAt }` | Публикует эксперимент для просмотра |
| `POST` | `/api/v1/workspaces/{workspaceId}/autosave` | `{ expectedVersion, stateHash, state }` | `{ stateVersion, savedAt }` | Надёжное сохранение при закрытии вкладки |

`PUT /state` используется для snapshot/recovery, а `POST /events` — для обычных действий. Нельзя безусловно перезаписывать state: при несовпадении версии возвращать `409 VERSION_CONFLICT` с актуальным `stateVersion` и state.

### 12.3 Event payloads

`POST /api/v1/workspaces/{workspaceId}/events` принимает:

```json
{
  "clientEventId": "client-uuid",
  "expectedVersion": 42,
  "eventType": "ITEM_MOVED",
  "payload": { "itemId": "flask-1", "x": 460, "y": 260 }
}
```

| Event | Required payload | Backend action |
|---|---|---|
| `ITEM_ADDED` | item без server timestamps | Проверить equipment catalog и добавить item |
| `ITEM_MOVED` | `itemId`, `x`, `y` | Проверить owner и границы canvas, сохранить позицию |
| `ITEM_RESIZED` | `itemId`, `width`, `height`, `scale` | Проверить min/max и обновить размер |
| `ITEM_ROTATED` | `itemId`, `rotation` | Нормализовать угол и сохранить |
| `ITEM_DELETED` | `itemId` | Удалить item и связанные connections |
| `ITEM_DUPLICATED` | `sourceItemId`, optional position | Создать новый item |
| `MATERIAL_ADDED` | `itemId`, `materialId`, `amountMl` | Проверить vessel, capacity, phase и добавить материал |
| `POUR_STARTED` | `sourceItemId`, `targetItemId`, `amountMl` | Проверить совместимость и создать operation |
| `POUR_COMPLETED` | operation ID | Атомарно изменить оба объёма и записать log |
| `CONNECT` | item/port endpoints, type, direction | Проверить порт и совместимость, создать connection |
| `DISCONNECT` | `connectionId` | Удалить connection |
| `ATTACH_EQUIPMENT` | `equipmentId`, `targetItemId`, `attachmentType` | Создать attachment и связь |
| `DETACH_EQUIPMENT` | `equipmentId`, `targetItemId` | Удалить attachment |
| `VIEWPORT_CHANGED` | `x`, `y`, `zoom` | Сохранить view state без chemistry event log |

Каждый event должен быть idempotent по `(userId, clientEventId)`. Backend обязан возвращать `newVersion`, нормализованный `stateDelta`, `safetyWarnings` и запись `logEntry`.

### 12.4 Operations API payloads

`POST /api/v1/chemistry/experiments/{sessionId}/operations`:

```json
{
  "commandId": "cmd-uuid",
  "expectedStateVersion": 42,
  "idempotencyKey": "heat-uuid",
  "command": {
    "type": "HEAT",
    "equipmentId": "burner-1",
    "targetItemId": "flask-1",
    "targetTemperatureC": 80,
    "powerPercent": 35
  }
}
```

Supported commands: `HEAT`, `COOL`, `FREEZE`, `EVAPORATE`, `BOIL`, `STIR`, `MIX`, `POUR`, `TRANSFER`, `WASH`, `DRY`, `STOP`. Response must include `stateDelta`, `executionStatus`, `currentTemperatureC`, `currentVolumes`, `warnings[]`, `logEntry` and `newVersion`.

### 12.5 WebSocket persistence and live updates

After REST load, a client subscribes to `/topic/workspaces/{workspaceId}` and publishes the same `SandboxEventCommand` to `/app/workspaces/{workspaceId}/events`. Backend must:

1. authenticate the STOMP user;
2. check workspace ownership/permissions;
3. apply the event transactionally;
4. persist it before broadcasting;
5. return an acknowledgement to `/user/queue/acks`;
6. broadcast the authoritative event/state delta;
7. return `409`-equivalent realtime error when `expectedVersion` is stale.

Required message types:

| Message | Fields | Destination |
|---|---|---|
| `WorkspaceEventAck` | `clientEventId`, `eventId`, `stateVersion`, `stateDelta`, `safetyWarnings` | `/user/queue/acks` |
| `WorkspaceEvent` | envelope + persisted payload | `/topic/workspaces/{workspaceId}` |
| `SimulationProgress` | `operationId`, `progress`, `temperatureC`, `volumeChanges`, `status` | `/topic/experiments/{sessionId}` |
| `RealtimeError` | `code`, `message`, `clientEventId`, `expectedVersion`, `actualVersion` | `/user/queue/errors` |

REST `GET state` remains the recovery source of truth after disconnects or missed events.

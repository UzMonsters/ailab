# Backend contract: Chemistry, Sandbox/Workspace, Sharing

Дата аудита: 2026-09-07  
Цель: backend должен стать единственным источником состояния для Sandbox, chemistry calculations и collaboration/sharing. Frontend уже разделяет `workspaceId`, `shareSessionToken` и `experimentSessionId`; смешивать эти идентификаторы больше нельзя.

## 1. Критические проблемы backend

### P0

1. Guest token после `POST /shared-workspaces/resolve` распознается WebSocket-слоем, но обычные REST endpoints workspace его не принимают одинаково. В результате shared workspace открывается, но initial state/autosave/events/chat/comments получают 401/403.
2. Preview service выдает upload URL `/api/v1/workspaces/{workspaceId}/previews/{previewId}/assets/{assetId}/upload`, но такого controller endpoint нет. Preview upload всегда незавершим.
3. Chemistry operation response backend вложенный (`payload.stateDelta.vesselDeltas`), тогда как старый frontend ждал flat `stateDelta/newVersion`. Frontend уже нормализует актуальный формат; backend должен зафиксировать его в OpenAPI и перестать менять форму.
4. Admin Sharing использует обычный user-scoped `GET /workspaces`, поэтому администратор видит только собственные/доступные workspace. Нужен отдельный global admin endpoint.

### P1

1. Нужна документированная связь workspace и chemistry experiment: `WorkspaceState.sessionId` должен быть явно назван `experimentSessionId` или сопровождаться typed field.
2. Completion preview не должен принимать произвольный публичный `url` от клиента; backend сам формирует URL по `assetId` после проверки storage.
3. `WorkspaceCommentThreadDto` не содержит body корневого комментария. Проверить service: либо первый message хранится как reply, либо текст сейчас теряется. Целевой DTO должен иметь `body`/`author`/`createdAt` у thread.
4. `DELETE /workspaces/{id}` фактически hard delete, но UI имеет restore. Сделать delete soft (`isDeleted=true`) и отдельный admin purge endpoint.
5. Ограничить query `sort` allow-list и унифицировать ошибки/optimistic locking.

## 2. Три разных идентификатора

| Поле | Назначение | Где используется |
|---|---|---|
| `workspaceId` | сохраненный canvas, участники, chat, comments | `/api/v1/workspaces/{workspaceId}/...`, WebSocket topics |
| `shareSessionToken` | короткоживущая guest authorization capability | REST header/query и WebSocket CONNECT |
| `experimentSessionId` | состояние chemistry-engine и operations | `/api/v1/chemistry/experiments/{experimentSessionId}/...` |

Нельзя записывать `shareSessionToken` в `experimentSessionId` и наоборот. Рекомендуемый `WorkspaceState`:

```json
{
  "workspaceId": "ws_1",
  "stateVersion": 17,
  "experimentSessionId": "exp_1",
  "items": [],
  "connections": [],
  "viewport": { "x": 0, "y": 0, "zoom": 1 },
  "updatedAt": "2026-09-07T12:30:00Z"
}
```

Для backward compatibility один release можно отдавать и deprecated `sessionId`, и `experimentSessionId`, затем удалить `sessionId`.

## 3. Авторизация shared REST и realtime

Предпочтительный transport гостевого токена:

```http
Authorization: ShareSession guest_sess_...
```

Временно можно поддерживать query `?sessionToken=...`, потому что текущий frontend его использует. Не писать token в access logs, analytics URL или error detail. Добавить `Cache-Control: no-store` для resolve и shared private state.

Middleware должен:

1. Проверить подпись/hash token, expiry, revoked share link, maxUses и workspace.
2. Создать principal типа `SHARE_GUEST` с `workspaceId`, `role`, `capabilities`.
3. Проверить capability для каждого REST command.
4. Использовать ту же policy-функцию для REST и WebSocket.

Минимальные capabilities: `workspace.read`, `workspace.edit`, `workspace.comment`, `workspace.chat`, `workspace.manage_members`, `workspace.manage_links`.

## 4. Workspace API

### GET `/api/v1/workspaces`

Query: `science=chemistry`, `search=...`, `sort=updatedAt,desc`, `page=0`, `size=20`, `includeDeleted=false`.

Response:

```json
{
  "items": [{
    "id": "ws_1", "name": "Acid-base lab", "science": "chemistry",
    "thumbnail": "https://cdn/...", "preview": { "status": "READY", "variants": {} },
    "isFavorite": false, "isDeleted": false, "stateVersion": 17,
    "createdAt": "2026-09-01T10:00:00Z", "updatedAt": "2026-09-07T12:30:00Z"
  }],
  "page": 0, "size": 20, "total": 1
}
```

Scope: только workspace текущего пользователя/участника; никогда не global admin list.

### GET/POST/PUT `/api/v1/workspaces/{id}`

Create:

```http
POST /api/v1/workspaces
{ "name": "Acid-base lab", "science": "chemistry" }
```

Response `201`: workspace DTO, `Location` header.

Update:

```http
PUT /api/v1/workspaces/ws_1
{ "name": "Titration", "isFavorite": true, "isDeleted": false, "thumbnail": null, "expectedVersion": 4 }
```

Backend DTO сейчас также имеет `stateVersion`; выбрать одно поле optimistic lock (`expectedVersion`) и использовать последовательно. Conflict -> `409 VERSION_CONFLICT` с `actualVersion`.

### Duplicate/delete/restore

```http
POST /api/v1/workspaces/ws_1/duplicate
{ "name": "Titration copy" }
```

Новый workspace получает собственные `workspaceId` и `experimentSessionId`; sharing/members/chat/comments не копируются.

- `DELETE /workspaces/{id}` -> soft delete, `204`.
- `POST /workspaces/{id}/restore` -> restored workspace DTO.
- Новый `DELETE /api/v1/admin/workspaces/{id}/purge` -> необратимое удаление с audit и explicit confirmation.

### State

```http
GET /api/v1/workspaces/ws_1/state
Authorization: Bearer ...
```

или для guest:

```http
GET /api/v1/workspaces/ws_1/state?sessionToken=guest_sess_...
```

Save:

```http
PUT /api/v1/workspaces/ws_1/state?expectedVersion=17
{
  "workspaceId": "ws_1", "stateVersion": 17, "experimentSessionId": "exp_1",
  "items": [{ "id": "vessel_1", "kind": "beaker", "x": 320, "y": 240, "rotation": 0, "contents": [] }],
  "connections": [], "viewport": { "x": 0, "y": 0, "zoom": 1 }
}
```

Response `200` возвращает canonical server state с `stateVersion:18`; не только ack. Проверить ownership каждого referenced material/equipment и лимиты размера payload.

### Events, undo/redo, autosave

```http
POST /api/v1/workspaces/ws_1/events
{ "eventId": "uuid", "clientId": "client_1", "expectedVersion": 18, "type": "ITEM_MOVED", "payload": { "itemId": "vessel_1", "x": 400, "y": 260 }, "occurredAt": "2026-09-07T12:31:00Z" }
```

```json
{ "eventId": "uuid", "workspaceId": "ws_1", "accepted": true, "stateVersion": 19, "serverTimestamp": "2026-09-07T12:31:00Z" }
```

- `GET /events?afterVersion=18&limit=100` -> ordered events, no gaps/duplicates.
- `POST /undo?expectedVersion=19`, `POST /redo?expectedVersion=20` -> canonical state.
- `POST /autosave` request `{expectedVersion,state,clientTimestamp}` -> `{stateVersion,savedAt}`.

События должны быть idempotent по `(workspaceId,eventId)`. Guest с `workspace.read` не может mutate.

### Permissions/members/invitations

`GET /workspaces/{id}/permissions`:

```json
{ "role": "EDITOR", "capabilities": ["workspace.read", "workspace.edit", "workspace.chat", "workspace.comment"] }
```

`GET /members`:

```json
[{ "userId": "usr_1", "displayName": "Jasur", "emailMasked": "ja***@example.com", "avatarUrl": null, "role": "OWNER", "status": "ACTIVE", "joinedAt": "2026-09-01T10:00:00Z", "lastSeenAt": "2026-09-07T12:00:00Z" }]
```

```http
PATCH /workspaces/{id}/members/{userId}
{ "role": "EDITOR" }
```

Owner нельзя удалить/понизить без атомарной передачи ownership. `DELETE /members/{userId}` -> `204`.

Invitation:

```http
POST /workspaces/{id}/invitations
{ "emailOrUserId": "student@example.com", "role": "EDITOR", "expiresAt": "2026-09-14T12:00:00Z", "message": "Join the experiment" }
```

```json
{ "invitationId": "inv_1", "workspaceId": "ws_1", "invitee": { "emailMasked": "st***@example.com" }, "role": "EDITOR", "status": "PENDING", "expiresAt": "2026-09-14T12:00:00Z", "createdAt": "2026-09-07T12:00:00Z" }
```

Accept: `POST /api/v1/workspace-invitations/{token}/accept`; response `{workspaceId,role,status:"ACCEPTED"}`. Token одноразовый, хранится hashed.

## 5. Share links

### Create

```http
POST /api/v1/workspaces/ws_1/share-links
{
  "role": "VIEWER",
  "expiresAt": "2026-09-14T12:00:00Z",
  "password": "optional secret",
  "maxUses": 100,
  "allowChat": true,
  "allowComments": true
}
```

Response:

```json
{
  "id": "link_1", "linkId": "link_1", "url": "https://app/.../shared/token-once",
  "role": "VIEWER", "expiresAt": "2026-09-14T12:00:00Z", "maxUses": 100, "useCount": 0,
  "allowChat": true, "allowComments": true,
  "capabilities": ["workspace.read", "workspace.chat", "workspace.comment"],
  "lastUsedAt": null, "createdAt": "2026-09-07T12:00:00Z"
}
```

Добавить `status: ACTIVE|REVOKED|EXPIRED|LIMIT_REACHED` и `revokedAt`; сейчас frontend не может надежно отличить revoked link. Raw resolve token возвращать только при create/rotate, не в list.

Update:

```http
PATCH /workspaces/ws_1/share-links/link_1
{ "role": "EDITOR", "expiresAt": null, "maxUses": 20, "allowChat": true, "allowComments": true }
```

Revoke: `DELETE /.../share-links/{linkId}` -> `204`; все guest sessions этого link немедленно становятся недействительны, WebSocket отключается.

### Resolve

```http
POST /api/v1/shared-workspaces/resolve
Content-Type: application/json
{ "token": "raw-share-token", "password": "optional secret" }
```

```json
{
  "workspaceId": "ws_1", "name": "Titration", "science": "chemistry",
  "preview": { "status": "READY", "variants": {} },
  "role": "VIEWER", "capabilities": ["workspace.read", "workspace.chat"],
  "requiresAuth": false, "expiresAt": "2026-09-14T12:00:00Z",
  "shareSessionToken": "guest_sess_..."
}
```

Если link защищен password и password не прислан: `401 SHARE_PASSWORD_REQUIRED`; неверный: `401 SHARE_PASSWORD_INVALID`; истек: `410 SHARE_LINK_EXPIRED`; revoked: `410 SHARE_LINK_REVOKED`; max uses: `410 SHARE_LINK_LIMIT_REACHED`. Не раскрывать, существует ли private workspace.

## 6. Chat, comments и realtime

### Chat REST

```http
POST /workspaces/ws_1/chat/messages
{ "clientMessageId": "uuid", "body": "Check the temperature", "replyToMessageId": null, "anchor": { "itemId": "vessel_1" } }
```

Response содержит `id`, `clientMessageId`, `workspaceId`, `author`, `body`, `anchor`, `createdAt`, `updatedAt`. Повтор clientMessageId idempotent.

- `GET /chat/messages?cursor=...&limit=50` -> `{items,nextCursor,unreadCount}`.
- `PATCH /chat/messages/{messageId}` request `{body}`; редактировать только свой message в установленном окне.
- `DELETE /chat/messages/{messageId}` -> tombstone/204.
- `POST /chat/read` request `{lastReadMessageId}` -> `{unreadCount:0}`.

### Comments

```http
POST /workspaces/ws_1/comments
{ "body": "Use a smaller flask", "anchor": { "itemId": "vessel_1", "x": 12, "y": 8 } }
```

Целевой thread DTO:

```json
{
  "id": "thread_1", "workspaceId": "ws_1", "body": "Use a smaller flask",
  "author": { "id": "usr_1", "displayName": "Jasur" },
  "anchor": { "itemId": "vessel_1", "x": 12, "y": 8 },
  "status": "OPEN", "createdAt": "2026-09-07T12:00:00Z", "updatedAt": "2026-09-07T12:00:00Z",
  "replies": []
}
```

- `POST /comments/{threadId}/replies` request `{body}`.
- `PATCH /comments/{threadId}` request `{status:"RESOLVED"}`.
- `GET /comments?status=OPEN&cursor=&limit=50` -> page envelope.

### WebSocket

Backend должен документировать transport (STOMP/SockJS или native WS), endpoint, CONNECT auth и destinations. Минимум:

- subscribe workspace state/events;
- subscribe presence;
- subscribe chat/comments;
- commands содержат `clientId`, `eventId`, `expectedVersion`;
- server messages содержат monotonically increasing `stateVersion` и `serverTimestamp`;
- reconnect: REST `GET /state` + `GET /events?afterVersion=` закрывает gap.

Ошибки realtime используют тот же `code/correlationId`; `VERSION_CONFLICT` заставляет клиента перечитать state, а не повторять command бесконечно.

## 7. Preview upload

```http
POST /workspaces/ws_1/preview-upload-urls
{
  "sourceStateVersion": 19,
  "variants": [{ "theme": "dark", "mimeType": "image/webp", "width": 1200, "height": 675, "checksum": "sha256:..." }]
}
```

```json
{
  "previewId": "preview_1", "sourceStateVersion": 19,
  "uploads": [{ "theme": "dark", "assetId": "asset_1", "uploadUrl": "https://signed-storage/...", "expiresAt": "2026-09-07T12:40:00Z" }]
}
```

После direct PUT:

```http
POST /workspaces/ws_1/previews/preview_1/complete
{ "sourceStateVersion": 19, "assets": [{ "theme": "dark", "assetId": "asset_1", "checksum": "sha256:..." }], "fallbackKey": "chemistry-default" }
```

Response `WorkspacePreviewDto`. Удалить несуществующий local upload URL или реализовать его как `PUT .../upload` с binary body; предпочтительно настоящее S3-compatible signed URL.

## 8. Admin Sharing API, которого не хватает

### GET `/api/v1/admin/workspaces`

Query: `q`, `science`, `status=ACTIVE|DELETED`, `ownerId`, `hasActiveLinks`, `page`, `size`, `sort`.

```json
{
  "items": [{
    "id": "ws_1", "name": "Titration", "science": "chemistry", "status": "ACTIVE",
    "owner": { "id": "usr_1", "displayName": "Jasur", "emailMasked": "ja***@example.com" },
    "memberCount": 4, "activeShareLinkCount": 2, "pendingInvitationCount": 1,
    "stateVersion": 19, "updatedAt": "2026-09-07T12:00:00Z"
  }],
  "page": { "page": 0, "size": 25, "totalElements": 1, "totalPages": 1 }
}
```

Aggregate counts одним SQL/query, без frontend N+1 (`list + members + links` для каждой строки). Detail endpoint:

`GET /api/v1/admin/workspaces/{id}` -> workspace, owner, members, invitations, share links, recent audit, storage/state size. Admin revoke/member operations должны иметь отдельные admin permissions и audit reason.

## 9. Chemistry experiment API — canonical contract

### Create experiment

```http
POST /api/v1/chemistry/experiments
{
  "sessionId": { "value": "exp_1" },
  "processCode": "workspace-sandbox",
  "processVersion": 1,
  "requestedAt": "2026-09-07T12:00:00Z"
}
```

Backend должен либо поддержать `workspace-sandbox`, либо вернуть catalog/config допустимых `processCode`, operation types, models и input units. Placeholder identifiers не должны неожиданно приводить к 500.

Canonical state:

```json
{
  "sessionId": { "value": "exp_1" },
  "status": "ACTIVE",
  "version": { "value": 3 },
  "clock": { "startedAt": "2026-09-07T12:00:00Z", "elapsedSeconds": 20 },
  "processExecution": { "processCode": "workspace-sandbox", "processVersion": 1, "currentStepId": "step_1" },
  "vessels": {}, "equipmentAllocations": {}, "environment": {}
}
```

`GET /api/v1/chemistry/experiments/{sessionId}` возвращает эту форму. Frontend types теперь принимают value objects и временно legacy fields.

### Execute operation

```http
POST /api/v1/chemistry/experiments/exp_1/operations
Idempotency-Key: uuid
{
  "expectedStateVersion": 3,
  "idempotencyKey": "uuid",
  "command": {
    "commandId": { "value": "cmd_1" },
    "stepId": "heat-vessel_1",
    "targetVesselId": "vessel_1",
    "operation": {
      "operationType": "HEAT",
      "modelSelection": {
        "calculationMethod": "SENSIBLE_HEAT",
        "reactionOrProfileIdentifier": null,
        "model": { "identifier": "workspace-sandbox", "version": "1" },
        "datasets": {}, "assumptions": {}
      }
    },
    "inputs": { "targetTemperatureC": "80", "durationSeconds": "20" },
    "materialDeltas": []
  }
}
```

Фактический backend response, который нужно зафиксировать:

```json
{
  "status": "APPLIED",
  "eventId": { "value": "evt_1" },
  "payload": {
    "stateDelta": {
      "vesselDeltas": [{
        "vesselId": "vessel_1",
        "materialDeltas": [{ "compoundCode": "H2O", "quantityDelta": "0", "unit": "MILLILITER", "physicalState": "LIQUID" }],
        "mixingNote": "heated",
        "finalTemperatureKelvin": 353.15,
        "finalPressureKpa": 101.325,
        "finalVolumeMl": 100
      }],
      "conservationLedger": {}
    }
  },
  "state": { "sessionId": { "value": "exp_1" }, "status": "ACTIVE", "version": { "value": 4 }, "vessels": {} },
  "audit": { "formulas": [], "inputs": {}, "outputs": {}, "safetyEvaluations": [] }
}
```

Frontend преобразует Kelvin -> Celsius, kPa -> bar и применяет `materialDeltas` к содержимому vessel. Backend обязан:

- всегда вернуть `state.version.value` после APPLIED;
- не применять command при stale expected version (`409 VERSION_CONFLICT`);
- сохранять idempotency по commandId/idempotencyKey;
- на safety block вернуть `422 SAFETY_VIOLATION` с violations и неизмененной version;
- все decimal quantities передавать как строку или единый documented number format.

### Events/replay/audit/measurements

- `POST /{sessionId}/events` request `{expectedVersion,idempotencyKey,payload}` -> canonical state.
- `POST /{sessionId}/replay` -> deterministic canonical state; не создает новую session.
- `GET /{sessionId}/audit/{eventId}` -> `{eventId,sessionId,commandId,inputs,formulas,outputs,safetyEvaluations,timestamp}`.
- `GET /{sessionId}/measurements?kind=TEMPERATURE` -> ordered `{id,kind,value,unit,vesselId,recordedAt,stateVersion}`.

## 10. Chemistry catalog/calculation endpoints

### Formula/equation

```http
POST /api/v1/chemistry/formulas/parse
{ "formula": "Ca(OH)2" }
```

```json
{ "canonicalFormula": "Ca(OH)2", "elements": { "Ca": 1, "O": 2, "H": 2 }, "molarMass": { "value": "74.092", "unit": "G_PER_MOL" }, "charge": 0 }
```

```http
POST /api/v1/chemistry/equations/balance
{ "equation": "H2 + O2 -> H2O" }
```

```json
{ "balancedEquation": "2 H2 + O2 -> 2 H2O", "reactants": [{ "formula": "H2", "coefficient": 2 }], "products": [{ "formula": "H2O", "coefficient": 2 }], "conserved": true }
```

Ошибки parse/balance -> `422 FORMULA_PARSE_ERROR` / `EQUATION_NOT_BALANCEABLE`, с `fieldErrors` и position/token, не generic 500.

### Catalogs

- `GET /chemistry/elements?page=&size=`; `GET /elements/{identifier}`; `GET /elements/{identifier}/properties`.
- `GET /chemistry/compounds?q=&formula=&page=&size=`; `GET /compounds/{identifier}`; `/properties`.
- `GET /chemistry/materials?q=&phase=&hazard=&page=&size=`.
- `GET /chemistry/equipment` и `/catalog`; `GET /equipment/{identifier}`.

List endpoints используют pagination envelope и locale. Published catalog only; admin drafts сюда не попадают. Identifier должен принимать canonical code, а ambiguity -> 400.

### Calculation families

Backend уже объявляет следующие POST endpoints; для каждого нужен concrete OpenAPI request/response, единицы и диапазоны:

| Family | Endpoints | Назначение |
|---|---|---|
| acid-base | `/water`, `/strong-acid`, `/strong-base`, `/weak-acid`, `/weak-base`, `/salt-hydrolysis`, `/buffer`, `/buffer/preparation`, `/buffer/perturbation`, `/titration/characteristic-points`, `/polyprotic-titration/characteristic-points` | pH, buffer/titration |
| thermodynamics | `/reference/{compoundCode}`, `/calculate`, `/hess-law`, `/calorimetry/sensible-heat`, `/thermal-mixing`, `/reaction-heat` | energy/enthalpy/calorimetry |
| gas | `/state`, `/mixture`, `/transformation` | gas state and transformations |
| kinetics | `/rate`, `/integrated-law`, `/half-life`, `/arrhenius`, `/progress` | reaction rates |
| electrochemistry | `/standard-cell`, `/nernst`, `/electrolysis` | cell potential/electrolysis |
| safety | `/evaluate` | rule violations/warnings |

Образец calculation contract:

```http
POST /api/v1/chemistry/gas/state
{
  "amount": { "value": "1.0", "unit": "MOL" },
  "temperature": { "value": "298.15", "unit": "KELVIN" },
  "pressure": { "value": "101.325", "unit": "KPA" },
  "volume": null,
  "model": "IDEAL"
}
```

```json
{
  "volume": { "value": "24.465", "unit": "LITER" },
  "model": { "identifier": "IDEAL_GAS", "version": "1" },
  "assumptions": ["ideal behavior"],
  "audit": { "formula": "PV=nRT", "inputs": {}, "outputs": {} }
}
```

Общие требования ко всем calculations:

- quantity = `{value:string,unit:enum}`; никакого неявного угадывания единиц;
- температура ниже 0 K, отрицательные concentration/volume/time -> `422 DOMAIN_VALIDATION_ERROR`;
- response содержит model identifier/version, dataset versions, assumptions и calculation audit;
- rounding/precision documented и reproducible;
- safety evaluate response: `{stage,verdict,violations:[{ruleCode,message,severity}],warnings:[...]}`;
- chemistry-engine exceptions преобразуются в domain problem JSON.

## 11. Общий error contract

```json
{
  "type": "https://api.aichemistry.local/problems/version-conflict",
  "title": "Version conflict",
  "status": 409,
  "code": "VERSION_CONFLICT",
  "detail": "Expected state version 3, actual 4",
  "instance": "/api/v1/chemistry/experiments/exp_1/operations",
  "correlationId": "uuid",
  "fieldErrors": [],
  "meta": { "expectedVersion": 3, "actualVersion": 4 },
  "timestamp": "2026-09-07T12:30:00Z"
}
```

Дополнительные codes: `WORKSPACE_NOT_FOUND`, `EXPERIMENT_NOT_FOUND`, `EVENT_NOT_FOUND`, `CAPABILITY_REQUIRED`, `SHARE_PASSWORD_REQUIRED`, `SHARE_LINK_EXPIRED`, `INVALID_OPERATION`, `UNKNOWN_MODEL`, `UNSUPPORTED_UNIT`, `SAFETY_VIOLATION`, `PREVIEW_UPLOAD_EXPIRED`, `CHECKSUM_MISMATCH`.

## 12. Security и data integrity

1. Проверять workspace scope на каждом nested resource, чтобы `linkId/threadId/messageId` другого workspace нельзя было использовать в URL текущего.
2. Rate limit resolve/password, chat, comments, invitation и chemistry commands.
3. Sanitization chat/comment/book rich text; CSP и safe URL rules для assets.
4. Не логировать Bearer/share/password/raw invitation tokens.
5. Persist audit для share create/update/revoke, member role changes, admin actions, chemistry safety overrides.
6. WebSocket subscription authorization проверять до subscribe и повторно при revoke/role change.
7. Maximum workspace payload/items/event size и maximum chemistry inputs.

## 13. Обязательные integration tests

1. Owner create workspace -> save state -> reload exact state/version.
2. Share resolve -> guest REST state -> realtime subscribe -> allowed edit/chat/comment.
3. Viewer mutation -> 403; editor manage links -> 403.
4. Revoke link немедленно ломает REST и realtime guest session.
5. Two clients same expectedVersion -> один success, второй 409 + recovery.
6. Preview signed upload -> complete -> preview GET; expired/checksum cases.
7. Start learning attempt -> use its `experimentId` -> operation -> new state version -> checkpoint.
8. Chemistry operation replay/idempotency and conservation checks.
9. Admin workspace list sees all workspaces and не делает N+1.
10. Unicode-path Maven build: protobuf generation сейчас ломается на `D:\\web-sites\\заказ`; исправить build/toolchain и запускать CI в Unicode path.

## 14. Definition of Done

- Sandbox загружает только backend state и сохраняет его с optimistic version.
- Chemistry operations изменяют canvas по canonical nested response.
- Shared link работает одинаково через REST и WebSocket.
- Admin Sharing получает global aggregate endpoint.
- Preview реально загружается и проверяется.
- OpenAPI фиксирует каждый req/res/error; generated frontend types проходят CI contract test.

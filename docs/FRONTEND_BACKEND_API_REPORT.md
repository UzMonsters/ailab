# jasScience — Frontend / Backend API Report

**Дата аудита:** 30 августа 2026  
**Основание:** текущий код в `frontend/`, `Backend/app/` и `Backend/identity-module/`.  
**Назначение:** карта интеграции для разработки UI, REST API и realtime-песочницы. Это не описание желаемого API: в таблицах зафиксировано то, что уже есть в коде.

## 1. Краткая архитектура

| Слой | Реализация | Назначение |
|---|---|---|
| Frontend | Next.js 16, React 19, TypeScript, Tailwind, next-intl | Пользовательский кабинет, Level Mode, Sandbox, Academy, Admin UI |
| HTTP client | `frontend/src/shared/api/client.ts` | JSON REST, Bearer access token, `credentials: include`, refresh при `401` |
| Realtime client | `frontend/src/entities/workspace/api/realtime/workspace-realtime.ts` | Нативный WebSocket + STOMP 1.2 |
| Backend | Java 21, Spring Boot 3.4 | REST API, security, workspace persistence, chemistry engine |
| Chemistry engine | `Backend/chemistry-engine` | Расчёты, simulation sessions, reactions, safety, thermodynamics |
| Persistence | PostgreSQL 15 | Пользователи, workspace, события, simulation session и каталоги |

```text
Browser / Next.js UI
  ├─ REST JSON ────────► http(s)://host:8080/api/v1/...
  ├─ Bearer token ────► Authorization: Bearer <accessToken>
  ├─ Refresh cookie ──► POST /api/v1/auth/refresh (HttpOnly cookie)
  └─ STOMP over WS ──► ws(s)://host:8080/ws
                            ├─ /app/...       client → backend
                            ├─ /topic/...     backend → all subscribers
                            └─ /user/queue/... backend → one user
```

### Общий HTTP-контракт

| Пункт | Фактическое поведение |
|---|---|
| Content type | `application/json` для всех вызовов стандартного клиента |
| Auth | Access token хранится в памяти frontend и отправляется как Bearer token |
| Refresh | При `401` frontend делает `POST /api/v1/auth/refresh`; refresh token ожидается из HttpOnly cookie, затем исходный запрос повторяется один раз |
| Errors | Нестатус `2xx` превращается во frontend `ApiError { status, message, errors?, fieldViolations? }` |
| Empty success | `204 No Content` становится `undefined` |
| Base URL | `NEXT_PUBLIC_API_URL`, иначе `http://localhost:8080` в development |

---

## 2. Admin: UI и фактический backend API

### 2.1. Что есть на frontend

Admin routes существуют как UI-страницы Next.js:

```text
/[locale]/admin
/[locale]/admin/users
/[locale]/admin/equipment, /admin/materials, /admin/elements, /admin/chemicals
/[locale]/admin/scenarios, /admin/safety, /admin/laboratories
/[locale]/admin/learning, /admin/learning/levels, /admin/learning/levels/new
/[locale]/admin/book, /admin/audit, /admin/settings
```

Фактический frontend API-клиент для Admin реализован только для **управления пользователями**: `frontend/src/entities/user/api/admin.api.ts`.

### 2.2. Admin REST — users

Все endpoints требуют администратора на backend.

| Method | Path | Request | Response | Frontend usage |
|---|---|---|---|---|
| `GET` | `/api/v1/admin/users` | — | `AdminUserResponse[]` | `adminApi.getUsers()` |
| `GET` | `/api/v1/admin/users/{id}` | path `id` | `AdminUserResponse` | `adminApi.getUser(id)` |
| `PUT` | `/api/v1/admin/users/{id}` | `AdminUpdateUserRequest` — `username`, `email`, `role` | `SuccessResponse { success }` | `adminApi.updateUser()` |
| `DELETE` | `/api/v1/admin/users/{id}` | path `id` | `SuccessResponse { success }` | `adminApi.deleteUser()` |

Создание пользователя в admin UI составное:

1. `POST /api/v1/auth/register` с `{ username, email, password }`.
2. Если нужна не обычная роль — `PUT /api/v1/admin/users/{id}` с `{ username, email, role }`.

### 2.3. Важный статус Admin

| Раздел admin UI | Backend CRUD/API в текущем коде | Статус |
|---|---|---|
| Users | Есть `/api/v1/admin/users` | Интегрируемый |
| Equipment | Есть публичный chemistry catalog `GET`, но нет admin `POST/PUT/DELETE` | UI / mock-дизайн |
| Materials / substances | Есть публичный catalog `GET`, но нет admin CRUD | UI / mock-дизайн |
| Elements / reactions | Есть публичные `GET` и calculation `POST`, но нет admin CRUD | UI / mock-дизайн |
| Learning levels / scenarios | Нет REST controller для уровней и редактора сценариев | UI / local state |
| Safety / laboratories / audit / settings / book | Нет выделенных admin REST controllers | UI / local state |

**Вывод:** Admin — это в основном UI design слой. Нельзя считать формы `/admin/*/new` подключёнными к backend до появления соответствующих админских REST endpoints.

---

## 3. Chemistry и Sandbox — REST API

### 3.1. Auth и пользователь (нужны и Chemistry, и Admin)

| Method | Path | Request (`req`) | Response (`res`) |
|---|---|---|---|
| `POST` | `/api/v1/auth/register` | `RegisterRequest { username, email, password }` | `RegisterResponse` (`201`) |
| `POST` | `/api/v1/auth/login` | `LoginRequest { email, password }` | `TokenResponse`; refresh token также устанавливается cookie |
| `POST` | `/api/v1/auth/refresh` | optional `{ refreshToken? }`, обычно cookie | `TokenResponse` + обновлённая cookie |
| `POST` | `/api/v1/auth/logout` | optional `{ refreshToken? }`, обычно cookie | `SuccessResponse { success }`; cookie очищается |
| `GET` | `/api/v1/users/me` | — | `UserMeResponse` |
| `PUT` | `/api/v1/users/me` | `UpdateProfileRequest` | `SuccessResponse` |
| `GET/PUT` | `/api/v1/users/me/preferences` | `PUT`: `UpdatePreferencesRequest` | `PreferencesResponse` / `SuccessResponse` |
| `GET` | `/api/v1/users/me/statistics` | — | `StatisticsResponse` |
| `PUT/DELETE` | `/api/v1/users/avatar` | `PUT`: `{ avatarUrl }` | `SuccessResponse` |
| `DELETE` | `/api/v1/users/me` | — | `SuccessResponse` |
| `GET` | `/api/v1/users/{id}` | path `id` | `PublicUserResponse` |

### 3.2. Workspaces: REST для canvas и сохранения

`frontend/src/entities/workspace/api/workspace.api.ts` уже вызывает эти маршруты. Почти все требуют авторизации.

| Method | Path | Request (`req`) | Response (`res`) |
|---|---|---|---|
| `GET` | `/api/v1/workspaces` | query: `science`, `search`, `sort`, `page`, `size`, `includeDeleted` | `WorkspacePageResponse<WorkspaceDetails>` |
| `POST` | `/api/v1/workspaces` | `CreateWorkspaceRequest { name, science, ... }` | `WorkspaceDetails` (`201`) |
| `GET` | `/api/v1/workspaces/{id}` | path `id` | `WorkspaceDetails` |
| `PUT` | `/api/v1/workspaces/{id}` | `UpdateWorkspaceRequest` (`name`, favorite/deleted state, thumbnail, version) | `WorkspaceDetails` |
| `DELETE` | `/api/v1/workspaces/{id}` | path `id` | `{ message }` |
| `POST` | `/api/v1/workspaces/{id}/duplicate` | optional `DuplicateWorkspaceRequest { name? }` | `WorkspaceDetails` |
| `POST` | `/api/v1/workspaces/{id}/restore` | — | `WorkspaceDetails` |
| `POST` | `/api/v1/workspaces/{id}/thumbnail` | `ThumbnailRequest { svg?, width?, height?, imageData? }` | `{ thumbnailUrl, updatedAt }` |
| `GET` | `/api/v1/workspaces/{id}/state` | — | `WorkspaceStateDto` |
| `PUT` | `/api/v1/workspaces/{id}/state?expectedVersion=n` | full `WorkspaceStateDto` | new canonical `WorkspaceStateDto` |
| `POST` | `/api/v1/workspaces/{id}/events` | `SandboxEventCommand` | `WorkspaceEventAck` |
| `GET` | `/api/v1/workspaces/{id}/events` | query `afterVersion?`, `limit?` | `Array<Record<string, unknown>>` |
| `POST` | `/api/v1/workspaces/{id}/undo?expectedVersion=n` | — | `WorkspaceStateDto` |
| `POST` | `/api/v1/workspaces/{id}/redo?expectedVersion=n` | — | `WorkspaceStateDto` |
| `POST` | `/api/v1/workspaces/{id}/publish` | optional `{ title?, description? }` | `{ workspaceId, shareUrl?, publishedAt? }` |
| `POST` | `/api/v1/workspaces/{id}/autosave` | `AutosaveRequest` | `{ stateVersion, savedAt }` |

#### Workspace event request/response

`SandboxEventCommand` — дискретное событие canvas. В нём используются `clientEventId` (идемпотентность), `expectedVersion` (optimistic locking), тип действия и payload.  
`WorkspaceEventAck` подтверждает результат и версию. При конфликте версий REST или WS возвращает/посылает `VERSION_CONFLICT` с expected/actual version.

### 3.3. Experiment / simulation session

| Method | Path | Request (`req`) | Response (`res`) |
|---|---|---|---|
| `POST` | `/api/v1/chemistry/experiments` | `CreateSimulationSessionRequest` | `SimulationState` |
| `GET` | `/api/v1/chemistry/experiments/{sessionId}` | path `sessionId` | `SimulationState` |
| `POST` | `/api/v1/chemistry/experiments/{sessionId}/operations` | `SimulationOperationRequest` (expected state version, idempotency key, scientific operation) | `SimulationExecutionResult` |
| `POST` | `/api/v1/chemistry/experiments/{sessionId}/events` | `AppendEventRequest` | `SimulationState` |
| `POST` | `/api/v1/chemistry/experiments/{sessionId}/replay` | — | `SimulationState` |
| `GET` | `/api/v1/chemistry/experiments/{sessionId}/audit/{eventId}` | path variables | `SimulationCalculationAudit` |

Frontend client: `frontend/src/entities/experiment/api/experiment.api.ts`. Сейчас в нём есть offline/mock fallback для create/get/operation/event/replay/audit при сетевой ошибке.

### 3.4. Catalog `GET` API

| Method | Path | Query / request | Response |
|---|---|---|---|
| `GET` | `/api/v1/chemistry/materials` | `query`, `phase`, `page`, `size` | `MaterialSummary[]` / catalog maps |
| `GET` | `/api/v1/chemistry/equipment` | `query`, `category`, `page`, `size` | `EquipmentSummary[]` / catalog maps |
| `GET` | `/api/v1/chemistry/equipment/{identifier}` | path | `EquipmentDetails` / map |
| `GET` | `/api/v1/chemistry/elements` | — | `ElementSummary[]` |
| `GET` | `/api/v1/chemistry/elements/{identifier}` | path | `ElementDetails` |
| `GET` | `/api/v1/chemistry/elements/{identifier}/properties` | path | `ElementPropertyDetails` |
| `GET` | `/api/v1/chemistry/compounds` | `name`, `formula`, `composition` | `CompoundSummary[]` |
| `GET` | `/api/v1/chemistry/compounds/{identifier}` | path | `CompoundDetails` |
| `GET` | `/api/v1/chemistry/compounds/{identifier}/properties` | path | `CompoundPhysicalPropertyDetails` |
| `GET` | `/api/v1/chemistry/thermodynamics/reference/{compoundCode}` | path | `ThermodynamicProfileDetails` |

### 3.5. Calculation `POST` API

Все calculation routes принимают JSON DTO и возвращают JSON result DTO. Frontend-обёртки определены в `frontend/src/entities/element/api/chemistry.api.ts`.

| Domain | `POST` paths | Request / response family |
|---|---|---|
| Formula / equations | `/chemistry/formulas/parse`; `/chemistry/equations/balance` | `{ formula }` → `ChemicalFormula`; `{ equation }` → `BalancedEquation` |
| Thermodynamics | `/thermodynamics/calculate`, `/hess-law`, `/calorimetry/sensible-heat`, `/calorimetry/thermal-mixing`, `/calorimetry/reaction-heat` | thermodynamic/calorimetry request DTO → result DTO |
| Acid-base | `/acid-base/water`, `/strong-acid`, `/strong-base`, `/weak-acid`, `/weak-base`, `/salt-hydrolysis`, `/buffer`, `/buffer/preparation`, `/buffer/perturbation`, `/titration/characteristic-points`, `/polyprotic-titration/characteristic-points` | acid/base, buffer and titration request DTO → calculation result |
| Kinetics | `/kinetics/rate`, `/integrated-law`, `/half-life`, `/arrhenius`, `/progress` | kinetic request DTO → rate/law/half-life/Arrhenius/progress result |
| Electrochemistry | `/electrochemistry/standard-cell`, `/nernst`, `/electrolysis` | electrochemical request → cell/Nernst/electrolysis result |
| Gas | `/gas/state`, `/mixture`, `/transformation` | gas-state/mixture/transformation request → state or mixture result |
| Safety | `/chemistry/safety/evaluate` | `LaboratorySafetyEvaluationRequest` → `LaboratorySafetyEvaluationResult` |

> Полные поля DTO определены в `frontend/src/shared/api/contracts/definitions.ts`, domain records backend и OpenAPI. Для нового frontend кода следует использовать типы из `definitions.ts`, а не собирать JSON вручную.

---

## 4. WebSocket / STOMP realtime

### 4.1. Подключение

| Параметр | Значение |
|---|---|
| Transport | WebSocket |
| Endpoint | `/ws` |
| Protocol | STOMP 1.2 |
| Auth | заголовок STOMP `authorization: Bearer <accessToken>` |
| Heartbeat | `10000,10000` |
| Frontend connector | `connectWorkspaceRealtime(workspaceId, sessionId, handlers)` |

Если access token отсутствует, frontend возвращает noop-connection и не пытается подключиться.

### 4.2. Client → server (`SEND`)

| Destination | Payload (`req`) | Backend action |
|---|---|---|
| `/app/workspaces/{workspaceId}/events` | `SandboxEventCommand` | Сохраняет и применяет canvas event, проверяет версию/идемпотентность |
| `/app/experiments/{sessionId}/commands` | `{ commandId?, idempotencyKey?, expectedStateVersion?, command/type, stepId?, targetVesselId?, inputs? }` | Преобразует в `SimulationCommand`, выполняет engine operation |
| `/app/workspaces/{workspaceId}/presence` | `{ status?: "ONLINE" | ... }` | Проверяет доступ и публикует presence event |

### 4.3. Server → client (`SUBSCRIBE`)

| Destination | Payload (`res`) | Для чего |
|---|---|---|
| `/topic/workspaces/{workspaceId}` | `WorkspaceEventAck` | Общие события workspace |
| `/topic/workspaces/{workspaceId}/presence` | `{ userId, status, at }` | Presence участников |
| `/topic/experiments/{sessionId}` | simulation payload | Новое состояние/результат experiment |
| `/user/queue/acks` | event или command acknowledgement | Подтверждение исходному пользователю |
| `/user/queue/errors` | `RealtimeError` | `VERSION_CONFLICT`, `EVENT_ERROR`, `SIMULATION_ERROR` |

### 4.4. Realtime response examples

```json
// /user/queue/acks — пример workspace ack
{
  "status": "acknowledged",
  "eventId": "evt-...",
  "stateVersion": 17
}
```

```json
// /user/queue/errors — пример конфликта optimistic locking
{
  "code": "VERSION_CONFLICT",
  "message": "Workspace state has changed",
  "clientEventId": "client-evt-...",
  "expectedVersion": 12,
  "actualVersion": 13
}
```

---

## 5. Frontend ↔ backend: фактические несоответствия и риски

| Area | Наблюдение | Последствие / рекомендация |
|---|---|---|
| Admin | Большинство admin страниц не имеет backend CRUD | Не подключать формы к «воображаемым» endpoints; сначала утвердить admin contract |
| Offline fallbacks | `workspace.api.ts` и `experiment.api.ts` заменяют ошибки mock-данными | Хорошо для demo, но production UI может скрыть outage; показывать `offline` state явно |
| API types | Frontend имеет нормализаторы (`user.api.ts`, `admin.api.ts`) | Продолжать держать adapter layer, не связывать UI напрямую с backend shape |
| Local sandbox engine | Часть Level Mode/mix/spill работает локально в React/engine | Для сохранения/коллаборации отправлять canonical state/event через workspace REST/WS |
| I18n | API error messages могут приходить с backend на английском | Для UX локализовать code/message mapping на frontend |
| Content deletion | Liquid/aqueous content нельзя «разделить» через UI | Текущая frontend логика скрывает удаление жидких компонентов; это соответствует модели растворов |
| Versioning | REST и WS поддерживают expected version/idempotency | Любая новая mutating операция должна передавать их и уметь обработать conflict |

## 6. Где смотреть полные контракты

- [API_AND_WEBSOCKET_CONTRACT.md](API_AND_WEBSOCKET_CONTRACT.md) — расширенный REST + STOMP contract.
- [BACKEND_FRONTEND_HANDOFF.md](BACKEND_FRONTEND_HANDOFF.md) — handoff с endpoint catalog и DTO.
- `frontend/src/shared/api/contracts/definitions.ts` — frontend TypeScript contracts.
- `Backend/app/src/main/java/com/ailab/chemistry/controller/` — chemistry REST controllers.
- `Backend/app/src/main/java/com/ailab/workspace/` — workspace REST + realtime controller.
- `Backend/identity-module/src/main/java/com/ailab/auth/controller/` и `.../user/controller/` — auth/user/admin controllers.

## 7. Итог

1. **Chemistry/Sandbox имеет реальный backend**: REST catalog/calculation/session/workspace API и STOMP realtime.
2. **Admin в текущей задаче — в основном frontend UI**; реальный API есть для users, но не для управления chemistry/learning catalog.
3. Для новых features сначала выбирать один из двух путей:  
   - локальный Level Mode / UI-only с явным local state; или  
   - persistent/real-time feature через versioned REST + STOMP.  
   Не смешивать эти два режима без синхронизационного контракта.

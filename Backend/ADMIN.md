# Admin

Канонический документ для `/admin`: users, settings, chemistry catalog, levels, book, assets, publication и audit.

## Результат сверки backend: что не создавать заново

| Уже реализовано в backend | Действие frontend/backend |
|---|---|
| `GET/GET by id/PUT/DELETE /api/v1/admin/users` | Не создавать новый Users CRUD. Расширить существующий controller pagination, filters, status/block, activity/progress, version и audit. |
| Public `GET /api/v1/chemistry/elements`, `compounds`, `equipment`, `materials` | Не создавать второй public catalog. Добавить отдельный admin draft/CRUD/publish слой поверх тех же domain entities. |
| `GET/POST/PUT/... /api/v1/workspaces` и experiment APIs | Не использовать для user dashboard как admin monitoring API. Построить только read-model/aggregation endpoints для admin. |
| Chemistry safety/calculation APIs | Не дублировать scientific logic в admin; admin управляет versioned definitions/rules, engine остаётся источником расчётов. |

Всё остальное из Admin mock — Dashboard, Audit Log, Settings, Learning CMS, Book CMS, Scenario CMS, catalog drafts/publish и Safety-rule CRUD — **ещё не реализовано** и должно быть создано.

## Модули

| Модуль | Что управляет |
|---|---|
| Dashboard | System health, pending validation, recent audit events |
| Users | Roles, status, search, pagination и блокировка |
| Catalog | Equipment, materials, ports, appearance и safety metadata |
| Levels | Draft scenarios, steps, checkpoints, guide targets и publish |
| Book | Chapters, pages, blocks, translations и assets |
| Settings | Project name, public URL, auth mode, feature flags, limits, preview и simulation config |
| Audit | Actor, action, before/after, request id, timestamp |

## Общие правила

- Все mutation endpoints используют RBAC, audit и optimistic locking.
- Draft и published версии разделены; published content immutable.
- Таблицы используют server pagination/filter/sort.
- Save кнопка всегда показывает saving/saved/error/conflict; декоративных кнопок быть не должно.
- Admin UI поддерживает RU/EN/UZ и показывает незаполненные переводы.

## API

| API | Что делает и зачем | Request | Response |
|---|---|---|---|
| `GET /api/v1/admin/me/permissions` | Определяет разрешённые admin actions. | Bearer token. | `200 { roles,permissions }`; `403`. |
| `GET /api/v1/admin/users` | Загружает серверную таблицу users. | `page,size,q,role,status,sort`. | `200 { items,page }`. |
| `PATCH /api/v1/admin/users/{id}` | Меняет role/status с причиной. | `{ role?,status?,reason }`, `If-Match`. | `200 AdminUser`; `409`. |
| `GET/PATCH /api/v1/admin/settings` | Читает/меняет название проекта, flags и limits. | PATCH изменяемые поля + `If-Match`. | `200 AdminSettings { version }`. |
| `GET/POST /api/v1/admin/catalog/equipment` | Список и создание equipment definitions. | POST `{ code,rendererKey,ports,limits,translations }`. | `200 list` / `201 Equipment`. |
| `PATCH /api/v1/admin/catalog/equipment/{id}` | Обновляет equipment/ports. | Partial definition + version. | `200 Equipment`; `422 PORT_SCHEMA_INVALID`. |
| `GET/POST /api/v1/admin/catalog/materials` | Управляет веществами и appearance. | POST `{ code,formula,phase,appearance,translations }`. | `200 list` / `201 Material`. |
| `GET/POST /api/v1/admin/learning/levels` | Загружает/создаёт level drafts. | POST `{ trackId,order,scenario,checkpoints,translations }`. | `201 LevelDraft`. |
| `POST /api/v1/admin/learning/levels/{id}/validate` | Проверяет ports, guide targets и переводы. | `{ version }`. | `200 { valid,errors,warnings }`. |
| `POST /api/v1/admin/learning/levels/{id}/publish` | Публикует version уровня. | `{ version,idempotencyKey }`. | `201 { publishedVersion }`. |
| `POST /api/v1/admin/assets/upload-urls` | Безопасно загружает book/catalog media. | `{ files:[...] }`. | `200 { uploads:[...] }`. |
| `GET /api/v1/admin/audit-events` | Показывает историю административных изменений. | Filters + pagination. | `200 { items,page }`. |

Book endpoints вынесены в канонический [BOOK.md](./BOOK.md); остальные admin-контракты полностью собраны ниже.

## Статусы и публикация

Content entities используют lifecycle `DRAFT → VALIDATING → PUBLISHED → ARCHIVED`. Ошибочная validation не меняет опубликованную version. Rollback создаёт новую published revision на основе выбранного snapshot и записывается в audit.

Каждая mutation передаёт:

```http
Authorization: Bearer <token>
If-Match: <entity-version>
Idempotency-Key: <uuid>
X-Request-Id: <uuid>
```

## Settings contract

`AdminSettings` должен разделять системные и пользовательские настройки:

```json
{
  "project": { "name":"jasScience", "publicUrl":"https://...", "defaultLocale":"ru" },
  "features": { "book":true, "levels":true, "sharing":false, "threeD":false },
  "auth": { "mode":"DEMO", "guestEnabled":true },
  "simulation": { "maxObjects":100, "operationTimeoutMs":10000 },
  "assets": { "maxImageBytes":5242880, "allowedMimeTypes":["image/png","image/webp","image/svg+xml"] },
  "version": 4
}
```

Secrets, database URLs и private keys никогда не возвращаются frontend. UI получает только безопасную конфигурацию.

## Backend и frontend задачи

| Приоритет | Backend | Frontend |
|---|---|---|
| P0 | RBAC middleware, audit, optimistic locking | Убрать прямые `mocks/admin/*`, использовать repositories |
| P0 | Versioned levels/catalog/book models | Loading/error/empty/saving/conflict states |
| P0 | Validation + immutable publish | Validation modal, diff и current version |
| P1 | Asset storage, MIME/size/virus checks | Upload progress, media picker и alt/caption |
| P1 | Server pagination/filter/sort | URL-backed tables и typed filters |
| P2 | Bulk import/export и approval workflow | Preview, scheduled publish и review UI |

## Безопасность и ошибки

- `USER` не получает admin endpoints даже при прямом HTTP-вызове.
- Audit хранит actor, IP/request id, action, entity, before/after summary и timestamp.
- Scenario нельзя публиковать, если guide target/port отсутствует в catalog version.
- Ошибки: `403 FORBIDDEN`, `404 ENTITY_NOT_FOUND`, `409 VERSION_CONFLICT`, `422 VALIDATION_ERROR`, `422 PORT_SCHEMA_INVALID`, `423 PUBLISH_IN_PROGRESS`.
- Mock mode помечается `Demo data; changes are local`; remote error никогда не превращается в fake success.

## Acceptance criteria

- Все admin forms реально сохраняют данные или явно обозначены demo/local.
- Два редактора не перезаписывают изменения друг друга.
- Published version воспроизводима, доступна для rollback и имеет audit event.
- Project name/settings после сохранения применяются в landing/dashboard без rebuild.

## Аудит текущего frontend mock и backend

| Frontend admin-раздел | Что уже показывает frontend | Что есть в backend сейчас | Что требуется |
|---|---|---|---|
| Dashboard | Overview, Learning, Laboratories, Science, Activity; KPI, trends, charts, active labs, export | Специального admin dashboard API нет | Aggregation/read-model API и async export |
| Users | Students/Teachers/Admins/Blocked, activity, learning progress, edit, block/delete | `AdminUserController`: list/get/update/delete | Расширить filters, block/unblock, activity/progress и audit-safe delete |
| Laboratories | Active sessions/workspaces, owner, object count, runtime/status | User workspace API есть, admin monitoring API нет | Admin session monitoring, details, pause/terminate only with permission/reason |
| Learning | Overview, Levels, Chapters, Tasks, Rewards, Progress, Localization | Learning domain отсутствует | Tracks/levels/steps/tasks/progress/localization CRUD |
| Book Studio | Chapters, spreads/pages, text/media/layout/locale, preview/save | Book backend отсутствует | Использовать полный contract из `BOOK.md` |
| Chemistry | Elements, Substances, Reactions, Properties, Hazards, Scenarios | Public elements/compounds/equipment/materials mostly GET | Versioned admin CRUD + validate/publish/archive |
| Equipment | Categories, ports, compatibility, status, usage | Public equipment GET | Admin CRUD, ports/compatibility validation, assets/renderer keys |
| Materials | Liquids, solids, gases, solutions, biological/physical samples | Public materials GET | Admin CRUD, phase/appearance/safety/localization |
| Scenarios | Published/Draft/Archived, difficulty, completion, builder | Отдельного admin scenario CRUD нет | Draft/version/validate/publish/archive/analytics |
| Safety | Rules by category/severity, add/edit/delete | Chemistry safety evaluate есть | Admin safety-rule CRUD/version/publish |
| Audit Log | Search/filter/detail; actor/action/entity/subject/source/result/severity/time | Централизованного admin audit query API нет | Append-only audit store, filters/detail/export/retention |
| Settings | General, subjects, languages, learning, laboratory, simulation, safety, appearance, features, administration | Admin settings API нет | Typed settings read/update/schema/history, no secret exposure |
| Physics/Biology mocks | Models/constants, specimens/processes/equipment | Domain API отсутствует | Оставить disabled/coming soon либо создать отдельные domain APIs; не выдавать mock за сохранённое |

## Admin Dashboard API

| API | Зачем нужен и что делает | Request | Response |
|---|---|---|---|
| `GET /api/v1/admin/dashboard/summary` | Заполняет KPI Overview: users, active labs, experiments, average score, safety incidents и trends. | Query `from`, `to`, `timezone`, optional `science`. | `200 { period,kpis:{totalUsers,activeLabs,experiments,averageScore,safetyIncidents},comparison }`. |
| `GET /api/v1/admin/dashboard/activity-series` | Даёт chart Platform Activity без hardcoded Mon–Sun. | Query `metric=experiments|users|sessions`, `from`, `to`, `bucket=hour|day|week`, `timezone`. | `200 { metric,unit,points:[{at,value}] }`. |
| `GET /api/v1/admin/dashboard/science-distribution` | Заполняет доли Chemistry/Physics/Biology. | Query `from`, `to`, `metric=labs|experiments`. | `200 { total,items:[{science,count,percentage}] }`. |
| `GET /api/v1/admin/dashboard/learning-summary` | Заполняет enrollments, completion time, success rate и recent activity. | Query `track?`, `from`, `to`. | `200 { enrollments,averageCompletionSeconds,successRate,recent:[...] }`. |
| `GET /api/v1/admin/dashboard/laboratory-summary` | Возвращает active/paused labs и science breakdown. | Query `science?`, `status?`. | `200 { activeNow,byScience,byStatus }`. |
| `GET /api/v1/admin/dashboard/activity-summary` | Заполняет Online now, experiments today, completed lessons, average session. | Query `at?`, `timezone`. | `200 { onlineNow,experimentsToday,lessonsCompleted,averageSessionSeconds }`. |
| `POST /api/v1/admin/reports` | Создаёт CSV/XLSX/PDF export, чтобы тяжёлый отчёт не блокировал request. | `{ type:"PLATFORM_OVERVIEW",format:"CSV",filters:{from,to,science?} }`. | `202 { jobId,status:"QUEUED" }`. |
| `GET /api/v1/admin/reports/{jobId}` | Проверяет export job и отдаёт короткоживущую download URL. | Path `jobId`. | `200 { status:"RUNNING|READY|FAILED",downloadUrl?,expiresAt?,error? }`. |

Dashboard aggregation должен читать готовые counters/read models, а не выполнять тяжёлые `COUNT(*)` по event tables на каждый page load. Все timestamps возвращаются в UTC, группировка учитывает переданный timezone.

## Users и laboratory monitoring API

| API | Зачем нужен и что делает | Request | Response |
|---|---|---|---|
| `GET /api/v1/admin/users` | Питает все user tabs с server pagination/filter/sort. | `page,size,q,role,status,createdFrom,createdTo,sort`. | `200 { items:[AdminUser],page }`. |
| `GET /api/v1/admin/users/{id}` | Открывает drawer/edit profile с admin-safe полями. | Path id. | `200 { user,roles,status,statistics,createdAt,lastSeenAt,version }`. |
| `PATCH /api/v1/admin/users/{id}` | Меняет profile/role/status; причина обязательна для role/block. | `{ username?,roles?,status?,reason }`, `If-Match`. | `200 AdminUser`; `409`; `422`. |
| `POST /api/v1/admin/users/{id}/block` | Явно блокирует user и отзывает sessions. | `{ reason,until? }`, `Idempotency-Key`. | `200 { id,status:"BLOCKED",sessionsRevoked }`. |
| `POST /api/v1/admin/users/{id}/unblock` | Снимает блокировку с audit. | `{ reason }`. | `200 { id,status:"ACTIVE" }`. |
| `GET /api/v1/admin/users/{id}/activity` | Заполняет Activity Logs пользователя. | `from,to,type,page,size`. | `200 { items:[UserActivity],page }`. |
| `GET /api/v1/admin/users/{id}/learning-progress` | Заполняет Progress tab. | Query `track?`. | `200 { tracks,attempts,completedLevels,lastActivityAt }`. |
| `DELETE /api/v1/admin/users/{id}` | Планирует/анонимизирует account, а не бесследно удаляет audit. | `{ reason,mode:"DEACTIVATE|SCHEDULE_DELETE" }`. | `202 { status,deletionScheduledFor? }`. |
| `GET /api/v1/admin/laboratory-sessions` | Питает Active Laboratories и Laboratories tab. | `page,size,q,science,status,ownerId,startedFrom`. | `200 { items:[{sessionId,workspaceId,name,science,owner,objectCount,runtimeSeconds,status,lastEventAt}],page }`. |
| `GET /api/v1/admin/laboratory-sessions/{id}` | Открывает безопасную диагностику session. | Path id. | `200 { session,workspaceSummary,safetyState,participants,latestEvents }`. |
| `POST /api/v1/admin/laboratory-sessions/{id}/pause` | Останавливает опасную/проблемную simulation. | `{ reason }`. | `200 { status:"PAUSED",pausedAt }`. |
| `POST /api/v1/admin/laboratory-sessions/{id}/terminate` | Завершает session при incident с обязательным audit. | `{ reason,notifyOwner:true }`. | `202 { status:"TERMINATING" }`. |

## Audit Logs API

| API | Зачем нужен и что делает | Request | Response |
|---|---|---|---|
| `GET /api/v1/admin/audit-events` | Заменяет `mockAuditEvents`, поддерживает поиск и filters по actor/action/entity/subject/source/result/severity/date. | `page,size,q,actorId,action,entityType,entityId,subject,source,result,severity,from,to,sort`. | `200 { items:[AuditEvent],page,facets:{actions,severities,sources} }`. |
| `GET /api/v1/admin/audit-events/{eventId}` | Открывает detail drawer с before/after, metadata и trace. | Path eventId. | `200 { id,occurredAt,actor,action,entity,source,result,severity,before,after,requestId,ip,userAgent,metadata }`. |
| `POST /api/v1/admin/audit-exports` | Экспортирует выбранный диапазон без передачи миллионов строк браузеру. | `{ format:"CSV|JSON",filters:{...} }`. | `202 { jobId,status:"QUEUED" }`. |
| `GET /api/v1/admin/audit-exports/{jobId}` | Возвращает статус и signed URL готового файла. | Path jobId. | `200 { status,downloadUrl?,expiresAt? }`. |
| `GET /api/v1/admin/audit-retention` | Показывает политику хранения audit. | Нет body. | `200 { retentionDays,immutable,archiveEnabled }`. |

`AuditEvent` append-only: UI не получает update/delete API. В audit записываются `user.login`, `user.blocked`, `setting.changed`, `scenario.published`, `equipment.updated`, `level.completed`, `safety.triggered`, workspace admin interventions и export actions.

## Settings API — все поля из frontend mock

| API | Зачем нужен и что делает | Request | Response |
|---|---|---|---|
| `GET /api/v1/admin/settings` | Загружает все settings одним versioned snapshot. | Нет body. | `200 AdminSettings` со структурами ниже. |
| `PATCH /api/v1/admin/settings` | Частично сохраняет General/Language/Learning/Lab/Simulation/Safety/Appearance/Features/Admin. | `{ general?,subjects?,languages?,learning?,laboratory?,simulation?,safety?,appearance?,features?,administration? }`, `If-Match`. | `200 { settings,version,updatedAt,restartRequiredKeys:[] }`; `409`; `422`. |
| `GET /api/v1/admin/settings/schema` | Возвращает типы, allowed values, min/max и описание, чтобы UI не дублировал validation. | Query `locale`. | `200 { groups:[{key,fields:[{key,type,allowed,min,max,sensitive,restartRequired}]}] }`. |
| `GET /api/v1/admin/settings/history` | Показывает кто менял settings и позволяет сравнить версии. | `page,size,from,to,actorId`. | `200 { items:[{version,actor,changedKeys,createdAt}],page }`. |
| `POST /api/v1/admin/settings/{version}/restore` | Восстанавливает старую безопасную конфигурацию как новую version. | `{ reason }`. | `201 { version,restoredFrom,settings }`. |
| `GET /api/v1/admin/subjects` | Управляет Chemistry/Physics/Biology availability/accent/order. | Нет body. | `200 { items:[{id,name,enabled,accent,order}] }`. |
| `PATCH /api/v1/admin/subjects/{id}` | Включает/выключает предмет без удаления данных. | `{ enabled?,accent?,order? }`. | `200 Subject`. |

`AdminSettings` обязан покрыть фактические mock keys:

- `general`: appName, adminTitle, environment (read-only), defaultLocale, timezone, dateFormat, supportEmail;
- `languages`: available RU/EN/UZ и default;
- `learning`: enableLevels, enableBadges, enablePrerequisites, allowReplay, showLockedNames, minPassingScore, maxAttempts; XP/defaultXp/hintPenalty удалить, если XP исключён;
- `laboratory`: workspaceGrid, objectsLimit, autosave, sharing;
- `simulation`: enableEvaporation, fluidTransfer, thermalShock, particles;
- `safety`: enableWarnings, pauseOnCriticalFailure;
- `appearance`: theme, density, animations;
- `features`: sandboxBeta, achievements, aiAssistant;
- `administration`: showEntityIds.

## Catalog, scenarios и safety API

| API | Зачем нужен и что делает | Request | Response |
|---|---|---|---|
| `GET/POST /api/v1/admin/chemistry/elements` | Заменяет mock periodic elements и создаёт draft element metadata. | POST `{ atomicNumber,symbol,properties,translations,status }`. | `200 page` / `201 ElementDraft`. |
| `GET/PATCH /api/v1/admin/chemistry/elements/{id}` | Detail/edit element и publish metadata. | PATCH partial + `If-Match`. | `200 ElementDraft`; `409`. |
| `GET/POST /api/v1/admin/chemistry/substances` | Заменяет mock substances; phase, formula, properties, hazards, appearance. | POST `{ code,formula,phase,properties,hazards,appearance,translations }`. | `201 SubstanceDraft`. |
| `GET/POST /api/v1/admin/chemistry/reactions` | Создаёт reaction rules, conditions, products и visual result. | POST `{ reactants,products,conditions,energy,appearance,safety,translations }`. | `201 ReactionDraft`. |
| `POST /api/v1/admin/chemistry/reactions/{id}/validate` | Проверяет mass/charge balance, referenced materials и result schema. | `{ version }`. | `200 { valid,errors,warnings,balance }`. |
| `GET/POST /api/v1/admin/equipment` | Управляет equipment, categories, 2D/3D assets и capabilities. | POST `{ code,rendererKey,category,ports,limits,assets,translations }`. | `201 EquipmentDraft`. |
| `PUT /api/v1/admin/equipment/{id}/ports` | Сохраняет полный versioned port set. | `{ version,ports:[{id,type,direction,connector,anchor2d,anchor3d}] }`. | `200 { version,ports,validation }`. |
| `PUT /api/v1/admin/equipment/{id}/compatibility` | Определяет adapters/allowed connections. | `{ version,rules:[...] }`. | `200 CompatibilitySet`. |
| `GET/POST /api/v1/admin/materials` | Управляет liquids/solids/gases/solutions/biological/physical samples. | POST `{ code,type,phase,properties,appearance,safety,translations,status }`. | `201 MaterialDraft`. |
| `GET/POST /api/v1/admin/scenarios` | Заменяет mock scenarios list/builder. | POST `{ subject,difficulty,availableItems,steps,checkpoints,guideTargets,translations }`. | `201 ScenarioDraft`. |
| `POST /api/v1/admin/scenarios/{id}/validate` | Проверяет шаги, ports, references, translations и engine compatibility. | `{ version }`. | `200 { valid,errors,warnings }`. |
| `POST /api/v1/admin/scenarios/{id}/publish` | Публикует immutable scenario version. | `{ version,idempotencyKey }`. | `201 { publishedVersion,publishedAt }`. |
| `GET/POST /api/v1/admin/safety-rules` | Заменяет safety mock CRUD. | POST `{ code,category,severity,condition,effect,translations }`. | `201 SafetyRuleDraft`. |
| `POST /api/v1/admin/safety-rules/{id}/publish` | Делает правило активным для evaluation engine. | `{ version }`. | `201 { publishedVersion }`. |

Все list endpoints имеют `page,size,q,status,sort`; все draft mutations имеют `version`, validation и audit. Public chemistry GET endpoints читают только published snapshot.

## Точный статус backend на момент аудита

| Backend endpoint | Реально есть | Не хватает для frontend mock |
|---|---|---|
| `GET /api/v1/admin/users` | Да: возвращает простой `List<AdminUserResponse>` | Нет `page`, `size`, search, role/status filters, sort, `lastActive`, user activity/progress. |
| `GET /api/v1/admin/users/{id}` | Да | Нет admin-only status/block information, sessions, audit summary, version. |
| `PUT /api/v1/admin/users/{id}` | Да: `{ username,email,role }` → `{ success:true }` | Нет partial update, status/block reason, optimistic locking, updated user payload. |
| `DELETE /api/v1/admin/users/{id}` | Да: `{ success:true }` | Нет deactivation/scheduled-delete/reason/audit-safe contract. |
| `GET /api/v1/chemistry/elements|compounds|equipment|materials` | Да, public catalog read API | Нет admin CRUD, drafts, translations, publish/version/audit. |
| `/api/v1/workspaces/*` | Да, owner workspace lifecycle/state/events/thumbnail | Нет admin dashboard/session monitoring aggregate APIs. |
| `/api/v1/chemistry/experiments/*` | Да, session/operation/audit/replay | Нет admin metrics/read models, level/scenario CMS. |
| `/api/v1/admin/dashboard/*` | Нет | Создать все endpoints из таблицы Dashboard. |
| `/api/v1/admin/audit-*` | Нет | Создать immutable audit query/export/retention API. |
| `/api/v1/admin/settings*` | Нет | Создать versioned settings/config/schema/history API. |

### Текущий Admin Users contract

```http
GET /api/v1/admin/users
Authorization: Bearer <admin-access-token>
```

```json
[
  {
    "id":"usr_01",
    "username":"Jasur Karimov",
    "email":"jasur@example.com",
    "role":"ADMIN",
    "avatarUrl":null,
    "level":4,
    "xp":48896,
    "language":"ru",
    "theme":"dark",
    "applicationSettings":{},
    "statistics":{},
    "achievements":[]
  }
]
```

Это объясняет экран `Level / XP` в текущем mock. Если XP действительно удаляется из продукта, миграция должна удалить XP одновременно из `UserMeResponse`, `AdminUserResponse`, таблицы Users и System Settings — нельзя оставлять его только на одном экране.

### Целевой Admin Users list contract

```http
GET /api/v1/admin/users?page=0&size=10&role=STUDENT&status=ACTIVE&sort=lastActive,desc
Authorization: Bearer <admin-access-token>
Accept-Language: ru
```

```json
{
  "items":[
    {
      "id":"usr_01",
      "displayName":"Jasur Karimov",
      "email":"jasur@example.com",
      "role":"ADMIN",
      "status":"ACTIVE",
      "level":4,
      "xp":48896,
      "lastActiveAt":"2026-09-01T09:14:00Z",
      "createdAt":"2026-05-10T08:00:00Z",
      "version":12
    }
  ],
  "page":{"number":0,"size":10,"totalElements":45,"totalPages":5},
  "facets":{"roles":{"STUDENT":38,"TEACHER":4,"ADMIN":3},"statuses":{"ACTIVE":42,"BLOCKED":3}}
}
```

### Целевой Settings request/response

```http
PATCH /api/v1/admin/settings
Authorization: Bearer <admin-access-token>
If-Match: "settings-v12"
Idempotency-Key: 25e7ba96-d8fd-4ec4-9e4d-9ce6c1ad3153
Content-Type: application/json
```

```json
{
  "general":{"appName":"jasScience","defaultLocale":"ru","timezone":"Asia/Tashkent","dateFormat":"DD.MM.YYYY","supportEmail":"support@jasscience.com"},
  "languages":{"available":["en","ru","uz"],"default":"ru"},
  "learning":{"enableLevels":true,"enableBadges":true,"enablePrerequisites":true,"allowReplay":true,"showLockedNames":true,"minPassingScore":70,"maxAttempts":5},
  "laboratory":{"workspaceGrid":true,"objectsLimit":50,"autosave":true,"sharing":true},
  "simulation":{"enableEvaporation":true,"fluidTransfer":true,"thermalShock":true,"particles":false},
  "safety":{"enableWarnings":true,"pauseOnCriticalFailure":true},
  "features":{"sandboxBeta":true,"achievements":true,"aiAssistant":false},
  "administration":{"showEntityIds":false}
}
```

```json
{
  "settings":{
    "general":{"appName":"jasScience","environment":"PRODUCTION","defaultLocale":"ru","timezone":"Asia/Tashkent","dateFormat":"DD.MM.YYYY","supportEmail":"support@jasscience.com"},
    "languages":{"available":["en","ru","uz"],"default":"ru"},
    "learning":{"enableLevels":true,"enableBadges":true,"enablePrerequisites":true,"allowReplay":true,"showLockedNames":true,"minPassingScore":70,"maxAttempts":5},
    "laboratory":{"workspaceGrid":true,"objectsLimit":50,"autosave":true,"sharing":true},
    "simulation":{"enableEvaporation":true,"fluidTransfer":true,"thermalShock":true,"particles":false},
    "safety":{"enableWarnings":true,"pauseOnCriticalFailure":true},
    "features":{"sandboxBeta":true,"achievements":true,"aiAssistant":false},
    "administration":{"showEntityIds":false}
  },
  "version":13,
  "etag":"settings-v13",
  "updatedAt":"2026-09-01T12:00:00Z",
  "updatedBy":{"id":"usr_01","displayName":"Jasur Karimov"},
  "restartRequiredKeys":[]
}
```

`appearance.theme`, density и animations — это прежде всего frontend preferences/theme tokens. Их нельзя делать глобальным server setting, который меняет тему всем пользователям. Глобально в backend можно хранить лишь design defaults или feature availability; личная тема остаётся в `PUT /api/v1/users/me/preferences`.

### Целевой Audit response

```json
{
  "items":[
    {
      "id":"aud_1000",
      "occurredAt":"2026-09-01T12:00:00Z",
      "actor":{"id":"usr_01","displayName":"Jasur Karimov","role":"ADMIN"},
      "action":"setting.changed",
      "entity":{"type":"SYSTEM_SETTINGS","id":"global","label":"System settings"},
      "source":"ADMIN_WEB",
      "result":"SUCCESS",
      "severity":"MEDIUM",
      "changedKeys":["laboratory.sharing","simulation.thermalShock"],
      "requestId":"req_a1b2"
    }
  ],
  "page":{"number":0,"size":50,"totalElements":1,"totalPages":1}
}
```

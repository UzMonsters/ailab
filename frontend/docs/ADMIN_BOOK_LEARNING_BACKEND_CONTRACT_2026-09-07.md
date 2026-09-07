# Backend contract: Admin, Book, Learning Level

Дата аудита: 2026-09-07  
Статус: frontend является целевым клиентом; документ описывает, что backend должен отдавать, чтобы текущие admin pages, Dashboard, Book Studio и `/lchemistry-level` работали без mock/fallback данных.

## 1. Результат аудита и порядок исправлений

### P0 — блокирует запуск

1. Удалить конфликтующий mapping в `AdminLearningScenariosController`: оставить только `/api/v1/admin/scenarios`. Сейчас controller также объявляет `/api/v1/admin/learning/levels`, который уже принадлежит `AdminLearningController`; Spring может завершить запуск с ambiguous mapping.
2. Реализовать настоящее хранилище assets. `AdminAssetController` возвращает `https://storage.jasscience.dev/uploads/...`, но такого upload endpoint/service в проекте нет. Book Studio не может загрузить изображения.
3. Создать и опубликовать книгу со slug `chemistry-lab` (или вернуть slug из конфигурации). Reader больше не маскирует отсутствие backend-книги локальным контентом.
4. Все пользовательские операции attempt/progress должны требовать authenticated user. Сейчас blanket permit для `/api/v1/learning/**` позволяет менять попытки без надежной идентификации владельца.

### P1 — корректность интеграции

1. Привести все ошибки к единому JSON-контракту из раздела 3.
2. На write endpoints строго проверять optimistic version; не игнорировать неверный `If-Match` или `expectedVersion`.
3. Разрешить CORS headers `If-Match`, `Idempotency-Key`, `X-Correlation-Id` и expose `ETag`, `Location`, `X-Correlation-Id`.
4. Заменить нетипизированные `Map<String,Object>` DTO в admin catalog на records/classes и опубликовать OpenAPI schema.
5. Ограничить допустимые `sort` поля allow-list, иначе произвольное поле приводит к 500.
6. Исправить audit before/after: сохранять immutable snapshot до изменения, а не две ссылки на уже измененный объект.

## 2. Общие правила API

- Base URL: `/api/v1`.
- Формат: `application/json; charset=utf-8`.
- Admin: `Authorization: Bearer <accessToken>`, роли/permissions проверяются на каждом endpoint.
- Локали контента: `en`, `ru`, `uz`; неизвестная локаль -> `400 INVALID_LOCALE`.
- Даты: ISO-8601 UTC, например `2026-09-07T12:30:00Z`.
- ID: стабильные строки/UUID; клиент не должен разбирать формат ID.
- Pagination admin catalog: целевой единый envelope:

```json
{
  "items": [],
  "page": { "page": 0, "size": 25, "totalElements": 0, "totalPages": 0 }
}
```

Backend сейчас местами возвращает `content`, местами `items`, а Book использует вложенный `page`. Нужно стандартизировать либо зафиксировать отдельные OpenAPI-типы. Frontend временно понимает оба варианта для generic admin lists.

### Headers для изменения данных

```http
Authorization: Bearer <token>
Content-Type: application/json
If-Match: "17"
Idempotency-Key: 5f30d0d7-...
X-Correlation-Id: 7e311da1-...
```

- `If-Match` или body `expectedVersion/version` обязательны там, где возможна конкурентная редактура.
- Повтор `Idempotency-Key` с тем же body возвращает тот же результат; с другим body -> `409 IDEMPOTENCY_KEY_REUSED`.
- Успешный update возвращает новый `ETag`.

## 3. Единый контракт ошибок

```json
{
  "type": "https://api.aichemistry.local/problems/version-conflict",
  "title": "Version conflict",
  "status": 409,
  "code": "VERSION_CONFLICT",
  "detail": "Book was changed by another editor",
  "instance": "/api/v1/admin/books/book-1/pages/page-2/blocks",
  "correlationId": "7e311da1-...",
  "fieldErrors": [
    { "field": "version", "code": "STALE", "message": "Expected 14, actual 15" }
  ],
  "timestamp": "2026-09-07T12:30:00Z"
}
```

Обязательные статусы:

| HTTP | code | Когда |
|---|---|---|
| 400 | `VALIDATION_ERROR`, `INVALID_QUERY`, `INVALID_LOCALE` | неверный JSON/query |
| 401 | `AUTH_REQUIRED`, `TOKEN_EXPIRED` | нет/просрочен access token |
| 403 | `FORBIDDEN`, `PERMISSION_DENIED` | нет admin permission |
| 404 | `BOOK_NOT_FOUND`, `LEVEL_NOT_FOUND`, `RESOURCE_NOT_FOUND` | объект отсутствует |
| 409 | `VERSION_CONFLICT`, `SLUG_CONFLICT`, `CODE_CONFLICT`, `INVALID_STATE` | конфликт версии/статуса |
| 422 | `PUBLISH_VALIDATION_FAILED` | draft сохранен, но publish невозможен |
| 429 | `RATE_LIMITED` | лимит запросов |
| 500 | `INTERNAL_ERROR` | без stack trace и внутренних exception messages |

`GlobalExceptionHandler` не должен возвращать `ex.getMessage()` пользователю и не должен использовать `printStackTrace`; полный stack trace идет только в structured logs вместе с `correlationId`.

## 4. Admin shell и permissions

### GET `/api/v1/admin/me/permissions`

Зачем: до отрисовки navigation скрыть запрещенные разделы и действия.

Response `200`:

```json
{
  "userId": "usr_1",
  "roles": ["ADMIN"],
  "permissions": [
    "dashboard.read", "books.read", "books.write", "books.publish",
    "learning.read", "learning.write", "learning.publish",
    "catalog.read", "catalog.write", "catalog.publish"
  ]
}
```

Требование: backend обязан отдельно проверять permission; скрытая frontend-кнопка не является защитой.

## 5. Dashboard

Все GET принимают общий query: `from`, `to`, `science`, `timezone`. `from/to` ISO date, interval не более 366 дней.

### GET `/api/v1/admin/dashboard/summary`

```json
{
  "users": { "total": 1402, "active": 389, "deltaPercent": 8.2 },
  "laboratories": { "total": 8011, "active": 27, "deltaPercent": 4.1 },
  "learning": { "attempts": 913, "completed": 604, "completionRate": 66.16 },
  "content": { "booksPublished": 1, "levelsPublished": 24, "drafts": 7 },
  "generatedAt": "2026-09-07T12:30:00Z"
}
```

### GET `/dashboard/activity-series`

```json
{ "interval": "DAY", "points": [{ "at": "2026-09-01", "users": 120, "sessions": 248, "attempts": 91 }] }
```

### GET `/dashboard/science-distribution`

```json
{ "items": [{ "science": "chemistry", "count": 701, "percent": 87.52 }] }
```

### GET `/dashboard/learning-summary`

```json
{
  "attempts": 913,
  "completed": 604,
  "completionRate": 66.16,
  "topLevels": [{ "levelId": "lvl_1", "title": "Mixtures", "attempts": 140, "completionRate": 72.1 }]
}
```

### GET `/dashboard/laboratory-summary`

```json
{ "active": 27, "paused": 3, "failed": 2, "averageDurationSeconds": 812 }
```

### GET `/dashboard/activity-summary`

```json
{ "items": [{ "id": "evt_1", "type": "BOOK_PUBLISHED", "actor": { "id": "usr_1", "displayName": "Admin" }, "at": "2026-09-07T12:20:00Z", "summary": "chemistry-lab v3" }] }
```

### POST `/api/v1/admin/reports`

Request:

```json
{ "type": "DASHBOARD", "format": "CSV", "filters": { "from": "2026-08-01", "to": "2026-09-07", "science": "chemistry" } }
```

Response `202`:

```json
{ "jobId": "report_1", "status": "PENDING", "createdAt": "2026-09-07T12:30:00Z" }
```

`GET /api/v1/admin/reports/{jobId}`:

```json
{ "jobId": "report_1", "status": "READY", "downloadUrl": "https://...signed...", "expiresAt": "2026-09-07T13:30:00Z" }
```

Допустимые статусы: `PENDING`, `RUNNING`, `READY`, `FAILED`. Frontend теперь polling-ит статус; backend не должен обещать мгновенную готовность.

## 6. Admin catalogs: materials, equipment, scenarios, chemistry

### Общий list/read/write pattern

- `GET /api/v1/admin/materials?q=&status=&page=0&size=25&sort=updatedAt,desc`
- `GET /api/v1/admin/equipment?...`
- `GET /api/v1/admin/scenarios?...`
- `GET /api/v1/admin/chemistry/{elements|substances|reactions}?...`
- `GET /{id}` -> полный editor document.
- `POST /` -> создать draft.
- `PATCH /{id}` -> изменить draft.
- `POST /{id}/validate` -> validation report.
- `POST /{id}/publish` -> published result.

List row обязательно содержит:

```json
{
  "id": "mat_1",
  "code": "HCL_01M",
  "status": "DRAFT",
  "version": 4,
  "translations": {
    "en": { "name": "Hydrochloric acid" },
    "ru": { "name": "Соляная кислота" },
    "uz": { "name": "Xlorid kislota" }
  },
  "updatedAt": "2026-09-07T12:00:00Z"
}
```

Material create/patch должен принимать документ редактора: `code`, `internalName`, `formula`, `phase`, `hazardClass`, `appearance`, `physicalProperties`, `safety`, `translations`, `version/expectedVersion`. Backend должен валидировать formula через chemistry-engine и возвращать field errors, а не 500.

Equipment create/patch: `code`, `category`, `kind`, `rendererKey`, `capabilities[]`, `translations`, `version`. Дополнительно:

```http
PUT /api/v1/admin/equipment/{id}/ports
{ "expectedVersion": 4, "ports": [{ "id": "top", "kind": "LIQUID", "direction": "IN_OUT", "capacity": 1 }] }
```

```http
PUT /api/v1/admin/equipment/{id}/compatibility
{ "expectedVersion": 5, "rules": [{ "materialCode": "HCL_01M", "allowed": true }] }
```

Scenario create/patch содержит `code`, `status`, `initialScene`, `steps`, `translations`, `version`. `initialScene.alias` и ссылки шагов должны проверяться на существование equipment/material/reaction. `validate` возвращает `{valid, errors[], warnings[]}`; `publish` запрещен при errors.

## 7. Book Studio — точный контракт текущего backend

### GET `/api/v1/admin/books?page=0&size=25&status=DRAFT&q=chemistry`

Response:

```json
{
  "items": [{
    "id": "book_1", "slug": "chemistry-lab", "status": "DRAFT",
    "defaultLocale": "ru", "translations": { "ru": { "title": "Химия", "description": "..." } },
    "draftVersion": 8, "publishedVersion": 7,
    "createdAt": "2026-09-01T10:00:00Z", "updatedAt": "2026-09-07T10:00:00Z"
  }],
  "page": { "page": 0, "size": 25, "totalElements": 1, "totalPages": 1 }
}
```

### POST `/api/v1/admin/books`

```json
{
  "slug": "chemistry-lab",
  "defaultLocale": "ru",
  "translations": {
    "ru": { "title": "Химическая лаборатория", "description": "Интерактивный учебник" },
    "en": { "title": "Chemistry laboratory", "description": "Interactive textbook" },
    "uz": { "title": "Kimyo laboratoriyasi", "description": "Interaktiv darslik" }
  }
}
```

Response `201`: `BookEditorDocument`, header `Location: /api/v1/admin/books/book_1`.

### GET/PATCH `/api/v1/admin/books/{bookId}`

GET возвращает полный `BookEditorDocument`: поля summary плюс `chapters[]`; у каждой главы `pages[]`; у страницы `blocks[]`, `version`, `missingLocales`.

PATCH request:

```json
{ "slug": "chemistry-lab", "defaultLocale": "ru", "translations": { "ru": { "title": "Химия" } } }
```

Нужно добавить `expectedVersion` в `PatchBookRequest` (сейчас его нет), иначе два редактора могут молча затереть изменения.

### Chapters

```http
POST /api/v1/admin/books/{bookId}/chapters
{ "position": 0, "translations": { "ru": { "title": "Основы" }, "en": { "title": "Basics" }, "uz": { "title": "Asoslar" } } }
```

```http
PATCH /api/v1/admin/books/{bookId}/chapters/{chapterId}
{ "position": 1, "translations": { "ru": { "title": "Растворы" } }, "expectedVersion": 8 }
```

```http
DELETE /api/v1/admin/books/{bookId}/chapters/{chapterId}
Content-Type: application/json
{ "expectedVersion": 8, "confirm": true }
```

Backend должен либо поддержать body для DELETE, либо заменить на `POST .../{chapterId}/delete`; текущий frontend не вызывает удаление до появления безопасного контракта.

### Pages и blocks

```http
POST /api/v1/admin/books/{bookId}/pages
{
  "chapterId": "chapter_1",
  "slug": "mixtures",
  "position": 0,
  "layout": "DEFAULT",
  "translations": { "ru": { "title": "Смеси" } }
}
```

```http
PATCH /api/v1/admin/books/{bookId}/pages/{pageId}
{ "slug": "mixtures", "position": 1, "layout": "DEFAULT", "translations": { "ru": { "title": "Смеси" } }, "expectedVersion": 4 }
```

```http
PUT /api/v1/admin/books/{bookId}/pages/{pageId}/blocks
{
  "version": 4,
  "blocks": [
    { "id": "block_1", "type": "heading", "level": 1, "content": { "ru": "Смеси", "en": "Mixtures", "uz": "Aralashmalar" } },
    { "id": "block_2", "type": "paragraph", "content": { "ru": "..." } }
  ]
}
```

Response:

```json
{ "pageId": "page_1", "version": 5, "blocks": [], "missingLocales": ["uz"], "updatedAt": "2026-09-07T12:30:00Z" }
```

### Validate, publish, rollback

```http
POST /api/v1/admin/books/{bookId}/validate
{ "version": 8 }
```

```json
{ "valid": false, "errors": [{ "path": "chapters[0].pages[0].blocks", "code": "EMPTY_PAGE", "message": "Page must contain content" }], "warnings": [] }
```

```http
POST /api/v1/admin/books/{bookId}/publish
{ "version": 8, "idempotencyKey": "uuid", "releaseNote": "Initial chemistry course" }
```

```json
{ "bookId": "book_1", "publishedVersion": 8, "publishedAt": "2026-09-07T12:30:00Z", "publishedBy": { "id": "usr_1", "displayName": "Admin" }, "releaseNote": "Initial chemistry course" }
```

```http
POST /api/v1/admin/books/{bookId}/rollback
{ "targetPublishedVersion": 7, "reason": "Broken media links" }
```

### Public reader

```http
GET /api/v1/books/chemistry-lab/manifest?locale=ru
```

```json
{
  "book": { "id": "book_1", "slug": "chemistry-lab", "title": "Химическая лаборатория", "description": "..." },
  "locale": "ru", "fallbackLocale": "en", "missingLocales": [], "publishedVersion": 8,
  "chapters": [{ "id": "chapter_1", "position": 0, "title": "Основы", "pageCount": 2, "pages": [{ "id": "page_1", "slug": "mixtures", "position": 0, "title": "Смеси" }] }]
}
```

```http
GET /api/v1/books/chemistry-lab/chapters/chapter_1?locale=ru
```

Возвращает `{chapter, pages:[{id,slug,position,title,blocks}], fallbackLocale, missingLocales}`. Только `PUBLISHED` snapshot; draft никогда не виден learner.

### Assets

```http
POST /api/v1/admin/assets/upload-urls
{ "files": [{ "name": "cover", "filename": "cover.webp", "mimeType": "image/webp", "sizeBytes": 120400, "checksum": "sha256:...", "kind": "IMAGE" }] }
```

```json
{ "uploads": [{ "assetId": "asset_1", "fileId": "file_1", "uploadUrl": "https://signed-storage/...", "downloadUrl": "https://cdn/...", "expiresAt": "2026-09-07T12:40:00Z" }] }
```

После binary PUT клиент вызывает:

```http
POST /api/v1/admin/assets/asset_1/complete
{ "checksum": "sha256:...", "alt": { "ru": "Колба" }, "caption": {}, "width": 1600, "height": 900 }
```

Backend обязан сам проверить object storage, size/checksum/MIME; нельзя доверять client-provided URL. Ошибки: `413 ASSET_TOO_LARGE`, `415 UNSUPPORTED_MEDIA_TYPE`, `422 CHECKSUM_MISMATCH`, `409 ASSET_ALREADY_COMPLETED`.

## 8. Learning Level и learner flow

### Public track/level

```http
GET /api/v1/learning/tracks/chemistry?locale=ru
```

```json
{
  "id": "track_chem", "code": "chemistry", "title": "Химия", "locale": "ru",
  "levels": [{ "id": "lvl_1", "code": "mixtures", "title": "Смеси", "position": 1, "status": "AVAILABLE", "progress": { "percent": 0, "completed": false } }]
}
```

```http
GET /api/v1/learning/levels/lvl_1?locale=ru
```

Возвращает опубликованный level: `id`, `code`, `title`, `description`, `steps`, `scenario`, `requirements`, `rewards`, `estimatedMinutes`, `version`. Не отдавать admin draft поля.

### Start attempt

```http
POST /api/v1/learning/levels/lvl_1/attempts
Authorization: Bearer <token>
Idempotency-Key: uuid
{}
```

Текущий фактический response backend:

```json
{ "attemptId": "attempt_1", "experimentId": "experiment_1", "currentStep": 0, "stateVersion": 0 }
```

`experimentId` — это session ID для `/api/v1/chemistry/experiments/{experimentId}`. Нельзя называть его workspace/share session token. Frontend теперь передает его в Sandbox отдельно.

### Attempt runtime

- `GET /api/v1/learning/attempts/{id}` -> `{id,levelId,status,currentStep,stateVersion,startedAt,completedAt,score,experimentId}`.
- `POST /attempts/{id}/events` request `{type, stepId, payload, expectedVersion, occurredAt}` -> updated attempt/event ack.
- `POST /attempts/{id}/checkpoints/{checkpointId}/evaluate` request `{stateVersion, evidence}` -> `{passed,score,feedback,nextStep,attemptVersion}`.
- `GET /attempts/{id}/guide?locale=ru&mode=hint` -> `{title,body,actions[],currentStep}`.
- `POST /attempts/{id}/hint-requests?locale=ru` request `{stepId,reason}` -> `{hintId,title,body,penalty}`.
- `POST /attempts/{id}/complete?locale=ru` request `{stateVersion}` -> `{status:"COMPLETED",score,rewards,completedAt}`.

Если backend attempt/level недоступен, frontend теперь показывает ошибку и не запускает static legacy scenario.

### User progress

```http
GET /api/v1/users/me/learning-progress?track=chemistry
```

```json
{
  "trackId": "track_chem", "completedLevels": 4, "totalLevels": 20, "percent": 20,
  "levels": [{ "levelId": "lvl_1", "status": "COMPLETED", "bestScore": 92, "lastAttemptId": "attempt_1", "updatedAt": "2026-09-07T12:00:00Z" }]
}
```

### Admin Learning endpoints

Основной editor contract:

- `GET /api/v1/admin/learning/overview`
- `GET|POST /tracks`
- `GET|POST /levels`; `GET|PATCH /levels/{id}`
- `PUT /levels/{id}/steps`
- `PUT /levels/{id}/scenario`
- `PUT /levels/{id}/requirements`
- `PUT /levels/{id}/rewards`
- `PUT /levels/{id}/translations/{locale}`
- `POST /levels/{id}/validate|preview-attempts|publish|archive`
- `GET /levels/{id}/analytics`
- `GET|POST /chapters`, `/tasks`, `/rewards`
- `GET /progress`, `POST /progress/{userId}/reset`, `GET /localization`.

Каждый PUT/PATCH получает `expectedVersion`, возвращает полный обновленный level и новый `version`. Publish request: `{version,idempotencyKey,releaseNote}`. Preview attempt должен возвращать тот же runtime DTO, что обычный start attempt, плюс `preview:true`; preview не должен влиять на learner analytics/progress.

## 9. Backend tests, обязательные перед merge

1. Application context starts: нет duplicate mappings.
2. Contract tests для каждого endpoint: status, JSON schema, auth, locale fallback.
3. Optimistic locking: два PATCH с одной версией -> один `200`, второй `409`.
4. Idempotency publish/start-attempt/report.
5. Book: create -> chapters/pages/blocks -> validate -> publish -> public manifest/chapter.
6. Learning: published level -> start attempt -> experiment operation -> checkpoint -> complete -> progress.
7. Asset: signed upload -> checksum verification -> complete -> public CDN URL.
8. Admin permissions: reader не может write/publish.
9. Invalid sort/filter возвращает 400, не 500.
10. Maven build должен работать из Unicode path. Сейчас protobuf step ломается из-за преобразования `D:\\web-sites\\заказ` в `?????`; обновить protobuf plugin/toolchain или передавать корректный Unicode absolute path.

## 10. Definition of Done

- Admin pages не используют mock rows и получают typed DTO.
- Dashboard report доходит до `READY` и URL скачивается.
- Book Studio создает, сохраняет, валидирует и публикует книгу; reader читает `chemistry-lab` из backend.
- Level editor публикует level; `/lchemistry-level` получает track и запускает attempt.
- Все ошибки имеют единый `code/correlationId/fieldErrors`.
- OpenAPI генерируется в CI, frontend types сравниваются с ним contract-check тестом.

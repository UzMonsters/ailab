# Book

Канонический документ для пользовательской книги и `/admin/book`.

## Результат сверки backend

В backend нет `Book`, `Chapter`, `Page`, `Block`, `Asset`, `BookVersion` entities и нет `/api/v1/books` или `/api/v1/admin/books` controller. Все Book Studio endpoints этого документа — новые и обязательные. Не дублируются только общие существующие механизмы: JWT/RBAC, user preferences и будущий общий object-storage service; сам book CMS пока нужно реализовать целиком.

## Модель контента

```text
Book
└── Chapter[]
    └── Page[]
        └── Block[]
```

Block types: paragraph, heading, list, quote, formula, table, callout, image, sandbox-equipment, sandbox-material, divider и interactive experiment link.

Каждый локализуемый объект хранит `ru`, `en`, `uz`, `defaultLocale` и `missingLocales`. Rich text хранится structured JSON, а не HTML/JSX. Marks: bold, italic, underline, strike, superscript, subscript, code, color, highlight, font family и font size.

## UI редактора

- Слева — дерево Book → Chapter → Page.
- В центре — page canvas и live preview.
- Справа — свойства выбранного block.
- Сверху — RU/EN/UZ, Validate, Preview, Publish.
- Image/SVG block поддерживает alt, caption, crop и light/dark variants.
- Equipment/material SVG переиспользуются из sandbox через общий `rendererKey`, а не копируются вручную.
- Callout поддерживает типы «Запомнить», «Важно», «Опасность», рамку, icon и background.

## API

| API | Что делает и зачем | Request | Response |
|---|---|---|---|
| `GET/POST /api/v1/admin/books` | Список и создание draft books. | POST `{ slug,defaultLocale,translations }`. | `201 Book { id,status,version }`. |
| `GET/PATCH /api/v1/admin/books/{id}` | Загружает editor document и сохраняет metadata. | PATCH partial + `If-Match`. | `200 BookEditorDocument`; `409`. |
| `POST /api/v1/admin/books/{id}/chapters` | Создаёт chapter в позиции. | `{ position,translations }`. | `201 Chapter`. |
| `POST /api/v1/admin/books/{id}/pages` | Создаёт page внутри chapter. | `{ chapterId,position,slug,translations }`. | `201 Page`. |
| `PUT /api/v1/admin/books/{id}/pages/{pageId}/blocks` | Сохраняет ordered structured blocks. | `{ version,blocks:[...] }`. | `200 { version,blocks }`; `409`. |
| `POST /api/v1/admin/assets/upload-urls` | Загружает image/SVG light/dark без base64. | `{ files:[{name,mimeType,size,theme?}] }`. | `200 { uploads }`. |
| `POST /api/v1/admin/books/{id}/validate` | Проверяет schema, assets и RU/EN/UZ. | `{ version }`. | `200 { valid,errors,warnings }`. |
| `POST /api/v1/admin/books/{id}/publish` | Создаёт immutable published snapshot. | `{ version,idempotencyKey }`. | `201 { publishedVersion }`. |
| `GET /api/v1/books/{slug}/manifest` | Загружает лёгкое оглавление. | Query `locale`. | `200 { book,chapters,publishedVersion }`. |
| `GET /api/v1/books/{slug}/chapters/{chapterId}` | Загружает chapter pages/blocks. | Query `locale`. | `200 { chapter,pages,fallbackLocale? }`. |
| `GET/PUT /api/v1/users/me/book-progress/{bookId}` | Синхронизирует последнюю страницу и bookmarks. | PUT `{ pageId,scrollAnchor,bookmarks }`. | `200 BookProgress`. |

Ниже сохранены объединённые модели, JSON-примеры, публикация и критерии готовности.

## Данные и block schemas

| Entity | Основные поля |
|---|---|
| Book | `id`, `slug`, `defaultLocale`, `translations`, `status`, `draftVersion`, `publishedVersion` |
| Chapter | `id`, `bookId`, `position`, `translations`, `status` |
| Page | `id`, `chapterId`, `slug`, `position`, `translations`, `blocks`, `version` |
| Block | `id`, `type`, `position`, `data`, `translations`, `style` |
| Asset | `id`, `kind`, `mimeType`, `variants`, `width`, `height`, `checksum`, `status` |

Пример callout:

```json
{
  "type":"CALLOUT",
  "data": { "variant":"REMEMBER", "icon":"brain" },
  "style": { "border":true, "backgroundToken":"callout.remember", "fontFamily":"serif", "fontSize":18 },
  "translations": {
    "ru": { "content":"Запомните правило растворимости" },
    "en": { "content":"Remember the solubility rule" },
    "uz": { "content":"Eruvchanlik qoidasini eslab qoling" }
  }
}
```

Пример image/equipment block:

```json
{
  "type":"SANDBOX_EQUIPMENT",
  "data": {
    "rendererKey":"beaker-250",
    "assetVariants": { "light":"asset_light", "dark":"asset_dark" },
    "state": { "volumeMl":150, "materialCode":"CuSO4(aq)" }
  },
  "translations": {
    "ru": { "alt":"Стакан с голубым раствором", "caption":"Раствор сульфата меди" },
    "en": { "alt":"Beaker with blue solution", "caption":"Copper sulfate solution" },
    "uz": { "alt":"Ko'k eritma solingan stakan", "caption":"Mis sulfat eritmasi" }
  }
}
```

## Publish, language fallback и cache

- Publish сначала запускает validation: schema, broken links, asset readiness, required translations и duplicate slugs.
- Fallback: requested locale → `defaultLocale`; response всегда сообщает `fallbackLocale` и `missingLocales`.
- Published manifest имеет immutable version и CDN cache; draft никогда не кешируется публично.
- Rollback не изменяет старый snapshot, а создаёт новую published revision.
- Progress хранит page id, scroll anchor и bookmarks; localStorage используется только как offline cache.

## Ошибки

`400 VALIDATION_ERROR`, `403 FORBIDDEN`, `404 BOOK_NOT_FOUND`, `409 VERSION_CONFLICT`, `422 MISSING_REQUIRED_TRANSLATION`, `422 ASSET_NOT_READY`, `422 BLOCK_SCHEMA_INVALID`.

## Acceptance criteria

- Admin создаёт книгу, главу, страницы и blocks без изменения кода.
- Rich text сохраняет marks, font family/size, strike, callout рамку и background.
- Каждый image/SVG имеет RU/EN/UZ alt/caption либо видимый fallback warning.
- SVG equipment/material из sandbox корректно отображается на light и dark theme.
- Reader загружает manifest и главы лениво, а bookmarks синхронизируются между устройствами.

Концепт интерфейса: ![Admin book editor](./images/admin-book-editor-concept.png)

## Полный REST contract Book Studio

### Общие правила

Все admin endpoints требуют `Authorization: Bearer <ADMIN|CONTENT_EDITOR token>`. Любое изменение draft передаёт `If-Match` или `version`; publish/rollback и создание сущностей получают `Idempotency-Key`. Публичный reader получает только `PUBLISHED` version. Timestamp — ISO-8601 UTC, ошибки — `application/problem+json`.

| API | Зачем нужен | Полный request | Полный response |
|---|---|---|---|
| `GET /api/v1/admin/books` | Список книг в Book Studio с draft/published состоянием. | Query: `page=0&size=20&q=&status=DRAFT|PUBLISHED|ARCHIVED&sort=updatedAt,desc`. | `200 { items:[BookSummary],page }`. |
| `POST /api/v1/admin/books` | Создаёт новую draft book. | `{ slug,defaultLocale,translations:{ru,en,uz} }`. | `201 BookEditorDocument`. |
| `GET /api/v1/admin/books/{bookId}` | Загружает дерево главы/страницы и draft data для editor. | Query `include=chapters,pages,translations,assets`. | `200 BookEditorDocument`; `404 BOOK_NOT_FOUND`. |
| `PATCH /api/v1/admin/books/{bookId}` | Меняет book metadata без потери pages. | `{ slug?,defaultLocale?,translations? }` + `If-Match`. | `200 Book`; `409 VERSION_CONFLICT`. |
| `POST /api/v1/admin/books/{bookId}/chapters` | Создаёт chapter. | `{ position,translations }`. | `201 Chapter`. |
| `PATCH /api/v1/admin/books/{bookId}/chapters/{chapterId}` | Меняет chapter title/order. | `{ position?,translations? }` + version. | `200 Chapter`. |
| `DELETE /api/v1/admin/books/{bookId}/chapters/{chapterId}` | Удаляет draft chapter только после explicit confirmation; published snapshots остаются. | Query/body `{ expectedVersion,confirm:true }`. | `204`; `409` если chapter имеет protected pages. |
| `POST /api/v1/admin/books/{bookId}/pages` | Создаёт page/spread в chapter. | `{ chapterId,slug,position,translations }`. | `201 Page`. |
| `PATCH /api/v1/admin/books/{bookId}/pages/{pageId}` | Меняет page metadata/layout. | `{ slug?,position?,layout?,translations? }`. | `200 Page`. |
| `PUT /api/v1/admin/books/{bookId}/pages/{pageId}/blocks` | Атомарно сохраняет порядок и structured content blocks. | `{ version,blocks:[...] }`. | `200 { pageId,version,blocks }`; `409`. |
| `POST /api/v1/admin/assets/upload-urls` | Получает signed upload URLs для image/SVG light/dark. | `{ files:[{name,mimeType,size,checksum,kind:"IMAGE|SVG"}] }`. | `200 { uploads:[{assetId,uploadUrl,expiresAt}] }`. |
| `POST /api/v1/admin/assets/{assetId}/complete` | Проверяет загруженный asset и создаёт immutable asset record. | `{ checksum,alt:{ru,en,uz},caption:{ru,en,uz},variants? }`. | `201 Asset`. |
| `POST /api/v1/admin/books/{bookId}/validate` | Проверяет content перед publish. | `{ version }`. | `200 ValidationReport`. |
| `POST /api/v1/admin/books/{bookId}/publish` | Публикует immutable version. | `{ version,idempotencyKey,releaseNote? }`. | `201 PublishResult` или `202 PublishJob`. |
| `POST /api/v1/admin/books/{bookId}/rollback` | Создаёт новую published revision из старой. | `{ targetPublishedVersion,reason }`. | `201 PublishResult`. |
| `GET /api/v1/books/{slug}/manifest` | Лёгкое public оглавление. | Query `locale=ru&version?`. | `200 PublicBookManifest`. |
| `GET /api/v1/books/{slug}/chapters/{chapterId}` | Lazy-load одной главы reader-ом. | Query `locale=ru&page?`. | `200 PublicChapter`. |
| `GET/PUT /api/v1/users/me/book-progress/{bookId}` | Resume page/bookmarks между устройствами. | PUT `{ pageId,scrollAnchor,bookmarks,updatedAt }`. | `200 BookProgress`. |

### Create book: request/response

```http
POST /api/v1/admin/books
Authorization: Bearer <editor-token>
Idempotency-Key: c34fa9c1-9e83-421f-8547-5f9f1aa30816
Content-Type: application/json
```

```json
{
  "slug":"chemistry-basics",
  "defaultLocale":"ru",
  "translations":{
    "ru":{"title":"Основы химии","description":"Интерактивный учебник"},
    "en":{"title":"Chemistry Basics","description":"Interactive textbook"},
    "uz":{"title":"Kimyo asoslari","description":"Interaktiv darslik"}
  }
}
```

```json
{
  "id":"book_chem_basics",
  "slug":"chemistry-basics",
  "status":"DRAFT",
  "defaultLocale":"ru",
  "translations":{
    "ru":{"title":"Основы химии","description":"Интерактивный учебник"},
    "en":{"title":"Chemistry Basics","description":"Interactive textbook"},
    "uz":{"title":"Kimyo asoslari","description":"Interaktiv darslik"}
  },
  "chapters":[],
  "draftVersion":1,
  "publishedVersion":null,
  "createdAt":"2026-09-01T12:00:00Z",
  "updatedAt":"2026-09-01T12:00:00Z"
}
```

### Save page blocks: request/response

```json
{
  "version":7,
  "blocks":[
    {
      "id":"block_intro",
      "type":"RICH_TEXT",
      "position":1,
      "data":{"document":{"type":"doc","content":[{"type":"paragraph","content":[{"type":"text","text":"Раствор — однородная смесь."}]}]}},
      "style":{"fontFamily":"Inter","fontSize":18,"align":"left"},
      "translations":{
        "ru":{"content":"Раствор — однородная смесь."},
        "en":{"content":"A solution is a homogeneous mixture."},
        "uz":{"content":"Eritma bir jinsli aralashmadir."}
      }
    },
    {
      "id":"block_remember",
      "type":"CALLOUT",
      "position":2,
      "data":{"variant":"REMEMBER","icon":"brain"},
      "style":{"border":true,"backgroundToken":"callout.remember","fontSize":16},
      "translations":{
        "ru":{"content":"Запомните: растворённые вещества нельзя отделить фильтрацией."},
        "en":{"content":"Remember: dissolved substances cannot be separated by filtration."},
        "uz":{"content":"Eslab qoling: erigan moddalarni filtrlash bilan ajratib bo‘lmaydi."}
      }
    },
    {
      "id":"block_beaker",
      "type":"SANDBOX_EQUIPMENT",
      "position":3,
      "data":{"rendererKey":"beaker-250","assetVariants":{"light":"asset_01_light","dark":"asset_01_dark"},"state":{"volumeMl":50,"materialCode":"CuSO4(aq)"}},
      "translations":{
        "ru":{"alt":"Стакан с голубым раствором","caption":"Раствор сульфата меди"},
        "en":{"alt":"Beaker with blue solution","caption":"Copper sulfate solution"},
        "uz":{"alt":"Ko'k eritma solingan stakan","caption":"Mis sulfat eritmasi"}
      }
    }
  ]
}
```

```json
{
  "pageId":"page_solutions",
  "version":8,
  "blocks":["...saved structured blocks..."],
  "missingLocales":[],
  "updatedAt":"2026-09-01T12:04:00Z"
}
```

### Asset/upload and publish responses

```json
{
  "uploads":[
    {"assetId":"asset_01_light","uploadUrl":"https://storage/...","expiresAt":"2026-09-01T12:15:00Z"},
    {"assetId":"asset_01_dark","uploadUrl":"https://storage/...","expiresAt":"2026-09-01T12:15:00Z"}
  ]
}
```

```json
{
  "valid":false,
  "errors":[{"path":"chapters[0].pages[2].blocks[3].translations.uz.caption","code":"MISSING_REQUIRED_TRANSLATION","message":"Caption is required"}],
  "warnings":[{"path":"pages[2].blocks[3]","code":"ASSET_VARIANT_MISSING","message":"Dark SVG variant will fall back to light"}]
}
```

```json
{
  "bookId":"book_chem_basics",
  "publishedVersion":3,
  "publishedAt":"2026-09-01T12:10:00Z",
  "publishedBy":{"id":"usr_01","displayName":"Jasur Karimov"},
  "releaseNote":"Добавлена глава о растворах"
}
```

### Public reader response

```json
{
  "book":{"id":"book_chem_basics","slug":"chemistry-basics","title":"Основы химии","description":"Интерактивный учебник"},
  "locale":"ru",
  "fallbackLocale":null,
  "missingLocales":[],
  "publishedVersion":3,
  "chapters":[{"id":"ch_01","position":1,"title":"Растворы","pageCount":4}]
}
```

Ошибки: `400 BLOCK_SCHEMA_INVALID`, `401 UNAUTHORIZED`, `403 FORBIDDEN`, `404 BOOK_NOT_FOUND`, `409 VERSION_CONFLICT`, `413 ASSET_TOO_LARGE`, `415 UNSUPPORTED_MEDIA_TYPE`, `422 MISSING_REQUIRED_TRANSLATION`, `422 ASSET_NOT_READY`.

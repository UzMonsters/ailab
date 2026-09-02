# Profile

Канонический документ для `/[locale]/profile`: UI, frontend integration и backend API.

## Результат сверки backend: что уже готово

| Уже есть | Что нужно сделать дальше |
|---|---|
| `GET/PUT /api/v1/users/me` | Подключить без потери level/xp/language/theme/settings/stats/achievements; расширить response только полями, нужными UI (`createdAt`, `updatedAt`, `version`). |
| `GET/PUT /api/v1/users/me/preferences` | Подключить реальный language/theme UI; не создавать второй preferences endpoint. |
| `GET /api/v1/users/me/statistics` | Подключить achievements в UI; при необходимости выделить learning-progress API отдельно. |
| `PUT/DELETE /api/v1/users/avatar` | Оставить для URL compatibility, добавить signed upload flow для реальных файлов. |
| `DELETE /api/v1/users/me` | Заменить immediate delete на re-auth + scheduled deletion/restore flow. |
| `GET /api/v1/users/{id}` | Оставить как public profile, не раскрывать settings/email. |

Password/email change, sessions, re-auth, avatar file upload, deletion request и learning progress API пока отсутствуют.

## Что должно быть в UI

| Раздел | Возможности |
|---|---|
| Overview | Username, email, avatar, краткая статистика и дата регистрации |
| Learning | Chemistry progress, активный уровень, завершённые уровни и badges |
| Preferences | RU/EN/UZ, light/dark/system, единицы, autosave |
| Security | Смена password/email, активные sessions, logout других устройств |
| Data & privacy | Export, privacy, scheduled account deletion и отмена удаления |

## Главные изменения

- Frontend должен сохранять все поля `UserMeResponse`, а не отбрасывать level, settings и achievements.
- Avatar загружается через signed URL или multipart; base64 data URL в `avatarUrl` не используется.
- Theme selector должен работать, а не быть принудительно зафиксирован на DARK.
- Ошибки statistics/preferences показываются как error/retry, а не заменяются незаметно defaults.
- Удаление аккаунта требует re-auth, текстового подтверждения и grace period.

## API

| API | Что делает и зачем | Request | Response |
|---|---|---|---|
| `GET /api/v1/users/me` | Загружает identity и профиль current user. | Bearer token. | `200 Profile`; `401` invalid session. |
| `PATCH /api/v1/users/me` | Изменяет username и публичные поля без полной перезаписи. | `{ username?, bio? }`, `If-Match`. | `200 Profile`; `409` conflict; `422` field errors. |
| `GET/PUT /api/v1/users/me/preferences` | Читает и сохраняет язык, theme, units, autosave. | PUT `{ language,theme,applicationSettings }`. | `200 Preferences`. |
| `GET /api/v1/users/me/statistics` | Возвращает lab statistics. | Нет body. | `200 { statistics,achievements }`. |
| `GET /api/v1/users/me/learning-progress` | Питает Learning tab. | Query `track=chemistry`. | `200 { completedLevels,activeAttempt,badges }`. |
| `POST /api/v1/users/me/avatar/upload-urls` | Начинает безопасную загрузку avatar. | `{ fileName,mimeType,size,checksum }`. | `200 { assetId,uploadUrl,expiresAt }`. |
| `POST /api/v1/users/me/avatar/complete` | Подтверждает asset после проверки. | `{ assetId,crop? }`. | `200 { avatarUrl,updatedAt }`. |
| `POST /api/v1/users/me/email-change` | Запускает подтверждаемую смену email. | `{ newEmail,reauthToken }`. | `202 { verificationRequired,expiresAt }`. |
| `POST /api/v1/users/me/password/change` | Безопасно меняет password. | `{ currentPassword,newPassword }`. | `204`. |
| `GET /api/v1/users/me/sessions` | Показывает активные устройства. | Нет body. | `200 { items:Session[] }`. |
| `DELETE /api/v1/users/me/sessions/{id}` | Отзывает выбранную session. | Path id. | `204`. |
| `POST /api/v1/users/me/deletion-requests` | Планирует удаление аккаунта. | `{ reauthToken,confirmation:"DELETE" }`. | `202 { deletionId,scheduledFor }`. |

Ниже в этом файле сохранён полный объединённый аудит frontend/backend.

## Фактическое состояние frontend/backend

| Возможность | Frontend | Backend | Решение |
|---|---|---|---|
| Current profile | `ProfilePage` и `userApi.getMe()` | `GET /users/me` | Сохранить все поля response в typed store. |
| Username | Edit dialog | `PUT /users/me` | Перейти на `PATCH` и version. |
| Avatar | FileReader → data URL | URL ограничен 500 символами | Заменить signed upload flow. |
| Preferences | Units/autosave; theme disabled | light/dark/system уже поддерживаются | Включить theme UI и синхронизацию. |
| Statistics | Четыре cards | Map statistics + achievements | Не отбрасывать achievements. |
| Security | Logout/delete | Basic delete endpoint | Добавить sessions, re-auth, email/password flows. |

Backend `UserMeResponse` должен содержать как минимум: `id`, `username`, `email`, `avatarUrl`, `createdAt`, `level`, `language`, `theme`, `applicationSettings`, `statistics`, `achievements`, `updatedAt`, `version`. Если XP исключён из продукта, поле `xp` удаляется из backend DTO и frontend types одновременно.

## Ошибки и состояния UI

| HTTP/code | Поведение frontend |
|---|---|
| `401 UNAUTHORIZED` | Очистить remote session; в demo mode не делать silent redirect. |
| `409 VERSION_CONFLICT` | Перезагрузить profile и предложить повторить изменения. |
| `413 ASSET_TOO_LARGE` | Показать допустимый размер avatar. |
| `415 UNSUPPORTED_MEDIA_TYPE` | Разрешить только JPEG/PNG/WebP. |
| `422 VALIDATION_ERROR` | Показать field errors возле соответствующих inputs. |
| Offline/5xx | Не подменять server values defaults; показать retry и сохранённые local values отдельно. |

## Acceptance criteria

- Profile одинаков на двух устройствах после входа.
- Avatar проходит MIME/size/image validation и хранится как CDN URL.
- RU/EN/UZ, light/dark/system и application preferences переживают reload.
- Achievements и chemistry progress отображаются из отдельных typed contracts.
- Delete account требует re-auth, подтверждение `DELETE` и допускает отмену до `scheduledFor`.

## Полный Profile API contract

### Общие правила

Frontend отправляет `Authorization: Bearer <access token>` и `Accept-Language: ru|en|uz`. Ответы используют UTC ISO-8601; mutation имеет `If-Match`/`version` там, где возможен конфликт. Текущий backend пока использует `PUT`, но целевой contract использует `PATCH` для частичных изменений.

| API | Зачем нужен | Request | Response |
|---|---|---|---|
| `GET /api/v1/users/me` | Загружает profile shell, roles, preferences summary и level data. | Headers only. | `200 ProfileResponse`. |
| `PUT /api/v1/users/me` **(есть сейчас)** | Меняет username/avatar URL в текущем backend. | `{ username,avatarUrl }`. | `200 UserMeResponse`. |
| `PATCH /api/v1/users/me` **(цель)** | Меняет только editable profile fields и возвращает новую version. | `{ username?,displayName?,bio? }`, `If-Match`. | `200 ProfileResponse`. |
| `GET /api/v1/users/me/preferences` | Загружает language/theme/application settings. | Headers only. | `200 PreferencesResponse`. |
| `PUT /api/v1/users/me/preferences` | Сохраняет language, theme и user-specific settings между устройствами. | `{ language,theme,applicationSettings }`. | `200 PreferencesResponse`. |
| `GET /api/v1/users/me/statistics` | Загружает cards и achievements. | Headers only. | `200 { statistics,achievements }`. |
| `PUT /api/v1/users/avatar` **(есть сейчас)** | Сохраняет внешний avatar URL. Не подходит для base64 image. | `{ avatarUrl }`. | `200 UserMeResponse`. |
| `POST /api/v1/users/me/avatar/upload-urls` **(цель)** | Выдаёт signed upload URL для real image. | `{ fileName,mimeType,size,checksum }`. | `200 AvatarUploadTicket`. |
| `POST /api/v1/users/me/avatar/complete` **(цель)** | Проверяет uploaded image и назначает avatar. | `{ assetId,crop? }`. | `200 AvatarResponse`. |
| `DELETE /api/v1/users/avatar` | Убирает custom avatar. | Headers only. | `204`. |
| `POST /api/v1/users/me/email-change` **(цель)** | Запускает verify flow смены email. | `{ newEmail,reauthToken }`. | `202 EmailChangeResponse`. |
| `POST /api/v1/users/me/password/change` **(цель)** | Меняет password и отзывает other sessions. | `{ currentPassword,newPassword }`. | `204`. |
| `GET /api/v1/users/me/sessions` **(цель)** | Показывает active devices. | Query `page,size`. | `200 SessionPage`. |
| `DELETE /api/v1/users/me/sessions/{id}` **(цель)** | Отзывает устройство. | Path id. | `204`. |
| `POST /api/v1/users/me/re-auth` **(цель)** | Выдаёт short-lived token для критичных действий. | `{ password }`. | `200 { reauthToken,expiresAt }`. |
| `POST /api/v1/users/me/deletion-requests` **(цель)** | Планирует reversible account deletion. | `{ reauthToken,confirmation:"DELETE" }`. | `202 DeletionRequest`. |
| `DELETE /api/v1/users/me/deletion-requests/{id}` **(цель)** | Отменяет scheduled deletion. | Path id. | `204`. |
| `GET /api/v1/users/me/learning-progress` **(цель)** | Питает Learning tab, а не смешивает progress с random stats map. | `track=chemistry`. | `200 LearningProgress`. |

### Реальный текущий backend response

```json
{
  "id":"usr_01",
  "username":"Jasur Karimov",
  "email":"jasur@example.com",
  "avatarUrl":null,
  "level":4,
  "xp":48896,
  "language":"ru",
  "theme":"dark",
  "applicationSettings":{"units":"METRIC","autosave":true},
  "statistics":{"totalExperimentsRun":12},
  "achievements":["FIRST_MIX"]
}
```

Frontend adapter обязан не терять `level`, `language`, `theme`, `applicationSettings`, `statistics`, `achievements`. `createdAt`, `updatedAt`, `version`, display name и security data отсутствуют в реальном response и должны быть добавлены backend, если UI их показывает.

### Preferences request/response

```http
PUT /api/v1/users/me/preferences
Authorization: Bearer <access-token>
Content-Type: application/json
```

```json
{
  "language":"ru",
  "theme":"system",
  "applicationSettings":{"units":"METRIC","autosave":true,"reducedMotion":false}
}
```

```json
{
  "language":"ru",
  "theme":"system",
  "applicationSettings":{"units":"METRIC","autosave":true,"reducedMotion":false}
}
```

Theme — персональная frontend preference. Backend её синхронизирует между устройствами, но не должен использовать её как глобальный Admin Appearance setting.

### Avatar upload flow

```json
{
  "fileName":"avatar.webp",
  "mimeType":"image/webp",
  "size":184233,
  "checksum":"sha256:..."
}
```

```json
{
  "assetId":"asset_avatar_42",
  "uploadUrl":"https://storage.example/upload/...",
  "expiresAt":"2026-09-01T12:15:00Z",
  "maxBytes":2097152,
  "allowedMimeTypes":["image/jpeg","image/png","image/webp"]
}
```

После direct upload frontend вызывает:

```json
{
  "assetId":"asset_avatar_42",
  "crop":{"x":0.1,"y":0.1,"width":0.8,"height":0.8}
}
```

и получает:

```json
{
  "avatarUrl":"https://cdn.example/avatar/usr_01-v3.webp",
  "assetId":"asset_avatar_42",
  "updatedAt":"2026-09-01T12:03:00Z"
}
```

### Ошибки и lifecycle

| Code | Meaning | Frontend action |
|---|---|---|
| `401 UNAUTHORIZED` | Access/refresh session invalid. | Clear remote credentials; redirect only outside explicit demo mode. |
| `403 ACCOUNT_INACTIVE` | Account blocked/deactivated by admin. | Clear session and show support guidance. |
| `409 PROFILE_VERSION_CONFLICT` | Другой device сохранил profile раньше. | Reload server profile, show merge/retry. |
| `413 ASSET_TOO_LARGE` | Avatar больше лимита. | Show max size before retry. |
| `415 UNSUPPORTED_MEDIA_TYPE` | Неверный формат image. | Explain permitted formats. |
| `422 VALIDATION_ERROR` | Username/email/password/theme invalid. | Bind `fieldErrors` к форме. |
| `429 REAUTH_RATE_LIMITED` | Слишком много password checks. | Cooldown; не повторять автоматически. |

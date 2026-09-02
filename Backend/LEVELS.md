# Levels

Канонический документ для `lchemistry-level`, attempts, steps, guide и completion.

## Результат сверки backend

Backend не содержит controller/model для learning tracks, levels, attempts, checkpoints, rewards, localization или progress. Существующие experiment/workspace endpoints нужно **переиспользовать** как каноническое состояние лаборатории для проверки step, но не заменять ими Learning API. Все `/api/v1/learning/*` и `/api/v1/admin/learning/*` в этом документе — новые endpoints, которые надо реализовать.

## Основные правила

- Карта уровней приходит из backend; не опубликованные уровни помечаются coming soon.
- Уровень содержит version, prerequisites, available equipment/materials, scenario и checkpoints.
- Step завершается не после UI click, а после server evaluation канонического experiment state.
- До подтверждения показывается `Проверяем…`; после `accepted=true` — зелёная галочка.
- Help показывает максимум один target одновременно: сначала tab/tool, затем item, затем конкретный port/action.
- При смене шага arrows и overlays обязательно удаляются.
- Completion идемпотентен и предлагает следующий уровень на RU/EN/UZ. XP не показывается, если механика XP исключена.

## Guide target

```json
{
  "target": {
    "kind": "PORT",
    "itemId": "thermometer-1",
    "portId": "sensor-out"
  },
  "text": "Соедините порт термометра с портом сосуда",
  "placement": "top",
  "sequence": 2
}
```

## API

| API | Что делает и зачем | Request | Response |
|---|---|---|---|
| `GET /api/v1/learning/tracks/chemistry` | Возвращает карту, locks и progress. | Query `locale`. | `200 { track,levels }`. |
| `GET /api/v1/learning/levels/{id}` | Загружает published definition. | Query `locale`. | `200 LevelDefinition`. |
| `POST /api/v1/learning/levels/{id}/attempts` | Начинает/resume попытку и experiment. | `{ clientAttemptId?,locale,workspaceId? }`. | `201 { attemptId,experimentId,currentStep,stateVersion }`. |
| `GET /api/v1/learning/attempts/{id}` | Восстанавливает прохождение после reload. | Path id. | `200 AttemptState`. |
| `POST /api/v1/learning/attempts/{id}/events` | Передаёт semantic event для проверки. | `{ eventId,type,payload,experimentStateVersion }`. | `202 { accepted,evaluatedCheckpointIds }`. |
| `POST /api/v1/learning/attempts/{id}/checkpoints/{checkpointId}/evaluate` | Проверяет условие step. | `{ idempotencyKey,stateVersion }`. | `200 { accepted,reason?,nextStep? }`; `422`. |
| `GET /api/v1/learning/attempts/{id}/guide` | Возвращает один актуальный UI target. | `mode=hint|detail|demo`. | `200 GuidePayload`. |
| `POST /api/v1/learning/attempts/{id}/hint-requests` | Увеличивает детализацию help. | `{ level:1|2|3,currentStepId }`. | `200 GuidePayload`. |
| `POST /api/v1/learning/attempts/{id}/complete` | Завершает level один раз и даёт next level. | `{ idempotencyKey,stateVersion }`. | `200 { completedAt,nextLevel,reward }`. |
| `GET /api/v1/users/me/learning-progress` | Синхронизирует progress между устройствами. | `track=chemistry`. | `200 UserLearningProgress`. |

Ниже сохранены объединённые domain rules, ошибки и acceptance criteria.

## Доменная модель

```text
LearningTrack
└── LevelDefinition (draft/published/version)
    ├── prerequisites
    ├── available equipment/materials
    └── ScenarioDefinition
        └── StepDefinition[]
            ├── requirements/checkpoints
            └── GuideTarget[]

UserProgress
└── Attempt
    ├── experimentId/stateVersion
    ├── completedSteps
    ├── hintUsage
    └── completion
```

Checkpoint проверяет семантическое состояние: item type/material code, volume range, port connection, measurement range, operation result или safety state. Он не должен зависеть от DOM selector, координат или текста кнопки.

## Поведение шагов и помощи

| Состояние | UI |
|---|---|
| Pending | Серый chip, действие ещё недоступно |
| Active | Один акцентный chip и краткая инструкция |
| Evaluating | Spinner `Проверяем…`; permanent check ещё нет |
| Rejected | Шаг остаётся active, отображается reason и contextual hint |
| Completed | Зелёная галочка; раскрытие показывает фактически выполненное условие |

Help level 1 подсвечивает нужный tab/tool, level 2 — item или port, level 3 — ghost cursor/path без выполнения действия. Одновременно видны максимум одна стрелка и одна pulse. Cleanup выполняется при смене step, target unmount, закрытии help и completion.

## Frontend/backend задачи

| Приоритет | Backend | Frontend |
|---|---|---|
| P0 | Tracks, levels, scenarios, attempts, checkpoints, progress | Remote `LearningRepository` + IndexedDB fallback |
| P0 | Server checkpoint evaluation по experiment state | Optimistic UI фиксировать только после accepted response |
| P0 | Draft/published/version | Загружать binding из attempt, не из hardcode map |
| P0 | Guest progress и migration после login | Явно разделять guest/local/authenticated modes |
| P1 | Resume/abandon attempt и analytics | Resume UI, duration, hint usage и error recovery |
| P1 | Admin scenario validation | Semantic guide renderer: tab/item/port/action |

## Ошибки

| HTTP/code | UI |
|---|---|
| `404 LEVEL_NOT_FOUND` | Вернуться на карту с notification. |
| `409 PREREQUISITE_NOT_MET` | Показать, какой уровень пройти. |
| `409 LEVEL_VERSION_CHANGED` | Обновить definition и начать совместимую попытку. |
| `409 STATE_VERSION_CONFLICT` | Hydrate experiment state и повторить evaluation. |
| `422 STEP_REQUIREMENT_NOT_MET` | Показать hint, не сбрасывать scene. |

## Acceptance criteria

- Уровни 1–5 проходят через attempts/checkpoints; остальные честно marked coming soon, если не опубликованы.
- Сначала добавляется необходимый сосуд, затем material/action согласно definition.
- Thermometer step завершается после корректного sensor connection и measurement, а не после визуальной линии.
- Completion дважды не создаёт две записи и не ломает next level.
- Progress одинаков после reload, login и смены устройства.
- Reward/next-level modal полностью локализован RU/EN/UZ и не содержит unresolved message keys.

## Что есть в frontend mock и чего нет в backend

Frontend admin уже показывает `Overview`, `Levels`, `Chapters`, `Tasks`, `Rewards`, `Progress`, `Localization`; level editor содержит `General`, `Learning/Content`, `Steps`, `Scenario`, `Requirements`, `Rewards/Unlocks`, `Localization`, `Preview`. Backend learning domain пока отсутствует, поэтому все эти данные должны стать versioned backend entities, а не остаться UI-only mock.

Если XP выключен продуктовым решением, поля `enableXp`, `defaultXp`, `hintPenalty` и `xpReward` удаляются из mock, frontend types и backend schema. Reward может содержать badge, certificate, unlocked level/equipment/material/book chapter — без XP.

## Admin Levels API

| API | Зачем нужен и что делает | Request | Response |
|---|---|---|---|
| `GET /api/v1/admin/learning/overview` | Заполняет learning admin overview: published/draft levels, attempts, completion и hint usage. | Query `track,from,to`. | `200 { levels:{total,published,draft},attempts,completionRate,averageDurationSeconds,hintUsage }`. |
| `GET /api/v1/admin/learning/tracks` | Возвращает tracks и их published version/order. | `page,size,status`. | `200 { items:[TrackSummary],page }`. |
| `POST /api/v1/admin/learning/tracks` | Создаёт track, например Chemistry Foundations. | `{ code,order,defaultLocale,translations }`. | `201 TrackDraft { id,version,status:"DRAFT" }`. |
| `GET /api/v1/admin/learning/levels` | Заменяет `mockLevels`, поддерживает status/search/order. | `trackId,status,q,page,size,sort`. | `200 { items:[LevelSummary],page }`. |
| `POST /api/v1/admin/learning/levels` | Создаёт level draft с базовыми полями. | `{ trackId,levelNumber,order,difficulty,estimatedMinutes,translations }`. | `201 LevelDraft { id,version,status }`. |
| `GET /api/v1/admin/learning/levels/{id}` | Загружает все tabs level editor одним consistent snapshot. | Query `include=steps,scenario,requirements,rewards,translations`. | `200 LevelEditorDocument { version,... }`. |
| `PATCH /api/v1/admin/learning/levels/{id}` | Сохраняет General/Content/Requirements без перезаписи steps. | `{ difficulty?,estimatedMinutes?,content?,requirements?,translations? }`, `If-Match`. | `200 LevelDraft`; `409`; `422`. |
| `PUT /api/v1/admin/learning/levels/{id}/steps` | Сохраняет ordered steps, checkpoints и guide targets атомарно. | `{ version,steps:[{id,order,type,instruction,requirements,guideTargets,translations}] }`. | `200 { version,steps,validationWarnings }`. |
| `PUT /api/v1/admin/learning/levels/{id}/scenario` | Привязывает/встраивает scenario и catalog version. | `{ version,scenarioId,catalogVersion,availableEquipmentIds,availableMaterialIds,initialState? }`. | `200 { version,scenarioBinding }`. |
| `PUT /api/v1/admin/learning/levels/{id}/requirements` | Настраивает prerequisites и unlock logic. | `{ version,prerequisiteLevelIds,requiredBadgeIds?,allowReplay,maxAttempts? }`. | `200 LevelRequirements`. |
| `PUT /api/v1/admin/learning/levels/{id}/rewards` | Сохраняет reward/unlocks без XP. | `{ version,badgeId?,unlockLevelIds,unlockEquipmentIds,unlockMaterialIds,unlockBookChapterIds }`. | `200 LevelRewards`. |
| `PUT /api/v1/admin/learning/levels/{id}/translations/{locale}` | Сохраняет title, goal, instructions, hints и completion text для RU/EN/UZ. | `{ title,summary,goal,steps:{...},reward:{...} }`. | `200 { locale,completeness,missingKeys,version }`. |
| `POST /api/v1/admin/learning/levels/{id}/validate` | Проверяет prerequisites cycle, ports/items, checkpoint schema, translations и previewability. | `{ version }`. | `200 { valid,errors:[{path,code,message}],warnings }`. |
| `POST /api/v1/admin/learning/levels/{id}/preview-attempts` | Создаёт isolated admin preview, который не меняет user progress. | `{ version,locale }`. | `201 { previewAttemptId,sandboxUrl,expiresAt }`. |
| `POST /api/v1/admin/learning/levels/{id}/publish` | Публикует immutable version после validation. | `{ version,idempotencyKey,releaseNote? }`. | `201 { publishedVersion,publishedAt }`. |
| `POST /api/v1/admin/learning/levels/{id}/archive` | Убирает level из новых стартов, сохраняя старые attempts. | `{ reason }`. | `200 { status:"ARCHIVED" }`. |
| `GET /api/v1/admin/learning/levels/{id}/analytics` | Заполняет progress/failure/hint metrics. | `from,to,locale?`. | `200 { starts,completions,completionRate,medianDurationSeconds,dropOffByStep,hintsByStep,failures }`. |

## Chapters, Tasks, Rewards, Progress и Localization API

| API | Зачем нужен и что делает | Request | Response |
|---|---|---|---|
| `GET/POST /api/v1/admin/learning/chapters` | Управляет группами уровней/учебными главами из Learning tab. | POST `{ trackId,order,levelIds,translations }`. | `201 LearningChapterDraft`. |
| `GET/POST /api/v1/admin/learning/tasks` | Создаёт reusable checkpoint/task definitions. | POST `{ code,type,validationRule,guideTemplate,translations }`. | `201 TaskDraft`. |
| `GET/POST /api/v1/admin/learning/rewards` | Управляет badges/unlocks/certificates. | POST `{ code,type,assetId,criteria,translations }`. | `201 RewardDraft`. |
| `GET /api/v1/admin/learning/progress` | Серверная таблица progress по users/levels. | `trackId,levelId,status,q,page,size,from,to`. | `200 { items:[{user,level,status,attempts,duration,lastActivityAt}],page }`. |
| `POST /api/v1/admin/learning/progress/{userId}/reset` | Сбрасывает progress только с permission/reason/audit. | `{ trackId?,levelId?,reason }`. | `202 { resetJobId,status:"QUEUED" }`. |
| `GET /api/v1/admin/learning/localization` | Показывает completeness RU/EN/UZ для tracks/levels/steps/rewards. | `entityType,locale,status,page,size`. | `200 { items:[{entityId,locale,completeness,missingKeys}],page }`. |

## Step/checkpoint contract

```json
{
  "id":"connect-thermometer",
  "order":2,
  "type":"PORT_CONNECTION",
  "translations":{
    "ru":{"title":"Подключите термометр","instruction":"Соедините sensor port термометра с сосудом"},
    "en":{"title":"Connect the thermometer","instruction":"Connect the thermometer sensor port to the vessel"},
    "uz":{"title":"Termometrni ulang","instruction":"Termometr sensor portini idishga ulang"}
  },
  "checkpoint":{
    "factType":"SENSOR_CONNECTED",
    "source":{"equipmentCode":"thermometer","portType":"SENSOR"},
    "target":{"capability":"CONTAINER","portType":"SENSOR"}
  },
  "guideTargets":[
    {"level":1,"kind":"TAB","id":"equipment"},
    {"level":2,"kind":"ITEM","catalogCode":"thermometer"},
    {"level":3,"kind":"PORT_PAIR","sourcePortType":"SENSOR","targetPortType":"SENSOR"}
  ]
}
```

Guide targets остаются semantic: frontend сам находит текущий DOM/2D/3D anchor. Поэтому один level definition работает на desktop, mobile и 3D renderer.

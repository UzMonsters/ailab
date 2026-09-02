# jasScience: Frontend / Backend comparison

Дата проверки: 31 августа 2026 г.

Область проверки: `lchemistry-level`, `workspace/sandbox` и все страницы `admin`.

## Краткий вывод

Сейчас frontend заметно шире backend по пользовательскому интерфейсу. `lchemistry-level` и большая часть sandbox работают как самостоятельный клиентский режим: уровни, прогресс, guided tutorial, визуальные реакции, spill/overflow, смешивание, нагревание и reward хранятся/рассчитываются в браузере.

Backend уже содержит хороший общий фундамент: авторизацию, пользователей, workspaces, persistence состояния, event log, undo/redo, realtime STOMP/WebSocket, каталог элементов/соединений/оборудования/материалов и отдельные химические расчёты. Однако эти возможности не полностью подключены к соответствующим экранам.

Admin сейчас в основном UI/mock слой. Это соответствует текущему требованию: admin нужен как дизайн интерфейса, без central mock domain и без распространения CRUD-изменений.

## 1. Есть в backend, но отсутствует или не используется frontend

### Химические расчёты

Backend предоставляет REST API для:

- разбора формулы: `POST /api/v1/chemistry/formulas/parse`;
- балансировки уравнений: `POST /api/v1/chemistry/equations/balance`;
- термодинамики: reference, calculate, Hess law, sensible heat, thermal mixing, reaction heat;
- acid/base: water, strong/weak acid/base, salt hydrolysis, buffer, titration и polyprotic titration;
- kinetics: rate, integrated law, half-life, Arrhenius, progress;
- electrochemistry: standard cell, Nernst, electrolysis;
- gas: state, mixture, transformation;
- safety evaluation: `POST /api/v1/chemistry/safety/evaluate`.

Frontend API-клиенты для многих этих методов существуют в `entities/element/api/chemistry.api.ts`, но в `lchemistry-level` и текущем sandbox они не вызываются как источник результата. Пользовательская sandbox-логика в основном использует локальный TypeScript engine.

### Реальные лабораторные sessions

Backend имеет API:

- `POST /api/v1/chemistry/experiments` — создать simulation session;
- `GET /api/v1/chemistry/experiments/{sessionId}` — получить состояние;
- `POST /api/v1/chemistry/experiments/{sessionId}/operations` — выполнить операцию;
- `POST /api/v1/chemistry/experiments/{sessionId}/events` — добавить событие;
- `POST /api/v1/chemistry/experiments/{sessionId}/replay` — replay;
- `GET /api/v1/chemistry/experiments/{sessionId}/audit/{eventId}` — calculation audit.

Frontend client `entities/experiment/api/experiment.api.ts` описывает эти вызовы, но основные действия sandbox — добавление сосуда, добавление вещества, pour, mix, sensor connection, heat и визуальные состояния — выполняются локально через `SimulationEngine`/history. Поэтому backend session API не является единственным источником правды для текущего UI.

### Realtime collaboration

Backend поддерживает STOMP/WebSocket:

- `/app/workspaces/{workspaceId}/events`;
- `/app/experiments/{sessionId}/commands`;
- `/app/workspaces/{workspaceId}/presence`.

Есть JWT interceptor, проверка доступа workspace, version conflict и realtime events. Frontend имеет `connectWorkspaceRealtime` и `useSandboxSync`, но realtime/persistence активируются только при наличии workspace id, auth и доступного backend. В anonymous/demo flow они фактически не используются.

### Измерения и sensor ingestion

Backend domain содержит simulation state, sensor-related contracts и обработку оборудования. Но текущий frontend показывает measurement только на основе локально подключённого термометра/датчика и не отправляет полноценное физическое измерение в backend. Реального IoT/device source в UI нет.

### Каталоги с backend source

Backend имеет:

- `GET /api/v1/chemistry/elements` и `/{identifier}` / `/properties`;
- `GET /api/v1/chemistry/compounds` и detail/properties;
- `GET /api/v1/chemistry/equipment` и detail;
- `GET /api/v1/chemistry/materials`.

Frontend умеет вызывать catalog API, но sandbox library сейчас содержит локальные presentation/data definitions и SVG/cartoon renderers. Полная синхронизация списка материалов, картинок, localized names и equipment с backend не сделана.

### Пользовательские и admin users API

Backend имеет:

- auth register/login/refresh/logout;
- `/api/v1/users/me`, profile, preferences, statistics, avatar;
- `/api/v1/admin/users` list/detail/update/delete.

Frontend user/admin API-клиенты присутствуют, но вход сейчас намеренно bypass-ится, пока auth не подключён: при `NEXT_PUBLIC_AUTH_ENABLED` не равном `true` форма сразу открывает dashboard. Это означает, что backend auth есть, но текущий пользовательский flow его не требует.

## 2. Есть во frontend, но отсутствует в backend

### `lchemistry-level`

Frontend реализует самостоятельную карту из 30 уровней:

- layout орбит/станций и connection graph;
- статусы `locked/current/completed`;
- progress bar и auto-centering;
- localized UI ru/en/uz;
- выбор уровня и переход в sandbox с `?level=N`;
- сохранение progress в `localStorage` (`chemistry-academy-progress-v2`);
- level intro и completion flow.

Backend API для chemistry academy levels, level definitions, prerequisites, completion, progress, rewards или next level в найденных controller routes нет. Поэтому уровень и прогресс не серверные и не привязаны к аккаунту.

### Guided tutorial

Frontend содержит tutorial для уровней 1–5:

- последовательные steps;
- подсветку tab, material, equipment, port и quick action;
- overlay arrows/guide cursor;
- автоматическое переключение шага;
- локализованные тексты ru/en/uz;
- локальные checks сценария.

Backend endpoint для tutorial steps, guide targets, completion rules и tutorial progress нет. Это полностью frontend behavior.

### Reward / level completion

Frontend показывает completion overlay, animation, badge/description, next-level button и локализованный reward copy. XP из UI был удалён по требованию. Backend endpoint для academy completion/reward/next-level offer нет; completion записывается локально в browser storage.

### Sandbox visual and interaction layer

В frontend есть функции, для которых отдельного backend API/domain contract не найдено:

- drag/pan/zoom/select/resize canvas UI;
- quick actions toolbar;
- cartoon material icons and equipment thumbnails;
- animated continuous pour stream;
- round spill/puddle on glass, fade-out примерно за 3 секунды;
- overflow badge and spill history visualization;
- shattered/microcracked visual state;
- funnel behavior: liquid passes down only when a lower connected vessel exists, otherwise spills away;
- local content merge for water + CuSO4(aq) into homogeneous solution;
- hiding deletion of individual components when mixture is homogeneous;
- local tabs `Details / Connections / History`;
- help arrows and highlighted targets;
- local reaction log, measurements, charts and simulation tabs;
- local scenario completion checks.

Backend has generic event/state primitives, but no matching public endpoint specifically describing these visual effects, tutorial target metadata or frontend-only interaction states.

### Sandbox scenarios

Frontend defines scenario catalog and checks such as:

- water in vessel;
- measure water;
- heat water;
- transfer water;
- CuSO4 solution;
- dilute KMnO4;
- HCl + NaOH neutralization;
- Zn + HCl;
- melt sulfur;
- simple distillation.

Backend has laboratory simulation primitives and chemistry calculation services, but no matching scenario catalog API for these named educational scenarios and no academy-level mapping 1–5/1–30.

### Admin UI

Frontend contains routes and screens for:

- admin dashboard;
- users;
- equipment list/detail/new;
- chemistry elements/substances/reactions list/detail/new;
- materials and chemicals;
- learning levels list/detail/new;
- scenarios list/detail/new;
- laboratories;
- safety;
- audit;
- admin book and settings.

Backend does not expose matching CRUD controllers for admin equipment, elements, substances, reactions, materials, learning levels or educational scenarios. Most of these screens import `frontend/src/mocks/admin/*` directly. The backend admin API currently covers users only, while chemistry catalogs are read-only public-style endpoints.

## 3. Частично совпадает, но контракт не завершён

| Область | Frontend | Backend | Состояние |
|---|---|---|---|
| Workspace CRUD | Dashboard and workspace API client | Full workspace controller | Mostly connected |
| Workspace state | Local engine plus optional sync | GET/PUT state, events, autosave | Optional/conditional |
| Undo/redo | Local history and API client | Workspace undo/redo | Not one guaranteed source of truth |
| Realtime | `useSandboxSync` + STOMP client | STOMP controller + JWT | Depends on auth/workspace |
| Equipment/material catalog | Local UI presentation plus catalog client | GET catalogs | UI still mostly local |
| Experiment session | Local simulation engine | Session/operation/replay API | Client exists, main UI mostly local |
| Auth | Login form, now demo bypass | Full JWT auth | Disabled by default in current flow |
| Admin users | API client available | CRUD exists | Potentially connectable |
| Admin chemistry/learning | Mock screens | No matching CRUD | UI-only by design |
| Academy levels | 30 local levels | No academy API | Frontend-only |

## 4. Главные технические расхождения

1. **Два источника состояния.** Sandbox local engine и backend simulation/workspace state могут описывать одну и ту же сцену разными структурами.
2. **Auth отключён в пользовательском flow.** Backend JWT существует, но demo login обходится без него, поэтому protected REST/WS не могут стабильно использоваться в этом режиме.
3. **Академия не серверная.** Progress, completion и reward не сохраняются в профиле пользователя.
4. **Admin chemistry/learning — mock UI.** CRUD-кнопки и формы не имеют соответствующих backend controllers; это ожидаемо при текущем требовании оставить admin только UI design.
5. **Catalog contract неполный для изображений.** Backend отдаёт описания, но frontend cartoon images/SVG и presentation mapping живут отдельно.
6. **Сценарии не имеют общего контракта.** Названия, шаги и checks находятся во frontend `scenarios.ts`, а backend не предоставляет versioned scenario definitions.

## 5. Что подключать в первую очередь, если понадобится production integration

1. Выбрать единый источник истины для sandbox: local engine или backend experiment session.
2. Добавить academy API: levels, progress, completion, reward и next-level.
3. Добавить read-only API scenario definitions либо зафиксировать frontend catalog как versioned static content.
4. Включить auth после готовности backend и убрать demo bypass через `NEXT_PUBLIC_AUTH_ENABLED=true`.
5. Подключить catalog API для материалов/equipment и определить контракт image URLs/localization.
6. Отдельно решить, нужны ли admin CRUD controllers; текущий UI может оставаться mock-only согласно поставленному требованию.

## Проверенные основные файлы

- `frontend/src/app/[locale]/lchemistry-level/page.tsx`
- `frontend/src/widgets/sandbox/SandboxWorkspace.tsx`
- `frontend/src/widgets/sandbox/SandboxCanvas.tsx`
- `frontend/src/widgets/sandbox/SandboxDock.tsx`
- `frontend/src/widgets/sandbox/scenarios.ts`
- `frontend/src/entities/*/api/*.ts`
- `Backend/app/src/main/java/com/ailab/*/controller/*`
- `Backend/app/src/main/java/com/ailab/workspace/websocket/*`
- `Backend/identity-module/src/main/java/com/ailab/*/controller/*`


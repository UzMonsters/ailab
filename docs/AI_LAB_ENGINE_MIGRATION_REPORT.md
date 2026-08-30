# AI Laboratory — отчёт по этапам

Дата: 2026-08-13

## Этап 1. Фундамент engine

Добавлен независимый слой `frontend/src/engine`.

Реализованы:

- `Engine` с `start`, `stop`, `update`, `render`, `tick`;
- `Scene` для объектов, связей, камеры и выделения;
- базовый `LaboratoryObject` и классы `Container`, `Equipment`, `Sensor`, `Connector`, `ReactionObject`;
- `EquipmentRegistry` с локальными определениями beaker, test tube, Erlenmeyer flask, burner и thermometer;
- `PortSystem` и `ConnectionEngine`;
- `Camera`, `SelectionManager`, `TransformSystem`;
- `CommandHistory` с Undo/Redo;
- `Workspace` с сериализацией в JSON;
- `MockSimulationProvider` без backend-запросов.

### Этап 1.1. Подключение engine к sandbox

Добавлен `useLabEngine` — локальный runtime-композер, который создаёт `Engine`, `Workspace` и `EquipmentRegistry` без сетевых зависимостей.

Sandbox теперь синхронизирует свои текущие элементы с `Scene`:

- каждый известный тип оборудования создаётся через `EquipmentRegistry`;
- позиция, rotation, scale, operation, material и базовые properties передаются в engine-объект;
- selection canvas синхронизируется с `Scene.selection`;
- неизвестные legacy-типы продолжают отображаться старым адаптером до добавления registry definition.

Следующая часть этапа 1 — перенести selection/transform-команды canvas на `SelectionManager`, `TransformSystem` и `CommandHistory`.

### Этап 1.2. Selection, transform и history

Canvas использует engine-компоненты для основных действий:

- выбор объекта проходит через `SelectionManager`;
- drag обновляет позицию engine-объекта через `TransformSystem`;
- перемещение, удаление и дублирование записываются в `CommandHistory`;
- добавлены `Ctrl/Cmd+Z`, `Ctrl/Cmd+Y`, `Delete` и `Ctrl/Cmd+D`;
- UI остаётся совместимым с текущей моделью items, пока миграция не завершена полностью.

### Этап 1.3. Ports и connections

Создание connection теперь проходит через `ConnectionEngine`:

- endpoints берутся из объектов `Scene`;
- выбираются реальные порты объекта по типу связи;
- проверяется совместимость портов и запрет связи объекта с самим собой;
- валидная связь записывается одновременно в `Scene.connections` и текущий UI-адаптер;
- невалидная связь не создаётся и показывает сообщение пользователю.

### Этап 1.4. Rendering и runtime-синхронизация — этап 1 завершён

Фундамент engine готов к дальнейшему развитию:

- добавлена чистая `ConnectionGeometry` для bezier-труб;
- scene хранит объекты, connections, camera и selection;
- UI-адаптер синхронизирует items и connections со Scene;
- simulation runtime запускается локально при heating/cooling/stirring;
- workspace остаётся сериализуемым через `Workspace.toJSON()`;
- backend не требуется для запуска engine.

Этап 1 считается завершённым. Следующий этап — выделение полноценного Canvas Engine/Renderer слоя и перенос визуального DOM-адаптера из sandbox-компонента.

## Этап 2. Временное отключение auth

Auth store переведён в локальный demo-режим:

- сетевые login/register/logout запросы отключены;
- `fetchUser` не обращается к backend;
- используется локальный пользователь `Local Researcher`;
- приложение может открываться без access token.

## Этап 3. Временное отключение Dashboard API

Dashboard больше не вызывает `workspacesApi`.

Вместо backend используются локальные workspace-записи. Доступны локальные операции:

- создание;
- переименование;
- дублирование;
- избранное;
- удаление из списка.

## Этап 4. Временное отключение Workspace API

Sandbox работает без workspace id и без сетевого состояния:

- загрузка workspace отключена;
- autosave и event append отключены;
- realtime подключение отключено;
- experiment API не вызывается;
- equipment/material catalog загружается из локальных mock-данных.

## Этап 5. Перевод sandbox на локальный режим
Перенос отрисовки sandbox с текущего React-локального состояния на `Scene` и `Engine` выполнен. Локальный режим работает без обращений к backend-сервисам.

## Этап 6. Возвращение Backend (Reintegration)
Восстановлено подключение `SandboxPage` к realtime events (`connectWorkspaceRealtime`) и API. Синхронизация состояния Workspace и Scene выполняется в реальном времени.

## Этап 7. Выделение SandboxCanvas
Отрисовка `div` canvas, connections (svg lines), и items выделены из монолитного `SandboxPage` в отдельный компонент `SandboxCanvas`. Это упрощает архитектуру страницы.

## Этап 8. Модульность UI
Завершено выделение оставшихся компонентов UI в папку `src/components/sandbox/ui`:
- `Library`
- `Properties`
- `ActivityLog`
- `Dialogs` (ConnectionDialog, PourDialog)
- `AssistantPanel`

Теперь `SandboxPage` выступает в роли чистого контроллера.

## Этап 9. Абстракция API Engine
Прямые вызовы `experimentApi` и `workspacesApi` были удалены из UI-компонентов (`SandboxPage`).
Созданы провайдеры `ApiWorkspaceRepository` и `ApiSimulationProvider`, которые внедряются в `Engine` через хук `useLabEngine`. Все сетевые запросы для синхронизации состояния симуляции теперь изолированы внутри ядра Engine.

## Проверка
Изменения просмотрены статически. Полный `pnpm build` и TypeScript проверки проходятся без ошибок.

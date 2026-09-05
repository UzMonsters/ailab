# jasScience frontend product audit — 2026-09-04

This report records the pre-implementation state requested for the frontend migration. It describes the code as inspected, not a target-state claim.

## Current admin routes

- Admin shell: `/[locale]/admin` with dashboard, users, laboratories, learning, book, equipment, materials, scenarios, safety, audit, settings, and nested chemistry routes.
- Equipment, material, scenario, reaction, element, substance, and learning-level list/create/detail routes already exist.
- Most detail routes use `AdminBackendEditor`; most lists use `AdminBackendResourceView`; create routes share `AdminCatalogCreateForm`.

## Current API modules

- `shared/api/client.ts` is the common HTTP client.
- `entities/admin/api/platform-admin.api.ts` exposes generic admin resources plus equipment ports/compatibility, scenario/reaction validation, learning, settings, and asset lifecycle endpoints.
- Domain clients also exist for auth, users, workspaces/collaboration, equipment catalog, experiments, learning, chemistry, and books.

## Current equipment flow

- Runtime catalog data is normalized through `EquipmentAdapter` and rendered through `EquipmentRendererRegistry`.
- Sandbox and Academy already consume the renderer registry.
- Admin creation previously carried a separate component-name renderer default and hardcoded options; detail editing is generic recursive JSON.
- Runtime ports exist, but no production visual normalized-coordinate port authoring UI exists yet.

## Current material flow

- Runtime material definitions and a normalizer exist.
- Admin material creation exposes only a limited set of scalar fields; detail editing is generic recursive JSON.
- There is no complete appearance, safety, relationship, or real Sandbox-preview editor yet.

## Current reaction flow

- The simulation engine contains a reaction system/registry and backend chemistry APIs exist.
- Admin creation is a basic reactant/product form. Detail editing remains generic and there is no complete equation/condition/effect authoring application.

## Current scenario flow

- Admin scenario creation is a primitive instruction/equipment/material step list.
- The Sandbox runtime imports `widgets/sandbox/scenarios.ts`, including JavaScript completion predicates.
- `SandboxWorkspace` contains a local `academyScenarioByLevel` mapping and dynamically requires the legacy scenario registry.

## Current learning-level flow

- Learning APIs and level routes exist.
- The create form still owns scenario-step/checkpoint/guide fields, contrary to the desired domain split.
- Runtime level content is also sourced from `data/chemistryLevels.ts`.

## Current Sandbox flow

- `SandboxWorkspace` composes the engine, canvas, catalog sidebars, panels, level intro, guide cursor, connections, sync, gestures, and assistant.
- Equipment rendering is registry-based; material rendering is normalized separately.
- Normal and learning behavior exist, but explicit `ADMIN_AUTHORING` and `ADMIN_PREVIEW` orchestration is not implemented.

## Current book flow

- Book Studio uses `BookStudioCanvas`, a block model, backend book APIs, and scenario lookup.
- `RichTextEditor` uses `contentEditable` and `document.execCommand`.
- Images are read as data URLs and stored on blocks; editor/reader rendering is not unified in one `BookPageRenderer`.
- `BookFlip` initializes bookmark state from the first prop value and needs guarded asynchronous synchronization.

## Hardcoded sources of truth

- `widgets/sandbox/scenarios.ts`: scenario definitions and executable completion functions.
- `data/chemistryLevels.ts`: curriculum/level definitions.
- `SandboxWorkspace.tsx`: `academyScenarioByLevel` mapping.
- `stores/admin.store.ts` and `mocks/admin/*`: prototype admin records.
- `entities/material/model/materialDefinitions.ts`: local runtime material definitions.
- Admin navigation labels and many admin/editor labels were hardcoded English.

## Large components that need refactoring

- `CodexSpreads.tsx` (~3,137 lines).
- `SandboxWorkspace.tsx` (~2,332 lines).
- `SandboxCanvas.tsx` (~919 lines).
- Sandbox `Library.tsx` (~746 lines).
- `CodexExperience.tsx` (~455 lines).
- Several other view/panel files exceed 300 lines and should be split only along domain boundaries.

## Legacy files that should eventually be removed

- `widgets/sandbox/scenarios.ts`, after a backend runtime provider fully replaces it.
- `data/chemistryLevels.ts`, after backend curriculum DTOs are authoritative.
- `stores/admin.store.ts` and `mocks/admin/*`, after all admin surfaces use backend resources.
- Duplicate book page interpretations, after a shared `BookPageRenderer` is adopted.

These files remain active for compatibility. They must be isolated behind adapters/providers before removal; they must not be deleted while they still power production routes.

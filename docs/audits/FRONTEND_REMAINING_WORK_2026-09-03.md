# Frontend report — remaining work and improvements

Date: 2026-09-03

## Completed baseline

- Sandbox is backend-catalog-first and has loading, empty, error and retry states.
- Responsive QA covers 390×844, 768×1024, 1366×768 and 1920×1080 without page overflow.
- Shared access has password and expiry/error UI.
- Book Studio has a canvas editor, SVG sanitisation, drag/resize, keyboard nudge, undo/redo, scenario blocks and two-page preview.
- Admin Settings is schema-driven; generic admin editing no longer relies on a raw JSON textarea.
- Admin Dashboard, Users, Laboratories, Audit and primary catalog screens have an English pass.

## Priority 0 — finish when backend contracts are available

### 1. Complete public shared-workspace opening

The access screen exists. Wire its resolved guest session into workspace REST state, events, chat and comments once the backend accepts guest tokens outside WebSocket connections.

### 2. Replace preview fallback with dark/light WebP generation

Capture the sandbox in both themes, upload both binaries through the backend preview lifecycle, and surface preview freshness/regenerate controls. Remove the SVG/data fallback only after the binary endpoint is proven.

### 3. Replace temporary book data URLs with stored assets

Book Studio can compose images and sanitized SVG safely, but uploaded assets need the final backend upload lifecycle before production persistence.

## Priority 1 — UX and accessibility

### 4. Finish Book Studio production controls

- move block history and pointer handling into a dedicated reducer to guarantee correct undo/redo after long drags;
- add layer list, reorder controls and page/block deletion confirmation;
- add Formula rendering rather than text-only notation;
- provide a real rendered adjacent page in the spread preview;
- add keyboard-resize and accessible drag instructions;
- support collaborative update/conflict notifications when another editor saves a page.

### 5. Finish the English pass

Older specialised create/detail views and a legacy unused `BookStudio.tsx` still contain Russian strings. Either remove the obsolete component or migrate all of its remaining copy. Translate all field labels in `AdminCatalogCreateForm`, chemistry sub-pages and resource detail modal content.

### 6. Improve resource detail modals

The generic list modal still stringifies nested values in places. Replace these with dedicated sections: translations, limits, ports, compatibility, safety, properties and publication history. Add focus trap, Escape close and focus restoration.

### 7. Global error/retry centre

Individual views have retry buttons. Add a central toast/error centre that can retain idempotent retry callbacks, announce failures via `aria-live` and avoid duplicate notices when several requests fail together.

### 8. Mobile/tablet interaction polish

- keep minimap hidden by default on tablet but expose it through the View menu;
- ensure both sidebars/drawers have focus traps and return focus to their toolbar trigger;
- add a compact canvas navigation hint when Move mode is activated;
- show an explicit `Unavailable` item if a saved scenario references unpublished catalog content.

## Priority 2 — test coverage and maintainability

### 9. Expand browser tests

Current responsive tests prove dimensions and shared keyboard access. Add authenticated test fixtures and coverage for:

- minimap drag, canvas pan persistence and Fit scene;
- catalog loading/error/retry and unavailable records;
- share password, expired token and permission denied states;
- Book Studio block dragging, resize, undo/redo, save and bookmark restore;
- admin pagination, filters, validation and modal focus trapping.

### 10. Resolve existing lint warnings

Lint has no errors, but warnings remain around plain `<img>` tags and React hook dependencies in sandbox/book components. Replace suitable images with `next/image`; make callbacks stable or document intentional dependency exceptions after verifying they do not introduce stale state.

### 11. Simplify generic admin editing over time

The current typed generic editor is safer than raw JSON but cannot convey domain semantics for all resources. Move high-value resources to dedicated forms using backend schemas: equipment ports/compatibility, material properties, scenario steps and learning levels.

## Recommended delivery order

1. Finish backend guest access and preview upload, then wire frontend lifecycle.
2. Finish book asset persistence and advanced canvas interaction.
3. Replace technical resource modals and complete English copy.
4. Add a global retry/a11y layer.
5. Expand authenticated Playwright coverage and clear remaining warnings.


# Frontend FSD Architecture Rules

This project uses a modified Feature-Sliced Design (FSD) architecture adapted for a web-based laboratory simulator containing a custom physics/chemistry engine.

## Directory Structure
- \pp/\ -> Next.js App Router only. No business logic, no complex components. Just pages and layouts.
- \widgets/\ -> Page composition. Large blocks assembling multiple features and entities (e.g. \SandboxWorkspace\).
- \eatures/\ -> User actions (e.g. \dd-equipment\, \pour-material\). What the user DOES with the entities.
- \entities/\ -> Domain objects (e.g. \equipment\, \material\, \connection\). Contains model, api, and UI components representing the entity.
- \engine/\ -> The core laboratory runtime simulation. 
- \shared/\ -> Generic reusable infrastructure (UI kit, api clients, libs).

## Dependencies Rule (Strict)
Dependencies can only flow downwards:
\pp\ -> \widgets\ -> \eatures\ -> \entities\ -> \shared\

## Engine Isolation Rule (Strict)
The \engine/\ directory is the runtime core. It must be PURE TypeScript.
- **NEVER** import React, Next.js, or UI components inside \engine/\.
- **NEVER** use \useState\, \useEffect\, or JSX inside \engine/\.
- Communication between the UI and the Engine happens via adapters/hooks (e.g. \useLabEngine.ts\) where the UI subscribes to the engine's \EventBus\ or mutations.

## Equipment Architecture
- Avoid massive inheritance trees (e.g. \LaboratoryObject\ -> \Container\ -> \GlassContainer\ -> \Beaker\).
- Use **Composition via Capabilities**. An object is defined by its data and capabilities (e.g., \capabilities: { container: { capacityMl: 250 }, heater: { power: 1200 } }\).
- Separate Data from Visuals. The Engine holds the data (\LaboratoryObject\). The UI holds the visual representation (\BeakerRenderer\). The Renderer reads the Engine object and renders SVG/Canvas accordingly.
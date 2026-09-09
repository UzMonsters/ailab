'use client';
import type { JsonObject } from '@/shared/api/contracts/platform';
import { SandboxWorkspace } from '@/widgets/sandbox/SandboxWorkspace';
import type { ScenarioDraft } from '../scenario.types';

export function ScenarioPreview({ draft, equipment, materials }: { draft: ScenarioDraft; equipment: JsonObject[]; materials: JsonObject[] }) {
  return <SandboxWorkspace previewDraft={draft} previewCatalog={{ equipment, materials }} embedded />;
}

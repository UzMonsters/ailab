'use client';
import type { JsonObject } from '@/shared/api/contracts/platform';
import type { Locale } from '@/shared/types/catalog';
import { SandboxWorkspace } from '@/widgets/sandbox/SandboxWorkspace';
import type { ScenarioDraft } from '../scenario.types';

export function ScenarioPreview({ draft, equipment, materials }: { draft: ScenarioDraft; equipment: JsonObject[]; materials: JsonObject[]; locale: Locale }) {
  return <section className="overflow-hidden rounded-2xl border border-violet-400/25 bg-[#080c14]">
    <div className="flex items-center justify-between border-b border-white/10 px-4 py-3"><div><p className="text-xs font-bold uppercase tracking-widest text-violet-300">Interactive admin preview</p><p className="mt-1 text-xs text-slate-400">This is the production Sandbox runtime with the unsaved draft injected as ADMIN_PREVIEW.</p></div><span className="rounded-full border border-emerald-400/30 bg-emerald-500/10 px-3 py-1 text-xs text-emerald-200">Real engine</span></div>
    <div className="h-[760px]"><SandboxWorkspace previewDraft={draft} previewCatalog={{ equipment, materials }} embedded /></div>
  </section>;
}

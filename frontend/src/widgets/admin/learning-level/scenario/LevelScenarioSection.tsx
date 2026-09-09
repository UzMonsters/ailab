'use client';

import { useCallback, useEffect, useState } from 'react';
import Link from 'next/link';
import { useLocale } from 'next-intl';
import { ExternalLink, Eye, Loader2, Search } from 'lucide-react';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
import type { JsonObject } from '@/shared/api/contracts/platform';
import type { LevelDraft } from '../levelEditor.types';
import { FormSection } from '@/widgets/admin/editor';
import { ScenarioPreview } from '@/widgets/admin/scenario/preview/ScenarioPreview';
import { scenarioDraft } from '@/widgets/admin/scenario/scenario.model';

const select = 'w-full rounded-lg border border-white/10 bg-[#080c14] px-3 py-2.5 text-sm text-white outline-none focus:border-violet-400 appearance-none';

function ScenarioCard({ scenario }: { scenario: JsonObject }) {
  const name = String(scenario.name ?? scenario.title ?? scenario.code ?? 'Untitled');
  const code = String(scenario.code ?? '');
  const status = String(scenario.status ?? 'DRAFT');
  const translations = scenario.translations && typeof scenario.translations === 'object' ? scenario.translations as JsonObject : {};
  const locales = ['ru', 'uz', 'en'] as const;
  const localizedLocales = locales.filter(loc => {
    const t = translations[loc];
    return t && typeof t === 'object' && typeof (t as JsonObject).name === 'string' && ((t as JsonObject).name as string).trim();
  });

  return (
    <div className="rounded-xl border border-white/10 bg-[#0b101a] p-4">
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <h3 className="text-sm font-semibold text-white">{name}</h3>
          {code && <p className="mt-0.5 text-xs text-slate-500">{code}</p>}
        </div>
        <span className={`shrink-0 rounded-full px-2 py-1 text-[10px] font-bold ${status === 'PUBLISHED' ? 'bg-emerald-500/10 text-emerald-300' : 'bg-amber-500/10 text-amber-300'}`}>
          {status}
        </span>
      </div>
      <div className="mt-3 flex flex-wrap gap-2 text-[10px] text-slate-400">
        <span>{localizedLocales.length}/3 localized</span>
        {localizedLocales.map(loc => (
          <span key={loc} className="rounded bg-emerald-500/10 px-1.5 py-0.5 text-emerald-300">{loc.toUpperCase()} ✓</span>
        ))}
      </div>
    </div>
  );
}

export function LevelScenarioSection({ draft, onChange }: { draft: LevelDraft; onChange: (patch: Partial<LevelDraft>) => void }) {
  const appLocale = useLocale();
  const [showPicker, setShowPicker] = useState(false);
  const [search, setSearch] = useState('');
  const [scenarios, setScenarios] = useState<JsonObject[]>([]);
  const [searchLoading, setSearchLoading] = useState(false);
  const [selectedScenario, setSelectedScenario] = useState<JsonObject | null>(null);
  const [showPreview, setShowPreview] = useState(false);
  const [catalogEquipment, setCatalogEquipment] = useState<JsonObject[]>([]);
  const [catalogMaterials, setCatalogMaterials] = useState<JsonObject[]>([]);

  const searchScenarios = useCallback(async (query: string) => {
    setSearchLoading(true);
    try {
      const response = await adminPlatformApi.scenarios.list({
        status: 'PUBLISHED',
        search: query.trim() || undefined,
        size: 20,
        sort: 'updatedAt,desc',
      });
      setScenarios(response.items ?? []);
    } catch (error) {
      console.error('Failed to search scenarios:', error);
    } finally {
      setSearchLoading(false);
    }
  }, []);

  useEffect(() => {
    if (!showPicker) return;
    const timer = window.setTimeout(() => void searchScenarios(search), 250);
    return () => window.clearTimeout(timer);
  }, [search, showPicker, searchScenarios]);

  useEffect(() => {
    if (!showPreview) return;
    let active = true;
    void Promise.all([
      adminPlatformApi.equipment.list({ status: 'PUBLISHED', page: 0, size: 100, sort: 'code,asc' }),
      adminPlatformApi.materials.list({ status: 'PUBLISHED', page: 0, size: 100, sort: 'code,asc' }),
    ]).then(([eq, mat]) => {
      if (!active) return;
      setCatalogEquipment(eq.items ?? eq.content ?? []);
      setCatalogMaterials(mat.items ?? mat.content ?? []);
    }).catch((error) => {
      console.error('Failed to load preview catalogs:', error);
    });
    return () => { active = false; };
  }, [showPreview]);

  useEffect(() => {
    if (!draft.scenarioId) {
      const timer = window.setTimeout(() => setSelectedScenario(null), 0);
      return () => window.clearTimeout(timer);
    }
    let active = true;
    void adminPlatformApi.scenarios.get(draft.scenarioId).then(raw => {
      if (active) setSelectedScenario(raw);
    }).catch(() => { if (active) setSelectedScenario(null); });
    return () => { active = false; };
  }, [draft.scenarioId]);

  const selectScenario = (id: string) => {
    onChange({ scenarioId: id });
    setShowPicker(false);
    setSearch('');
  };

  return (
    <div className="space-y-6">
      <FormSection title="Scenario" description="Link this Level to a published Scenario. Steps, Completion Rules and Help are configured in the Scenario Editor.">
        <div className="space-y-4">
          {selectedScenario ? (
            <div className="space-y-3">
              <ScenarioCard scenario={selectedScenario} />
              <div className="flex flex-wrap gap-2">
                <Link
                   href={`/${appLocale}/admin/scenarios/${draft.scenarioId}`}
                  className="inline-flex items-center gap-2 rounded-lg border border-white/10 bg-white/[.025] px-3 py-2 text-sm text-slate-200 hover:bg-white/[.06]"
                >
                  <ExternalLink size={14} /> Open Scenario
                </Link>
                <button
                  onClick={() => setShowPreview(!showPreview)}
                  className="inline-flex items-center gap-2 rounded-lg border border-white/10 bg-white/[.025] px-3 py-2 text-sm text-slate-200 hover:bg-white/[.06]"
                >
                  <Eye size={14} /> {showPreview ? 'Hide Preview' : 'Preview'}
                </button>
                <button
                  onClick={() => setShowPicker(true)}
                  className="inline-flex items-center gap-2 rounded-lg border border-violet-400/30 bg-violet-600/10 px-3 py-2 text-sm text-violet-200 hover:bg-violet-600/20"
                >
                  Change
                </button>
              </div>
            </div>
          ) : (
            <div className="rounded-xl border border-dashed border-white/10 p-8 text-center">
              <p className="text-sm text-slate-400">No Scenario linked</p>
              <button
                onClick={() => setShowPicker(true)}
                className="mt-3 inline-flex items-center gap-2 rounded-lg bg-violet-600 px-4 py-2 text-sm font-semibold text-white hover:bg-violet-500"
              >
                Select Scenario
              </button>
            </div>
          )}
        </div>
      </FormSection>

      {showPreview && selectedScenario && draft.scenarioId && (
        <FormSection title="Scenario Preview" description="Interactive preview of the linked Scenario.">
          <div className="h-[600px] overflow-hidden rounded-xl border border-violet-400/20">
            <ScenarioPreview
              draft={scenarioDraft(selectedScenario)}
              equipment={catalogEquipment}
              materials={catalogMaterials}
            />
          </div>
        </FormSection>
      )}

      {showPicker && (
        <div className="fixed inset-0 z-[400] flex items-center justify-center bg-slate-950/60 backdrop-blur-sm" role="dialog" aria-modal="true" aria-label="Select Scenario">
          <div className="w-full max-w-lg rounded-2xl border border-white/10 bg-[#0b101a] p-6 shadow-2xl">
            <h3 className="text-lg font-semibold text-white">Select Scenario</h3>
            <p className="mt-1 text-sm text-slate-400">Search and select a published Scenario.</p>
            <label className="mt-4 flex items-center gap-2 rounded-lg border border-white/10 bg-[#080c14] px-3">
              <Search size={14} className="text-slate-500" />
              <input
                autoFocus
                value={search}
                onChange={e => setSearch(e.target.value)}
                className="w-full bg-transparent py-2.5 text-sm outline-none"
                placeholder="Search by name or code..."
              />
              {searchLoading && <Loader2 size={14} className="animate-spin text-violet-300" />}
            </label>
            <div className="mt-3 max-h-72 space-y-1 overflow-auto">
              {scenarios.map(s => {
                const sId = String(s.id ?? s.code);
                const sName = String(s.name ?? s.title ?? s.code ?? sId);
                const isSelected = sId === draft.scenarioId;
                return (
                  <button
                    key={sId}
                    onClick={() => selectScenario(sId)}
                    className={`flex w-full items-center gap-3 rounded-lg px-3 py-2.5 text-left transition-colors ${isSelected ? 'bg-violet-600/20 text-violet-200' : 'hover:bg-white/5 text-slate-300'}`}
                  >
                    <div className="min-w-0 flex-1">
                      <p className="truncate text-sm font-medium">{sName}</p>
                      <p className="text-[10px] text-slate-500">{String(s.code ?? sId)}</p>
                    </div>
                    <span className={`rounded-full px-2 py-0.5 text-[10px] ${String(s.status) === 'PUBLISHED' ? 'bg-emerald-500/10 text-emerald-300' : 'bg-amber-500/10 text-amber-300'}`}>
                      {String(s.status ?? 'DRAFT')}
                    </span>
                  </button>
                );
              })}
              {!scenarios.length && !searchLoading && (
                <p className="p-4 text-center text-sm text-slate-500">No scenarios found</p>
              )}
            </div>
            <div className="mt-4 flex justify-end">
              <button onClick={() => { setShowPicker(false); setSearch(''); }}
                className="rounded-lg border border-white/10 px-4 py-2 text-sm text-slate-300 hover:bg-white/5">
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

'use client';

import { useState } from 'react';
import type { LevelDraft } from '../levelEditor.types';
import { FormSection } from '@/widgets/admin/editor';
import { LevelIntro, type LevelIntroDefinition } from '@/widgets/sandbox/LevelIntro';
import type { RuntimeLocale } from '@/widgets/sandbox/runtime/runtime.types';
import { CONTENT_LOCALES, type Locale } from '@/shared/types/catalog';

export function LevelPreview({ draft }: { draft: LevelDraft }) {
  const [previewLocale, setPreviewLocale] = useState<Locale>('en');
  const [showIntro, setShowIntro] = useState(false);

  const previewLevel: LevelIntroDefinition = {
    id: draft.levelNumber || 1,
    scenarioId: draft.scenarioId || 'draft',
    title: {
      ru: draft.translations.ru.title || 'Untitled level',
      uz: draft.translations.uz.title || 'Untitled level',
      en: draft.translations.en.title || 'Untitled level',
    },
    objective: {
      ru: draft.translations.ru.goal,
      uz: draft.translations.uz.goal,
      en: draft.translations.en.goal,
    },
    learningPoints: Array.from(
      { length: Math.max(draft.translations.ru.learningObjectives.length, draft.translations.uz.learningObjectives.length, draft.translations.en.learningObjectives.length) },
      (_, i) => ({
        ru: draft.translations.ru.learningObjectives[i] || '—',
        uz: draft.translations.uz.learningObjectives[i] || '—',
        en: draft.translations.en.learningObjectives[i] || '—',
      }),
    ),
    duration: {
      ru: `~${draft.estimatedMinutes} мин.`,
      uz: `~${draft.estimatedMinutes} daqiqa`,
      en: `~${draft.estimatedMinutes} min`,
    },
    allowedEquipment: [],
    allowedMaterials: [],
  };

  return (
    <div className="space-y-6">
      <FormSection title="Preview" description="Preview how this Level appears to the learner.">
        <div className="mb-4 flex gap-2">
          {CONTENT_LOCALES.map(x => (
            <button key={x} onClick={() => setPreviewLocale(x)}
              className={`rounded px-3 py-1.5 text-sm font-semibold uppercase ${previewLocale === x ? 'bg-violet-600 text-white' : 'border border-white/10 text-[#9aa6ba]'}`}>
              {x}
            </button>
          ))}
        </div>

        <div className="space-y-4">
          <div className="rounded-xl border border-white/10 bg-[#080c14] p-5">
            <div className="flex items-start justify-between">
              <div>
                <span className="text-[11px] font-black uppercase tracking-[.18em] text-cyan-400">
                  Level {draft.levelNumber}
                </span>
                <h2 className="mt-1 text-xl font-bold text-white">
                  {draft.translations[previewLocale].title || 'Untitled'}
                </h2>
              </div>
              <span className="rounded-full border border-white/10 px-2 py-1 text-xs text-slate-400">
                {draft.difficulty}
              </span>
            </div>

            {draft.translations[previewLocale].goal && (
              <div className="mt-4 rounded-lg border border-white/5 bg-white/[.02] p-3">
                <p className="text-[10px] font-black uppercase tracking-[.14em] text-slate-500">Objective</p>
                <p className="mt-1 text-sm text-slate-300">{draft.translations[previewLocale].goal}</p>
              </div>
            )}

            {draft.translations[previewLocale].learningObjectives.length > 0 && (
              <div className="mt-4">
                <p className="text-[10px] font-black uppercase tracking-[.14em] text-slate-500">Learning Objectives</p>
                <ul className="mt-2 space-y-1">
                  {draft.translations[previewLocale].learningObjectives.map((obj, i) => (
                    <li key={i} className="flex items-start gap-2 text-sm text-slate-300">
                      <span className="text-cyan-400 mt-0.5">✓</span>
                      {obj}
                    </li>
                  ))}
                </ul>
              </div>
            )}

            <div className="mt-4 flex items-center gap-4 text-xs text-slate-500">
              <span>~{draft.estimatedMinutes} min</span>
              <span>•</span>
              <span>{draft.trackId}</span>
            </div>
          </div>

          <button
            onClick={() => setShowIntro(true)}
            className="inline-flex items-center gap-2 rounded-lg bg-[var(--primary)] px-4 py-2.5 text-sm font-semibold text-white hover:brightness-110"
          >
            Show Level Intro Modal
          </button>
        </div>
      </FormSection>

      {showIntro && (
        <LevelIntro
          level={previewLevel}
          locale={previewLocale as RuntimeLocale}
          onStart={() => setShowIntro(false)}
        />
      )}
    </div>
  );
}

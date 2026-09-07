'use client';

import { useState } from 'react';
import { Plus, GripVertical, Trash2 } from 'lucide-react';
import { CONTENT_LOCALES, type Locale } from '@/shared/types/catalog';
import type { LevelDraft, LevelTranslation } from '../levelEditor.types';
import { FormSection, FieldHelp } from '@/widgets/admin/editor';

const input = 'w-full rounded-lg border border-white/10 bg-[#080c14] px-3 py-2.5 text-sm text-white outline-none focus:border-violet-400';

export function LevelLocalization({ draft, onChange }: { draft: LevelDraft; onChange: (patch: Partial<LevelDraft>) => void }) {
  const [activeLocale, setActiveLocale] = useState<Locale>('en');

  const updateTranslation = (locale: Locale, patch: Partial<LevelTranslation>) => {
    onChange({
      translations: {
        ...draft.translations,
        [locale]: { ...draft.translations[locale], ...patch },
      },
    });
  };

  const addObjective = (locale: Locale) => {
    const current = draft.translations[locale].learningObjectives;
    updateTranslation(locale, { learningObjectives: [...current, ''] });
  };

  const updateObjective = (locale: Locale, index: number, value: string) => {
    const current = [...draft.translations[locale].learningObjectives];
    current[index] = value;
    updateTranslation(locale, { learningObjectives: current });
  };

  const removeObjective = (locale: Locale, index: number) => {
    const current = draft.translations[locale].learningObjectives.filter((_, i) => i !== index);
    updateTranslation(locale, { learningObjectives: current });
  };

  const moveObjective = (locale: Locale, index: number, direction: -1 | 1) => {
    const current = [...draft.translations[locale].learningObjectives];
    const newIndex = index + direction;
    if (newIndex < 0 || newIndex >= current.length) return;
    [current[index], current[newIndex]] = [current[newIndex], current[index]];
    updateTranslation(locale, { learningObjectives: current });
  };

  const current = draft.translations[activeLocale];

  return (
    <div className="space-y-6">
      <FormSection title="Localization" description="Localized content for every supported language.">
        <div className="mb-4 flex gap-2">
          {CONTENT_LOCALES.map(x => (
            <button key={x} onClick={() => setActiveLocale(x)}
              className={`rounded px-3 py-1.5 text-sm font-semibold uppercase ${activeLocale === x ? 'bg-violet-600 text-white' : 'border border-white/10 text-[#9aa6ba]'}`}>
              {x}
            </button>
          ))}
        </div>

        <div className="grid gap-4 md:grid-cols-2">
          <div>
            <label className="text-sm text-[#c1cada]">Title *</label>
            <input
              value={current.title}
              onChange={e => updateTranslation(activeLocale, { title: e.target.value })}
              className={`mt-1.5 ${input}`}
              placeholder="Level title"
            />
          </div>

          <div>
            <label className="text-sm text-[#c1cada]">Goal</label>
            <input
              value={current.goal}
              onChange={e => updateTranslation(activeLocale, { goal: e.target.value })}
              className={`mt-1.5 ${input}`}
              placeholder="What the learner will achieve"
            />
          </div>

          <div className="md:col-span-2">
            <label className="text-sm text-[#c1cada]">Summary</label>
            <textarea
              value={current.summary}
              onChange={e => updateTranslation(activeLocale, { summary: e.target.value })}
              className={`mt-1.5 ${input} min-h-24`}
              placeholder="Brief description of this level"
            />
          </div>

          <div className="md:col-span-2">
            <label className="text-sm text-[#c1cada]">Intro</label>
            <textarea
              value={current.intro}
              onChange={e => updateTranslation(activeLocale, { intro: e.target.value })}
              className={`mt-1.5 ${input} min-h-24`}
              placeholder="Detailed introduction shown to the learner"
            />
          </div>
        </div>
      </FormSection>

      <FormSection title="Learning Objectives" description="What the learner will be able to do after completing this level.">
        <div className="space-y-2">
          {current.learningObjectives.map((objective, index) => (
            <div key={index} className="flex items-center gap-2">
              <GripVertical size={14} className="shrink-0 text-slate-600 cursor-grab" />
              <span className="shrink-0 w-6 text-center text-xs text-slate-500">{index + 1}</span>
              <input
                value={objective}
                onChange={e => updateObjective(activeLocale, index, e.target.value)}
                className={`flex-1 ${input}`}
                placeholder={`Objective ${index + 1}`}
              />
              <button onClick={() => moveObjective(activeLocale, index, -1)}
                className="shrink-0 text-slate-500 hover:text-white disabled:opacity-30"
                disabled={index === 0}>
                ↑
              </button>
              <button onClick={() => moveObjective(activeLocale, index, 1)}
                className="shrink-0 text-slate-500 hover:text-white disabled:opacity-30"
                disabled={index === current.learningObjectives.length - 1}>
                ↓
              </button>
              <button onClick={() => removeObjective(activeLocale, index)}
                className="shrink-0 text-slate-500 hover:text-rose-300">
                ✕
              </button>
            </div>
          ))}
          <button
            onClick={() => addObjective(activeLocale)}
            className="flex items-center gap-2 rounded-lg border border-violet-400/30 px-3 py-2 text-sm text-violet-300 hover:bg-violet-600/10"
          >
            <Plus size={14} /> Add objective
          </button>
        </div>
      </FormSection>
    </div>
  );
}

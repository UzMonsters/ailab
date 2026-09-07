'use client';

import type { LevelDraft, LevelDifficulty } from '../levelEditor.types';
import { difficultyLabel, TRACK_OPTIONS } from '../levelEditor.types';
import { FormSection, FieldHelp } from '@/widgets/admin/editor';
import type { Locale } from '@/shared/types/catalog';

const input = 'w-full rounded-lg border border-white/10 bg-[#080c14] px-3 py-2.5 text-sm text-white outline-none focus:border-violet-400';
const select = `${input} appearance-none`;

export function LevelDetails({ draft, onChange }: { draft: LevelDraft; onChange: (patch: Partial<LevelDraft>) => void }) {
  const track = TRACK_OPTIONS.find(t => t.id === draft.trackId);

  return (
    <div className="space-y-6">
      <FormSection title="Details" description="Core metadata for this learning level.">
        <div className="grid gap-4 md:grid-cols-2">
          <div>
            <label className="text-sm text-[#c1cada]">Track</label>
            <select
              value={draft.trackId}
              onChange={e => onChange({ trackId: e.target.value })}
              className={`mt-1.5 ${select}`}
            >
              {TRACK_OPTIONS.map(t => (
                <option key={t.id} value={t.id}>{t.label.en}</option>
              ))}
            </select>
            <FieldHelp>
              {track ? `Current track: ${track.label.en}` : 'Select a learning track'}
            </FieldHelp>
          </div>

          <div>
            <label className="text-sm text-[#c1cada]">Difficulty</label>
            <select
              value={draft.difficulty}
              onChange={e => onChange({ difficulty: e.target.value as LevelDifficulty })}
              className={`mt-1.5 ${select}`}
            >
              {(['BEGINNER', 'INTERMEDIATE', 'ADVANCED'] as LevelDifficulty[]).map(d => (
                <option key={d} value={d}>{difficultyLabel[d].en}</option>
              ))}
            </select>
            <FieldHelp>
              {difficultyLabel[draft.difficulty].en} difficulty level
            </FieldHelp>
          </div>

          <div>
            <label className="text-sm text-[#c1cada]">Level Number</label>
            <input
              type="number"
              min="1"
              value={draft.levelNumber}
              onChange={e => onChange({ levelNumber: Math.max(1, Number(e.target.value)) })}
              className={`mt-1.5 ${input}`}
            />
            <FieldHelp>
              Displayed to the learner (for example, &quot;Level 1&quot;)
            </FieldHelp>
          </div>

          <div>
            <label className="text-sm text-[#c1cada]">Order</label>
            <input
              type="number"
              min="1"
              value={draft.order}
              onChange={e => onChange({ order: Math.max(1, Number(e.target.value)) })}
              className={`mt-1.5 ${input}`}
            />
            <FieldHelp>
              Position inside the Track
            </FieldHelp>
          </div>

          <div>
            <label className="text-sm text-[#c1cada]">Estimated Duration</label>
            <div className="mt-1.5 flex items-center gap-2">
              <input
                type="number"
                min="1"
                value={draft.estimatedMinutes}
                onChange={e => onChange({ estimatedMinutes: Math.max(1, Number(e.target.value)) })}
                className={`w-24 ${input}`}
              />
              <span className="text-sm text-slate-400">minutes</span>
            </div>
            <FieldHelp>
              Approximate time for the learner
            </FieldHelp>
          </div>
        </div>
      </FormSection>
    </div>
  );
}

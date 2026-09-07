'use client';

import { useEffect, useMemo, useState } from 'react';
import { BadgeCheck, Loader2 } from 'lucide-react';
import { CONTENT_LOCALES, type Locale } from '@/shared/types/catalog';
import type { LevelDraft } from '../levelEditor.types';
import { FormSection } from '@/widgets/admin/editor';

type ValidationIssue = { field: string; message: string; severity: 'error' | 'warning' };

export function LevelValidation({ draft, id }: { draft: LevelDraft; id?: string }) {
  const [scenarioValid, setScenarioValid] = useState<boolean | null>(null);

  useEffect(() => {
    let active = true;
    const timer = window.setTimeout(() => {
      if (!draft.scenarioId) {
        setScenarioValid(null);
        return;
      }
      import('@/entities/admin/api/platform-admin.api').then(({ adminPlatformApi }) => {
        adminPlatformApi.scenarios.get(draft.scenarioId).then(raw => {
          if (!active) return;
          const status = String(raw.status ?? 'DRAFT');
          const steps = Array.isArray(raw.steps) ? raw.steps : [];
          setScenarioValid(status === 'PUBLISHED' && steps.length > 0);
        }).catch(() => active && setScenarioValid(false));
      });
    }, 0);
    return () => { active = false; window.clearTimeout(timer); };
  }, [draft.scenarioId]);

  const issues = useMemo(() => {
    const result: ValidationIssue[] = [];

    if (!draft.trackId.trim()) {
      result.push({ field: 'Track', message: 'Track is required.', severity: 'error' });
    }

    if (draft.levelNumber < 1) {
      result.push({ field: 'Level Number', message: 'Must be at least 1.', severity: 'error' });
    }

    if (draft.order < 1) {
      result.push({ field: 'Order', message: 'Must be at least 1.', severity: 'error' });
    }

    if (draft.estimatedMinutes < 1) {
      result.push({ field: 'Duration', message: 'Must be at least 1 minute.', severity: 'error' });
    }

    CONTENT_LOCALES.forEach(locale => {
      if (!draft.translations[locale].title.trim()) {
        result.push({ field: `${locale.toUpperCase()} Title`, message: 'Title is required.', severity: 'error' });
      }
    });

    if (!draft.scenarioId) {
      result.push({ field: 'Scenario', message: 'A Scenario must be linked.', severity: 'error' });
    } else if (scenarioValid === false) {
      result.push({ field: 'Scenario', message: 'Linked Scenario is not published or has no steps.', severity: 'warning' });
    }

    CONTENT_LOCALES.forEach(locale => {
      if (draft.translations[locale].learningObjectives.length === 0) {
        result.push({ field: `${locale.toUpperCase()} Objectives`, message: 'At least one learning objective is recommended.', severity: 'warning' });
      }
    });

    return result;
  }, [draft, scenarioValid]);

  const errors = issues.filter(i => i.severity === 'error');
  const warnings = issues.filter(i => i.severity === 'warning');

  return (
    <div className="space-y-6">
      <FormSection title="Validation" description="Check level integrity before publishing.">
        {scenarioValid === null && draft.scenarioId && (
          <div className="flex items-center gap-2 text-sm text-slate-400">
            <Loader2 size={14} className="animate-spin" /> Checking scenario...
          </div>
        )}

        {errors.length === 0 && warnings.length === 0 ? (
          <p className="text-sm text-emerald-300">All checks passed.</p>
        ) : (
          <ul className="space-y-2">
            {errors.map((issue, i) => (
              <li key={`e-${i}`} className="flex items-start gap-2 text-sm text-rose-300">
                <BadgeCheck size={14} className="mt-0.5 shrink-0 text-rose-400" />
                <span><strong>{issue.field}</strong>: {issue.message}</span>
              </li>
            ))}
            {warnings.map((issue, i) => (
              <li key={`w-${i}`} className="flex items-start gap-2 text-sm text-amber-200">
                <BadgeCheck size={14} className="mt-0.5 shrink-0 text-amber-400" />
                <span><strong>{issue.field}</strong>: {issue.message}</span>
              </li>
            ))}
          </ul>
        )}
      </FormSection>
    </div>
  );
}

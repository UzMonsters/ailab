'use client';
import { useEffect, useMemo, useState } from 'react';
import { learningApi } from '@/entities/learning/api/learning.api';
import type { JsonObject } from '@/shared/api/contracts/platform';
import { legacyScenarioForLevel, legacyScenarioRuntime } from './legacy/legacyScenarioAdapter';
import { normalizeScenarioRuntime } from './normalizeScenarioRuntime';
import type { RuntimeLoadState, RuntimeLocale } from './runtime.types';

type Options = { attemptId?: string | null; levelId?: string | null; legacyLevelNumber?: string | null; locale: RuntimeLocale; fallbackScenarioId?: string };

export function useScenarioRuntime({ attemptId, levelId, legacyLevelNumber, locale, fallbackScenarioId }: Options) {
  const legacyId = fallbackScenarioId ?? legacyScenarioForLevel(legacyLevelNumber ?? null);
  const fallback = useMemo(() => legacyScenarioRuntime(legacyId, locale), [legacyId, locale]);
  const [state, setState] = useState<RuntimeLoadState>({ scenario: null, level: null, attempt: null, loading: Boolean(attemptId || levelId), error: null });

  useEffect(() => {
    let active = true;
    if (!attemptId && !levelId) return () => { active = false; };
    const load = async () => {
      let attempt: JsonObject | null = null, level: JsonObject | null = null;
      if (attemptId) attempt = await learningApi.attempt(attemptId);
      let scenario = attempt ? normalizeScenarioRuntime(attempt, locale) : null;
      const resolvedLevelId = attempt ? String(attempt.levelId ?? (attempt.level as JsonObject | undefined)?.id ?? '') : String(levelId ?? '');
      if (!scenario && resolvedLevelId) { level = await learningApi.level(resolvedLevelId, locale); scenario = normalizeScenarioRuntime({ ...level, level, scenario: level.scenario }, locale); }
      if (!active) return;
      setState({ scenario: scenario ?? fallback, level, attempt, loading: false, error: scenario || fallback ? null : 'The selected Level does not contain a published Scenario.' });
    };
    void load().catch((reason) => { if (active) setState({ scenario: fallback, level: null, attempt: null, loading: false, error: reason instanceof Error ? reason.message : 'Scenario could not be loaded.' }); });
    return () => { active = false; };
  }, [attemptId, fallback, levelId, locale]);

  return attemptId || levelId ? state : { scenario: fallback, level: null, attempt: null, loading: false, error: null };
}

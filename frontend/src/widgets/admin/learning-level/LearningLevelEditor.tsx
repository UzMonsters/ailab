'use client';

import { useCallback, useEffect, useMemo, useState } from 'react';
import { useLocale } from 'next-intl';
import { useRouter } from 'next/navigation';
import { Loader2 } from 'lucide-react';
import { adminLearningApi } from '@/entities/learning/api/learning.api';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
import type { JsonObject } from '@/shared/api/contracts/platform';
import { CONTENT_LOCALES, type Locale } from '@/shared/types/catalog';
import { errorMessage } from '@/shared/utils/errorMessage';
import { useToastStore } from '@/stores/toast.store';
import {
  AdminEditorActions, AdminEditorHeader, AdminEditorShell, AdminEditorTabs,
  type EditorSaveState,
} from '@/widgets/admin/editor';
import type { LevelDraft, LevelDifficulty } from './levelEditor.types';
import { emptyLevel } from './levelEditor.types';
import { LevelDetails } from './details/LevelDetails';
import { LevelLocalization } from './localization/LevelLocalization';
import { LevelScenarioSection } from './scenario/LevelScenarioSection';
import { LevelPreview } from './preview/LevelPreview';
import { LevelValidation } from './validation/LevelValidation';

const tabs = ['details', 'localization', 'scenario', 'preview', 'validation'] as const;
type Tab = (typeof tabs)[number];

function levelPayload(draft: LevelDraft): JsonObject {
  return {
    trackId: draft.trackId,
    levelNumber: draft.levelNumber,
    order: draft.order,
    difficulty: draft.difficulty,
    estimatedMinutes: draft.estimatedMinutes,
    scenarioId: draft.scenarioId || undefined,
    translations: Object.fromEntries(
      CONTENT_LOCALES.map(locale => [locale, {
        title: draft.translations[locale].title,
        summary: draft.translations[locale].summary,
        goal: draft.translations[locale].goal,
        intro: draft.translations[locale].intro,
        learningObjectives: draft.translations[locale].learningObjectives,
      }]),
    ),
  };
}

function levelFromRaw(raw: JsonObject): LevelDraft {
  const translations = raw.translations && typeof raw.translations === 'object' ? raw.translations as JsonObject : {};
  const tl = (locale: Locale) => {
    const t = translations[locale] && typeof translations[locale] === 'object' ? translations[locale] as JsonObject : {};
    return {
      title: String(t.title ?? ''),
      summary: String(t.summary ?? ''),
      goal: String(t.goal ?? ''),
      intro: String(t.intro ?? ''),
      learningObjectives: Array.isArray(t.learningObjectives) ? t.learningObjectives.map(String) : [],
    };
  };
  return {
    id: String(raw.id ?? ''),
    trackId: String(raw.trackId ?? 'chemistry'),
    levelNumber: Number(raw.levelNumber ?? raw.order ?? 1),
    order: Number(raw.order ?? 1),
    difficulty: String(raw.difficulty ?? 'BEGINNER') as LevelDifficulty,
    estimatedMinutes: Number(raw.estimatedMinutes ?? 10),
    status: String(raw.status ?? 'DRAFT') as LevelDraft['status'],
    version: Number(raw.version ?? 0),
    scenarioId: String(raw.scenarioId ?? (raw.scenario as JsonObject | undefined)?.id ?? ''),
    translations: { ru: tl('ru'), uz: tl('uz'), en: tl('en') },
  };
}

export default function LearningLevelEditor({ id }: { id?: string }) {
  const locale = useLocale() as Locale;
  const router = useRouter();
  const toast = useToastStore(x => x.addToast);

  const [draft, setDraft] = useState<LevelDraft>(emptyLevel);
  const [baseline, setBaseline] = useState('');
  const [tab, setTab] = useState<Tab>('details');
  const [loading, setLoading] = useState(true);
  const [saveState, setSaveState] = useState<EditorSaveState>('saved');
  const [error, setError] = useState('');

  const payload = useMemo(() => levelPayload(draft), [draft]);
  const dirty = JSON.stringify(payload) !== baseline;
  const state = saveState === 'saving' || saveState === 'failed' ? saveState : dirty ? 'dirty' : 'saved';

  useEffect(() => {
    let active = true;
    const load = id
      ? adminLearningApi.level(id)
      : Promise.resolve({} as JsonObject);
    void load
      .then(raw => {
        if (!active) return;
        const next = id ? levelFromRaw(raw) : emptyLevel();
        setDraft(next);
        setBaseline(JSON.stringify(levelPayload(next)));
      })
      .catch(reason => active && setError(errorMessage(reason, 'Unable to load Level.')))
      .finally(() => active && setLoading(false));
    return () => { active = false; };
  }, [id]);

  const change = useCallback(
    (patch: Partial<LevelDraft>) => setDraft(current => ({ ...current, ...patch })),
    [],
  );

  const save = useCallback(async () => {
    setSaveState('saving');
    setError('');
    try {
      const result = id
        ? await adminLearningApi.patchLevel(id, payload)
        : await adminLearningApi.createLevel(payload);
      const next = levelFromRaw(result);
      setDraft(next);
      setBaseline(JSON.stringify(levelPayload(next)));
      setSaveState('saved');
      toast({ type: 'success', title: id ? 'Level saved' : 'Level created' });
      if (!id) router.replace(`/${locale}/admin/learning/levels/${String(result.id)}`);
    } catch (reason) {
      setSaveState('failed');
      setError(errorMessage(reason, 'Save failed'));
    }
    }, [id, locale, payload, router, toast]);

  const publish = useCallback(async () => {
    if (!id) return;
    setSaveState('saving');
    setError('');
    try {
      await adminLearningApi.publish(id);
      setSaveState('saved');
      toast({ type: 'success', title: 'Level published' });
      void adminLearningApi.level(id).then(raw => {
        const next = levelFromRaw(raw);
        setDraft(next);
        setBaseline(JSON.stringify(levelPayload(next)));
      });
    } catch (reason) {
      setSaveState('failed');
      setError(errorMessage(reason, 'Publish failed'));
    }
  }, [id, toast]);

  if (loading) {
    return (
      <div className="grid min-h-[60vh] place-items-center">
        <Loader2 className="animate-spin text-violet-400" />
      </div>
    );
  }

  const tabItems = tabs.map(x => ({
    id: x,
    label: x[0].toUpperCase() + x.slice(1),
  }));

  const sidebar = (
    <nav className="sticky top-28 space-y-1 rounded-xl border border-white/10 bg-[#0b101a] p-2">
      {tabItems.map(item => (
        <button key={item.id} onClick={() => setTab(item.id)}
          className={`flex w-full items-center gap-2 rounded-lg px-3 py-2.5 text-left text-sm ${tab === item.id ? 'bg-violet-500/15 text-violet-200' : 'text-slate-400 hover:bg-white/5'}`}>
          <span className="flex-1">{item.label}</span>
        </button>
      ))}
    </nav>
  );

  const header = (
    <AdminEditorHeader
      title={draft.translations[locale].title || 'Level Editor'}
      code={draft.id ? `Level ${draft.levelNumber}` : 'New Level'}
      status={draft.status}
      revision={draft.version}
      dirtyState={state}
      breadcrumbs={[
        { label: 'Levels', href: `/${locale}/admin/learning/levels` },
        { label: draft.translations[locale].title || `Level ${draft.levelNumber}` },
      ]}
      actions={
        <AdminEditorActions
          busy={state === 'saving'}
          canPublish={Boolean(id)}
          onPreview={() => setTab('preview')}
          onValidate={() => setTab('validation')}
          onSave={() => void save()}
          onPublish={id ? () => void publish() : undefined}
        />
      }
    />
  );

  return (
    <AdminEditorShell
      header={header}
      sidebar={sidebar}
      tabs={
        <div className="xl:hidden">
          <AdminEditorTabs tabs={tabItems} active={tab} onChange={x => setTab(x as Tab)} />
        </div>
      }
    >
      {error && (
        <div role="alert" className="rounded-xl border border-rose-500/20 bg-rose-500/10 p-4 text-sm text-rose-200">
          {error}
          <button onClick={() => void save()} className="mt-2 underline">Retry</button>
        </div>
      )}

      {tab === 'details' && (
        <LevelDetails draft={draft} onChange={change} />
      )}

      {tab === 'localization' && (
        <LevelLocalization draft={draft} onChange={change} />
      )}

      {tab === 'scenario' && (
        <LevelScenarioSection draft={draft} onChange={change} />
      )}

      {tab === 'preview' && (
        <LevelPreview draft={draft} />
      )}

      {tab === 'validation' && (
        <LevelValidation draft={draft} id={id} />
      )}
    </AdminEditorShell>
  );
}

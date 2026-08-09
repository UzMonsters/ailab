'use client';

import { useEffect, useState } from 'react';
import { Check, X } from 'lucide-react';

type Props = { storageKey: string; locale: string; kind: 'dashboard' | 'sandbox' };

export default function OnboardingHint({ storageKey, locale, kind }: Props) {
  const [open, setOpen] = useState(false);
  useEffect(() => { const timer = window.setTimeout(() => setOpen(localStorage.getItem(storageKey) !== 'done'), 0); return () => window.clearTimeout(timer); }, [storageKey]);
  if (!open) return null;
  const sandbox = kind === 'sandbox';
  const title = sandbox ? (locale === 'ru' ? 'Быстрый старт Sandbox' : 'Sandbox quick start') : (locale === 'ru' ? 'Добро пожаловать в лабораторию' : 'Welcome to your laboratory');
  const steps = sandbox ? (locale === 'ru' ? ['Добавьте колбу и небольшую горелку из Equipment.', 'Перетащите горелку под колбу — появится Attached.', 'Выберите Materials, добавьте воду и используйте Heat / Stir / Pour.', 'Для соединений нажмите Connect, выберите два порта и тип связи.'] : ['Add a flask and the small burner from Equipment.', 'Drag the burner under the flask until Attached appears.', 'Choose Materials, add water, then use Heat / Stir / Pour.', 'Use Connect to choose two ports and a connection type.']) : (locale === 'ru' ? ['Создайте workspace или откройте шаблон эксперимента.', 'Откройте workspace, чтобы перейти в Sandbox.', 'Используйте sidebar для Recent, Favorites и Templates.'] : ['Create a workspace or open an experiment template.', 'Open a workspace to enter the Sandbox.', 'Use the sidebar for Recent, Favorites and Templates.']);
  const close = () => { localStorage.setItem(storageKey, 'done'); setOpen(false); };
  return <div className="fixed inset-0 z-[120] grid place-items-center bg-black/45 p-4" role="dialog" aria-modal="true" aria-labelledby={`${storageKey}-title`}><div className="w-full max-w-md rounded-2xl border border-[var(--border)] bg-[var(--card)] p-6 shadow-2xl"><div className="flex items-start justify-between"><div><p className="text-xs font-semibold uppercase tracking-wider text-[var(--primary)]">AI Laboratory</p><h2 id={`${storageKey}-title`} className="mt-1 text-xl font-bold">{title}</h2></div><button className="touch-target rounded-lg" onClick={close} aria-label="Close"><X size={18} /></button></div><ol className="mt-5 space-y-3">{steps.map((step, index) => <li key={step} className="flex gap-3 text-sm"><span className="grid h-6 w-6 shrink-0 place-items-center rounded-full bg-[var(--primary)] text-white">{index + 1}</span><span>{step}</span></li>)}</ol><button className="mt-6 min-h-11 w-full rounded-xl bg-[var(--primary)] font-semibold text-white" onClick={close}><Check size={16} className="mr-2 inline" />{locale === 'ru' ? 'Понятно' : 'Got it'}</button></div></div>;
}

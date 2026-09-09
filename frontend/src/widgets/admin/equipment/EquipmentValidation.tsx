'use client';

import { AlertTriangle, CheckCircle2, CircleX } from 'lucide-react';
import { useTranslations } from 'next-intl';
import { FormSection } from '@/widgets/admin/editor';
import type { EquipmentValidationIssue } from './equipmentEditor.types';

export function EquipmentValidation({ issues, onOpenIssue }: { issues: EquipmentValidationIssue[]; onOpenIssue: (issue: EquipmentValidationIssue) => void }) {
  const t = useTranslations('admin.equipmentEditor');
  const errors = issues.filter(issue => issue.severity === 'error');
  return <FormSection title={t('validation')} description={t('validationHelp')}>{issues.length === 0 ? <div className="flex items-center gap-3 rounded-xl border border-emerald-400/20 bg-emerald-500/10 p-5 text-emerald-200"><CheckCircle2/><div><strong>{t('readyToPublish')}</strong><p className="mt-1 text-xs text-emerald-300/70">{t('noValidationIssues')}</p></div></div> : <div className="space-y-3"><div className={`rounded-xl border p-4 text-sm ${errors.length ? 'border-rose-400/20 bg-rose-500/10 text-rose-200' : 'border-amber-400/20 bg-amber-500/10 text-amber-200'}`}>{t('validationSummary', { errors: errors.length, warnings: issues.length - errors.length })}</div>{issues.map(issue => <button type="button" key={issue.id} onClick={() => onOpenIssue(issue)} className="flex w-full items-center gap-3 rounded-xl border border-white/[.07] p-4 text-left hover:bg-white/[.03]">{issue.severity === 'error' ? <CircleX className="text-rose-300" size={18}/> : <AlertTriangle className="text-amber-300" size={18}/>}<span className="flex-1 text-sm text-slate-200">{issue.message}</span><span className="text-xs uppercase text-violet-300">{issue.tab}</span></button>)}</div>}</FormSection>;
}

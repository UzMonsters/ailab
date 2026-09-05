/* eslint-disable @next/next/no-img-element */
'use client';

import { useEffect, useRef, useState } from 'react';
import { ImageIcon, Loader2, Replace, Trash2, Upload } from 'lucide-react';
import { useTranslations } from 'next-intl';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
import { getApiBaseUrl } from '@/shared/api/client';
import { sanitizeSvgMarkup } from '@/shared/lib/sanitizeSvg';
import { FormSection } from '@/widgets/admin/editor';
import type { EquipmentAsset, EquipmentMedia as EquipmentMediaValue } from './equipmentEditor.types';

type Slot = keyof EquipmentMediaValue;
const slots: Array<{ key: Slot; accept: string; kind: 'IMAGE' | 'SVG' }> = [
  { key: 'thumbnail', accept: 'image/png,image/jpeg,image/webp,image/svg+xml', kind: 'IMAGE' },
  { key: 'imageLight', accept: 'image/png,image/jpeg,image/webp', kind: 'IMAGE' },
  { key: 'imageDark', accept: 'image/png,image/jpeg,image/webp', kind: 'IMAGE' },
  { key: 'svgLight', accept: 'image/svg+xml', kind: 'SVG' },
  { key: 'svgDark', accept: 'image/svg+xml', kind: 'SVG' },
];
const dimensions = (url: string) => new Promise<{ width?: number; height?: number }>(resolve => { const image = new Image(); image.onload = () => resolve({ width: image.naturalWidth, height: image.naturalHeight }); image.onerror = () => resolve({}); image.src = url; });
const firstUpload = (response: Record<string, unknown>) => Array.isArray(response.uploads) ? response.uploads[0] as Record<string, unknown> | undefined : undefined;

export function EquipmentMedia({ value, onChange, onError }: { value: EquipmentMediaValue; onChange: (value: EquipmentMediaValue) => void; onError: (message: string) => void }) {
  const t = useTranslations('admin.equipmentEditor');
  const [busy, setBusy] = useState<Slot | null>(null);
  const [previews, setPreviews] = useState<Partial<Record<Slot, string>>>({});
  const previewUrls = useRef<Partial<Record<Slot, string>>>({});
  useEffect(() => () => { Object.values(previewUrls.current).forEach(url => URL.revokeObjectURL(url)); }, []);

  const upload = async (slot: Slot, file: File, kind: 'IMAGE' | 'SVG') => {
    if (file.size > 5 * 1024 * 1024) { onError(t('fileTooLarge')); return; }
    setBusy(slot);
    try {
      const effectiveKind = kind === 'SVG' || file.type === 'image/svg+xml' || file.name.toLowerCase().endsWith('.svg') ? 'SVG' : 'IMAGE';
      let blob: Blob = file;
      if (effectiveKind === 'SVG') { const safe = sanitizeSvgMarkup(await file.text()); if (!safe) throw new Error(t('unsafeSvg')); blob = new Blob([safe], { type: 'image/svg+xml' }); }
      const localUrl = URL.createObjectURL(blob);
      const previousUrl = previewUrls.current[slot];
      if (previousUrl) URL.revokeObjectURL(previousUrl);
      previewUrls.current[slot] = localUrl;
      setPreviews(current => ({ ...current, [slot]: localUrl }));
      const measured = await dimensions(localUrl);
      const response = await adminPlatformApi.assets.uploadUrls({ files: [{ filename: file.name, contentType: blob.type || file.type, sizeBytes: blob.size, kind: effectiveKind }] });
      const target = firstUpload(response);
      if (!target?.uploadUrl || !target.assetId) throw new Error(t('uploadTargetMissing'));
      const uploadUrl = String(target.uploadUrl).startsWith('/') ? `${getApiBaseUrl()}${target.uploadUrl}` : String(target.uploadUrl);
      const uploaded = await fetch(uploadUrl, { method: 'PUT', headers: { 'Content-Type': blob.type || file.type }, body: blob });
      if (!uploaded.ok) throw new Error(`${t('uploadFailed')} (${uploaded.status})`);
      const completed = await adminPlatformApi.assets.complete(String(target.assetId), { width: measured.width, height: measured.height });
      const asset: EquipmentAsset = { assetId: String(target.assetId), url: String(completed.downloadUrl ?? target.downloadUrl ?? ''), filename: file.name, mimeType: blob.type || file.type, sizeBytes: blob.size, ...measured };
      onChange({ ...value, [slot]: asset });
    } catch (reason) { onError(reason instanceof Error ? reason.message : t('uploadFailed')); } finally { setBusy(null); }
  };

  return <FormSection title={t('media')} description={t('mediaHelp')}><div className="grid gap-4 lg:grid-cols-2 2xl:grid-cols-3">{slots.map(slot => {
    const asset = value[slot.key]; const preview = previews[slot.key] ?? asset?.url;
    return <article key={slot.key} onDragOver={event => event.preventDefault()} onDrop={event => { event.preventDefault(); const file = event.dataTransfer.files[0]; if (file) void upload(slot.key, file, slot.kind); }} className="overflow-hidden rounded-xl border border-white/[.08] bg-white/[.02]">
      <div className="flex items-center justify-between border-b border-white/[.06] px-4 py-3"><div><p className="text-xs font-bold uppercase tracking-wider text-violet-300">{t(`mediaSlots.${slot.key}`)}</p><p className="mt-1 text-[11px] text-slate-500">{slot.kind}</p></div>{asset && <button type="button" onClick={() => onChange({ ...value, [slot.key]: undefined })} aria-label={t('remove')} className="text-rose-300"><Trash2 size={15}/></button>}</div>
      <div className="grid aspect-[4/3] place-items-center bg-black/20 p-4">{busy === slot.key ? <Loader2 className="animate-spin text-violet-300"/> : preview ? <img src={preview} alt="" className="max-h-full max-w-full object-contain"/> : <ImageIcon className="text-slate-700" size={36}/>}</div>
      <div className="p-4"><p className="truncate text-xs text-slate-400">{asset ? `${asset.filename} · ${(asset.sizeBytes / 1024).toFixed(1)} KB${asset.width ? ` · ${asset.width}×${asset.height}` : ''}` : t('dropFile')}</p><label className="mt-3 flex cursor-pointer items-center justify-center gap-2 rounded-lg border border-white/10 px-3 py-2 text-sm font-semibold text-slate-200 hover:bg-white/[.05]">{asset ? <Replace size={14}/> : <Upload size={14}/>} {asset ? t('replace') : t('upload')}<input type="file" accept={slot.accept} className="sr-only" onChange={event => { const file = event.target.files?.[0]; if (file) void upload(slot.key, file, slot.kind); event.target.value = ''; }}/></label></div>
    </article>;
  })}</div></FormSection>;
}

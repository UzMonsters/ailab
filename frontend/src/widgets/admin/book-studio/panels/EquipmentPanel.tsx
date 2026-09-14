'use client';

import { useState, useEffect } from 'react';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
import { useBookStudioStore, labelEntity } from '../store/useBookStudioStore';
import { useAssetDragDrop } from '../dnd/useAssetDragDrop';
import { Search, FlaskConical } from 'lucide-react';
import type { JsonObject } from '@/shared/api/contracts/platform';

export function EquipmentPanel() {
  const [search, setSearch] = useState('');
  const [items, setItems] = useState<JsonObject[]>([]);
  const [loading, setLoading] = useState(false);
  const { handleDragStart } = useAssetDragDrop();

  useEffect(() => {
    const timer = setTimeout(async () => {
      setLoading(true);
      try {
        const res = await adminPlatformApi.equipment.list({ size: 50, ...(search ? { q: search } : {}) });
        setItems(res.items || []);
      } catch { setItems([]); }
      setLoading(false);
    }, 300);
    return () => clearTimeout(timer);
  }, [search]);

  return (
    <div className="space-y-3">
      <div className="relative">
        <Search size={14} className="absolute left-2 top-1/2 -translate-y-1/2 text-slate-500" />
        <input
          value={search}
          onChange={e => setSearch(e.target.value)}
          placeholder="Search equipment..."
          className="w-full rounded border border-white/10 bg-[#080c14] pl-7 pr-2 py-1.5 text-xs text-white outline-none focus:border-violet-500"
        />
      </div>
      {loading && <div className="space-y-2">{[1, 2, 3].map(i => <div key={i} className="h-16 animate-pulse rounded-lg bg-white/5" />)}</div>}
      {!loading && items.length === 0 && (
        <div className="py-8 text-center text-xs text-slate-500">
          <FlaskConical size={24} className="mx-auto mb-2 text-slate-600" />
          No equipment available
        </div>
      )}
      <div className="space-y-1.5">
        {items.map(item => (
          <div
            key={String(item.id)}
            draggable
            onDragStart={e => handleDragStart(e, 'EQUIPMENT_REFERENCE', { equipmentId: item.id, rendererKey: item.rendererKey || item.code || 'beaker' })}
            className="flex items-center gap-2 rounded-lg border border-white/5 bg-white/[.03] p-2 cursor-grab hover:border-violet-500/40 transition-colors"
          >
            <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded bg-violet-600/10">
              <FlaskConical size={14} className="text-violet-400" />
            </div>
            <div className="min-w-0">
              <p className="text-xs font-medium text-slate-200 truncate">{labelEntity(item)}</p>
              <p className="text-[10px] text-slate-500">{String(item.code || item.kind || '')}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

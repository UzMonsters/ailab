'use client';

import { useBookStudioStore, labelEntity, type Block } from '../store/useBookStudioStore';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
import { useState, useEffect } from 'react';
import type { JsonObject } from '@/shared/api/contracts/platform';

export function ExperimentProperties({ block }: { block: Block }) {
  const { patchBlock, scenarios } = useBookStudioStore();
  return (
    <div className="space-y-3">
      <p className="text-[10px] text-slate-500 uppercase tracking-wider">Experiment</p>
      <div>
        <label className="text-[10px] text-slate-500">Scenario</label>
        <select value={block.scenarioId || ''} onChange={e => patchBlock(block.id, { scenarioId: e.target.value })}
          className="w-full rounded border border-white/10 bg-[#080c14] px-2 py-1 text-xs text-white outline-none">
          <option value="">Select scenario</option>
          {scenarios.map(s => (
            <option key={String(s.id)} value={String(s.id)}>{labelEntity(s)}</option>
          ))}
        </select>
      </div>
      <label className="flex items-center gap-2 text-[10px] text-slate-500">
        <input type="checkbox" checked={block.showTitle !== false} onChange={e => patchBlock(block.id, { showTitle: e.target.checked })} className="accent-violet-500" />
        Show title
      </label>
      <label className="flex items-center gap-2 text-[10px] text-slate-500">
        <input type="checkbox" checked={block.showDescription !== false} onChange={e => patchBlock(block.id, { showDescription: e.target.checked })} className="accent-violet-500" />
        Show description
      </label>
    </div>
  );
}

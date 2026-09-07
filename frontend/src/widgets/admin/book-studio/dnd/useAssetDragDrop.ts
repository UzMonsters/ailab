'use client';

import { useCallback } from 'react';
import { useBookStudioStore, type Block } from '../store/useBookStudioStore';
import type { JsonObject } from '@/shared/api/contracts/platform';
import type { BookBlockKind } from '../BookPageRenderer';

export function useAssetDragDrop() {
  const { addBlock } = useBookStudioStore();

  const handleDrop = useCallback((e: React.DragEvent, kind: string, data?: JsonObject) => {
    e.preventDefault();
    const rect = (e.target as HTMLElement).getBoundingClientRect();
    const x = (e.clientX - rect.left);
    const y = (e.clientY - rect.top);

    const overrides: Partial<Block> = { x: Math.max(0, x - 90), y: Math.max(0, y - 90) };

    if (data) {
      if (data.equipmentId) overrides.equipmentId = String(data.equipmentId);
      if (data.materialId) overrides.materialId = String(data.materialId);
      if (data.reactionId) overrides.reactionId = String(data.reactionId);
      if (data.src) overrides.src = String(data.src);
      if (data.assetId) overrides.assetId = String(data.assetId);
      if (data.formula) overrides.formula = String(data.formula);
      if (data.scenarioId) overrides.scenarioId = String(data.scenarioId);
    }

    addBlock(kind as BookBlockKind, overrides);
  }, [addBlock]);

  const handleDragStart = useCallback((e: React.DragEvent, kind: string, data?: JsonObject) => {
    e.dataTransfer.setData('application/json', JSON.stringify({ kind, data }));
    e.dataTransfer.effectAllowed = 'copy';
  }, []);

  return { handleDrop, handleDragStart };
}

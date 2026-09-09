import React from 'react';
import { useTranslations } from 'next-intl';
import type { Item } from './types';

export interface ContextMenuProps {
  x: number;
  y: number;
  item: Item;
  onClose: () => void;
  onAction: (action: string, itemId: string) => void;
}

export function ContextMenu({ x, y, item, onClose, onAction }: ContextMenuProps) {
  const ts = useTranslations("sandbox");
  React.useEffect(() => {
    const handleClickOutside = () => onClose();
    document.addEventListener('pointerdown', handleClickOutside);
    return () => document.removeEventListener('pointerdown', handleClickOutside);
  }, [onClose]);

  return (
    <div
      className="fixed z-50 flex w-40 flex-col overflow-hidden rounded-md border border-[var(--border)] bg-[var(--card)] py-1 shadow-lg"
      style={{ left: x, top: y }}
      onPointerDown={(e) => e.stopPropagation()}
      onContextMenu={(e) => { e.preventDefault(); e.stopPropagation(); }}
    >
      <button
        onClick={() => { onAction('delete', item.id); onClose(); }}
        className="px-3 py-1.5 text-left text-sm text-red-500 hover:bg-red-500/10"
      >
        {ts("contextMenu.deleteEquipment")}
      </button>
    </div>
  );
}

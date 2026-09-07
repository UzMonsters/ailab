'use client';

import { X } from 'lucide-react';
import { useBookStudioStore } from '../store/useBookStudioStore';
import { useTranslations } from 'next-intl';
import { EquipmentPanel } from '../panels/EquipmentPanel';
import { MaterialsPanel } from '../panels/MaterialsPanel';
import { ReactionsPanel } from '../panels/ReactionsPanel';
import { ExperimentsPanel } from '../panels/ExperimentsPanel';
import { ScientistsPanel } from '../panels/ScientistsPanel';
import { TextPanel } from '../panels/TextPanel';
import { GraphicsPanel } from '../panels/GraphicsPanel';
import { LayoutsPanel } from '../panels/LayoutsPanel';
import { FormulaPanel } from '../panels/FormulaPanel';
import { DataPanel } from '../panels/DataPanel';
import { MediaPanel } from '../panels/MediaPanel';
import { MyFilesPanel } from '../panels/MyFilesPanel';
import { ThemePanel } from '../panels/ThemePanel';

const panelComponents: Record<string, React.FC> = {
  layouts: LayoutsPanel,
  text: TextPanel,
  graphics: GraphicsPanel,
  scientists: ScientistsPanel,
  equipment: EquipmentPanel,
  materials: MaterialsPanel,
  reactions: ReactionsPanel,
  experiments: ExperimentsPanel,
  formulas: FormulaPanel,
  data: DataPanel,
  media: MediaPanel,
  files: MyFilesPanel,
  theme: ThemePanel,
};

export function BookStudioAssetPanel() {
  const t = useTranslations('bookStudio');
  const { activeLeftTab, setActiveLeftTab } = useBookStudioStore();

  if (!activeLeftTab) return null;

  const Panel = panelComponents[activeLeftTab];
  const title = t(activeLeftTab as any) || activeLeftTab;

  return (
    <aside
      className="flex flex-col border-r border-white/10 bg-[#0c121e] overflow-hidden"
      style={{ width: 280, minWidth: 280 }}
    >
      <div className="flex items-center justify-between border-b border-white/10 px-3 py-2">
        <h3 className="text-xs font-semibold uppercase tracking-wider text-slate-400">{title}</h3>
        <button
          onClick={() => setActiveLeftTab(null)}
          className="flex h-6 w-6 items-center justify-center rounded hover:bg-white/5 text-slate-400 hover:text-white transition-colors"
          aria-label="Close panel"
        >
          <X size={14} />
        </button>
      </div>
      <div className="flex-1 overflow-y-auto p-3">
        {Panel ? <Panel /> : <p className="text-xs text-slate-500">Panel not available</p>}
      </div>
    </aside>
  );
}

'use client';

import { Settings, Layers, FileText } from 'lucide-react';
import { useBookStudioStore, type InspectorTab } from '../store/useBookStudioStore';
import { useTranslations } from 'next-intl';
import { PropertiesPanel } from '../inspector/PropertiesPanel';
import { LayersPanel } from '../inspector/LayersPanel';
import { PagesPanel } from '../inspector/PagesPanel';

const tabs: { id: InspectorTab; label: string; icon: React.ElementType }[] = [
  { id: 'properties', label: 'Properties', icon: Settings },
  { id: 'layers', label: 'Layers', icon: Layers },
  { id: 'pages', label: 'Pages', icon: FileText },
];

export function BookStudioInspector() {
  const t = useTranslations('bookStudio');
  const { inspectorTab, setInspectorTab, rightPanelOpen, setRightPanelOpen } = useBookStudioStore();

  if (!rightPanelOpen) {
    return (
      <button
        onClick={() => setRightPanelOpen(true)}
        className="flex h-8 w-8 items-center justify-center border-l border-white/10 bg-[#0c121e] text-slate-400 hover:text-white transition-colors"
        aria-label="Open inspector"
      >
        <Settings size={16} />
      </button>
    );
  }

  return (
    <aside
      className="flex flex-col border-l border-white/10 bg-[#0c121e] overflow-hidden"
      style={{ width: 300, minWidth: 300 }}
    >
      <div className="flex border-b border-white/10">
        {tabs.map(tab => {
          const Icon = tab.icon;
          return (
            <button
              key={tab.id}
              onClick={() => setInspectorTab(tab.id)}
              className={`flex flex-1 items-center justify-center gap-1.5 py-2 text-xs font-medium transition-colors ${
                inspectorTab === tab.id
                  ? 'border-b-2 border-violet-500 text-white'
                  : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <Icon size={14} />
              <span>{t(tab.id as any) || tab.label}</span>
            </button>
          );
        })}
      </div>
      <div className="flex-1 overflow-y-auto p-3">
        {inspectorTab === 'properties' && <PropertiesPanel />}
        {inspectorTab === 'layers' && <LayersPanel />}
        {inspectorTab === 'pages' && <PagesPanel />}
      </div>
    </aside>
  );
}

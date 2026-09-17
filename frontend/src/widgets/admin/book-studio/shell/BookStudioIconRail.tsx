'use client';

import {
  LayoutGrid, Type, Shapes, FlaskConical, Atom, Microscope,
  UserRound, Sigma, ChartNoAxesCombined, Image, FolderOpen, Palette,
  Sparkles,
} from 'lucide-react';
import { useBookStudioStore, type EditorTab } from '../store/useBookStudioStore';
import { useTranslations } from 'next-intl';

type RailItem = { id: EditorTab; label: string; icon: React.ElementType };

const railItems: RailItem[] = [
  { id: 'layouts', label: 'Layouts', icon: LayoutGrid },
  { id: 'text', label: 'Text', icon: Type },
  { id: 'graphics', label: 'Graphics', icon: Shapes },
  { id: 'equipment', label: 'Equipment', icon: FlaskConical },
  { id: 'materials', label: 'Materials', icon: Atom },
  { id: 'reactions', label: 'Reactions', icon: Sparkles },
  { id: 'experiments', label: 'Experiments', icon: Microscope },
  { id: 'formulas', label: 'Formulas', icon: Sigma },
  { id: 'data', label: 'Data', icon: ChartNoAxesCombined },
  { id: 'media', label: 'Media', icon: Image },
  { id: 'files', label: 'My Files', icon: FolderOpen },
  { id: 'theme', label: 'Theme', icon: Palette },
];

export function BookStudioIconRail() {
  const t = useTranslations('bookStudio');
  const { activeLeftTab, setActiveLeftTab } = useBookStudioStore();

  return (
    <nav
      className="flex flex-col items-center gap-0.5 border-r border-white/10 bg-[#0c121e] py-2 overflow-y-auto"
      style={{ width: 52, minWidth: 52 }}
      aria-label="Element library"
    >
      {railItems.map(item => {
        const Icon = item.icon;
        const isActive = activeLeftTab === item.id;
        return (
          <button
            key={item.id}
            onClick={() => setActiveLeftTab(item.id)}
            title={t(item.id as any) || item.label}
            className={`flex h-9 w-9 items-center justify-center rounded-lg transition-colors ${
              isActive
                ? 'bg-violet-600/25 text-violet-300'
                : 'text-slate-400 hover:bg-white/5 hover:text-slate-200'
            }`}
            aria-label={t(item.id as any) || item.label}
            aria-pressed={isActive}
          >
            <Icon size={18} />
          </button>
        );
      })}
    </nav>
  );
}

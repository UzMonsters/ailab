'use client';

import { useBookStudioStore } from '../store/useBookStudioStore';

const paperStyles = [
  { name: 'Cream', bg: '#fff9e9' },
  { name: 'White', bg: '#ffffff' },
  { name: 'Graph', bg: '#f0f4f8' },
];

const themes = [
  { name: 'Light', mode: 'light' },
  { name: 'Dark', mode: 'dark' },
];

export function ThemePanel() {
  const { book } = useBookStudioStore();
  const settings = (book?.settings as any) || {};

  const updateSettings = (updates: any) => {
    useBookStudioStore.setState(s => {
      if (!s.book) return s;
      return {
        book: {
          ...s.book,
          settings: { ...((s.book.settings as any) || {}), ...updates }
        },
        dirty: true,
      };
    });
  };

  return (
    <div className="space-y-4">
      <div>
        <p className="text-[10px] text-slate-500 uppercase tracking-wider mb-2">Book Theme</p>
        <div className="grid grid-cols-2 gap-1.5">
          {themes.map((t, i) => (
            <button
              key={i}
              onClick={() => updateSettings({ theme: t.mode })}
              className={`flex flex-col items-center gap-1 rounded-lg border p-2 transition-colors ${settings.theme === t.mode ? 'border-violet-500 bg-violet-500/10' : 'border-white/5 bg-white/[.03] hover:border-violet-500/40'}`}
            >
              <span className="text-xs text-slate-300">{t.name}</span>
            </button>
          ))}
        </div>
      </div>
      <div>
        <p className="text-[10px] text-slate-500 uppercase tracking-wider mb-2">Paper Style</p>
        <div className="grid grid-cols-3 gap-1.5">
          {paperStyles.map((p, i) => (
            <button
              key={i}
              onClick={() => updateSettings({ backgroundColor: p.bg })}
              className={`flex flex-col items-center gap-1 rounded-lg border p-2 transition-colors ${settings.backgroundColor === p.bg ? 'border-violet-500 bg-violet-500/10' : 'border-white/5 bg-white/[.03] hover:border-violet-500/40'}`}
            >
              <div className="h-8 w-full rounded border border-white/10" style={{ background: p.bg }} />
              <span className="text-[10px] text-slate-400">{p.name}</span>
            </button>
          ))}
        </div>
      </div>
      <div>
        <p className="text-[10px] text-slate-500 uppercase tracking-wider mb-2">Colors</p>
        <div className="space-y-2">
          {['Primary', 'Secondary', 'Accent', 'Text'].map((label, i) => (
            <label key={i} className="flex items-center justify-between text-xs text-slate-400">
              <span>{label}</span>
              <input type="color" className="h-5 w-8 rounded border border-white/10" defaultValue={['#8b5cf6', '#22d3ee', '#f59e0b', '#1e293b'][i]} />
            </label>
          ))}
        </div>
      </div>
    </div>
  );
}

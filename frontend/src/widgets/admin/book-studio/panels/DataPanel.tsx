'use client';

import { useBookStudioStore } from '../store/useBookStudioStore';
import { BarChart3, PieChart, TrendingUp, Activity } from 'lucide-react';

const widgets = [
  { label: 'Progress ring', icon: Activity, widgetType: 'progress-ring', w: 150, h: 150 },
  { label: 'Percentage donut', icon: PieChart, widgetType: 'percentage', w: 160, h: 160 },
  { label: 'Bar chart', icon: BarChart3, widgetType: 'bar', w: 300, h: 150 },
  { label: 'Value card', icon: TrendingUp, widgetType: 'value-card', w: 200, h: 120 },
];

export function DataPanel() {
  const addBlock = useBookStudioStore(s => s.addBlock);

  return (
    <div className="space-y-3">
      <p className="text-[10px] text-slate-500 uppercase tracking-wider">Data widgets</p>
      <div className="space-y-1.5">
        {widgets.map((w, i) => {
          const Icon = w.icon;
          return (
            <button
              key={i}
              onClick={() => addBlock('DATA_WIDGET', { widgetType: w.widgetType, w: w.w, h: w.h, widgetValue: 75, widgetLabel: w.label })}
              className="flex w-full items-center gap-2 rounded-lg border border-white/5 bg-white/[.03] p-2 text-left hover:border-violet-500/40 transition-colors"
            >
              <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded bg-blue-600/10">
                <Icon size={14} className="text-blue-400" />
              </div>
              <span className="text-xs text-slate-300">{w.label}</span>
            </button>
          );
        })}
      </div>
    </div>
  );
}

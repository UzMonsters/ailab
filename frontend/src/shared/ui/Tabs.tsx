'use client';
import { useState } from 'react';
import { cn } from '@/shared/lib/utils';

interface Tab {
  key: string;
  label: string;
  icon?: React.ReactNode;
}

interface TabsProps {
  tabs: Tab[];
  activeTab: string;
  onChange: (tab: string) => void;
  className?: string;
}

export default function Tabs({ tabs, activeTab, onChange, className }: TabsProps) {
  return (
    <div className={cn('flex gap-1 p-1 bg-[var(--input)] rounded-[var(--radius-md)] border border-[var(--border)] overflow-x-auto', className)}>
      {tabs.map((tab) => (
        <button
          key={tab.key}
          onClick={() => onChange(tab.key)}
          className={cn(
            'px-4 py-2.5 rounded-[var(--radius-sm)] text-sm font-medium whitespace-nowrap transition-all',
            activeTab === tab.key
              ? 'bg-[#8b5cf6]/15 text-[#8b5cf6] border border-[#8b5cf6]/30'
              : 'text-[var(--muted-foreground)] hover:text-[var(--foreground)]'
          )}
        >
          {tab.icon && <span className="mr-2">{tab.icon}</span>}
          {tab.label}
        </button>
      ))}
    </div>
  );
}

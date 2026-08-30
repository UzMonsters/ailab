import React from 'react';

interface AdminPageHeaderProps {
  title: string;
  description?: string;
  counters?: { label: string; value: string | number }[];
  filters?: React.ReactNode;
  actions?: React.ReactNode;
}

export default function AdminPageHeader({ title, description, counters, filters, actions }: AdminPageHeaderProps) {
  return (
    <div className="mb-8 border-b border-[rgba(255,255,255,0.07)] pb-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-wide">{title}</h1>
          {description && <p className="text-[#8490a3] text-sm mt-1">{description}</p>}
        </div>
        
        {actions && (
          <div className="flex items-center gap-2">
            {actions}
          </div>
        )}
      </div>

      {(counters || filters) && (
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mt-6">
          {counters && counters.length > 0 && (
            <div className="flex items-center gap-6">
              {counters.map((c, i) => (
                <div key={i}>
                  <span className="text-white font-bold text-lg">{c.value}</span>
                  <span className="text-[#8490a3] text-xs ml-2 uppercase tracking-wider">{c.label}</span>
                </div>
              ))}
            </div>
          )}
          
          {filters && (
            <div className="flex items-center gap-2 flex-wrap">
              {filters}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

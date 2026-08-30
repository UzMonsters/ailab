'use client';

import React from 'react';
import { useToastStore } from '@/stores/toast.store';
import { X, CheckCircle2, AlertCircle, Info, AlertTriangle } from 'lucide-react';

export default function ToastProvider() {
  const { toasts, removeToast } = useToastStore();

  return (
    <div className="fixed bottom-4 right-4 z-[9999] flex flex-col gap-2 pointer-events-none">
      {toasts.map(toast => {
        let Icon = Info;
        let colorClass = 'text-blue-400 border-blue-400/20 bg-blue-400/10';
        
        if (toast.type === 'success') {
          Icon = CheckCircle2;
          colorClass = 'text-emerald-400 border-emerald-400/20 bg-emerald-400/10';
        } else if (toast.type === 'error') {
          Icon = AlertCircle;
          colorClass = 'text-red-400 border-red-400/20 bg-red-400/10';
        } else if (toast.type === 'warning') {
          Icon = AlertTriangle;
          colorClass = 'text-amber-400 border-amber-400/20 bg-amber-400/10';
        }

        return (
          <div 
            key={toast.id}
            className={`pointer-events-auto flex items-start gap-3 p-4 rounded-xl border backdrop-blur-md shadow-lg w-80 max-w-full ${colorClass}`}
            style={{ animation: 'slideInUp 0.3s cubic-bezier(0.16, 1, 0.3, 1)' }}
          >
            <Icon size={20} className="shrink-0 mt-0.5" />
            <div className="flex-1">
              <h4 className="text-sm font-semibold text-white">{toast.title}</h4>
              {toast.message && <p className="text-xs text-white/70 mt-1">{toast.message}</p>}
            </div>
            <button 
              onClick={() => removeToast(toast.id)}
              className="text-white/50 hover:text-white transition-colors"
            >
              <X size={16} />
            </button>
          </div>
        );
      })}
      <style dangerouslySetInnerHTML={{__html: `
        @keyframes slideInUp {
          from { transform: translateY(100%); opacity: 0; }
          to { transform: translateY(0); opacity: 1; }
        }
      `}} />
    </div>
  );
}

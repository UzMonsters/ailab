'use client';

import React, { Component, type ReactNode } from 'react';
import { AlertTriangle, RefreshCw } from 'lucide-react';

interface Props {
  children: ReactNode;
  fallback?: ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
}

export class ErrorBoundary extends Component<Props, State> {
  public state: State = {
    hasError: false,
    error: null,
  };

  public static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  public componentDidCatch(error: Error, errorInfo: React.ErrorInfo) {
    console.error('Sandbox Error Boundary caught an exception:', error, errorInfo);
  }

  private handleReset = () => {
    this.setState({ hasError: false, error: null });
    if (typeof window !== 'undefined') {
      window.location.reload();
    }
  };

  public render() {
    if (this.state.hasError) {
      if (this.props.fallback) {
        return this.props.fallback;
      }

      return (
        <div className="flex h-screen w-screen flex-col items-center justify-center bg-[#070b13] p-6 text-center text-white">
          <div className="max-w-md animate-fade-in-up rounded-2xl border border-red-500/30 bg-[#0d1322] p-8 shadow-2xl backdrop-blur-xl">
            <div className="mx-auto mb-4 flex h-14 w-14 items-center justify-center rounded-full border border-red-500/40 bg-red-500/10 text-red-400 shadow-[0_0_20px_rgba(239,68,68,0.3)]">
              <AlertTriangle size={28} />
            </div>
            
            <h2 className="text-lg font-bold text-white">Сбой симуляции Sandbox</h2>
            <p className="mt-2 text-xs text-white/70 leading-relaxed">
              Произошла непредвиденная ошибка в визуальном или физическом движке лаборатории.
            </p>

            {this.state.error && (
              <pre className="mt-4 max-h-32 overflow-x-auto rounded-xl border border-white/10 bg-black/60 p-3 text-[10px] font-mono text-red-300 text-left">
                {this.state.error.message}
              </pre>
            )}

            <button
              type="button"
              onClick={this.handleReset}
              className="mt-6 flex w-full items-center justify-center gap-2 rounded-xl bg-[var(--primary)] py-3 text-xs font-bold text-white shadow-lg transition-all hover:bg-[var(--primary-hover)] active:scale-95"
            >
              <RefreshCw size={14} />
              Перезагрузить Sandbox
            </button>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}

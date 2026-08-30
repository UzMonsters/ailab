'use client';
import { useEffect, useState } from 'react';
import { X } from 'lucide-react';

interface ToastProps {
  message: string;
  type?: 'success' | 'error' | 'info';
  onClose: () => void;
  duration?: number;
}

const colors = {
  // Toasts intentionally keep a dark surface in both themes so they remain
  // readable over the light sandbox canvas and do not look washed out.
  success: 'border-lime-400/70 bg-[#17210f]/95 text-lime-300 shadow-[0_12px_35px_rgba(101,163,13,0.28)]',
  error: 'border-rose-400/70 bg-[#241116]/95 text-rose-300 shadow-[0_12px_35px_rgba(225,29,72,0.24)]',
  info: 'border-violet-400/70 bg-[#171322]/95 text-violet-300 shadow-[0_12px_35px_rgba(124,58,237,0.24)]',
};

export default function Toast({ message, type = 'info', onClose, duration = 4000 }: ToastProps) {
  const [visible, setVisible] = useState(true);
  useEffect(() => {
    const timer = setTimeout(() => { setVisible(false); setTimeout(onClose, 300); }, duration);
    return () => clearTimeout(timer);
  }, [duration, onClose]);
  return (
    <div className={`fixed bottom-6 right-6 z-[200] max-w-sm px-4 py-3 rounded-[var(--radius-md)] border backdrop-blur-xl flex items-center gap-3 transition-all duration-300 ${colors[type]} ${visible ? 'opacity-100 translate-y-0' : 'opacity-0 translate-y-4'}`}>
      <span className="text-sm flex-1">{message}</span>
      <button onClick={() => { setVisible(false); setTimeout(onClose, 300); }} className="opacity-60 hover:opacity-100"><X size={14} /></button>
    </div>
  );
}

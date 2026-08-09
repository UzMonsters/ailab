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
  success: 'border-[#14F195]/30 bg-[#14F195]/10 text-[#14F195]',
  error: 'border-[#F43F5E]/30 bg-[#F43F5E]/10 text-[#F43F5E]',
  info: 'border-[#8b5cf6]/30 bg-[#8b5cf6]/10 text-[#8b5cf6]',
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

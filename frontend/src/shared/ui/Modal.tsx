'use client';
import { useEffect } from 'react';
import { cn } from '@/shared/lib/utils';

interface ModalProps {
  open: boolean;
  onClose: () => void;
  title: string;
  children: React.ReactNode;
  className?: string;
}

export default function Modal({ open, onClose, title, children, className }: ModalProps) {
  useEffect(() => {
    if (open) document.body.style.overflow = 'hidden';
    else document.body.style.overflow = '';
    return () => { document.body.style.overflow = ''; };
  }, [open]);

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
      <div className="absolute inset-0 bg-black/60" onClick={onClose} />
      <div className={cn('relative bg-[var(--card)] backdrop-blur-xl border border-[var(--border)] rounded-[var(--radius-lg)] shadow-[0_30px_60px_rgba(0,0,0,0.6)] w-full max-w-md p-6 animate-[fadeIn_0.3s_ease]', className)}>
        <h2 className="text-xl font-bold mb-6">{title}</h2>
        {children}
      </div>
    </div>
  );
}

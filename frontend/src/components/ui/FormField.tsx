'use client';
import { cn } from '@/lib/utils';

interface FormFieldProps {
  label: string;
  error?: string;
  required?: boolean;
  children: React.ReactNode;
  className?: string;
}

export default function FormField({ label, error, required, children, className }: FormFieldProps) {
  return (
    <div className={cn('flex flex-col gap-1.5', className)}>
      <label className="text-xs font-medium text-[var(--muted-foreground)]">
        {label}{required && <span className="text-[#F43F5E] ml-0.5">*</span>}
      </label>
      {children}
      {error && <p className="text-xs text-[#F43F5E] flex items-center gap-1"><span>⚠</span>{error}</p>}
    </div>
  );
}

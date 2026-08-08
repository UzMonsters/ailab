import { cn } from '@/lib/utils';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  icon?: React.ReactNode;
  error?: string;
}

export default function Input({ icon, error, className, ...props }: InputProps) {
  return (
    <div className="w-full">
      <div className="relative flex items-center">
        {icon && <span className="absolute left-3 text-[var(--muted-foreground)]">{icon}</span>}
        <input
          className={cn(
            'w-full bg-[var(--input)] border border-[var(--border)] rounded-[var(--radius-md)] px-4 py-3 text-sm text-[var(--foreground)] outline-none transition-all focus:border-[var(--ring)] focus:shadow-[0_0_15px_rgba(139,92,246,0.2)] placeholder:text-[var(--muted-foreground)]',
            icon && 'pl-10',
            error && 'border-red-500',
            className
          )}
          {...props}
        />
      </div>
      {error && <p className="text-xs text-red-500 mt-1">{error}</p>}
    </div>
  );
}

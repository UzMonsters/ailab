import { cn } from '@/lib/utils';

interface BadgeProps {
  children: React.ReactNode;
  variant?: 'default' | 'purple' | 'teal' | 'amber' | 'rose';
  className?: string;
}

const variants = {
  default: 'bg-[var(--muted)] text-[var(--muted-foreground)]',
  purple: 'bg-[#8b5cf6]/12 text-[#c0a1ff] border border-[#8b5cf6]/30',
  teal: 'bg-[#14F195]/12 text-[#14F195] border border-[#14F195]/30',
  amber: 'bg-[#F59E0B]/12 text-[#F59E0B] border border-[#F59E0B]/30',
  rose: 'bg-[#F43F5E]/12 text-[#F43F5E] border border-[#F43F5E]/30',
};

export default function Badge({ children, variant = 'default', className }: BadgeProps) {
  return (
    <span className={cn(
      'inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-[10px] font-mono uppercase tracking-wider',
      variants[variant],
      className
    )}>
      {children}
    </span>
  );
}

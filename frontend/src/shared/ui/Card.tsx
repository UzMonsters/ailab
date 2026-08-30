import { cn } from '@/shared/lib/utils';

interface CardProps {
  children: React.ReactNode;
  className?: string;
  hover?: boolean;
}

export default function Card({ children, className, hover }: CardProps) {
  return (
    <div className={cn(
      'border border-[var(--border)] bg-[var(--card)] rounded-[var(--radius-lg)] shadow-[inset_0_1px_0_rgba(255,255,255,0.06)]',
      hover && 'transition-transform duration-300 hover:-translate-y-1',
      className
    )}>
      {children}
    </div>
  );
}

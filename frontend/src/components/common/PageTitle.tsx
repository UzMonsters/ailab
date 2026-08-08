interface PageTitleProps {
  title: string;
  subtitle?: string;
  className?: string;
}

export default function PageTitle({ title, subtitle, className }: PageTitleProps) {
  return (
    <div className={`mb-6 ${className || ''}`}>
      <h1 className="text-2xl font-bold tracking-tight">{title}</h1>
      {subtitle && <p className="text-[var(--muted-foreground)] text-sm mt-1">{subtitle}</p>}
    </div>
  );
}

interface EmptyStateProps {
  icon: React.ReactNode;
  title: string;
  description: string;
  action?: React.ReactNode;
  className?: string;
}

export default function EmptyState({ icon, title, description, action, className }: EmptyStateProps) {
  return (
    <div className={`text-center py-16 ${className || ''}`}>
      <div className="text-5xl mb-4 opacity-30">{icon}</div>
      <h3 className="text-lg font-semibold mb-2">{title}</h3>
      <p className="text-sm text-[var(--muted-foreground)] max-w-sm mx-auto mb-6">{description}</p>
      {action}
    </div>
  );
}

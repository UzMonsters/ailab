import { useTranslations } from 'next-intl';

export default function AdminLabsPage() {
  const t = useTranslations('admin');
  return <div className="p-4 md:p-6"><h1 className="text-2xl font-bold">{t('labsTitle')}</h1><p className="text-[var(--muted-foreground)] mt-2">{t('laboratoriesComingSoon')}</p></div>;
}

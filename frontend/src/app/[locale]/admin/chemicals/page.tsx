import { useTranslations } from 'next-intl';

export default function AdminChemicalsPage() {
  const t = useTranslations('admin');
  return <div className="p-4 md:p-6"><h1 className="text-2xl font-bold">{t('chemicals')}</h1><p className="text-[var(--muted-foreground)] mt-2">{t('chemicalsComingSoon')}</p></div>;
}

import { useTranslations } from 'next-intl';

export default function AdminEquipmentPage() {
  const t = useTranslations('admin');
  return <div className="p-4 md:p-6"><h1 className="text-2xl font-bold">{t('equipment')}</h1><p className="text-[var(--muted-foreground)] mt-2">{t('equipmentComingSoon')}</p></div>;
}

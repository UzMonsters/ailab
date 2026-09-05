'use client';

import { useLocale, useTranslations } from 'next-intl';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
import AdminBackendResourceView from '@/widgets/admin/AdminBackendResourceView';

export default function ReactionsPage() {
  const locale = useLocale();
  const t = useTranslations('admin.navigation');

  return <AdminBackendResourceView
    title={t('reactions')}
    description={t('reactionListDescription')}
    api={adminPlatformApi.chemistry.reactions}
    locale={locale}
    createHref="/admin/science/chemistry/reactions/new"
    detailHref={id => `/admin/science/chemistry/reactions/${id}`}
  />;
}

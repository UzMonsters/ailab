'use client';
import { useLocale } from 'next-intl';
import AdminBackendResourceView from '@/widgets/admin/AdminBackendResourceView';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
export default function Page(){const locale=useLocale();return <AdminBackendResourceView title="Chemical Elements" description="Draft and published versions of the periodic catalog." api={adminPlatformApi.chemistry.elements} locale={locale} createHref="/admin/science/chemistry/elements/new" detailHref={(id)=>`/admin/science/chemistry/elements/${id}`}/>}

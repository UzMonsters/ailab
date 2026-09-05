'use client';
import { useLocale } from 'next-intl';
import AdminBackendResourceView from '@/widgets/admin/AdminBackendResourceView';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
export default function Page(){const locale=useLocale();return <AdminBackendResourceView title="Scenarios" description="Learning scenarios with backend validation and publishing." api={adminPlatformApi.scenarios} locale={locale} createHref="/admin/scenarios/new" detailHref={(id)=>`/admin/scenarios/${id}`}/>}

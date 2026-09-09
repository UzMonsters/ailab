'use client';
import { useLocale } from 'next-intl';
import AdminBackendResourceView from '@/widgets/admin/AdminBackendResourceView';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
export default function Page(){return <AdminBackendResourceView title="Chemical Substances" description="Editorial catalog of compounds and reagents." api={adminPlatformApi.chemistry.substances} locale={useLocale()}/>}

'use client';
import { useLocale } from 'next-intl';
import AdminBackendResourceView from '@/widgets/admin/AdminBackendResourceView';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
const auditApi={list:adminPlatformApi.audit.list};
export default function Page(){return <AdminBackendResourceView title="Audit Log" description="Immutable administrative and laboratory events from the backend." api={auditApi} locale={useLocale()}/>}

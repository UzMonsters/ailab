'use client';
import { useLocale } from 'next-intl';
import AdminBackendResourceView from '@/widgets/admin/AdminBackendResourceView';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
export default function Page(){return <AdminBackendResourceView title="Safety Rules" description="Managed safety-gate rules and version publishing." api={adminPlatformApi.safetyRules} locale={useLocale()} createHref="/admin/safety/new"/>}

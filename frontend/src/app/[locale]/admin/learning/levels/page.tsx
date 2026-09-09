'use client';
import { useLocale } from 'next-intl';
import AdminBackendResourceView from '@/widgets/admin/AdminBackendResourceView';
import { adminLearningApi } from '@/entities/learning/api/learning.api';
const api={list:adminLearningApi.levels,publish:adminLearningApi.publish,validate:(id:string)=>adminLearningApi.validate(id)};
export default function Page(){const locale=useLocale();return <AdminBackendResourceView title="Learning Levels" description="Versioned levels, validation and publishing from the learning backend." api={api} locale={locale} createHref="/admin/learning/levels/new" detailHref={(id)=>`/admin/learning/levels/${id}`}/>}

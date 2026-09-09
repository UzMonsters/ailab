'use client';
import {useParams} from 'next/navigation';import AdminBackendEditor from '@/widgets/admin/AdminBackendEditor';import {adminPlatformApi} from '@/entities/admin/api/platform-admin.api';export default function Page(){const {id}=useParams<{id:string}>();return <AdminBackendEditor title="Chemical Substance" id={id} api={adminPlatformApi.chemistry.substances}/>}

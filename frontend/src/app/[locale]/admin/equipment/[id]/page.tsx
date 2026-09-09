'use client';
import { useParams } from 'next/navigation';
import EquipmentEditor from '@/widgets/admin/equipment/EquipmentEditor';

export default function Page() {
  const params = useParams<{ id: string }>();
  return <EquipmentEditor id={params.id}/>;
}

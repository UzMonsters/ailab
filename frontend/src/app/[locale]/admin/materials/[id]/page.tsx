import MaterialEditor from '@/widgets/admin/material/MaterialEditor';
export default async function Page({ params }: { params: Promise<{ id: string }> }) { const { id } = await params; return <MaterialEditor id={id}/>; }

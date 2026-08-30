import ElementEditorForm from '@/widgets/admin/ElementEditorForm';

export default async function ElementPage({ params }: { params: Promise<{ id: string }> }) { const { id } = await params; return <ElementEditorForm elementId={id} />; }

import EquipmentEditorPage from '../[id]/page';

export default async function NewEquipmentPage({ params }: { params: Promise<{ locale: string }> }) {
  const { locale } = await params;
  return <EquipmentEditorPage params={{ locale, id: 'new' }} />;
}

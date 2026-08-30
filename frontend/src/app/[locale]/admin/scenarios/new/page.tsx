'use client';

import ScenarioEditorPage from '../[id]/page';

export default function NewScenarioPage({ params }: { params: { locale: string } }) {
  return <ScenarioEditorPage params={{ ...params, id: 'new' }} />;
}

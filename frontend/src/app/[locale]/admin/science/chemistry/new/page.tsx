'use client';
import Link from 'next/link';
import { useParams } from 'next/navigation';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';

export default function NewChemistryEntryPage() {
  const { locale } = useParams<{ locale: string }>();
  return <div className="p-6 max-w-4xl"><AdminPageHeader title="Add chemistry entry" description="Create an element, compound, reaction or chemistry lesson." />
    <form className="mt-6 grid gap-5 rounded-xl border border-white/[.08] bg-[#0b101a] p-6" onSubmit={(e) => e.preventDefault()}>
      <div className="grid gap-4 md:grid-cols-2"><label>Type<select className="admin-input" defaultValue="compound"><option value="element">Element</option><option value="compound">Compound</option><option value="reaction">Reaction</option><option value="lesson">Lesson</option></select></label><label>Identifier<input className="admin-input" placeholder="e.g. CuSO4" /></label></div>
      <div className="grid gap-4 md:grid-cols-3"><label>RU name<input className="admin-input" /></label><label>UZ name<input className="admin-input" /></label><label>EN name<input className="admin-input" /></label></div>
      <label>Formula / equation<input className="admin-input" placeholder="Chemical formula or balanced equation" /></label><label>Description<textarea className="admin-input min-h-28" /></label>
      <div className="flex gap-3"><button className="rounded-lg bg-violet-600 px-5 py-2 text-sm font-semibold text-white">Save draft</button><Link href={`/${locale}/admin/science/chemistry`} className="rounded-lg border border-white/10 px-5 py-2 text-sm text-white no-underline">Cancel</Link></div>
    </form></div>;
}

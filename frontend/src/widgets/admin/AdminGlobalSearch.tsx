'use client';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import { Loader2, Search } from 'lucide-react';
import { adminPlatformApi } from '@/entities/admin/api/platform-admin.api';
import { adminLearningApi } from '@/entities/learning/api/learning.api';
import { adminBookApi } from '@/entities/book/api/book.api';
import { workspacesApi } from '@/entities/workspace/api/workspace.api';
import type { JsonObject } from '@/shared/api/contracts/platform';
type Result={id:string;label:string;group:string;href:string};
const rows=(value:{items?:JsonObject[];content?:JsonObject[]})=>value.items??value.content??[];
const label=(item:JsonObject)=>String(item.name??item.title??item.code??item.slug??item.id);
export default function AdminGlobalSearch({locale}:{locale:string}){
  const [query,setQuery]=useState(''); const [results,setResults]=useState<Result[]>([]); const [loading,setLoading]=useState(false); const [open,setOpen]=useState(false);
  useEffect(()=>{if(query.trim().length<2)return;let active=true;const timer=setTimeout(()=>{setLoading(true);void Promise.allSettled([adminPlatformApi.equipment.list({q:query,size:5}),adminPlatformApi.materials.list({q:query,size:5}),adminPlatformApi.scenarios.list({q:query,size:5}),adminLearningApi.levels({q:query,size:5}),adminBookApi.list({q:query,size:5}),workspacesApi.list({search:query,size:5})]).then(values=>{if(!active)return;const mapped:Result[]=[];const add=(items:JsonObject[],group:string,path:string)=>items.forEach(item=>mapped.push({id:String(item.id??item.code),label:label(item),group,href:`/${locale}/admin/${path}/${String(item.id??item.code)}`}));if(values[0].status==='fulfilled')add(rows(values[0].value),'Equipment','equipment');if(values[1].status==='fulfilled')add(rows(values[1].value),'Materials','materials');if(values[2].status==='fulfilled')add(rows(values[2].value),'Scenarios','scenarios');if(values[3].status==='fulfilled')add(rows(values[3].value),'Levels','learning/levels');if(values[4].status==='fulfilled')add(rows(values[4].value),'Books','book');if(values[5].status==='fulfilled')(values[5].value as unknown as JsonObject[]).forEach(item=>mapped.push({id:String(item.id),label:label(item),group:'Workspaces',href:`/${locale}/admin/sharing`}));setResults(mapped);setOpen(true)}).finally(()=>active&&setLoading(false))},250);return()=>{active=false;clearTimeout(timer)}},[locale,query]);
  const visible=open&&query.trim().length>=2; return <div className="relative hidden sm:block"><div className="global-search flex"><Search size={16}/><input value={query} onChange={e=>setQuery(e.target.value)} onFocus={()=>setOpen(true)} type="search" placeholder="Search equipment, materials, scenarios…" aria-label="Global admin search"/>{loading&&<Loader2 size={14} className="animate-spin"/>}</div>{visible&&<div className="absolute left-0 top-full z-50 mt-2 max-h-96 w-[420px] overflow-auto rounded-xl border border-white/10 bg-[#0b101a] p-2 shadow-2xl">{results.length===0&&!loading?<p className="p-4 text-sm text-slate-500">No results found.</p>:results.map(result=><Link key={`${result.group}-${result.id}`} href={result.href} onClick={()=>setOpen(false)} className="flex items-center justify-between rounded-lg px-3 py-2 text-sm hover:bg-white/5"><span>{result.label}</span><span className="text-[10px] uppercase text-violet-300">{result.group}</span></Link>)}</div>}</div>;
}

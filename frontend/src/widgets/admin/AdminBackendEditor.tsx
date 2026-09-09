'use client';
import { useEffect, useState } from 'react';
import { AlertCircle, CheckCircle2, Loader2 } from 'lucide-react';
import type { JsonObject } from '@/shared/api/contracts/platform';
import { errorMessage } from '@/shared/utils/errorMessage';
import { AdminEditorActions, AdminEditorHeader, AdminEditorShell, AdminEditorTabs, FormSection, type EditorSaveState } from './editor';

type EditorApi={get:(id:string)=>Promise<JsonObject>;create:(body:JsonObject)=>Promise<JsonObject>;patch:(id:string,body:JsonObject)=>Promise<JsonObject>;publish?:(id:string,body?:JsonObject)=>Promise<JsonObject>;validate?:(id:string,body?:JsonObject)=>Promise<JsonObject>};
type Path=(string|number)[];
const input='mt-1 w-full rounded-lg border border-white/10 bg-[#070b13] px-3 py-2 text-sm text-white outline-none focus:border-violet-500';
const titleCase=(key:string)=>key.replace(/([A-Z])/g,' $1').replace(/[_-]/g,' ').replace(/^./,c=>c.toUpperCase());
const updateAt=(root:unknown,path:Path,value:unknown):JsonObject=>{const clone=structuredClone(root) as JsonObject;let cursor:JsonObject|unknown[]=clone;path.slice(0,-1).forEach(segment=>{cursor=(cursor as JsonObject)[String(segment)] as JsonObject;});(cursor as JsonObject)[String(path.at(-1))]=value;return clone;};

function FieldTree({value,path,onChange}:{value:unknown;path:Path;onChange:(path:Path,value:unknown)=>void}){
  if(Array.isArray(value)){
    if(value.every(item=>['string','number','boolean'].includes(typeof item))) return <input className={input} value={value.join(', ')} onChange={e=>onChange(path,e.target.value.split(',').map(v=>v.trim()).filter(Boolean))}/>;
    return <div className="space-y-3">{value.map((item,index)=><div key={index} className="rounded-xl border border-white/[.07] bg-black/15 p-3"><p className="mb-2 text-xs font-semibold uppercase text-violet-300">Item {index+1}</p><FieldTree value={item} path={[...path,index]} onChange={onChange}/></div>)}</div>;
  }
  if(value&&typeof value==='object') return <div className="grid gap-4 md:grid-cols-2">{Object.entries(value as JsonObject).filter(([key])=>!['id','createdAt','updatedAt','publishedAt'].includes(key)).map(([key,child])=><label key={key} className={child&&typeof child==='object'?'md:col-span-2':'block'}><span className="text-xs text-slate-400">{titleCase(key)}</span><FieldTree value={child} path={[...path,key]} onChange={onChange}/></label>)}</div>;
  if(typeof value==='boolean') return <button type="button" role="switch" aria-checked={value} onClick={()=>onChange(path,!value)} className={`relative mt-1 h-7 w-12 rounded-full ${value?'bg-violet-600':'bg-slate-700'}`}><span className={`absolute top-1 h-5 w-5 rounded-full bg-white transition ${value?'left-6':'left-1'}`}/></button>;
  if(typeof value==='number') return <input className={input} type="number" value={value} onChange={e=>onChange(path,Number(e.target.value))}/>;
  const long=String(value??'').length>100;
  return long?<textarea className={`${input} min-h-28`} value={String(value??'')} onChange={e=>onChange(path,e.target.value)}/>:<input className={input} value={String(value??'')} onChange={e=>onChange(path,e.target.value)}/>;
}

export default function AdminBackendEditor({title,id,api,initial={status:'DRAFT'}}:{title:string;id?:string;api:EditorApi;initial?:JsonObject}){
 const [document,setDocument]=useState<JsonObject>(initial),[loading,setLoading]=useState(Boolean(id)),[busy,setBusy]=useState(false),[error,setError]=useState(''),[notice,setNotice]=useState(''),[saveState,setSaveState]=useState<EditorSaveState>('saved');
 useEffect(()=>{if(!id)return;let active=true;void api.get(id).then(value=>{if(active){setDocument(value);setSaveState('saved')}}).catch(reason=>active&&setError(errorMessage(reason,'Record could not be loaded'))).finally(()=>active&&setLoading(false));return()=>{active=false}},[api,id]);
 const save=async()=>{setBusy(true);setSaveState('saving');setError('');try{const saved=id?await api.patch(id,document):await api.create(document);setDocument(saved);setSaveState('saved');setNotice(id?'Changes saved.':'Draft created. Open it from the list to continue editing.');}catch(reason){setSaveState('failed');setError(errorMessage(reason,'Save failed'));}finally{setBusy(false)}};
 const act=async(kind:'validate'|'publish')=>{if(!id||!api[kind])return;setBusy(true);setError('');try{const result=await api[kind]!(id,kind==='publish'?{version:document.version??document.draftVersion,idempotencyKey:crypto.randomUUID()}:undefined);setNotice(kind==='publish'?'Version published.':Boolean(result.valid)===false?'Validation found problems. Open the highlighted fields.':'Validation passed.');}catch(reason){setError(errorMessage(reason,'Operation failed'));}finally{setBusy(false)}};
 const editorActions=<AdminEditorActions busy={busy} canValidate={Boolean(id&&api.validate)} canPublish={Boolean(id&&api.publish)} onValidate={id&&api.validate?()=>void act('validate'):undefined} onPublish={id&&api.publish?()=>void act('publish'):undefined} onSave={()=>void save()}/>;
 return <AdminEditorShell header={<AdminEditorHeader title={title} code={id?`${title.toLowerCase()} / ${id}`:'New backend record'} status={String(document.status??'DRAFT')} revision={document.revision as string|number|undefined} dirtyState={saveState} breadcrumbs={[{label:title},{label:id??'New'}]} actions={editorActions}/>} tabs={<AdminEditorTabs tabs={[{id:'advanced',label:'Advanced / Raw backend data'}]} active="advanced" onChange={()=>undefined}/>}><div aria-live="polite">{notice&&<div className="flex gap-2 rounded-xl border border-emerald-500/20 bg-emerald-500/10 p-3 text-sm text-emerald-300"><CheckCircle2 size={16}/>{notice}</div>}{error&&<div className="flex gap-2 rounded-xl border border-red-500/20 bg-red-500/10 p-4 text-sm text-red-300"><AlertCircle size={17}/>{error}</div>}</div>{loading?<div className="grid min-h-72 place-items-center"><Loader2 className="animate-spin text-violet-400"/></div>:<FormSection title="Raw backend record" description="Advanced/debug view. Domain editors should expose structured fields in their primary tabs."><FieldTree value={document} path={[]} onChange={(path,value)=>{setSaveState('dirty');setDocument(current=>updateAt(current,path,value));}}/></FormSection>}</AdminEditorShell>;
}

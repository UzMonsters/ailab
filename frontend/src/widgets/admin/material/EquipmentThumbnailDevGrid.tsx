import { EquipmentThumbnail } from '@/entities/equipment/ui/EquipmentRendererRegistry';

const previewItems = [
  ['beaker', 'Beaker'], ['erlenmeyer', 'Erlenmeyer Flask'], ['roundflask', 'Round Flask'],
  ['testtube', 'Test Tube'], ['petridish', 'Petri Dish'], ['graduatedcylinder', 'Graduated Cylinder'],
  ['burette', 'Burette'], ['pipette', 'Pipette'], ['condenser', 'Condenser'],
  ['thermometer', 'Thermometer'], ['hotplate', 'Hot Plate'], ['burner', 'Bunsen Burner'],
  ['ringstand', 'Ring Stand'],
] as const;

export function EquipmentThumbnailDevGrid() {
  if (process.env.NODE_ENV !== 'development') return null;
  return <details className="mt-5 rounded-xl border border-dashed border-violet-400/20 bg-violet-500/[.03] p-3"><summary className="cursor-pointer text-xs font-semibold text-violet-300">Thumbnail visual QA grid</summary><div className="mt-3 grid grid-cols-[repeat(auto-fit,minmax(108px,1fr))] gap-2">{previewItems.map(([type,label])=><div key={type} className="grid min-h-[145px] grid-rows-[35px_100px] justify-items-center rounded-xl border border-white/[.07] bg-black/15 px-2 py-1.5"><span className="self-center text-center text-[11px] font-semibold leading-4 text-slate-400">{label}</span><EquipmentThumbnail type={type} alt={label} frameWidth={88} frameHeight={100} size={88}/></div>)}</div></details>;
}


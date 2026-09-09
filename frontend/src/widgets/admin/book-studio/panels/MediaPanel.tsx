'use client';

import { useState, useRef } from 'react';
import { useBookStudioStore } from '../store/useBookStudioStore';
import { Upload, Image as ImageIcon } from 'lucide-react';

export function MediaPanel() {
  const { uploadImage, uploadSvg, addBlock } = useBookStudioStore();
  const imageRef = useRef<HTMLInputElement>(null);
  const svgRef = useRef<HTMLInputElement>(null);

  return (
    <div className="space-y-3">
      <p className="text-[10px] text-slate-500 uppercase tracking-wider">Upload</p>
      <button
        onClick={() => imageRef.current?.click()}
        className="flex w-full items-center justify-center gap-1 rounded-lg border border-dashed border-violet-500/30 bg-violet-600/5 p-3 text-xs text-violet-300 hover:bg-violet-600/10 transition-colors"
      >
        <Upload size={14} />
        Upload image
      </button>
      <button
        onClick={() => svgRef.current?.click()}
        className="flex w-full items-center justify-center gap-1 rounded-lg border border-dashed border-white/10 bg-white/[.02] p-3 text-xs text-slate-400 hover:bg-white/5 transition-colors"
      >
        <Upload size={14} />
        Upload SVG
      </button>
      <input ref={imageRef} hidden type="file" accept="image/png,image/jpeg,image/webp" onChange={e => { if (e.target.files?.[0]) void uploadImage(e.target.files[0]); e.target.value = ''; }} />
      <input ref={svgRef} hidden type="file" accept="image/svg+xml" onChange={e => { if (e.target.files?.[0]) void uploadSvg(e.target.files[0]); e.target.value = ''; }} />

      <p className="text-[10px] text-slate-500 uppercase tracking-wider">Quick add</p>
      <button
        onClick={() => addBlock('IMAGE')}
        className="flex w-full items-center gap-2 rounded-lg border border-white/5 bg-white/[.03] p-2 text-left hover:border-violet-500/40 transition-colors"
      >
        <ImageIcon size={14} className="text-slate-400" />
        <span className="text-xs text-slate-300">Add image placeholder</span>
      </button>
    </div>
  );
}

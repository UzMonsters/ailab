'use client';

import { FolderOpen } from 'lucide-react';

export function MyFilesPanel() {
  return (
    <div className="py-8 text-center text-xs text-slate-500">
      <FolderOpen size={24} className="mx-auto mb-2 text-slate-600" />
      <p>No uploaded files yet</p>
      <p className="mt-1 text-[10px] text-slate-600">Use the Media panel to upload images and SVGs</p>
    </div>
  );
}

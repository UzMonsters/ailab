import React from 'react';
import { Save, X } from 'lucide-react';

interface StickySaveFooterProps {
  isVisible: boolean;
  onSave: () => void;
  onDiscard: () => void;
  saveText?: string;
}

export default function StickySaveFooter({ isVisible, onSave, onDiscard, saveText = 'Save changes' }: StickySaveFooterProps) {
  if (!isVisible) return null;

  return (
    <div className="fixed bottom-0 left-0 right-0 z-50 p-4 animate-in slide-in-from-bottom-4 pointer-events-none">
      <div className="max-w-4xl mx-auto flex items-center justify-between bg-[#141b2a] border border-[#8b5cf6]/30 shadow-[0_0_20px_rgba(139,92,246,0.15)] rounded-xl p-4 pointer-events-auto">
        <div className="flex flex-col">
          <span className="text-white font-medium">Unsaved changes</span>
          <span className="text-sm text-[#8490a3]">You have pending modifications.</span>
        </div>
        <div className="flex items-center gap-3">
          <button 
            onClick={onDiscard}
            className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-[#8490a3] hover:text-white hover:bg-white/5 rounded-lg transition-colors"
          >
            <X size={16} />
            Discard
          </button>
          <button 
            onClick={onSave}
            className="flex items-center gap-2 px-5 py-2 text-sm font-medium text-white bg-[#8b5cf6] hover:bg-[#7c3aed] rounded-lg transition-colors shadow-sm"
          >
            <Save size={16} />
            {saveText}
          </button>
        </div>
      </div>
    </div>
  );
}

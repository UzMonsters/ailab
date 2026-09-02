import React, { useEffect } from 'react';
import { X } from 'lucide-react';

interface AdminDrawerProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  children: React.ReactNode;
  width?: string;
}

export default function AdminDrawer({ isOpen, onClose, title, children, width = 'md:w-[480px]' }: AdminDrawerProps) {
  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) onClose();
    };
    document.addEventListener('keydown', handleEscape);
    return () => document.removeEventListener('keydown', handleEscape);
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return (
    <>
      <div 
        className="fixed inset-0 bg-black/60 backdrop-blur-sm z-[100]" 
        onClick={onClose}
        style={{ animation: 'fadeIn 0.2s ease-out' }}
      />
      <div 
        className={`fixed top-0 right-0 h-full w-full ${width} bg-[#0b101a] border-l border-[rgba(255,255,255,0.07)] shadow-2xl z-[101] flex flex-col`}
        style={{ animation: 'slideInRight 0.3s cubic-bezier(0.16, 1, 0.3, 1)' }}
      >
        <div className="flex items-center justify-between p-6 border-b border-[rgba(255,255,255,0.05)] bg-[#090d16]">
          <h2 className="text-xl font-semibold text-white">{title}</h2>
          <button 
            onClick={onClose}
            className="p-2 -mr-2 text-[#8490a3] hover:text-white hover:bg-[rgba(255,255,255,0.05)] rounded-lg transition-colors"
          >
            <X size={20} />
          </button>
        </div>
        <div className="flex-1 overflow-y-auto p-6 scrollbar-thin">
          {children}
        </div>
      </div>
      <style dangerouslySetInnerHTML={{__html: `
        @keyframes slideInRight {
          from { transform: translateX(100%); }
          to { transform: translateX(0); }
        }
        @keyframes fadeIn {
          from { opacity: 0; }
          to { opacity: 1; }
        }
      `}} />
    </>
  );
}

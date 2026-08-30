'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';

const REACTION_TABS = [
  'Overview', 'Reactants', 'Products', 'Conditions', 'Kinetics', 
  'Energy', 'Visual Effects', 'Simulation', 'Compatibility', 
  'Scenarios', 'Localization', 'Preview'
];

export default function ReactionNewPage() {
  const router = useRouter();
  const [activeTab, setActiveTab] = useState(REACTION_TABS[0]);

  const renderTabContent = () => {
    switch (activeTab) {
      case 'Reactants':
      case 'Products':
        return (
          <div className="space-y-4">
            <h3 className="text-lg font-medium text-white">{activeTab} Builder</h3>
            <div className="flex flex-col gap-4">
              <div className="flex items-center gap-4 bg-[#141b2a] p-4 rounded-lg border border-[rgba(255,255,255,0.05)]">
                <div className="flex items-center gap-2">
                  <span className="text-sm text-[#a9a5b8]">Coefficient</span>
                  <input 
                    type="number" 
                    defaultValue={1} 
                    className="w-16 bg-[#0b101a] border border-[#2a2e39] rounded px-2 py-1 text-center text-white focus:outline-none focus:border-[#8b5cf6]" 
                  />
                </div>
                <div className="flex items-center gap-2 flex-1">
                  <span className="text-sm text-[#a9a5b8]">Substance</span>
                  <select className="flex-1 bg-[#0b101a] border border-[#2a2e39] rounded px-2 py-1 text-white focus:outline-none focus:border-[#8b5cf6]">
                    <option>HCl (Hydrochloric Acid)</option>
                    <option>NaOH (Sodium Hydroxide)</option>
                    <option>H2O (Water)</option>
                    <option>NaCl (Sodium Chloride)</option>
                  </select>
                </div>
                <button className="text-[#ef4444] hover:text-[#dc2626] px-2 py-1 text-sm font-medium transition-colors">
                  Remove
                </button>
              </div>
              <button className="self-start px-4 py-2 bg-[#141b2a] border border-[rgba(255,255,255,0.05)] rounded-lg text-sm text-white hover:bg-[rgba(255,255,255,0.02)] transition-colors">
                + Add {activeTab === 'Reactants' ? 'Reactant' : 'Product'}
              </button>
            </div>
          </div>
        );
      case 'Visual Effects':
        return (
          <div className="space-y-4">
            <h3 className="text-lg font-medium text-white">Visual Effects</h3>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <label className="flex items-center gap-3 p-4 bg-[#141b2a] rounded-lg border border-[rgba(255,255,255,0.05)] cursor-pointer hover:border-[rgba(255,255,255,0.1)] transition-colors">
                <input type="checkbox" className="accent-[#8b5cf6] w-4 h-4" />
                <span className="text-white text-sm">Color Change</span>
              </label>
              <label className="flex items-center gap-3 p-4 bg-[#141b2a] rounded-lg border border-[rgba(255,255,255,0.05)] cursor-pointer hover:border-[rgba(255,255,255,0.1)] transition-colors">
                <input type="checkbox" className="accent-[#8b5cf6] w-4 h-4" />
                <span className="text-white text-sm">Gas Evolution (Bubbles)</span>
              </label>
              <label className="flex items-center gap-3 p-4 bg-[#141b2a] rounded-lg border border-[rgba(255,255,255,0.05)] cursor-pointer hover:border-[rgba(255,255,255,0.1)] transition-colors">
                <input type="checkbox" className="accent-[#8b5cf6] w-4 h-4" />
                <span className="text-white text-sm">Precipitate Formation</span>
              </label>
            </div>
          </div>
        );
      default:
        return (
          <div className="flex flex-col items-center justify-center h-64 border-2 border-dashed border-[rgba(255,255,255,0.05)] rounded-lg bg-[#0b101a]">
            <div className="w-12 h-12 mb-4 rounded-full bg-[#141b2a] flex items-center justify-center">
              <span className="text-xl text-[#a9a5b8]">⚙️</span>
            </div>
            <p className="text-[#a9a5b8] text-sm">Configuration options for {activeTab} will appear here.</p>
          </div>
        );
    }
  };

  return (
    <div className="p-6 h-full flex flex-col">
      <AdminPageHeader 
        title="Create Reaction"
        description="Configure chemical reactions, conditions, and visual properties."
        actions={
          <div className="flex gap-2">
            <button 
              onClick={() => router.back()}
              className="px-4 py-2 rounded-lg text-sm font-medium text-[#8490a3] bg-[#141b2a] border border-[rgba(255,255,255,0.05)] hover:text-white transition-colors"
            >
              Cancel
            </button>
            <button className="px-4 py-2 rounded-lg text-sm font-medium text-white bg-[#8b5cf6] hover:bg-[#7c3aed] transition-colors shadow-lg shadow-[#8b5cf6]/20">
              Save Reaction
            </button>
          </div>
        }
      />
      
      <div className="flex gap-2 overflow-x-auto pb-4 mb-2 scrollbar-thin scrollbar-thumb-[#2a2e39] scrollbar-track-transparent">
        {REACTION_TABS.map(tab => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`px-4 py-2 whitespace-nowrap rounded-lg text-sm font-medium transition-colors ${
              activeTab === tab
                ? 'bg-[#141b2a] text-white border border-[rgba(255,255,255,0.1)] shadow-sm'
                : 'text-[#8490a3] hover:text-white hover:bg-[rgba(255,255,255,0.02)] border border-transparent'
            }`}
          >
            {tab}
          </button>
        ))}
      </div>

      <div className="flex-1 bg-[#0b101a] border border-[rgba(255,255,255,0.05)] rounded-xl p-6 text-[#a9a5b8] overflow-y-auto custom-scrollbar">
        {renderTabContent()}
      </div>
    </div>
  );
}

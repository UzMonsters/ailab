'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';

const SUBSTANCE_TABS = [
  'General', 'Composition', 'Physical', 'Thermal', 'Flow', 
  'Visual', 'Container Compatibility', 'Reactivity', 'Simulation', 
  'Localization', 'Preview'
];

export default function SubstanceNewPage() {
  const router = useRouter();
  const [activeTab, setActiveTab] = useState(SUBSTANCE_TABS[0]);

  // State for Composition Builder
  const [elements, setElements] = useState([{ symbol: 'H', count: 2 }, { symbol: 'O', count: 1 }]);

  const renderTabContent = () => {
    switch (activeTab) {
      case 'Composition':
        return (
          <div className="space-y-4">
            <h3 className="text-lg font-medium text-white">Visual Element Builder</h3>
            <div className="flex flex-wrap items-center gap-4 bg-[#141b2a] p-4 rounded-lg border border-[rgba(255,255,255,0.05)]">
              {elements.map((el, i) => (
                <div key={i} className="flex items-center gap-2">
                  <input 
                    type="text" 
                    value={el.symbol} 
                    onChange={(e) => {
                      const newEl = [...elements];
                      newEl[i].symbol = e.target.value;
                      setElements(newEl);
                    }} 
                    className="w-12 bg-[#0b101a] border border-[#2a2e39] rounded px-2 py-1 text-center text-white focus:outline-none focus:border-[#8b5cf6]" 
                  />
                  <input 
                    type="number" 
                    value={el.count} 
                    onChange={(e) => {
                      const newEl = [...elements];
                      newEl[i].count = parseInt(e.target.value) || 0;
                      setElements(newEl);
                    }} 
                    className="w-16 bg-[#0b101a] border border-[#2a2e39] rounded px-2 py-1 text-center text-white focus:outline-none focus:border-[#8b5cf6]" 
                  />
                  {i < elements.length - 1 && <span className="text-[#a9a5b8] font-bold">+</span>}
                </div>
              ))}
              <button 
                onClick={() => setElements([...elements, { symbol: 'C', count: 1 }])}
                className="text-sm px-3 py-1 bg-[#2a2e39] text-white rounded hover:bg-[#3b4252] transition-colors"
              >
                +
              </button>
            </div>
            <p className="text-sm text-[#a9a5b8]">
              Formula Preview: <span className="font-mono text-white bg-[#141b2a] px-2 py-1 rounded">
                {elements.map(e => `${e.symbol}${e.count > 1 ? e.count : ''}`).join('')}
              </span>
            </p>
          </div>
        );
      case 'Container Compatibility':
        return (
          <div className="space-y-4">
            <h3 className="text-lg font-medium text-white">Compatibility Matrix</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
              {['Glass', 'Plastic', 'Metal', 'Ceramic'].map(material => (
                <div key={material} className="p-4 bg-[#141b2a] rounded-lg border border-[rgba(255,255,255,0.05)] flex justify-between items-center">
                  <span className="text-white font-medium">{material}</span>
                  <select className="bg-[#0b101a] border border-[#2a2e39] rounded px-2 py-1 text-sm text-[#a9a5b8] focus:outline-none focus:border-[#8b5cf6]">
                    <option value="excellent">Excellent</option>
                    <option value="good">Good</option>
                    <option value="poor">Poor</option>
                    <option value="incompatible">Incompatible</option>
                  </select>
                </div>
              ))}
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
        title="Create Substance"
        description="Configure substance chemical properties and behaviors."
        actions={
          <div className="flex gap-2">
            <button 
              onClick={() => router.back()}
              className="px-4 py-2 rounded-lg text-sm font-medium text-[#8490a3] bg-[#141b2a] border border-[rgba(255,255,255,0.05)] hover:text-white transition-colors"
            >
              Cancel
            </button>
            <button className="px-4 py-2 rounded-lg text-sm font-medium text-white bg-[#8b5cf6] hover:bg-[#7c3aed] transition-colors shadow-lg shadow-[#8b5cf6]/20">
              Save Substance
            </button>
          </div>
        }
      />
      
      <div className="flex gap-2 overflow-x-auto pb-4 mb-2 scrollbar-thin scrollbar-thumb-[#2a2e39] scrollbar-track-transparent">
        {SUBSTANCE_TABS.map(tab => (
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

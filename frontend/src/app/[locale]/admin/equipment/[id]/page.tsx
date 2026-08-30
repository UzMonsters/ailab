'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';
import AdminDesignPanel from '@/widgets/admin/AdminDesignPanel';
import { useToastStore } from '@/stores/toast.store';
import { Thermometer, ShieldAlert, Trash2, Crosshair, Box } from 'lucide-react';

export default function EquipmentEditorPage({ params }: { params: { locale: string, id: string } }) {
  const router = useRouter();
  const { addToast } = useToastStore();
  
  const [activeTab, setActiveTab] = useState('General');
  const [hasChanges, setHasChanges] = useState(false);

  const tabs = ['General', 'Visual', 'Capacity', 'Ports', 'Thermal', 'Pressure', 'Mechanical', 'Compatibility', 'Simulation', 'Scenarios', 'Localization', 'Preview'];

  const handleSave = () => {
    addToast({ title: 'Equipment saved successfully', type: 'success' });
    setHasChanges(false);
  };

  return (
    <div className="p-6 md:p-10 max-w-[1400px] mx-auto pb-32">
      <div className="mb-4">
        <button onClick={() => router.push(`/${params.locale}/admin/equipment`)} className="text-[#8490a3] hover:text-white text-sm flex items-center gap-1">
          ← Back to Equipment
        </button>
      </div>
      
      <AdminPageHeader 
        title={params.id === 'new' ? 'Create Equipment' : 'Edit Equipment: 250ml Beaker'}
        description="Configure properties and simulation capabilities for this laboratory apparatus."
      />

      <div className="flex flex-col md:flex-row gap-8 mt-6 h-[600px]">
        {/* Navigation */}
        <div className="w-full md:w-48 shrink-0 flex flex-row md:flex-col gap-1 overflow-y-auto border-b md:border-b-0 border-[rgba(255,255,255,0.05)] pb-4 md:pb-0 hide-scrollbar pr-2">
          {tabs.map(tab => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              className={`px-4 py-3 md:py-2.5 text-left rounded-lg font-medium text-sm transition-colors whitespace-nowrap ${
                activeTab === tab 
                  ? 'bg-violet-600 text-white' 
                  : 'text-[#8490a3] hover:text-white hover:bg-[rgba(255,255,255,0.02)]'
              }`}
            >
              {tab}
            </button>
          ))}
        </div>

        {/* Content */}
        <div className="flex-1 min-w-0 flex flex-col h-full bg-[#0b101a] border border-[rgba(255,255,255,0.05)] rounded-xl overflow-hidden relative">
          
          {activeTab === 'General' && (
            <div className="p-6 space-y-6 overflow-y-auto h-full">
              <h3 className="text-lg font-semibold text-white mb-6">General Details</h3>
              <div className="grid gap-4 md:grid-cols-2">
                <label className="block">
                  <span className="text-sm text-[#8490a3] mb-1 block">Name</span>
                  <input className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg p-2.5 text-white outline-none focus:border-violet-500" defaultValue="250ml Beaker" onChange={() => setHasChanges(true)} />
                </label>
                <label className="block">
                  <span className="text-sm text-[#8490a3] mb-1 block">Subject</span>
                  <select className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg p-2.5 text-white outline-none focus:border-violet-500" onChange={() => setHasChanges(true)}>
                    <option>Chemistry</option>
                    <option>Physics</option>
                    <option>Biology</option>
                    <option>Multiple</option>
                  </select>
                </label>
                <label className="block">
                  <span className="text-sm text-[#8490a3] mb-1 block">Category</span>
                  <input className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg p-2.5 text-white outline-none focus:border-violet-500" defaultValue="Vessels" onChange={() => setHasChanges(true)} />
                </label>
              </div>
            </div>
          )}

          {activeTab === 'Visual' && (
            <div className="p-6 space-y-6 overflow-y-auto h-full">
              <h3 className="text-lg font-semibold text-white mb-6">Visual Representation</h3>
              <label className="block">
                <span className="text-sm text-[#8490a3] mb-1 block">SVG Asset URL</span>
                <input className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg p-2.5 text-white outline-none focus:border-violet-500" defaultValue="/assets/equipment/beaker_250.svg" onChange={() => setHasChanges(true)} />
              </label>
              <div className="mt-6 bg-[#141b2a] border border-[rgba(255,255,255,0.05)] rounded-xl h-64 flex items-center justify-center">
                <Box size={64} className="text-[#8490a3] opacity-50" />
              </div>
            </div>
          )}

          {activeTab === 'Capacity' && (
            <div className="p-6 space-y-6 overflow-y-auto h-full">
              <h3 className="text-lg font-semibold text-white mb-6">Volume & Capacity</h3>
              <div className="grid gap-4 md:grid-cols-2">
                <label className="block">
                  <span className="text-sm text-[#8490a3] mb-1 block">Max Volume (ml)</span>
                  <input type="number" className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg p-2.5 text-white outline-none focus:border-violet-500" defaultValue="250" onChange={() => setHasChanges(true)} />
                </label>
                <label className="block">
                  <span className="text-sm text-[#8490a3] mb-1 block">Fill Height (px)</span>
                  <input type="number" className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg p-2.5 text-white outline-none focus:border-violet-500" defaultValue="120" onChange={() => setHasChanges(true)} />
                </label>
              </div>
            </div>
          )}

          {activeTab === 'Ports' && (
            <div className="flex h-full w-full">
               {/* Left: Canvas */}
               <div className="flex-1 bg-[#141b2a] relative overflow-hidden flex items-center justify-center p-6 border-r border-[rgba(255,255,255,0.05)]">
                <div className="absolute inset-0 opacity-10 bg-[radial-gradient(circle_at_center,rgba(255,255,255,0.2)_1px,transparent_1px)]" style={{ backgroundSize: '20px 20px' }}></div>
                
                {/* Equipment asset canvas */}
                <div className="relative w-64 h-80 border-2 border-dashed border-[#8490a3]/50 rounded-xl flex items-center justify-center z-10 bg-[#0b101a]/50">
                   <span className="text-[#8490a3] text-sm font-medium">SVG Asset Bounds</span>
                   
                   {/* Connection ports */}
                   <div className="absolute -top-3 left-1/2 -translate-x-1/2 w-6 h-6 bg-violet-500/20 border-2 border-violet-500 rounded-full flex items-center justify-center cursor-pointer hover:bg-violet-500/40 transition-colors">
                     <Crosshair size={12} className="text-violet-400" />
                   </div>
                   <div className="absolute top-1/2 -right-3 -translate-y-1/2 w-6 h-6 bg-amber-500/20 border-2 border-amber-500 rounded-full flex items-center justify-center cursor-pointer hover:bg-amber-500/40 transition-colors">
                     <Crosshair size={12} className="text-amber-400" />
                   </div>
                </div>
              </div>
              {/* Right: Port List */}
              <div className="w-80 bg-[#0b101a] p-4 flex flex-col gap-4 overflow-y-auto">
                <div className="flex justify-between items-center">
                  <div className="text-xs font-bold text-[#8490a3] uppercase tracking-wider">Defined Ports</div>
                  <button className="text-xs bg-violet-600/20 text-violet-400 px-2 py-1 rounded hover:bg-violet-600/40">+ Add Port</button>
                </div>
                
                <div className="space-y-3">
                  <div className="bg-[#141b2a] border border-violet-500/50 rounded-lg p-3">
                    <div className="flex justify-between items-center mb-2">
                      <span className="text-sm font-semibold text-white flex items-center gap-2"><div className="w-2 h-2 rounded-full bg-violet-500"></div> Top Opening</span>
                      <button className="text-[#8490a3] hover:text-red-400"><Trash2 size={14}/></button>
                    </div>
                    <div className="grid grid-cols-2 gap-2">
                      <label className="block text-xs text-[#8490a3]">Type
                        <select className="mt-1 w-full bg-[#0b101a] border border-[rgba(255,255,255,0.1)] rounded p-1 text-white outline-none">
                          <option>Liquid In/Out</option>
                          <option>Gas Escape</option>
                        </select>
                      </label>
                      <label className="block text-xs text-[#8490a3]">X / Y (px)
                        <div className="flex gap-1 mt-1">
                          <input type="number" defaultValue="50" className="w-full bg-[#0b101a] border border-[rgba(255,255,255,0.1)] rounded p-1 text-white outline-none text-center" />
                          <input type="number" defaultValue="0" className="w-full bg-[#0b101a] border border-[rgba(255,255,255,0.1)] rounded p-1 text-white outline-none text-center" />
                        </div>
                      </label>
                    </div>
                  </div>

                  <div className="bg-[#141b2a] border border-amber-500/50 rounded-lg p-3">
                    <div className="flex justify-between items-center mb-2">
                      <span className="text-sm font-semibold text-white flex items-center gap-2"><div className="w-2 h-2 rounded-full bg-amber-500"></div> Side Attachment</span>
                      <button className="text-[#8490a3] hover:text-red-400"><Trash2 size={14}/></button>
                    </div>
                    <div className="grid grid-cols-2 gap-2">
                      <label className="block text-xs text-[#8490a3]">Type
                        <select className="mt-1 w-full bg-[#0b101a] border border-[rgba(255,255,255,0.1)] rounded p-1 text-white outline-none">
                          <option>Sensor Node</option>
                          <option>Clamp Node</option>
                        </select>
                      </label>
                      <label className="block text-xs text-[#8490a3]">X / Y (px)
                        <div className="flex gap-1 mt-1">
                          <input type="number" defaultValue="100" className="w-full bg-[#0b101a] border border-[rgba(255,255,255,0.1)] rounded p-1 text-white outline-none text-center" />
                          <input type="number" defaultValue="50" className="w-full bg-[#0b101a] border border-[rgba(255,255,255,0.1)] rounded p-1 text-white outline-none text-center" />
                        </div>
                      </label>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'Thermal' && (
            <div className="p-6 space-y-4 overflow-y-auto h-full">
              <h3 className="text-lg font-semibold text-white mb-4">Thermal Properties</h3>
              
              <div className="grid gap-4 md:grid-cols-2 max-w-2xl">
                <label className="block">
                  <span className="text-sm text-[#8490a3] mb-1 block">Max Safe Temperature (°C)</span>
                  <input type="number" className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg p-2.5 text-white outline-none focus:border-violet-500" defaultValue="400" />
                </label>
                <label className="block">
                  <span className="text-sm text-[#8490a3] mb-1 block">Min Safe Temperature (°C)</span>
                  <input type="number" className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg p-2.5 text-white outline-none focus:border-violet-500" defaultValue="-20" />
                </label>
                <label className="block col-span-2">
                  <span className="text-sm text-[#8490a3] mb-1 block">Thermal Shock Limit (Δ°C/sec)</span>
                  <input type="number" className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg p-2.5 text-white outline-none focus:border-violet-500" defaultValue="50" />
                </label>
              </div>

              <div className="mt-8 p-4 border border-red-500/20 bg-red-500/5 rounded-xl flex items-start gap-4 max-w-2xl">
                <ShieldAlert className="text-red-400 mt-1" />
                <div>
                  <h4 className="text-red-400 font-bold">Failure Behavior</h4>
                  <p className="text-sm text-[#8490a3] mt-1">If thermal limits are exceeded, the object will break, triggering a failure event in the scenario engine.</p>
                </div>
              </div>
            </div>
          )}

          {['Pressure', 'Mechanical', 'Compatibility', 'Simulation', 'Scenarios', 'Localization', 'Preview'].includes(activeTab) && <AdminDesignPanel domain="equipment" tab={activeTab} />}

        </div>
      </div>

      {hasChanges && (
        <div className="fixed bottom-0 left-0 lg:left-[260px] right-0 p-4 bg-[#0b101a] border-t border-[rgba(255,255,255,0.1)] flex items-center justify-between z-50 px-6 lg:px-12" style={{ animation: 'slideInUp 0.3s' }}>
          <div className="flex items-center gap-3">
            <span className="w-2 h-2 rounded-full bg-amber-500 animate-pulse"></span>
            <span className="text-white font-medium">Unsaved changes</span>
          </div>
          <div className="flex items-center gap-3">
            <button onClick={() => setHasChanges(false)} className="px-4 py-2 text-sm font-medium text-[#8490a3] hover:text-white">Discard</button>
            <button onClick={handleSave} className="px-4 py-2 text-sm font-bold text-white bg-violet-600 hover:bg-violet-700 rounded-lg shadow-lg">Save changes</button>
          </div>
        </div>
      )}
    </div>
  );
}

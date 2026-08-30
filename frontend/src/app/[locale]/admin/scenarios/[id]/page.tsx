'use client';

import React, { useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAdminStore } from '@/stores/admin.store';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';
import AdminDesignPanel from '@/widgets/admin/AdminDesignPanel';
import { useToastStore } from '@/stores/toast.store';
import { GripVertical, Plus, Box, ShieldAlert, Thermometer, Droplets, Link as LinkIcon, Trash2, ChevronRight } from 'lucide-react';

export default function ScenarioEditorPage({ params }: { params: { locale: string, id: string } }) {
  const router = useRouter();
  const { addToast } = useToastStore();
  const scenarios = useAdminStore(state => state.scenarios);
  const scenario = scenarios.find(s => s.id === params.id) || scenarios[0];
  
  const [activeTab, setActiveTab] = useState('General');
  const [hasChanges, setHasChanges] = useState(false);
  const [selectedStep, setSelectedStep] = useState<number | null>(null);

  const tabs = ['General', 'Setup', 'Steps', 'Conditions', 'Measurements', 'Results', 'Hints', 'Rewards', 'Safety', 'Localization', 'Preview'];

  const handleSave = () => {
    addToast({ title: 'Scenario saved successfully', type: 'success' });
    setHasChanges(false);
  };

  const stepsMock = [
    'Add Beaker to workspace', 'Add 50ml Water', 'Add Thermometer', 'Place on Hot Plate', 'Heat to 100°C', 'Observe boiling'
  ];

  return (
    <div className="p-6 md:p-10 max-w-[1400px] mx-auto pb-32">
      <div className="mb-4">
        <button onClick={() => router.push(`/${params.locale}/admin/scenarios`)} className="text-[#8490a3] hover:text-white text-sm flex items-center gap-1">
          ← Back to Scenarios
        </button>
      </div>
      
      <AdminPageHeader 
        title={`Edit Scenario: ${scenario?.title || 'New Scenario'}`}
        description="Scenario builder and configuration."
        counters={[
          { label: 'Subject', value: scenario?.subject || 'Chemistry' },
          { label: 'Difficulty', value: scenario?.difficulty || 'Intermediate' },
        ]}
      />

      <div className="flex flex-col md:flex-row gap-8 mt-6 h-[600px]">
        {/* Navigation */}
        <div className="w-full md:w-56 shrink-0 flex flex-row md:flex-col gap-1 overflow-y-auto border-b md:border-b-0 border-[rgba(255,255,255,0.05)] pb-4 md:pb-0 hide-scrollbar pr-2">
          {tabs.map(tab => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              className={`px-4 py-3 md:py-2.5 text-left rounded-lg font-medium text-sm transition-colors whitespace-nowrap ${
                activeTab === tab 
                  ? 'bg-[#8b5cf6] text-white' 
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
              <h3 className="text-lg font-semibold text-white mb-6">General Information</h3>
              <div className="space-y-4">
                <div>
                  <label className="block text-sm text-[#8490a3] mb-1">Scenario Title</label>
                  <input type="text" defaultValue={scenario?.title} onChange={() => setHasChanges(true)} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg p-2.5 text-white outline-none focus:border-[#8b5cf6]" />
                </div>
                <div>
                  <label className="block text-sm text-[#8490a3] mb-1">Description</label>
                  <textarea rows={4} defaultValue="Mock description for the scenario." onChange={() => setHasChanges(true)} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg p-2.5 text-white outline-none focus:border-[#8b5cf6] resize-none"></textarea>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'Setup' && (
            <div className="flex h-full w-full">
              {/* Left: Equipment List */}
              <div className="w-64 border-r border-[rgba(255,255,255,0.05)] bg-[#0b101a] p-4 flex flex-col gap-2 overflow-y-auto">
                <div className="text-xs font-bold text-[#8490a3] uppercase tracking-wider mb-2">Equipment</div>
                <div className="p-3 bg-[#141b2a] rounded border border-[rgba(255,255,255,0.05)] text-sm text-white flex items-center gap-2 cursor-grab hover:border-[#8b5cf6]/50"><Box size={16}/> 250ml Beaker</div>
                <div className="p-3 bg-[#141b2a] rounded border border-[rgba(255,255,255,0.05)] text-sm text-white flex items-center gap-2 cursor-grab hover:border-[#8b5cf6]/50"><Box size={16}/> Bunsen Burner</div>
                <div className="p-3 bg-[#141b2a] rounded border border-[rgba(255,255,255,0.05)] text-sm text-white flex items-center gap-2 cursor-grab hover:border-[#8b5cf6]/50"><Thermometer size={16}/> Thermometer</div>
                <div className="text-xs font-bold text-[#8490a3] uppercase tracking-wider mb-2 mt-4">Materials</div>
                <div className="p-3 bg-[#141b2a] rounded border border-[rgba(255,255,255,0.05)] text-sm text-white flex items-center gap-2 cursor-grab hover:border-[#8b5cf6]/50"><Droplets size={16} className="text-blue-400"/> Distilled Water</div>
              </div>
              {/* Center: Canvas */}
              <div className="flex-1 bg-[#141b2a] relative overflow-hidden flex items-center justify-center">
                <div className="absolute inset-0 opacity-10 bg-[radial-gradient(circle_at_center,rgba(255,255,255,0.2)_1px,transparent_1px)]" style={{ backgroundSize: '20px 20px' }}></div>
                <div className="relative z-10 p-6 border-2 border-dashed border-[#8b5cf6]/50 rounded-xl bg-[#8b5cf6]/10 flex flex-col items-center justify-center text-center cursor-pointer">
                  <Box size={48} className="text-[#8b5cf6] mb-2" />
                  <span className="text-sm font-medium text-white">250ml Beaker</span>
                </div>
              </div>
              {/* Right: Properties */}
              <div className="w-72 border-l border-[rgba(255,255,255,0.05)] bg-[#0b101a] p-4 flex flex-col gap-4 overflow-y-auto">
                <div className="text-xs font-bold text-[#8490a3] uppercase tracking-wider">Properties: Beaker</div>
                <div>
                  <label className="block text-xs text-[#8490a3] mb-1">Initial Content</label>
                  <select className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded p-2 text-sm text-white outline-none">
                    <option>Empty</option>
                    <option>Water</option>
                  </select>
                </div>
                <div>
                  <label className="block text-xs text-[#8490a3] mb-1">Initial Volume (ml)</label>
                  <input type="number" defaultValue={0} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded p-2 text-sm text-white outline-none" />
                </div>
                <div>
                  <label className="block text-xs text-[#8490a3] mb-1">Position X/Y</label>
                  <div className="flex gap-2">
                    <input type="number" defaultValue={100} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded p-2 text-sm text-white outline-none" />
                    <input type="number" defaultValue={150} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded p-2 text-sm text-white outline-none" />
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'Steps' && (
            <div className="flex h-full w-full">
              {/* Timeline */}
              <div className="flex-1 p-6 overflow-y-auto border-r border-[rgba(255,255,255,0.05)]">
                <div className="flex justify-between items-center mb-6">
                  <h3 className="text-lg font-semibold text-white">Visual Step Timeline</h3>
                  <button className="px-3 py-1.5 bg-[#8b5cf6] rounded text-sm text-white flex items-center gap-2 hover:bg-[#7c3aed]">
                    <Plus size={14} /> Add Step
                  </button>
                </div>
                <div className="space-y-3 relative before:absolute before:inset-y-0 before:left-[21px] before:w-[2px] before:bg-[rgba(255,255,255,0.05)]">
                  {stepsMock.map((step, idx) => (
                    <div 
                      key={idx} 
                      onClick={() => setSelectedStep(idx)}
                      className={`relative flex items-center gap-4 bg-[#141b2a] border ${selectedStep === idx ? 'border-[#8b5cf6]' : 'border-[rgba(255,255,255,0.05)]'} rounded-lg p-3 group cursor-pointer hover:border-[#8b5cf6]/50 ml-10`}
                    >
                      <div className={`absolute -left-10 w-6 h-6 rounded-full border-[3px] border-[#0b101a] ${selectedStep === idx ? 'bg-[#8b5cf6]' : 'bg-[#141b2a]'} z-10`}></div>
                      <GripVertical size={16} className="text-[#8490a3] cursor-grab hover:text-white" />
                      <div className="flex-1">
                        <div className="text-sm font-medium text-white">{step}</div>
                        <div className="text-xs text-[#8490a3] mt-0.5">Action: Add, Target: Beaker</div>
                      </div>
                      <button className="p-1.5 text-[#8490a3] hover:text-red-400 hover:bg-red-400/10 rounded"><Trash2 size={16}/></button>
                    </div>
                  ))}
                </div>
              </div>
              {/* Step Properties */}
              <div className="w-80 bg-[#0b101a] p-4 flex flex-col gap-4 overflow-y-auto">
                <div className="text-xs font-bold text-[#8490a3] uppercase tracking-wider mb-2">Step Properties</div>
                {selectedStep !== null ? (
                  <div className="space-y-4">
                    <div>
                      <label className="block text-xs text-[#8490a3] mb-1">Action Type</label>
                      <select className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded p-2 text-sm text-white outline-none">
                        <option>Add Object</option>
                        <option>Connect</option>
                        <option>Set Property</option>
                      </select>
                    </div>
                    <div>
                      <label className="block text-xs text-[#8490a3] mb-1">Target Object</label>
                      <select className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded p-2 text-sm text-white outline-none">
                        <option>Beaker 1</option>
                        <option>Thermometer 1</option>
                      </select>
                    </div>
                    <div>
                      <label className="block text-xs text-[#8490a3] mb-1">Required Conditions to Complete</label>
                      <div className="p-2 border border-dashed border-[rgba(255,255,255,0.2)] rounded text-xs text-[#8490a3] text-center cursor-pointer hover:bg-[rgba(255,255,255,0.02)]">
                        + Add Condition
                      </div>
                    </div>
                  </div>
                ) : (
                  <div className="text-sm text-[#8490a3] text-center mt-10">Select a step to edit its properties.</div>
                )}
              </div>
            </div>
          )}

          {activeTab === 'Conditions' && (
            <div className="p-6 space-y-6 overflow-y-auto h-full">
              <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-semibold text-white">Visual Rule Builder</h3>
                <div className="flex gap-2">
                  <button className="px-3 py-1.5 bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded text-sm text-white hover:bg-[rgba(255,255,255,0.05)]">
                    + Group
                  </button>
                  <button className="px-3 py-1.5 bg-[#8b5cf6] rounded text-sm text-white hover:bg-[#7c3aed]">
                    + Condition
                  </button>
                </div>
              </div>
              <div className="bg-[#141b2a] border border-[rgba(255,255,255,0.05)] rounded-xl p-4">
                <div className="flex items-center gap-2 mb-4">
                  <select className="bg-transparent text-sm font-bold text-[#8b5cf6] outline-none">
                    <option>AND</option>
                    <option>OR</option>
                  </select>
                  <span className="text-xs text-[#8490a3]">All following conditions must be met</span>
                </div>
                
                <div className="space-y-3 pl-4 border-l-2 border-[#8b5cf6]/30 ml-2 relative">
                  <div className="p-3 border border-[#8b5cf6]/30 bg-[#0b101a] rounded-lg flex items-center gap-3">
                    <Thermometer size={16} className="text-[#8b5cf6]"/>
                    <select className="bg-transparent text-sm text-white outline-none w-32 border-b border-[rgba(255,255,255,0.1)]"><option>Beaker 1 Temp</option></select>
                    <select className="bg-transparent text-sm text-white outline-none w-16 border-b border-[rgba(255,255,255,0.1)]"><option>&gt;=</option></select>
                    <input type="text" defaultValue="90" className="bg-transparent text-sm text-amber-400 outline-none w-12 border-b border-[rgba(255,255,255,0.1)] text-center"/>
                    <span className="text-xs text-[#8490a3]">°C</span>
                    <div className="flex-1"></div>
                    <button className="text-[#8490a3] hover:text-red-400"><Trash2 size={14}/></button>
                  </div>
                  
                  <div className="p-3 border border-[#8b5cf6]/30 bg-[#0b101a] rounded-lg flex items-center gap-3">
                    <LinkIcon size={16} className="text-[#8b5cf6]"/>
                    <select className="bg-transparent text-sm text-white outline-none w-32 border-b border-[rgba(255,255,255,0.1)]"><option>Thermometer 1</option></select>
                    <span className="text-sm text-[#8490a3]">connected to</span>
                    <select className="bg-transparent text-sm text-white outline-none w-32 border-b border-[rgba(255,255,255,0.1)]"><option>Beaker 1</option></select>
                    <div className="flex-1"></div>
                    <button className="text-[#8490a3] hover:text-red-400"><Trash2 size={14}/></button>
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'Measurements' && (
            <div className="p-6 space-y-6 overflow-y-auto h-full">
               <div className="flex justify-between items-center mb-4">
                <h3 className="text-lg font-semibold text-white">Required Measurements</h3>
                <button className="px-3 py-1.5 bg-[#8b5cf6] rounded text-sm text-white hover:bg-[#7c3aed]">
                  + Add Measurement
                </button>
              </div>
              <div className="bg-[#141b2a] border border-[rgba(255,255,255,0.05)] rounded-xl p-4 flex gap-4 items-end">
                <div className="flex-1">
                  <label className="block text-xs text-[#8490a3] mb-1">Label</label>
                  <input type="text" defaultValue="Boiling Point" className="w-full bg-[#0b101a] border border-[rgba(255,255,255,0.1)] rounded p-2 text-sm text-white outline-none" />
                </div>
                <div className="w-24">
                  <label className="block text-xs text-[#8490a3] mb-1">Unit</label>
                  <input type="text" defaultValue="°C" className="w-full bg-[#0b101a] border border-[rgba(255,255,255,0.1)] rounded p-2 text-sm text-white outline-none" />
                </div>
                <div className="w-24">
                  <label className="block text-xs text-[#8490a3] mb-1">Min Value</label>
                  <input type="number" defaultValue="98" className="w-full bg-[#0b101a] border border-[rgba(255,255,255,0.1)] rounded p-2 text-sm text-white outline-none" />
                </div>
                <div className="w-24">
                  <label className="block text-xs text-[#8490a3] mb-1">Max Value</label>
                  <input type="number" defaultValue="102" className="w-full bg-[#0b101a] border border-[rgba(255,255,255,0.1)] rounded p-2 text-sm text-white outline-none" />
                </div>
                <button className="p-2 mb-0.5 bg-red-400/10 text-red-400 rounded hover:bg-red-400/20"><Trash2 size={16}/></button>
              </div>
            </div>
          )}

          {activeTab === 'Safety' && (
            <div className="p-6 space-y-4 overflow-y-auto h-full">
              <h3 className="text-lg font-semibold text-white">Required Warnings</h3>
              <div className="p-4 border border-red-500/20 bg-red-500/5 rounded-xl flex items-start gap-4">
                <ShieldAlert className="text-red-400 mt-1" />
                <div>
                  <h4 className="text-red-400 font-bold">Thermal Shock Risk</h4>
                  <p className="text-sm text-[#8490a3] mt-1">If heating occurs too rapidly, break the vessel.</p>
                </div>
              </div>
            </div>
          )}

          {['Results', 'Hints', 'Rewards', 'Localization', 'Preview'].includes(activeTab) && <AdminDesignPanel domain="scenario" tab={activeTab} />}

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
            <button onClick={handleSave} className="px-4 py-2 text-sm font-bold text-white bg-[#8b5cf6] hover:bg-[#7c3aed] rounded-lg shadow-lg">Save changes</button>
          </div>
        </div>
      )}
    </div>
  );
}

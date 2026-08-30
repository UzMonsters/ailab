'use client';
import React, { useState, useEffect } from 'react';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';
import AdminDesignPanel from '@/widgets/admin/AdminDesignPanel';
import { useRouter, useParams } from 'next/navigation';
import { mockLevels } from '@/mocks/admin/learning';
import { 
  ArrowLeft, 
  Settings, 
  FileText, 
  Lock, 
  FlaskConical, 
  Award, 
  Globe, 
  Play,
  Save,
  Trash2
} from 'lucide-react';

const tabs = [
  { id: 'general', label: 'General', icon: <Settings size={16} /> },
  { id: 'content', label: 'Content', icon: <FileText size={16} /> },
  { id: 'requirements', label: 'Requirements', icon: <Lock size={16} /> },
  { id: 'scenario', label: 'Scenario', icon: <FlaskConical size={16} /> },
  { id: 'rewards', label: 'Rewards', icon: <Award size={16} /> },
  { id: 'localization', label: 'Localization', icon: <Globe size={16} /> },
  { id: 'preview', label: 'Preview', icon: <Play size={16} /> },
];

export default function EditLevelPage() {
  const router = useRouter();
  const params = useParams();
  const id = params.id as string;
  const [activeTab, setActiveTab] = useState('general');

  const level = mockLevels.find(l => l.id === id);

  if (!level) {
    return (
      <div className="p-4 md:p-6 min-h-screen bg-[#070b14] text-white flex flex-col items-center justify-center">
        <h2 className="text-xl font-bold mb-4">Level not found</h2>
        <button onClick={() => router.back()} className="text-[#8b5cf6] hover:underline">Go Back</button>
      </div>
    );
  }

  return (
    <div className="p-4 md:p-6 min-h-screen bg-[#070b14] text-white">
      <div className="mb-4">
        <button 
          onClick={() => router.back()} 
          className="flex items-center gap-2 text-[#8490a3] hover:text-white transition-colors text-sm font-medium"
        >
          <ArrowLeft size={16} />
          Back to Levels
        </button>
      </div>

      <AdminPageHeader 
        title={`Edit Level: ${level.title}`} 
        description={`Manage settings for level ${level.levelNumber}`}
        actions={
          <div className="flex gap-2">
            <button 
              className="flex items-center gap-2 px-4 py-2 bg-red-500/10 hover:bg-red-500/20 text-red-500 border border-red-500/20 rounded-lg text-sm font-medium transition-colors"
            >
              <Trash2 size={16} />
              Delete
            </button>
            <button 
              className="px-4 py-2 bg-[#141b2a] hover:bg-[#1a2333] border border-[rgba(255,255,255,0.1)] text-white rounded-lg text-sm font-medium transition-colors"
            >
              Save Draft
            </button>
            <button 
              className="flex items-center gap-2 px-4 py-2 bg-[#8b5cf6] hover:bg-[#7c3aed] text-white rounded-lg text-sm font-medium transition-colors"
            >
              <Save size={16} />
              Save Changes
            </button>
          </div>
        }
      />

      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <div className="md:col-span-1">
          <div className="bg-[#0b101a] border border-[rgba(255,255,255,0.05)] rounded-xl overflow-hidden flex flex-col p-2 space-y-1">
            {tabs.map(tab => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center gap-3 px-4 py-3 text-sm font-medium rounded-lg transition-colors w-full text-left ${
                  activeTab === tab.id 
                    ? 'bg-[#8b5cf6]/10 text-[#8b5cf6]' 
                    : 'text-[#8490a3] hover:bg-[#141b2a] hover:text-white'
                }`}
              >
                {tab.icon}
                {tab.label}
              </button>
            ))}
          </div>
        </div>
        
        <div className="md:col-span-3">
          <div className="bg-[#0b101a] border border-[rgba(255,255,255,0.05)] rounded-xl p-6 min-h-[500px]">
            {activeTab === 'general' && (
              <div className="space-y-4 max-w-2xl">
                <h3 className="text-lg font-semibold mb-4 text-white">General Information</h3>
                
                <div className="space-y-1">
                  <label className="text-sm text-[#8490a3]">Level Title</label>
                  <input type="text" defaultValue={level.title} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-white focus:outline-none focus:border-[#8b5cf6]" />
                </div>
                
                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-1">
                    <label className="text-sm text-[#8490a3]">Level Number</label>
                    <input type="number" defaultValue={level.levelNumber} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-white focus:outline-none focus:border-[#8b5cf6]" />
                  </div>
                  <div className="space-y-1">
                    <label className="text-sm text-[#8490a3]">XP Reward</label>
                    <input type="number" defaultValue={level.xp} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-white focus:outline-none focus:border-[#8b5cf6]" />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-1">
                    <label className="text-sm text-[#8490a3]">Subject</label>
                    <select defaultValue={level.subject} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-white focus:outline-none focus:border-[#8b5cf6]">
                      <option value="Chemistry">Chemistry</option>
                      <option value="Physics">Physics</option>
                    </select>
                  </div>
                  <div className="space-y-1">
                    <label className="text-sm text-[#8490a3]">Difficulty</label>
                    <select defaultValue={level.difficulty} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-white focus:outline-none focus:border-[#8b5cf6]">
                      <option value="Beginner">Beginner</option>
                      <option value="Intermediate">Intermediate</option>
                      <option value="Advanced">Advanced</option>
                    </select>
                  </div>
                </div>
                
                <div className="space-y-1">
                  <label className="text-sm text-[#8490a3]">Chapter</label>
                  <select defaultValue={level.chapter} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-white focus:outline-none focus:border-[#8b5cf6]">
                    <option value="Laboratory Basics">Laboratory Basics</option>
                    <option value="Matter & Reactions">Matter & Reactions</option>
                    <option value="Laboratory Systems">Laboratory Systems</option>
                  </select>
                </div>
                
                <div className="space-y-1 mt-4">
                  <label className="text-sm text-[#8490a3]">Status</label>
                  <select defaultValue={level.status} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-white focus:outline-none focus:border-[#8b5cf6]">
                    <option value="Draft">Draft</option>
                    <option value="Published">Published</option>
                  </select>
                </div>
              </div>
            )}

            {activeTab !== 'general' && <AdminDesignPanel domain="level" tab={activeTab} />}
          </div>
        </div>
      </div>
    </div>
  );
}

'use client';

import React, { useState } from 'react';
import { useToastStore } from '@/stores/toast.store';
import { 
  Settings, BookOpen, Languages, Brain, Beaker, 
  MonitorPlay, ShieldAlert, Palette, Sparkles, 
  Shield, Save, AlertCircle 
} from 'lucide-react';
import { mockSettings } from '@/mocks/admin/settings';

export default function SettingsPage() {
  const [activeTab, setActiveTab] = useState('General');
  const [hasChanges, setHasChanges] = useState(false);
  const { addToast } = useToastStore();

  const tabs = [
    { id: 'General', icon: Settings },
    { id: 'Subjects', icon: BookOpen },
    { id: 'Languages', icon: Languages },
    { id: 'Learning', icon: Brain },
    { id: 'Laboratory', icon: Beaker },
    { id: 'Simulation', icon: MonitorPlay },
    { id: 'Safety', icon: ShieldAlert },
    { id: 'Appearance', icon: Palette },
    { id: 'Features', icon: Sparkles },
    { id: 'Administration', icon: Shield },
  ];

  const handleSave = () => {
    addToast({ title: 'Settings saved', type: 'success' });
    setHasChanges(false);
  };

  return (
    <div className="p-4 md:p-6 lg:p-8 max-w-7xl mx-auto pb-32">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-[var(--foreground)]">System Settings</h1>
        <p className="text-[var(--muted-foreground)] mt-2">Configure global platform behavior, features, and appearance.</p>
      </div>

      <div className="flex flex-col md:flex-row gap-8">
        {/* Settings Navigation Sidebar */}
        <div className="w-full md:w-64 shrink-0">
          <nav className="flex flex-row md:flex-col gap-1 overflow-x-auto md:overflow-visible pb-4 md:pb-0 hide-scrollbar bg-[var(--card)] p-2 rounded-2xl border border-[var(--border)]">
            {tabs.map(tab => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center gap-3 px-4 py-3 rounded-xl font-medium text-sm transition-all whitespace-nowrap ${
                  activeTab === tab.id 
                    ? 'bg-blue-500/10 text-blue-400' 
                    : 'text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/5'
                }`}
              >
                <tab.icon size={18} />
                {tab.id}
              </button>
            ))}
          </nav>
        </div>

        {/* Settings Content Area */}
        <div className="flex-1 bg-[var(--card)] border border-[var(--border)] rounded-2xl p-6 lg:p-8 min-h-[500px]">
          
          {activeTab === 'General' && (
            <div className="space-y-8 animate-in fade-in duration-300">
              <div>
                <h2 className="text-xl font-semibold mb-6">General Information</h2>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label className="block text-sm font-medium text-[var(--muted-foreground)] mb-2">Application Name</label>
                    <input type="text" defaultValue={mockSettings.general.appName} onChange={() => setHasChanges(true)} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-xl px-4 py-2.5 text-[var(--foreground)] focus:outline-none focus:border-blue-500 transition-colors" />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-[var(--muted-foreground)] mb-2">Admin Dashboard Title</label>
                    <input type="text" defaultValue={mockSettings.general.adminTitle} onChange={() => setHasChanges(true)} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-xl px-4 py-2.5 text-[var(--foreground)] focus:outline-none focus:border-blue-500 transition-colors" />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-[var(--muted-foreground)] mb-2">Support Email</label>
                    <input type="email" defaultValue={mockSettings.general.supportEmail} onChange={() => setHasChanges(true)} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-xl px-4 py-2.5 text-[var(--foreground)] focus:outline-none focus:border-blue-500 transition-colors" />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-[var(--muted-foreground)] mb-2">Environment</label>
                    <select defaultValue={mockSettings.general.environment} onChange={() => setHasChanges(true)} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-xl px-4 py-2.5 text-[var(--foreground)] focus:outline-none focus:border-blue-500 transition-colors">
                      <option>Development</option>
                      <option>Staging</option>
                      <option>Production</option>
                    </select>
                  </div>
                </div>
              </div>

              <hr className="border-[var(--border)]" />

              <div>
                <h2 className="text-xl font-semibold mb-6">Localization & Time</h2>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label className="block text-sm font-medium text-[var(--muted-foreground)] mb-2">Timezone</label>
                    <select defaultValue={mockSettings.general.timezone} onChange={() => setHasChanges(true)} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-xl px-4 py-2.5 text-[var(--foreground)] focus:outline-none focus:border-blue-500 transition-colors">
                      <option>Asia/Tashkent</option>
                      <option>Europe/London</option>
                      <option>America/New_York</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-[var(--muted-foreground)] mb-2">Date Format</label>
                    <select defaultValue={mockSettings.general.dateFormat} onChange={() => setHasChanges(true)} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-xl px-4 py-2.5 text-[var(--foreground)] focus:outline-none focus:border-blue-500 transition-colors">
                      <option>DD.MM.YYYY</option>
                      <option>MM/DD/YYYY</option>
                      <option>YYYY-MM-DD</option>
                    </select>
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'Subjects' && (
            <div className="space-y-6 animate-in fade-in duration-300">
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-xl font-semibold">Active Subjects</h2>
                <button className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-sm font-medium rounded-lg transition-colors">Add Subject</button>
              </div>
              
              <div className="space-y-4">
                {mockSettings.subjects.map(subject => (
                  <div key={subject.id} className="flex items-center justify-between p-4 border border-[var(--border)] rounded-xl bg-[var(--input)]">
                    <div className="flex items-center gap-4">
                      <div className="w-3 h-3 rounded-full" style={{ backgroundColor: subject.accent.includes('/') ? subject.accent.split('/')[0] : subject.accent }}></div>
                      <span className="font-medium text-[var(--foreground)]">{subject.name}</span>
                    </div>
                    <label className="relative inline-flex items-center cursor-pointer">
                      <input type="checkbox" defaultChecked={subject.enabled} onChange={() => setHasChanges(true)} className="sr-only peer" />
                      <div className="w-11 h-6 bg-black/50 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-500"></div>
                    </label>
                  </div>
                ))}
              </div>
            </div>
          )}

          {activeTab === 'Learning' && (
            <div className="space-y-8 animate-in fade-in duration-300">
              <div>
                <h2 className="text-xl font-semibold mb-6">Gamification & Progress</h2>
                <div className="space-y-4">
                  {[
                    { id: 'enableLevels', label: 'Enable Level System', desc: 'Students gain levels as they complete scenarios.' },
                    { id: 'enableXp', label: 'Enable Experience Points (XP)', desc: 'Award XP for actions and scenario completion.' },
                    { id: 'enableBadges', label: 'Enable Badges', desc: 'Show achievement badges on student profiles.' },
                    { id: 'enablePrerequisites', label: 'Enforce Prerequisites', desc: 'Prevent starting advanced scenarios without completing basics.' },
                  ].map(setting => (
                    <div key={setting.id} className="flex items-center justify-between p-4 border border-[var(--border)] rounded-xl hover:border-blue-500/50 transition-colors">
                      <div>
                        <div className="font-medium text-[var(--foreground)]">{setting.label}</div>
                        <div className="text-sm text-[var(--muted-foreground)] mt-0.5">{setting.desc}</div>
                      </div>
                      <label className="relative inline-flex items-center cursor-pointer shrink-0">
                        <input type="checkbox" defaultChecked={(mockSettings.learning as any)[setting.id]} onChange={() => setHasChanges(true)} className="sr-only peer" />
                        <div className="w-11 h-6 bg-black/50 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-500"></div>
                      </label>
                    </div>
                  ))}
                </div>
              </div>

              <hr className="border-[var(--border)]" />

              <div>
                <h2 className="text-xl font-semibold mb-6">Scoring Defaults</h2>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label className="block text-sm font-medium text-[var(--muted-foreground)] mb-2">Default XP per Scenario</label>
                    <input type="number" defaultValue={mockSettings.learning.defaultXp} onChange={() => setHasChanges(true)} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-xl px-4 py-2.5 text-[var(--foreground)] focus:outline-none focus:border-blue-500 transition-colors" />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-[var(--muted-foreground)] mb-2">Minimum Passing Score (%)</label>
                    <input type="number" defaultValue={mockSettings.learning.minPassingScore} onChange={() => setHasChanges(true)} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-xl px-4 py-2.5 text-[var(--foreground)] focus:outline-none focus:border-blue-500 transition-colors" />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-[var(--muted-foreground)] mb-2">Max Attempts Allowed</label>
                    <input type="number" defaultValue={mockSettings.learning.maxAttempts} onChange={() => setHasChanges(true)} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-xl px-4 py-2.5 text-[var(--foreground)] focus:outline-none focus:border-blue-500 transition-colors" />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-[var(--muted-foreground)] mb-2">Hint Penalty (%)</label>
                    <input type="number" defaultValue={mockSettings.learning.hintPenalty} onChange={() => setHasChanges(true)} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-xl px-4 py-2.5 text-[var(--foreground)] focus:outline-none focus:border-blue-500 transition-colors" />
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'Languages' && (
            <div className="space-y-8 animate-in fade-in duration-300">
              <div>
                <h2 className="text-xl font-semibold mb-6">Language Settings</h2>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label className="block text-sm font-medium text-[var(--muted-foreground)] mb-2">Default Language</label>
                    <select defaultValue={mockSettings.languages.default} onChange={() => setHasChanges(true)} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-xl px-4 py-2.5 text-[var(--foreground)] focus:outline-none focus:border-blue-500 transition-colors">
                      <option>English</option>
                      <option>Russian</option>
                      <option>Uzbek</option>
                    </select>
                  </div>
                </div>
              </div>
              <div className="space-y-4">
                <h3 className="text-lg font-medium text-[var(--foreground)]">Available Languages</h3>
                {mockSettings.languages.available.map(lang => (
                  <div key={lang} className="flex items-center justify-between p-4 border border-[var(--border)] rounded-xl bg-[var(--input)]">
                    <span className="font-medium text-[var(--foreground)]">{lang}</span>
                    <label className="relative inline-flex items-center cursor-pointer">
                      <input type="checkbox" defaultChecked={true} onChange={() => setHasChanges(true)} className="sr-only peer" />
                      <div className="w-11 h-6 bg-black/50 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-500"></div>
                    </label>
                  </div>
                ))}
              </div>
            </div>
          )}

          {activeTab === 'Laboratory' && (
            <div className="space-y-8 animate-in fade-in duration-300">
              <div>
                <h2 className="text-xl font-semibold mb-6">Laboratory Defaults</h2>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                  <div>
                    <label className="block text-sm font-medium text-[var(--muted-foreground)] mb-2">Maximum Objects Limit</label>
                    <input type="number" defaultValue={mockSettings.laboratory.objectsLimit} onChange={() => setHasChanges(true)} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-xl px-4 py-2.5 text-[var(--foreground)] focus:outline-none focus:border-blue-500 transition-colors" />
                  </div>
                </div>
                <div className="space-y-4">
                  {[
                    { id: 'workspaceGrid', label: 'Show Workspace Grid', desc: 'Display a snapping grid in the laboratory view.', value: mockSettings.laboratory.workspaceGrid },
                    { id: 'autosave', label: 'Enable Autosave', desc: 'Automatically save laboratory state periodically.', value: mockSettings.laboratory.autosave },
                    { id: 'sharing', label: 'Allow Sharing', desc: 'Allow users to share laboratory workspaces.', value: mockSettings.laboratory.sharing },
                  ].map(setting => (
                    <div key={setting.id} className="flex items-center justify-between p-4 border border-[var(--border)] rounded-xl hover:border-blue-500/50 transition-colors">
                      <div>
                        <div className="font-medium text-[var(--foreground)]">{setting.label}</div>
                        <div className="text-sm text-[var(--muted-foreground)] mt-0.5">{setting.desc}</div>
                      </div>
                      <label className="relative inline-flex items-center cursor-pointer shrink-0">
                        <input type="checkbox" defaultChecked={setting.value} onChange={() => setHasChanges(true)} className="sr-only peer" />
                        <div className="w-11 h-6 bg-black/50 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-500"></div>
                      </label>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {activeTab === 'Simulation' && (
            <div className="space-y-8 animate-in fade-in duration-300">
              <div>
                <h2 className="text-xl font-semibold mb-6">Simulation Engine</h2>
                <div className="space-y-4">
                  {[
                    { id: 'enableEvaporation', label: 'Enable Evaporation', desc: 'Simulate evaporation of liquids over time.', value: mockSettings.simulation.enableEvaporation },
                    { id: 'fluidTransfer', label: 'Realistic Fluid Transfer', desc: 'Use advanced physics for pouring and mixing fluids.', value: mockSettings.simulation.fluidTransfer },
                    { id: 'thermalShock', label: 'Thermal Shock', desc: 'Glassware can shatter due to extreme temperature changes.', value: mockSettings.simulation.thermalShock },
                    { id: 'particles', label: 'Particle Effects', desc: 'Show visual particle effects for reactions and phase changes.', value: mockSettings.simulation.particles },
                  ].map(setting => (
                    <div key={setting.id} className="flex items-center justify-between p-4 border border-[var(--border)] rounded-xl hover:border-blue-500/50 transition-colors">
                      <div>
                        <div className="font-medium text-[var(--foreground)]">{setting.label}</div>
                        <div className="text-sm text-[var(--muted-foreground)] mt-0.5">{setting.desc}</div>
                      </div>
                      <label className="relative inline-flex items-center cursor-pointer shrink-0">
                        <input type="checkbox" defaultChecked={setting.value} onChange={() => setHasChanges(true)} className="sr-only peer" />
                        <div className="w-11 h-6 bg-black/50 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-500"></div>
                      </label>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {activeTab === 'Safety' && (
            <div className="space-y-8 animate-in fade-in duration-300">
              <div>
                <h2 className="text-xl font-semibold mb-6">Safety Mechanisms</h2>
                <div className="space-y-4">
                  {[
                    { id: 'enableWarnings', label: 'Safety Warnings', desc: 'Warn users before dangerous interactions.', value: mockSettings.safety.enableWarnings },
                    { id: 'pauseOnCriticalFailure', label: 'Pause on Critical Failure', desc: 'Pause the simulation if an explosion or major hazard occurs.', value: mockSettings.safety.pauseOnCriticalFailure },
                  ].map(setting => (
                    <div key={setting.id} className="flex items-center justify-between p-4 border border-[var(--border)] rounded-xl hover:border-blue-500/50 transition-colors">
                      <div>
                        <div className="font-medium text-[var(--foreground)]">{setting.label}</div>
                        <div className="text-sm text-[var(--muted-foreground)] mt-0.5">{setting.desc}</div>
                      </div>
                      <label className="relative inline-flex items-center cursor-pointer shrink-0">
                        <input type="checkbox" defaultChecked={setting.value} onChange={() => setHasChanges(true)} className="sr-only peer" />
                        <div className="w-11 h-6 bg-black/50 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-500"></div>
                      </label>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {activeTab === 'Appearance' && (
            <div className="space-y-8 animate-in fade-in duration-300">
              <div>
                <h2 className="text-xl font-semibold mb-6">User Interface</h2>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                  <div>
                    <label className="block text-sm font-medium text-[var(--muted-foreground)] mb-2">Default Theme</label>
                    <select defaultValue={mockSettings.appearance.theme} onChange={() => setHasChanges(true)} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-xl px-4 py-2.5 text-[var(--foreground)] focus:outline-none focus:border-blue-500 transition-colors">
                      <option>Dark</option>
                      <option>Light</option>
                      <option>System</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-[var(--muted-foreground)] mb-2">Density</label>
                    <select defaultValue={mockSettings.appearance.density} onChange={() => setHasChanges(true)} className="w-full bg-[var(--input)] border border-[var(--border)] rounded-xl px-4 py-2.5 text-[var(--foreground)] focus:outline-none focus:border-blue-500 transition-colors">
                      <option>Compact</option>
                      <option>Comfortable</option>
                      <option>Spacious</option>
                    </select>
                  </div>
                </div>
                <div className="space-y-4">
                  <div className="flex items-center justify-between p-4 border border-[var(--border)] rounded-xl hover:border-blue-500/50 transition-colors">
                    <div>
                      <div className="font-medium text-[var(--foreground)]">Enable Animations</div>
                      <div className="text-sm text-[var(--muted-foreground)] mt-0.5">Use UI animations and transitions.</div>
                    </div>
                    <label className="relative inline-flex items-center cursor-pointer shrink-0">
                      <input type="checkbox" defaultChecked={mockSettings.appearance.animations} onChange={() => setHasChanges(true)} className="sr-only peer" />
                      <div className="w-11 h-6 bg-black/50 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-500"></div>
                    </label>
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'Features' && (
            <div className="space-y-8 animate-in fade-in duration-300">
              <div>
                <h2 className="text-xl font-semibold mb-6">Feature Flags</h2>
                <div className="space-y-4">
                  {[
                    { id: 'sandboxBeta', label: 'Sandbox (Beta)', desc: 'Enable experimental sandbox mode.', value: mockSettings.features.sandboxBeta },
                    { id: 'achievements', label: 'Achievements System', desc: 'Enable the achievements and rewards module.', value: mockSettings.features.achievements },
                    { id: 'aiAssistant', label: 'AI Assistant', desc: 'Enable AI-powered contextual help.', value: mockSettings.features.aiAssistant },
                  ].map(setting => (
                    <div key={setting.id} className="flex items-center justify-between p-4 border border-[var(--border)] rounded-xl hover:border-blue-500/50 transition-colors">
                      <div>
                        <div className="font-medium text-[var(--foreground)]">{setting.label}</div>
                        <div className="text-sm text-[var(--muted-foreground)] mt-0.5">{setting.desc}</div>
                      </div>
                      <label className="relative inline-flex items-center cursor-pointer shrink-0">
                        <input type="checkbox" defaultChecked={setting.value} onChange={() => setHasChanges(true)} className="sr-only peer" />
                        <div className="w-11 h-6 bg-black/50 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-500"></div>
                      </label>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {activeTab === 'Administration' && (
            <div className="space-y-8 animate-in fade-in duration-300">
              <div>
                <h2 className="text-xl font-semibold mb-6">Admin Settings</h2>
                <div className="space-y-4">
                  <div className="flex items-center justify-between p-4 border border-[var(--border)] rounded-xl hover:border-blue-500/50 transition-colors">
                    <div>
                      <div className="font-medium text-[var(--foreground)]">Show Entity IDs</div>
                      <div className="text-sm text-[var(--muted-foreground)] mt-0.5">Display internal IDs in tables and lists for debugging.</div>
                    </div>
                    <label className="relative inline-flex items-center cursor-pointer shrink-0">
                      <input type="checkbox" defaultChecked={mockSettings.administration.showEntityIds} onChange={() => setHasChanges(true)} className="sr-only peer" />
                      <div className="w-11 h-6 bg-black/50 peer-focus:outline-none rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-500"></div>
                    </label>
                  </div>
                  
                  <div className="p-4 border border-red-500/30 rounded-xl bg-red-500/5 mt-8">
                    <h3 className="font-semibold text-red-500 mb-2">Danger Zone</h3>
                    <p className="text-sm text-[var(--muted-foreground)] mb-4">Actions here can cause permanent data loss. Please proceed with caution.</p>
                    <button type="button" className="px-4 py-2 bg-red-500/10 hover:bg-red-500/20 text-red-500 text-sm font-medium rounded-lg transition-colors border border-red-500/20">
                      Reset Mock Data
                    </button>
                  </div>
                </div>
              </div>
            </div>
          )}

        </div>
      </div>

      {/* Sticky Save Footer */}
      {hasChanges && (
        <div className="fixed bottom-0 left-0 right-0 p-4 bg-[var(--card)] border-t border-[var(--border)] flex items-center justify-center lg:justify-between z-50 px-6 lg:px-[calc(50vw-36rem)] shadow-[0_-10px_40px_rgba(0,0,0,0.5)]" style={{ animation: 'slideInUp 0.3s ease-out' }}>
          <div className="hidden lg:flex items-center gap-3 text-amber-500 bg-amber-500/10 px-4 py-2 rounded-lg border border-amber-500/20">
            <AlertCircle size={18} />
            <span className="font-medium text-sm">You have unsaved changes</span>
          </div>
          <div className="flex items-center gap-4">
            <button onClick={() => setHasChanges(false)} className="px-5 py-2.5 text-sm font-medium text-[var(--muted-foreground)] hover:text-[var(--foreground)] hover:bg-white/5 rounded-lg transition-colors">
              Discard Changes
            </button>
            <button onClick={handleSave} className="px-6 py-2.5 text-sm font-bold text-white bg-blue-600 hover:bg-blue-700 rounded-lg transition-colors flex items-center gap-2 shadow-lg shadow-blue-500/25">
              <Save size={18} /> Save All Changes
            </button>
          </div>
        </div>
      )}
      
      <style jsx global>{`
        @keyframes slideInUp {
          from { transform: translateY(100%); opacity: 0; }
          to { transform: translateY(0); opacity: 1; }
        }
      `}</style>
    </div>
  );
}

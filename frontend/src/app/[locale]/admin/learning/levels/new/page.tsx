'use client';
import React, { useState } from 'react';
import AdminPageHeader from '@/widgets/admin/AdminPageHeader';
import AdminDesignPanel from '@/widgets/admin/AdminDesignPanel';
import { useRouter } from 'next/navigation';
import { 
  ArrowLeft, 
  Settings, 
  GraduationCap, 
  ListOrdered, 
  FlaskConical,
  Lock,
  Award,
  Unlock,
  Globe,
  Play,
  Save,
  Plus,
  Trash2,
  GripVertical
} from 'lucide-react';

const tabs = [
  { id: 'general', label: 'General', icon: <Settings size={16} /> },
  { id: 'learning', label: 'Learning', icon: <GraduationCap size={16} /> },
  { id: 'steps', label: 'Steps', icon: <ListOrdered size={16} /> },
  { id: 'scenario', label: 'Scenario', icon: <FlaskConical size={16} /> },
  { id: 'requirements', label: 'Requirements', icon: <Lock size={16} /> },
  { id: 'rewards', label: 'Rewards', icon: <Award size={16} /> },
  { id: 'unlocks', label: 'Unlocks', icon: <Unlock size={16} /> },
  { id: 'localization', label: 'Localization', icon: <Globe size={16} /> },
  { id: 'preview', label: 'Preview', icon: <Play size={16} /> },
];

export default function LevelEditorPage({ params }: { params?: { id: string } }) {
  const router = useRouter();
  const [activeTab, setActiveTab] = useState('general');
  const isEdit = !!params?.id;

  // General State
  const [general, setGeneral] = useState({ title: '', levelNumber: 1, xp: 100, subject: 'Chemistry', difficulty: 'Beginner', chapter: 'Laboratory Basics' });
  
  // Learning State
  const [objectives, setObjectives] = useState([{ id: 1, text: '' }]);
  const [skills, setSkills] = useState(['']);

  // Steps State
  const [steps, setSteps] = useState([{ id: 1, type: 'Information', content: '' }]);

  // Scenario State
  const [scenario, setScenario] = useState({ primary: '', secondary: '' });

  // Requirements State
  const [requirements, setRequirements] = useState([{ id: 1, type: 'Level Completion', target: '' }]);

  // Rewards State
  const [rewards, setRewards] = useState({ baseXP: 100, conditionalXP: [{ id: 1, condition: '', xp: 0 }], badges: [''] });

  // Unlocks State
  const [unlocks, setUnlocks] = useState([{ id: 1, type: 'Equipment', item: '' }]);

  // Localization State
  const [localization, setLocalization] = useState([{ lang: 'en', title: '' }]);

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
        title={isEdit ? `Edit Level: ${params.id}` : "Create New Level"} 
        description="Configure your learning level details"
        actions={
          <div className="flex gap-2">
            <button className="px-4 py-2 bg-[#141b2a] hover:bg-[#1a2333] border border-[rgba(255,255,255,0.1)] text-white rounded-lg text-sm font-medium transition-colors">
              Save Draft
            </button>
            <button className="flex items-center gap-2 px-4 py-2 bg-[#8b5cf6] hover:bg-[#7c3aed] text-white rounded-lg text-sm font-medium transition-colors">
              <Save size={16} />
              Publish
            </button>
          </div>
        }
      />

      <div className="grid grid-cols-1 md:grid-cols-4 lg:grid-cols-5 gap-6">
        <div className="md:col-span-1">
          <div className="bg-[#0b101a] border border-[rgba(255,255,255,0.05)] rounded-xl overflow-hidden flex flex-col p-2 space-y-1 sticky top-6">
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
        
        <div className="md:col-span-3 lg:col-span-4">
          <div className="bg-[#0b101a] border border-[rgba(255,255,255,0.05)] rounded-xl p-6 min-h-[600px]">
            
            {/* GENERAL TAB */}
            {activeTab === 'general' && (
              <div className="space-y-6 max-w-2xl">
                <h3 className="text-lg font-semibold text-white">General Information</h3>
                
                <div className="space-y-1">
                  <label className="text-sm text-[#8490a3]">Level Title</label>
                  <input type="text" value={general.title} onChange={e => setGeneral({...general, title: e.target.value})} placeholder="e.g. Первое нагревание" className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-white focus:outline-none focus:border-[#8b5cf6]" />
                </div>
                
                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-1">
                    <label className="text-sm text-[#8490a3]">Level Number</label>
                    <input type="number" value={general.levelNumber} onChange={e => setGeneral({...general, levelNumber: parseInt(e.target.value)})} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-white focus:outline-none focus:border-[#8b5cf6]" />
                  </div>
                  <div className="space-y-1">
                    <label className="text-sm text-[#8490a3]">XP Reward</label>
                    <input type="number" value={general.xp} onChange={e => setGeneral({...general, xp: parseInt(e.target.value)})} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-white focus:outline-none focus:border-[#8b5cf6]" />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div className="space-y-1">
                    <label className="text-sm text-[#8490a3]">Subject</label>
                    <select value={general.subject} onChange={e => setGeneral({...general, subject: e.target.value})} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-white focus:outline-none focus:border-[#8b5cf6]">
                      <option>Chemistry</option>
                      <option>Physics</option>
                      <option>Biology</option>
                    </select>
                  </div>
                  <div className="space-y-1">
                    <label className="text-sm text-[#8490a3]">Difficulty</label>
                    <select value={general.difficulty} onChange={e => setGeneral({...general, difficulty: e.target.value})} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-white focus:outline-none focus:border-[#8b5cf6]">
                      <option>Beginner</option>
                      <option>Intermediate</option>
                      <option>Advanced</option>
                    </select>
                  </div>
                </div>
                
                <div className="space-y-1">
                  <label className="text-sm text-[#8490a3]">Chapter</label>
                  <select value={general.chapter} onChange={e => setGeneral({...general, chapter: e.target.value})} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-white focus:outline-none focus:border-[#8b5cf6]">
                    <option>Laboratory Basics</option>
                    <option>Matter & Reactions</option>
                    <option>Laboratory Systems</option>
                  </select>
                </div>
              </div>
            )}

            {/* LEARNING TAB */}
            {activeTab === 'learning' && (
              <div className="space-y-8 max-w-2xl">
                <div>
                  <h3 className="text-lg font-semibold text-white mb-4">Learning Objectives</h3>
                  <div className="space-y-3">
                    {objectives.map((obj, i) => (
                      <div key={obj.id} className="flex gap-2">
                        <input type="text" value={obj.text} onChange={e => {
                          const newObj = [...objectives];
                          newObj[i].text = e.target.value;
                          setObjectives(newObj);
                        }} placeholder="Enter learning objective" className="flex-1 bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-white focus:outline-none focus:border-[#8b5cf6]" />
                        <button onClick={() => setObjectives(objectives.filter(o => o.id !== obj.id))} className="p-2 text-red-400 hover:bg-red-400/10 rounded-lg"><Trash2 size={18} /></button>
                      </div>
                    ))}
                    <button onClick={() => setObjectives([...objectives, { id: Date.now(), text: '' }])} className="text-sm text-[#8b5cf6] hover:text-[#7c3aed] flex items-center gap-1"><Plus size={16} /> Add Objective</button>
                  </div>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-white mb-4">Skills Acquired</h3>
                  <div className="flex flex-wrap gap-2 mb-3">
                    {skills.map((skill, i) => (
                      <div key={i} className="flex items-center gap-1 bg-[#141b2a] border border-[rgba(255,255,255,0.1)] px-3 py-1.5 rounded-full text-sm">
                        <input value={skill} onChange={e => {
                          const newSkills = [...skills];
                          newSkills[i] = e.target.value;
                          setSkills(newSkills);
                        }} className="bg-transparent outline-none w-24" placeholder="Skill name" />
                        <button onClick={() => setSkills(skills.filter((_, index) => index !== i))} className="text-[#8490a3] hover:text-red-400"><Trash2 size={14} /></button>
                      </div>
                    ))}
                  </div>
                  <button onClick={() => setSkills([...skills, ''])} className="text-sm text-[#8b5cf6] hover:text-[#7c3aed] flex items-center gap-1"><Plus size={16} /> Add Skill Tag</button>
                </div>
              </div>
            )}

            {/* STEPS TAB */}
            {activeTab === 'steps' && (
              <div className="space-y-6">
                <div className="flex justify-between items-center">
                  <h3 className="text-lg font-semibold text-white">Steps Timeline</h3>
                  <button onClick={() => setSteps([...steps, { id: Date.now(), type: 'Action', content: '' }])} className="px-3 py-1.5 bg-[#8b5cf6] text-white rounded-lg text-sm flex items-center gap-1"><Plus size={16} /> Add Step</button>
                </div>
                
                <div className="space-y-4">
                  {steps.map((step, i) => (
                    <div key={step.id} className="flex gap-4 items-start bg-[#141b2a] border border-[rgba(255,255,255,0.1)] p-4 rounded-xl">
                      <div className="mt-2 text-[#8490a3] cursor-grab"><GripVertical size={20} /></div>
                      <div className="w-8 h-8 rounded-full bg-[#0b101a] border border-[rgba(255,255,255,0.1)] flex items-center justify-center font-bold text-sm shrink-0">{i + 1}</div>
                      <div className="flex-1 space-y-3">
                        <div className="flex gap-4">
                          <select value={step.type} onChange={e => {
                            const newSteps = [...steps];
                            newSteps[i].type = e.target.value;
                            setSteps(newSteps);
                          }} className="bg-[#0b101a] border border-[rgba(255,255,255,0.1)] rounded-lg px-3 py-1.5 text-sm text-white focus:outline-none focus:border-[#8b5cf6]">
                            <option>Information</option>
                            <option>Action</option>
                            <option>Quiz</option>
                            <option>Observation</option>
                          </select>
                        </div>
                        <textarea value={step.content} onChange={e => {
                          const newSteps = [...steps];
                          newSteps[i].content = e.target.value;
                          setSteps(newSteps);
                        }} rows={3} placeholder="Step description or content..." className="w-full bg-[#0b101a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-white focus:outline-none focus:border-[#8b5cf6] resize-none"></textarea>
                      </div>
                      <button onClick={() => setSteps(steps.filter(s => s.id !== step.id))} className="text-red-400 hover:bg-red-400/10 p-2 rounded-lg mt-1"><Trash2 size={18} /></button>
                    </div>
                  ))}
                  {steps.length === 0 && <div className="text-center text-[#8490a3] py-8">No steps defined. Add a step to begin building the timeline.</div>}
                </div>
              </div>
            )}

            {/* SCENARIO TAB */}
            {activeTab === 'scenario' && (
              <div className="space-y-6 max-w-2xl">
                <h3 className="text-lg font-semibold text-white">Scenario Attachment</h3>
                <p className="text-sm text-[#8490a3]">Link this level to a simulation scenario from the Engine.</p>
                
                <div className="space-y-1">
                  <label className="text-sm text-[#8490a3]">Primary Scenario</label>
                  <select value={scenario.primary} onChange={e => setScenario({...scenario, primary: e.target.value})} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-white focus:outline-none focus:border-[#8b5cf6]">
                    <option value="">Select scenario...</option>
                    <option value="basic-heating">Basic Heating (sc-01)</option>
                    <option value="titration-1">Titration Setup (sc-02)</option>
                    <option value="microscope-1">Microscope Intro (sc-03)</option>
                  </select>
                </div>
                
                <div className="space-y-1">
                  <label className="text-sm text-[#8490a3]">Secondary Scenario (Optional)</label>
                  <select value={scenario.secondary} onChange={e => setScenario({...scenario, secondary: e.target.value})} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-white focus:outline-none focus:border-[#8b5cf6]">
                    <option value="">None</option>
                    <option value="safety-check">Safety Protocol Check</option>
                  </select>
                </div>
              </div>
            )}

            {/* REQUIREMENTS TAB */}
            {activeTab === 'requirements' && (
              <div className="space-y-6 max-w-2xl">
                <div className="flex justify-between items-center">
                  <h3 className="text-lg font-semibold text-white">Prerequisites</h3>
                  <button onClick={() => setRequirements([...requirements, { id: Date.now(), type: 'Level Completion', target: '' }])} className="px-3 py-1.5 bg-[#8b5cf6] text-white rounded-lg text-sm flex items-center gap-1"><Plus size={16} /> Add Requirement</button>
                </div>

                <div className="space-y-3">
                  {requirements.map((req, i) => (
                    <div key={req.id} className="flex gap-2 items-center">
                      <select value={req.type} onChange={e => {
                        const newReqs = [...requirements];
                        newReqs[i].type = e.target.value;
                        setRequirements(newReqs);
                      }} className="bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#8b5cf6]">
                        <option>Level Completion</option>
                        <option>Minimum Level</option>
                        <option>Badge Required</option>
                      </select>
                      <input type="text" value={req.target} onChange={e => {
                        const newReqs = [...requirements];
                        newReqs[i].target = e.target.value;
                        setRequirements(newReqs);
                      }} placeholder="Target ID or Value" className="flex-1 bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-sm text-white focus:outline-none focus:border-[#8b5cf6]" />
                      <button onClick={() => setRequirements(requirements.filter(r => r.id !== req.id))} className="p-2 text-red-400 hover:bg-red-400/10 rounded-lg"><Trash2 size={18} /></button>
                    </div>
                  ))}
                  {requirements.length === 0 && <div className="text-sm text-[#8490a3]">No prerequisites. This level will be available immediately.</div>}
                </div>
              </div>
            )}

            {/* REWARDS TAB */}
            {activeTab === 'rewards' && (
              <div className="space-y-8 max-w-2xl">
                <div>
                  <h3 className="text-lg font-semibold text-white mb-4">Completion Rewards</h3>
                  <div className="space-y-1">
                    <label className="text-sm text-[#8490a3]">Base XP</label>
                    <input type="number" value={rewards.baseXP} onChange={e => setRewards({...rewards, baseXP: parseInt(e.target.value)})} className="w-full bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-white focus:outline-none focus:border-[#8b5cf6]" />
                  </div>
                </div>

                <div>
                  <div className="flex justify-between items-center mb-4">
                    <h3 className="text-md font-semibold text-white">Conditional Bonus XP</h3>
                    <button onClick={() => setRewards({...rewards, conditionalXP: [...rewards.conditionalXP, { id: Date.now(), condition: '', xp: 0 }]})} className="text-sm text-[#8b5cf6] hover:text-[#7c3aed] flex items-center gap-1"><Plus size={16} /> Add Bonus</button>
                  </div>
                  <div className="space-y-3">
                    {rewards.conditionalXP.map((cxp, i) => (
                      <div key={cxp.id} className="flex gap-2">
                        <input type="text" value={cxp.condition} onChange={e => {
                          const newCxp = [...rewards.conditionalXP];
                          newCxp[i].condition = e.target.value;
                          setRewards({...rewards, conditionalXP: newCxp});
                        }} placeholder="Condition (e.g. Under 5 mins)" className="flex-1 bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-sm text-white focus:outline-none focus:border-[#8b5cf6]" />
                        <input type="number" value={cxp.xp} onChange={e => {
                          const newCxp = [...rewards.conditionalXP];
                          newCxp[i].xp = parseInt(e.target.value);
                          setRewards({...rewards, conditionalXP: newCxp});
                        }} placeholder="XP" className="w-24 bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-sm text-white focus:outline-none focus:border-[#8b5cf6]" />
                        <button onClick={() => setRewards({...rewards, conditionalXP: rewards.conditionalXP.filter(c => c.id !== cxp.id)})} className="p-2 text-red-400 hover:bg-red-400/10 rounded-lg"><Trash2 size={18} /></button>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            )}

            {/* UNLOCKS TAB */}
            {activeTab === 'unlocks' && (
              <div className="space-y-6 max-w-2xl">
                <div className="flex justify-between items-center">
                  <h3 className="text-lg font-semibold text-white">Content Unlocks</h3>
                  <button onClick={() => setUnlocks([...unlocks, { id: Date.now(), type: 'Equipment', item: '' }])} className="px-3 py-1.5 bg-[#8b5cf6] text-white rounded-lg text-sm flex items-center gap-1"><Plus size={16} /> Add Unlock</button>
                </div>

                <div className="space-y-3">
                  {unlocks.map((unlock, i) => (
                    <div key={unlock.id} className="flex gap-2 items-center">
                      <select value={unlock.type} onChange={e => {
                        const newUnlocks = [...unlocks];
                        newUnlocks[i].type = e.target.value;
                        setUnlocks(newUnlocks);
                      }} className="bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-[#8b5cf6]">
                        <option>Equipment</option>
                        <option>Scenario</option>
                        <option>Level</option>
                        <option>Badge</option>
                      </select>
                      <input type="text" value={unlock.item} onChange={e => {
                        const newUnlocks = [...unlocks];
                        newUnlocks[i].item = e.target.value;
                        setUnlocks(newUnlocks);
                      }} placeholder="Item ID" className="flex-1 bg-[#141b2a] border border-[rgba(255,255,255,0.1)] rounded-lg px-4 py-2 text-sm text-white focus:outline-none focus:border-[#8b5cf6]" />
                      <button onClick={() => setUnlocks(unlocks.filter(u => u.id !== unlock.id))} className="p-2 text-red-400 hover:bg-red-400/10 rounded-lg"><Trash2 size={18} /></button>
                    </div>
                  ))}
                  {unlocks.length === 0 && <div className="text-sm text-[#8490a3]">No unlocks configured.</div>}
                </div>
              </div>
            )}

            {/* LOCALIZATION & PREVIEW */}
            {['localization', 'preview'].includes(activeTab) && <AdminDesignPanel domain="level" tab={activeTab} />}

          </div>
        </div>
      </div>
    </div>
  );
}

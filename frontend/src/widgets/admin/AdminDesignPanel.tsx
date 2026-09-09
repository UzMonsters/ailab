'use client';

import { Activity, CheckCircle2, CircleGauge, Globe2, Play, Sparkles } from 'lucide-react';

type Domain = 'equipment' | 'scenario' | 'level';

const copy: Record<Domain, Record<string, { eyebrow: string; title: string; description: string; fields: string[]; metrics: [string, string][] }>> = {
  equipment: {
    Pressure: { eyebrow: 'Physics profile', title: 'Pressure envelope', description: 'Define the operating window and the visual warning thresholds.', fields: ['Maximum pressure · 2.4 bar', 'Relief valve · Automatic', 'Failure behavior · Controlled vent'], metrics: [['Safe range', '0–1.8 bar'], ['Warning', '1.9 bar'], ['Critical', '2.4 bar']] },
    Mechanical: { eyebrow: 'Interaction model', title: 'Mechanical behavior', description: 'Tune placement, collision and attachment behavior on the lab canvas.', fields: ['Mass · 112 g', 'Center of gravity · 42% height', 'Snap profile · Vessel / bench'], metrics: [['Anchors', '3'], ['Rotation', 'Free'], ['Stability', 'High']] },
    Compatibility: { eyebrow: 'Capability matrix', title: 'Compatible operations', description: 'A visual matrix of supported media and laboratory connections.', fields: ['Liquids · Full support', 'Gas transfer · Adapter required', 'Direct flame · Limited'], metrics: [['Supported', '18'], ['Conditional', '4'], ['Blocked', '2']] },
    Simulation: { eyebrow: 'Engine behavior', title: 'Simulation profile', description: 'Choose how the apparatus participates in thermal and fluid calculations.', fields: ['Solver mode · Real-time', 'Thermal coupling · Enabled', 'Evaporation model · Standard'], metrics: [['Tick rate', '60 Hz'], ['Accuracy', 'High'], ['Cost', '0.7×']] },
    Scenarios: { eyebrow: 'Usage map', title: 'Scenario coverage', description: 'See where this equipment is presented, required or assessed.', fields: ['Basic heating · Required', 'Acid–base titration · Available', 'Safety onboarding · Demonstration'], metrics: [['Published', '12'], ['Draft', '3'], ['Completion', '86%']] },
    Localization: { eyebrow: 'Translation desk', title: 'Localized equipment copy', description: 'Review display names, descriptions and safety labels by language.', fields: ['Russian · Complete', 'Uzbek · Needs review', 'English · Complete'], metrics: [['Coverage', '92%'], ['Strings', '27'], ['Warnings', '1']] },
    Preview: { eyebrow: 'Canvas preview', title: '250 ml Beaker', description: 'A compact presentation of the object as learners will encounter it.', fields: ['Glass vessel · Borosilicate', 'Capacity · 250 ml', 'Ports · Top, base, sensor'], metrics: [['Heat', '180 °C'], ['Pressure', '2.4 bar'], ['Status', 'Ready']] },
  },
  scenario: {
    Results: { eyebrow: 'Assessment output', title: 'Result states', description: 'Shape the success, partial success and failure summaries.', fields: ['Success · Target temperature reached', 'Partial · Completed with one hint', 'Failure · Safety threshold exceeded'], metrics: [['Checks', '6'], ['Required', '4'], ['Score cap', '100']] },
    Hints: { eyebrow: 'Guidance ladder', title: 'Progressive hints', description: 'Reveal help gradually without taking control away from the learner.', fields: ['01 · Inspect the heat source', '02 · Check vessel placement', '03 · Compare the thermometer reading'], metrics: [['Hints', '3'], ['XP cost', '−5'], ['Delay', '18 sec']] },
    Rewards: { eyebrow: 'Motivation layer', title: 'Completion rewards', description: 'Preview the reward package and optional mastery bonuses.', fields: ['Base reward · 120 XP', 'No-hint bonus · 30 XP', 'Badge · Thermal Observer'], metrics: [['Total XP', '150'], ['Badge', '1'], ['Unlocks', '2']] },
    Localization: { eyebrow: 'Translation desk', title: 'Scenario language coverage', description: 'Keep instructions, hints and result messages aligned.', fields: ['Russian · 34 / 34 strings', 'Uzbek · 31 / 34 strings', 'English · 34 / 34 strings'], metrics: [['Coverage', '97%'], ['Review', '3'], ['Locales', '3']] },
    Preview: { eyebrow: 'Learner preview', title: 'Heating Water Safely', description: 'Review the opening card, pacing and success criteria before publishing.', fields: ['Goal · Bring 50 ml water to 90 °C', 'Equipment · Beaker, plate, thermometer', 'Estimated time · 6 minutes'], metrics: [['Steps', '6'], ['Difficulty', 'Basic'], ['XP', '120']] },
  },
  level: {
    content: { eyebrow: 'Lesson structure', title: 'Learning sequence', description: 'Compose the reading, observation and practice rhythm.', fields: ['01 · Safety briefing', '02 · Guided observation', '03 · Sandbox practice'], metrics: [['Blocks', '8'], ['Reading', '4 min'], ['Practice', '7 min']] },
    requirements: { eyebrow: 'Access rules', title: 'Prerequisites', description: 'Review what learners need before this level becomes available.', fields: ['Complete · Laboratory orientation', 'Score · 70% or higher', 'Badge · Safety Basics'], metrics: [['Rules', '3'], ['Required', '2'], ['Optional', '1']] },
    scenario: { eyebrow: 'Practical link', title: 'Attached experience', description: 'Connect the lesson to its hands-on simulation experience.', fields: ['Primary · Basic heating', 'Checkpoint · Temperature at 90 °C', 'Fallback · Guided demonstration'], metrics: [['Steps', '6'], ['Duration', '8 min'], ['Status', 'Ready']] },
    rewards: { eyebrow: 'Progression', title: 'Rewards and unlocks', description: 'Show what learners earn and what becomes available next.', fields: ['Completion · 100 XP', 'Mastery bonus · 25 XP', 'Unlock · Measuring liquids'], metrics: [['Total XP', '125'], ['Unlocks', '1'], ['Badge', 'Optional']] },
    localization: { eyebrow: 'Translation desk', title: 'Level language coverage', description: 'Compare lesson copy and learning objectives across locales.', fields: ['Russian · Complete', 'Uzbek · 88% complete', 'English · Complete'], metrics: [['Coverage', '96%'], ['Strings', '42'], ['Review', '5']] },
    preview: { eyebrow: 'Student preview', title: 'First Heating', description: 'A polished overview of the level card and its learning promise.', fields: ['Chapter 01 · Laboratory Basics', 'Learn safe heating and observation', 'Includes one guided simulation'], metrics: [['Level', '01'], ['Duration', '12 min'], ['Reward', '100 XP']] },
  },
};

export default function AdminDesignPanel({ domain, tab }: { domain: Domain; tab: string }) {
  const fallback = copy[domain].preview ?? Object.values(copy[domain])[0];
  const item = copy[domain][tab] ?? copy[domain][tab.toLowerCase()] ?? fallback;
  const isPreview = tab.toLowerCase() === 'preview';

  return (
    <div className="h-full overflow-y-auto p-5 md:p-7 text-[var(--admin-text)]">
      <div className="mb-6 flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
        <div>
          <div className="mb-2 flex items-center gap-2 text-[11px] font-bold uppercase tracking-[.18em] text-violet-400"><Sparkles size={14} />{item.eyebrow}</div>
          <h3 className="text-2xl font-bold">{item.title}</h3>
          <p className="mt-2 max-w-2xl text-sm leading-6 text-[var(--admin-secondary)]">{item.description}</p>
        </div>
        <div className="flex gap-2"><span className="rounded-full border border-emerald-400/20 bg-emerald-400/10 px-3 py-1.5 text-xs font-semibold text-emerald-400">UI ready</span><span className="rounded-full border border-[var(--admin-border)] bg-[var(--admin-panel-2)] px-3 py-1.5 text-xs text-[var(--admin-secondary)]">Draft</span></div>
      </div>

      <div className="grid gap-4 sm:grid-cols-3">
        {item.metrics.map(([label, value]) => <div key={label} className="rounded-xl border border-[var(--admin-border)] bg-[var(--admin-panel-2)] p-4"><div className="text-xs text-[var(--admin-secondary)]">{label}</div><div className="mt-1 text-xl font-bold">{value}</div></div>)}
      </div>

      <div className="mt-5 grid gap-5 lg:grid-cols-[1.2fr_.8fr]">
        <section className="rounded-2xl border border-[var(--admin-border)] bg-[var(--admin-panel-2)] p-5">
          <div className="mb-4 flex items-center justify-between"><h4 className="font-semibold">Configuration</h4><CircleGauge size={18} className="text-violet-400" /></div>
          <div className="space-y-3">{item.fields.map((field, index) => <label key={field} className="block"><span className="mb-1.5 block text-xs text-[var(--admin-secondary)]">Property {String(index + 1).padStart(2, '0')}</span><div className="flex items-center justify-between rounded-xl border border-[var(--admin-border)] bg-[var(--admin-panel)] px-4 py-3 text-sm"><span>{field}</span><CheckCircle2 size={16} className="text-emerald-400" /></div></label>)}</div>
        </section>
        <section className="relative min-h-64 overflow-hidden rounded-2xl border border-violet-400/20 bg-gradient-to-br from-violet-500/15 via-[var(--admin-panel-2)] to-cyan-400/10 p-5">
          <div className="absolute -right-16 -top-16 h-44 w-44 rounded-full bg-violet-500/20 blur-3xl" />
          <div className="relative flex h-full flex-col">
            <div className="flex items-center justify-between"><span className="text-xs font-bold uppercase tracking-widest text-[var(--admin-secondary)]">{isPreview ? 'Live card' : 'Signal preview'}</span>{tab === 'Localization' ? <Globe2 size={18} /> : isPreview ? <Play size={18} /> : <Activity size={18} />}</div>
            <div className="my-auto py-6"><div className="mb-4 flex h-16 w-16 items-center justify-center rounded-2xl border border-white/10 bg-white/5 text-2xl">{domain === 'equipment' ? '⚗' : domain === 'scenario' ? '⌁' : '01'}</div><div className="text-lg font-bold">{item.title}</div><p className="mt-2 text-sm leading-6 text-[var(--admin-secondary)]">Designed as a focused admin surface with clear status, hierarchy and review cues.</p></div>
            <div className="grid grid-cols-8 items-end gap-1.5">{[34, 52, 44, 68, 58, 82, 71, 92].map((height, i) => <div key={i} className="rounded-t bg-gradient-to-t from-violet-600 to-cyan-400" style={{ height }} />)}</div>
          </div>
        </section>
      </div>
    </div>
  );
}

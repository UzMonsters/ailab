'use client';
import { useRef, useState, useEffect } from 'react';
import { ArrowRight, BrainCircuit, Sparkles, BarChart3, Zap, Paperclip } from 'lucide-react';

function Reveal({ children, className = '' }: { children: React.ReactNode; className?: string }) {
  const ref = useRef<HTMLDivElement>(null);
  const [visible, setVisible] = useState(false);
  useEffect(() => {
    const node = ref.current;
    if (!node) return;
    const observer = new IntersectionObserver(([entry]) => {
      if (entry.isIntersecting) { setVisible(true); observer.disconnect(); }
    }, { threshold: 0.12 });
    observer.observe(node);
    return () => observer.disconnect();
  }, []);
  return <div ref={ref} data-reveal className={`${className} ${visible ? 'is-visible' : ''}`}>{children}</div>;
}

function SectionDecor() {
  return (
    <div className="section-decor section-decor-workspace" aria-hidden="true">
      <img className="decor-img decor-blueprint" src="/decor-blueprint.png" alt="" loading="lazy" />
      <img className="decor-img decor-molecule" src="/decor-molecule.png" alt="" loading="lazy" />
    </div>
  );
}

export default function AIAssistantSection() {
  const [assistantInput, setAssistantInput] = useState('');
  const [assistantReply, setAssistantReply] = useState('Water is polar because oxygen attracts electrons more strongly than hydrogen, creating partial charges across the molecule.');
  const [thinking, setThinking] = useState(false);
  const [analyzing, setAnalyzing] = useState('Why is water polar?');

  const askAssistant = (prompt?: string) => {
    const text = (prompt ?? assistantInput).trim();
    if (!text || thinking) return;
    setThinking(true);
    setAnalyzing(text);
    setAssistantInput('');
    window.setTimeout(() => {
      setAssistantReply(`I mapped ${text} across structure, energy, and reaction pathways. The highest-confidence model is ready to inspect.`);
      setThinking(false);
    }, 850);
  };

  return (
    <section className="section-wrap section-block workspace-grid" id="workspace-ai">
      <SectionDecor />
      <Reveal className="tool-panel assistant-panel reveal-right">
        <div className="tool-heading">
          <div>
            <p className="eyebrow">Research copilot</p>
            <h2>Ask better questions.</h2>
            <p>Turn a hypothesis into a model with a little help from AI.</p>
          </div>
          <span className="tool-icon"><BrainCircuit /></span>
        </div>
        <div className="chat-window">
          <div className="chat-suggest">
            <span className="chat-suggest-label">Suggested prompts</span>
            <div className="chat-suggest-row">
              <button onClick={() => askAssistant('Show molecular bonds')}>Show molecular bonds</button>
              <button onClick={() => askAssistant('Simulate reaction with Hydrogen')}>Simulate reaction with Hydrogen</button>
            </div>
          </div>
          <div className="chat-messages">
            <div className="user-bubble">Why is water polar?</div>
            <div className={`assistant-row ${thinking ? 'is-thinking' : 'reply-enter'}`}>
              <span className="bot-avatar"><Sparkles /></span>
              <div className="assistant-bubble">
                <Sparkles />
                {thinking ? (
                  <div className="ai-analysis">
                    <div className="ai-status">Analyzing {analyzing}…</div>
                    <div className="ai-progress"><i /></div>
                    <dl>
                      <div><dt>Sources</dt><dd>Nature · PubChem · Science</dd></div>
                      <div><dt>Confidence</dt><dd>98.7%</dd></div>
                      <div><dt>Est. simulation</dt><dd>1.4 sec</dd></div>
                    </dl>
                  </div>
                ) : (
                  <p key={assistantReply}>{assistantReply}</p>
                )}
              </div>
            </div>
          </div>
          <div className="citation">
            <span>Source trace</span>
            <a href="#sciences">Molecular polarity / 04 papers <ArrowRight /></a>
          </div>
          <div className="prompt-chips">
            <button onClick={() => askAssistant('Explain molecular polarity')}><Sparkles /> Explain</button>
            <button onClick={() => askAssistant('Analyze this reaction')}><BarChart3 /> Analyze</button>
            <button onClick={() => askAssistant('Predict a reaction')}><Zap /> Predict</button>
          </div>
          <div className="prompt-row">
            <button className="attach-button" aria-label="Attach scientific data"><Paperclip /></button>
            <input aria-label="Ask the AI chemistry assistant" placeholder="Ask anything..." value={assistantInput} onChange={(e) => setAssistantInput(e.target.value)} onKeyDown={(e) => { if (e.key === 'Enter') askAssistant(); }} />
            <button className="send-button" aria-label="Send message" onClick={() => askAssistant()}><ArrowRight /></button>
          </div>
        </div>
      </Reveal>
    </section>
  );
}

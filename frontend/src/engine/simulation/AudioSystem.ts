import { Workspace } from '../workspace/Workspace';
import type { EngineSystem } from './EngineSystem';

export class AudioSystem implements EngineSystem {
  private ctx: AudioContext | null = null;
  private lastBoilTime = 0;
  private lastPourTime = 0;
  private lastClinkTime = 0;
  
  constructor() {
    if (typeof window !== 'undefined') {
      try {
        const AudioContextClass = window.AudioContext || (window as Window & { webkitAudioContext?: typeof AudioContext }).webkitAudioContext;
        if (AudioContextClass) {
          this.ctx = new AudioContextClass();
        }
      } catch (e) {
        console.warn('AudioContext not supported');
      }
      
      // Auto-resume on interaction
      const resumeAudio = () => {
        if (this.ctx && this.ctx.state === 'suspended') {
          this.ctx.resume().catch(() => {});
        }
        window.removeEventListener('click', resumeAudio);
      };
      window.addEventListener('click', resumeAudio);
    }
  }

  playGlassClink() {
    const now = performance.now();
    if (now - this.lastClinkTime < 200) return;
    this.lastClinkTime = now;
    console.debug('[AudioSystem] Playing: glass_clink.mp3');
    this.playBlip(800, 0.05);
  }

  playGlassBreak() {
    const now = performance.now();
    if (now - this.lastClinkTime < 300) return;
    this.lastClinkTime = now;
    console.debug('[AudioSystem] Playing: glass_break.mp3');
    this.playBlip(1200, 0.15);
    this.playNoise(0.25);
  }

  playBoiling() {
    const now = performance.now();
    if (now - this.lastBoilTime < 200) return; // Throttle bubbling
    this.lastBoilTime = now;
    console.debug('[AudioSystem] Playing: boiling.mp3');
    this.playBubble();
  }

  playPour() {
    const now = performance.now();
    if (now - this.lastPourTime < 100) return; // Throttle pouring
    this.lastPourTime = now;
    console.debug('[AudioSystem] Playing: pour.mp3');
    this.playNoise(0.1);
  }

  private playBlip(freq: number, duration: number) {
    if (!this.ctx) return;
    if (this.ctx.state === 'suspended') return;
    
    const osc = this.ctx.createOscillator();
    const gain = this.ctx.createGain();
    osc.connect(gain);
    gain.connect(this.ctx.destination);
    
    osc.frequency.value = freq;
    osc.type = 'sine';
    
    gain.gain.setValueAtTime(0.1, this.ctx.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.001, this.ctx.currentTime + duration);
    
    osc.start();
    osc.stop(this.ctx.currentTime + duration);
  }

  private playBubble() {
    if (!this.ctx) return;
    if (this.ctx.state === 'suspended') return;
    
    const osc = this.ctx.createOscillator();
    const gain = this.ctx.createGain();
    osc.connect(gain);
    gain.connect(this.ctx.destination);
    
    const baseFreq = 300 + Math.random() * 200;
    osc.frequency.setValueAtTime(baseFreq, this.ctx.currentTime);
    osc.frequency.exponentialRampToValueAtTime(baseFreq + 400, this.ctx.currentTime + 0.1);
    
    osc.type = 'sine';
    
    gain.gain.setValueAtTime(0, this.ctx.currentTime);
    gain.gain.linearRampToValueAtTime(0.1, this.ctx.currentTime + 0.01);
    gain.gain.exponentialRampToValueAtTime(0.001, this.ctx.currentTime + 0.1);
    
    osc.start();
    osc.stop(this.ctx.currentTime + 0.1);
  }

  private playNoise(duration: number) {
    if (!this.ctx) return;
    if (this.ctx.state === 'suspended') return;
    
    const bufferSize = this.ctx.sampleRate * duration;
    const buffer = this.ctx.createBuffer(1, bufferSize, this.ctx.sampleRate);
    const data = buffer.getChannelData(0);
    for (let i = 0; i < bufferSize; i++) {
      data[i] = Math.random() * 2 - 1;
    }
    
    const noise = this.ctx.createBufferSource();
    noise.buffer = buffer;
    
    const filter = this.ctx.createBiquadFilter();
    filter.type = 'lowpass';
    filter.frequency.value = 800;
    
    const gain = this.ctx.createGain();
    
    noise.connect(filter);
    filter.connect(gain);
    gain.connect(this.ctx.destination);
    
    gain.gain.setValueAtTime(0, this.ctx.currentTime);
    gain.gain.linearRampToValueAtTime(0.05, this.ctx.currentTime + 0.02);
    gain.gain.linearRampToValueAtTime(0, this.ctx.currentTime + duration);
    
    noise.start();
  }

  update(deltaSeconds: number) {
    return false;
  }
}
